package com.example.auth.client;

import com.example.auth.model.request.IntrospectRequest;
import com.example.auth.util.WebUtil;
import feign.Contract;
import feign.Logger;
import feign.RequestInterceptor;
import feign.codec.Encoder;
import feign.codec.ErrorDecoder;
import feign.form.spring.SpringFormEncoder;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.openfeign.support.SpringMvcContract;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.net.http.HttpHeaders;

@Configuration
@RequiredArgsConstructor
public class KeycloakFeignConfig {

    private final WebUtil webUtil;


    @Bean
    public ErrorDecoder errorDecoder() {
        return new KeycloakErrorDecoder();
    }

    @Bean
    public Logger.Level feignLoggerLevel() {
        return Logger.Level.FULL;
    }

    @Bean
    public RequestInterceptor feignRequestInterceptor() {
        return requestTemplate -> {
            requestTemplate.header("Authorization", webUtil.getHeader("Authorization"));
        };
    }
}
