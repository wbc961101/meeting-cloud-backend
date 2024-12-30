package com.wang.meeting.websocket.common;

import com.wang.meeting.model.MeetingRequest;
import com.wang.meeting.websocket.v1.MyWebSocketSessionV1;
import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Mono;
import reactor.util.context.ContextView;

@Slf4j
public abstract class BaseHandler implements Handler {

    protected abstract void beforeAction(ContextView contextView, MyWebSocketSessionV1 sessionV1, MeetingRequest request);

    protected abstract Mono<Void> process(ContextView ctx, MyWebSocketSessionV1 sessionV1, MeetingRequest request);

    protected abstract void afterAction(ContextView ctx, MyWebSocketSessionV1 sessionV1);

    protected abstract void errorAction(ContextView ctx, MyWebSocketSessionV1 sessionV1, Throwable e);

    @Override
    public Mono<Void> handle(MyWebSocketSessionV1 sessionV1, MeetingRequest request) {
        return Mono.just(request)
                .flatMap(req ->
                        Mono.deferContextual(contextView -> {
                            try {
                                beforeAction(contextView, sessionV1, req);
                            } catch (Throwable e) {
                                log.error("error when before.");
                            }
                            return process(contextView, sessionV1, req).onErrorResume(e -> {
                                try {
                                    errorAction(contextView, sessionV1, e);
                                } catch (Throwable throwable) {
                                    log.error("error when error.");
                                }
                                return Mono.empty();
                            }).doOnTerminate(() -> {
                                try {
                                    afterAction(contextView, sessionV1);
                                } catch (Throwable e) {
                                    log.error("error when after.");
                                }
                            });
                        }));
    }
}