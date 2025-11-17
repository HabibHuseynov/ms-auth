package com.example.auth.util;


import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import java.util.Optional;

@Component
@Slf4j
public class WebUtil {

    @Autowired
    private HttpServletRequest httpServletRequest;

    public String getHeader(String headerKey) {
        HttpServletRequest request = getHttpServletRequest();
        if (request == null) {
            return "";
        }
        return Optional.ofNullable(request.getHeader(headerKey))
                .orElse("");
    }

    private HttpServletRequest getHttpServletRequest() {
        try {
            if (httpServletRequest != null && httpServletRequest.getHeaderNames() != null) {
                return httpServletRequest;
            }
        } catch (Exception ex) {
            log.debug("[WebUtil.getHttpServletRequest] : {}", ex.getMessage());
        }

        return null;
    }

}
