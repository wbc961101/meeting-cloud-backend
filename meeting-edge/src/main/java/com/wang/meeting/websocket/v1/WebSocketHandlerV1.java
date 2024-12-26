package com.wang.meeting.websocket.v1;

import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.socket.WebSocketHandler;
import org.springframework.web.reactive.socket.WebSocketSession;
import reactor.core.publisher.Mono;

@Component
@Slf4j
public class WebSocketHandlerV1 implements WebSocketHandler {
    @Override
    public @NonNull  Mono<Void> handle(WebSocketSession session) {
        MyWebSocketSessionV1 meetingSession = new MyWebSocketSessionV1(session);
        return meetingSession.handle();
    }
}
