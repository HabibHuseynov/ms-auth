//package com.example.auth.config;
//
//import jakarta.servlet.FilterChain;
//import jakarta.servlet.ServletException;
//import jakarta.servlet.http.HttpServletRequest;
//import jakarta.servlet.http.HttpServletResponse;
//import org.springframework.beans.factory.annotation.Value;
//import org.springframework.core.annotation.Order;
//import org.springframework.http.*;
//import org.springframework.stereotype.Component;
//import org.springframework.web.client.RestTemplate;
//import org.springframework.web.filter.OncePerRequestFilter;
//
//import java.io.IOException;
//import java.util.List;
//import java.util.Map;
//
//@Component
//@Order(1)
//public class KeycloakEntitlementFilter extends OncePerRequestFilter {
//
//    private final RestTemplate restTemplate = new RestTemplate();
//
//    @Value("${keycloak.auth-server-url}")
//    private String keycloakUrl;
//    @Value("${keycloak.realm}")
//    private String realm;
//    @Value("${keycloak.resource}")
//    private String clientId;
//
//    private String entitlementEndpoint;
//
//    @Override
//    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain)
//            throws ServletException, IOException {
//
//        if (entitlementEndpoint == null) {
//            entitlementEndpoint = keycloakUrl + "/realms/" + realm + "/authz/entitlement/" + clientId;
//        }
//
//        String authHeader = request.getHeader("Authorization");
//        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
//            response.sendError(401, "No token");
//            return;
//        }
//
//        String token = authHeader.substring(7);
//        String path = request.getRequestURI();
//        String method = request.getMethod();
//
//        boolean hasPermission = hasPermissionFromKeycloak(token, path, method);
//
//        if (!hasPermission) {
//            response.setStatus(403);
//            response.getWriter().write("{\"error\": \"Access denied by Keycloak Authorization Services\"}");
//            return;
//        }
//
//        chain.doFilter(request, response);
//    }
//
//    private boolean hasPermissionFromKeycloak(String bearerToken, String path, String method) {
//        try {
//            HttpHeaders headers = new HttpHeaders();
//            headers.setBearerAuth(bearerToken);
//            headers.setContentType(MediaType.APPLICATION_JSON);
//
//            Map<String, Object> body = Map.of(
//                    "permission", List.of(
//                            Map.of("id", path),
//                            Map.of("id", path + "#" + method.toLowerCase())
//                    )
//            );
//
//            HttpEntity<Map<String, Object>> entity = new HttpEntity<>(body, headers);
//
//            ResponseEntity<Map> resp = restTemplate.exchange(
//                    entitlementEndpoint,
//                    HttpMethod.POST,
//                    entity,
//                    Map.class
//            );
//
//            return resp.getStatusCode() == HttpStatus.OK && resp.getBody() != null && !resp.getBody().isEmpty();
//
//        } catch (Exception e) {
//            // 403, 404, or any error = deny
//            logger.debug("Keycloak denied access: " + e.getMessage());
//            return false;
//        }
//    }
//}
