package com.wang.meeting.websocket.v1;

import com.wang.meeting.websocket.common.BaseWebSocketSession;
import io.netty.util.internal.StringUtil;
import lombok.extern.slf4j.Slf4j;
import org.reactivestreams.Publisher;
import org.reactivestreams.Subscription;
import org.springframework.web.reactive.socket.WebSocketMessage;
import org.springframework.web.reactive.socket.WebSocketSession;
import reactor.core.publisher.Mono;
import reactor.netty.channel.AbortedException;

import java.time.Duration;
import java.util.concurrent.TimeoutException;

@Slf4j
public class MyWebSocketSessionV1 extends BaseWebSocketSession {
    public MyWebSocketSessionV1(WebSocketSession session) {
        super(session);
    }

    public Mono<Void> handle() {
        return session.receive()
                .doOnSubscribe(this::OnSubscribe)
                .timeout(Duration.ofSeconds(20))
                .flatMap(this::handleMessage)
                .onErrorResume(this::handleError)
                .doOnTerminate(this::onTerminate)
                .then();
    }

    private Publisher<? extends Void> handleError(Throwable throwable) {
        if (throwable instanceof TimeoutException || throwable instanceof AbortedException) {
            log.error(throwable.getMessage(), throwable);
        }
        return Mono.empty();
    }

    private void onTerminate() {
        log.info("onTerminate");
    }

    private void onError(Throwable throwable) {
        log.error(throwable.getMessage(), throwable);
    }

    private Mono<Void> handleMessage(WebSocketMessage webSocketMessage) {
        String text = webSocketMessage.getPayloadAsText();
        if (StringUtil.isNullOrEmpty(text)) {
            return Mono.empty();
        }
        if ("ping".equals(text)) {
            return session.send(Mono.just(session.textMessage("pong")));
        } else {
            log.info("receive message: {}", text);
        }
        return Mono.empty();
    }

    private void OnSubscribe(Subscription subscription) {
        log.info("OnSubscribe");
    }
}
