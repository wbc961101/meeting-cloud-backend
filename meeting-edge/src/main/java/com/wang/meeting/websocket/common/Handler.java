package com.wang.meeting.websocket.common;

import com.wang.meeting.model.MeetingRequest;
import com.wang.meeting.websocket.v1.MyWebSocketSessionV1;
import reactor.core.publisher.Mono;


public interface Handler {

    Mono<Void> handle(MyWebSocketSessionV1 session, MeetingRequest request);
}
