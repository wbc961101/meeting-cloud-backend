package com.wang.meeting.websocket.v1;

import com.wang.meeting.websocket.v1.handler.BaseActionHandler;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.socket.WebSocketHandler;
import org.springframework.web.reactive.socket.WebSocketSession;
import reactor.core.publisher.Mono;

import javax.annotation.Resource;
import java.util.Map;

@Component
@Slf4j
public class WebSocketHandlerV1 implements WebSocketHandler {

    @Resource
    private Map<String, BaseActionHandler> handlerMap;

    @Override
    public @NonNull Mono<Void> handle(WebSocketSession session) {
        MyWebSocketSessionV1 meetingSession = new MyWebSocketSessionV1(session, handlerMap);
        return meetingSession.handle();
    }
}
