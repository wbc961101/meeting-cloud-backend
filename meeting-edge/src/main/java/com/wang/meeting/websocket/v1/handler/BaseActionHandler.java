package com.wang.meeting.websocket.v1.handler;

import com.wang.meeting.model.MeetingRequest;
import com.wang.meeting.websocket.common.BaseHandler;
import com.wang.meeting.websocket.v1.MyWebSocketSessionV1;
import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Mono;
import reactor.util.context.ContextView;

@Slf4j
public class BaseActionHandler extends BaseHandler {

    @Override
    public void beforeAction(ContextView contextView, MyWebSocketSessionV1 sessionV1, MeetingRequest request) {
        log.info("before action");
    }

    @Override
    public Mono<Void> process(ContextView ctx, MyWebSocketSessionV1 sessionV1, MeetingRequest request) {
        return Mono.empty();
    }

    @Override
    public void afterAction(ContextView ctx, MyWebSocketSessionV1 sessionV1) {
        log.info("after action");
    }

    @Override
    public void errorAction(ContextView ctx, MyWebSocketSessionV1 sessionV1, Throwable e) {
        log.info("error action");
    }
}
