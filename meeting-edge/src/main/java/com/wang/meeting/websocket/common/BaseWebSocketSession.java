package com.wang.meeting.websocket.common;

import lombok.Getter;
import org.springframework.web.reactive.socket.WebSocketSession;

@Getter
public class BaseWebSocketSession implements MeetingWebSocketSession {

    protected final WebSocketSession session;

    protected final String id;

    protected String appId;

    protected String userId;

    protected String deviceId;

    public BaseWebSocketSession(WebSocketSession session) {
        this.session = session;
        this.id = session.getId();
    }


    @Override
    public String getId() {
        return this.id;
    }

    @Override
    public String getAppId() {
        return appId;
    }

    @Override
    public WebSocketSession getSession() {
        return session;
    }

    @Override
    public String getUserId() {
        return userId;
    }

    @Override
    public String getDeviceId() {
        return deviceId;
    }
}
