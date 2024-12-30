package com.wang.meeting.websocket.v1.handler;

import com.wang.meeting.model.MeetingRequest;
import com.wang.meeting.websocket.v1.MyWebSocketSessionV1;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;
import reactor.util.context.ContextView;

import javax.annotation.Resource;

@Component("sendToServer")
@Slf4j
public class SendToServerHandler extends BaseActionHandler {

    @Resource
    WebClient webClient;

    @Override
    public Mono<Void> process(ContextView ctx, MyWebSocketSessionV1 sessionV1, MeetingRequest request) {
        return webClient.get()
                .uri("https://www.baidu.com")
                .retrieve()
                .bodyToMono(String.class)
                .map(str -> {
                    log.info(str);
                    sessionV1.getSession().send(Mono.just(sessionV1.getSession().textMessage("ok"))).subscribe();
                    return Mono.empty();
                }).doOnTerminate(() -> log.info("")).then();
    }
}
