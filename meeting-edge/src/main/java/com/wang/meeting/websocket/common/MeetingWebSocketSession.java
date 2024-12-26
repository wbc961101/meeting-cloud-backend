package com.wang.meeting.websocket.common;

import org.springframework.web.reactive.socket.WebSocketSession;

public interface MeetingWebSocketSession {

    String getId();

    String getAppId();

    WebSocketSession getSession();

    String getUserId();

    String getDeviceId();
}
