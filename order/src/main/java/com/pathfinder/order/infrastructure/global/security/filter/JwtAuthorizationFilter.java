package com.pathfinder.order.infrastructure.global.security.filter;

import com.auth0.jwt.JWT;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.pathfinder.order.infrastructure.global.security.auth.CustomUserDetails;
import com.pathfinder.order.infrastructure.global.security.jwt.JwtProperties;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Slf4j
@Component
@RequiredArgsConstructor
public class JwtAuthorizationFilter extends OncePerRequestFilter {

    private final JwtProperties jwtProperties;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {
        String authorizationHeader = request.getHeader(jwtProperties.getAccessHeaderName());
        log.info("JwtAuthorizationFilter executing for path: {}", request.getRequestURI());
        log.info("Authorization header present: {}", authorizationHeader != null);

        if (!StringUtils.hasText(authorizationHeader) || !authorizationHeader.startsWith(jwtProperties.getHeaderPrefix())) {
            log.info("Missing or invalid Authorization header. Expected prefix: '{}'", jwtProperties.getHeaderPrefix());
            filterChain.doFilter(request, response);
            return;
        }

        String accessJwt = authorizationHeader.substring(jwtProperties.getHeaderPrefix().length()).trim();
        log.info("Extracted JWT token (length: {})", accessJwt.length());

        try {
            DecodedJWT decodedAccessJwt = JWT.decode(accessJwt);
            log.info("JWT decoded successfully. Claims: id={}, username={}, roles={}", 
                decodedAccessJwt.getClaim("id"), 
                decodedAccessJwt.getClaim("username"),
                decodedAccessJwt.getClaim("roleList"));
            
            CustomUserDetails userDetails = CustomUserDetails.of(decodedAccessJwt);
            log.info("UserDetails created: username={}, authorities={}", 
                userDetails.getUsername(), 
                userDetails.getAuthorities());

            UsernamePasswordAuthenticationToken authenticationToken =
                new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
            SecurityContextHolder.getContext().setAuthentication(authenticationToken);
            log.info("Authentication set in SecurityContext for user: {}", userDetails.getUsername());
        } catch (RuntimeException exception) {
            log.error("Failed to decode or process JWT: {}", exception.getMessage(), exception);
            SecurityContextHolder.clearContext();
        }

        filterChain.doFilter(request, response);
    }
}
