package com.wang.meeting.config;

import com.wang.meeting.websocket.v1.WebSocketHandlerV1;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.HandlerMapping;
import org.springframework.web.reactive.handler.SimpleUrlHandlerMapping;
import org.springframework.web.reactive.socket.WebSocketHandler;

import javax.annotation.Resource;
import java.util.HashMap;
import java.util.Map;

@Configuration
public class WebSocketHandlerConfig {

    @Resource
    private WebSocketHandlerV1 webSocketHandlerV1;


    @Bean
    public HandlerMapping handlerMapping() {

        Map<String, WebSocketHandler> map = new HashMap<>(1);
        map.put("/ws" + "/**", webSocketHandlerV1);

        SimpleUrlHandlerMapping handlerMapping = new SimpleUrlHandlerMapping();
        handlerMapping.setOrder(1);
        handlerMapping.setUrlMap(map);
        return handlerMapping;
    }
}
