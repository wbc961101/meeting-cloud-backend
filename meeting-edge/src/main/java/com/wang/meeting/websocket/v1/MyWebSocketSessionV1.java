package com.wang.meeting.websocket.v1;

import com.alibaba.fastjson2.JSON;
import com.wang.meeting.model.MeetingRequest;
import com.wang.meeting.websocket.common.BaseWebSocketSession;
import com.wang.meeting.websocket.v1.handler.BaseActionHandler;
import io.netty.util.internal.StringUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.reactivestreams.Subscription;
import org.springframework.web.reactive.socket.WebSocketMessage;
import org.springframework.web.reactive.socket.WebSocketSession;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;
import reactor.netty.channel.AbortedException;

import java.time.Duration;
import java.util.Map;
import java.util.Objects;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.TimeoutException;
import java.util.concurrent.atomic.AtomicBoolean;

@Slf4j
public class MyWebSocketSessionV1 extends BaseWebSocketSession {

    private final Map<String, BaseActionHandler> handlerMap;

    private final Queue<MeetingRequest> upMessageQueue = new ConcurrentLinkedQueue<>();

    private final AtomicBoolean executing = new AtomicBoolean(Boolean.FALSE);


    public MyWebSocketSessionV1(WebSocketSession session, Map<String, BaseActionHandler> handlerMap) {
        super(session);
        this.handlerMap = handlerMap;
    }

    public Mono<Void> handle() {
        return session.receive()
                .doOnSubscribe(this::OnSubscribe)
                .timeout(Duration.ofSeconds(60))
                .doOnNext(this::handleUpMessage)
                .doOnError(this::handleError)
                .doOnTerminate(this::onTerminate)
                .then();
    }

    private void handleError(Throwable throwable) {
        if (throwable instanceof TimeoutException || throwable instanceof AbortedException) {
            log.error(throwable.getMessage(), throwable);
        }
    }

    private void onTerminate() {
        log.info("onTerminate");
    }

    private void onError(Throwable throwable) {
        log.error(throwable.getMessage(), throwable);
    }

    private void handleUpMessage(WebSocketMessage webSocketMessage) {
        String text = webSocketMessage.getPayloadAsText();
        log.info(text);
        if (StringUtil.isNullOrEmpty(text)) {
            session.send(Mono.just(session.textMessage("empty message."))).subscribe();
            return;
        }
        if ("ping".equals(text)) {
            session.send(Mono.just(session.textMessage("pong"))).subscribe();
            return;
        }
        MeetingRequest meetingRequest = null;
        try {
            meetingRequest = JSON.parseObject(text, MeetingRequest.class);
        } catch (RuntimeException e) {
            log.error("parse text message error.");
            session.send(Mono.just(session.textMessage("parse message error."))).subscribe();
            return;
        }
        String type = Objects.isNull(meetingRequest.getType()) ? null : meetingRequest.getType();
        if (StringUtils.equals("req", type)) {
            if (upMessageQueue.size() >= 20) {
                session.send(Mono.just(session.textMessage("to many message."))).subscribe();
            } else {
                boolean offer = upMessageQueue.offer(meetingRequest);
                if (!offer) {
                    log.error("message offered failed.");
                }
                if (executing.compareAndSet(false, true)) {
                    handleMessage();
                }
            }
        } else {
            session.send(Mono.just(session.textMessage("unknown message type."))).subscribe();
        }
    }

    private void OnSubscribe(Subscription subscription) {
        log.info("OnSubscribe");
    }

    private void handleMessage() {
        MeetingRequest request = upMessageQueue.poll();
        if (request == null) {
            if (executing.compareAndSet(true, false)) {
                handleMessage();
            }
        } else {
            executing.set(true);
            doProcessing(request);
        }
    }

    private void doProcessing(MeetingRequest request) {
        BaseActionHandler handler = handlerMap.get(request.getAction());
        Mono<Void> res = Mono.defer(() -> handler.handle(this, request)
                .publishOn(Schedulers.boundedElastic()));
        res.subscribe(
                unused -> log.info("success"),
                throwable -> log.error(throwable.getMessage()),
                this::handleMessage);
    }
}
