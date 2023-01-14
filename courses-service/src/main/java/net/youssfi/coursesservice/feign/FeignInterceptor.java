package net.youssfi.coursesservice.feign;

import feign.RequestInterceptor;
import feign.RequestTemplate;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.stereotype.Component;

@Component
public class FeignInterceptor implements RequestInterceptor {
    @Override
    public void apply(RequestTemplate requestTemplate) {
        // Pour le cas de Spring security, sans Keycloak Adapter
        SecurityContext context = SecurityContextHolder.getContext();
        JwtAuthenticationToken authentication = (JwtAuthenticationToken) context.getAuthentication();
        String tokenValue = authentication.getToken().getTokenValue();
        requestTemplate.header("Authorization","Bearer "+tokenValue);

        // Pour le cas de Keycloak Adapter
        /*
        SecurityContext context = SecurityContextHolder.getContext();
        Authentication authentication = context.getAuthentication();
        Principal principal = (Principal) authentication.getPrincipal();
        KeycloakPrincipal keycloakPrincipal= (KeycloakPrincipal) principal;
        String accessToken = keycloakPrincipal.getKeycloakSecurityContext().getTokenString();
        requestTemplate.header("Authorization","Bearer "+accessToken);
         */
    }
}
