package com.wang.meeting.config;

import io.netty.handler.ssl.SslContext;
import io.netty.handler.ssl.SslContextBuilder;
import io.netty.handler.ssl.util.InsecureTrustManagerFactory;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.reactive.ReactorClientHttpConnector;
import org.springframework.web.reactive.function.client.ExchangeFilterFunction;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;
import reactor.netty.http.client.HttpClient;
import reactor.netty.resources.ConnectionProvider;
import reactor.netty.tcp.SslProvider;

import javax.net.ssl.SSLException;
import java.time.Duration;

@Configuration
@Slf4j
public class WebClientConfig {

    @Bean
    public WebClient webClient() throws SSLException {
        ConnectionProvider provider = ConnectionProvider
                .builder("webClient")
                .maxConnections(200)
                .pendingAcquireTimeout(Duration.ofSeconds(5))
                .pendingAcquireMaxCount(2000)
                .maxIdleTime(Duration.ofSeconds(60))
                .maxLifeTime(Duration.ofSeconds(60))
                .build();

        SslContext sslContext = SslContextBuilder.forClient()
                .trustManager(InsecureTrustManagerFactory.INSTANCE)
                .build();

        ReactorClientHttpConnector httpConnector = new ReactorClientHttpConnector(HttpClient.create(provider)
                .responseTimeout(Duration.ofSeconds(30))
                .secure(SslProvider.builder().sslContext(sslContext).build()));


        return WebClient.builder()
                .clientConnector(httpConnector)
                .codecs(codecs -> codecs.defaultCodecs().maxInMemorySize(1024 * 1024))
                .filter(ExchangeFilterFunction.ofRequestProcessor(clientRequest -> {
                            long startTime = System.currentTimeMillis();
                            return Mono.just(clientRequest).doFinally(signalType -> {
                                long l = System.currentTimeMillis() - startTime;
                                if (l > 10 * 1000) {
                                    log.warn("timeout up 10s.");
                                }
                            });
                        }
                ))
                .build();
    }
}
