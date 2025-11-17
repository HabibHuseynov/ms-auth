package com.example.auth.client;

import feign.Logger;
import feign.RequestInterceptor;
import feign.codec.ErrorDecoder;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@RequiredArgsConstructor
public class KeycloakAdminFeignConfig {

    private final KeycloakAdminTokenProvider tokenProvider;

    @Bean
    public ErrorDecoder errorAdminDecoder() {
        return new KeycloakErrorDecoder();
    }

    @Bean
    public Logger.Level feignAdminLoggerLevel() {
        return Logger.Level.FULL;
    }

    @Bean
    public RequestInterceptor adminRequestInterceptor() {
        return template -> {
            String token = tokenProvider.getToken();
            template.header("Authorization", "Bearer " + token);
        };
    }
}
