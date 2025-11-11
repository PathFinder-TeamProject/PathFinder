package com.pathfinder.delivery.infrastructure.external.security.filter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.Serializable;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

@Slf4j
@Component
public class JwtAuthorizationFilter extends OncePerRequestFilter {

    public static final String USERNAME_HEADER = "X-User-Username";
    public static final String ROLE_HEADER = "X-User-Role";
    public static final String PRINCIPAL_REQUEST_ATTRIBUTE =
            JwtAuthorizationFilter.class.getName() + ".principal";

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {

        String requestURI = request.getRequestURI();
        log.debug("=== JWT Authorization Filter (Delivery) ===");
        log.debug("요청 URI: {}", requestURI);

        GatewayPrincipal principal = resolvePrincipal(request);

        if (principal != null) {
            establishAuthentication(request, principal);
        } else {
            log.warn("Gateway 헤더를 찾지 못했습니다. SecurityContext를 초기화하지 않고 체인을 진행합니다.");
        }

        filterChain.doFilter(request, response);
    }

    protected GatewayPrincipal resolvePrincipal(HttpServletRequest request) {
        String username = normalizedHeader(request, USERNAME_HEADER);
        String roleHeader = normalizedHeader(request, ROLE_HEADER);

        if (!StringUtils.hasText(username) || !StringUtils.hasText(roleHeader)) {
            return null;
        }

        GatewayPrincipal principal = GatewayPrincipal.of(username, roleHeader);
        log.debug("Gateway 인증 정보 수신 - username: {}, roles: {}", principal.username(), principal.roles());
        return principal;
    }

    protected void establishAuthentication(HttpServletRequest request, GatewayPrincipal principal) {
        UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(
                principal,
                null,
                principal.authorities()
        );

        SecurityContext context = SecurityContextHolder.createEmptyContext();
        context.setAuthentication(authentication);
        SecurityContextHolder.setContext(context);

        request.setAttribute(PRINCIPAL_REQUEST_ATTRIBUTE, principal);

        log.debug("SecurityContext에 Gateway 인증 정보 설정 완료 - principal: {}, authorities: {}",
                principal.username(),
                principal.authorities());
    }

    private String normalizedHeader(HttpServletRequest request, String headerName) {
        String headerValue = request.getHeader(headerName);
        if (headerValue == null) {
            Object attribute = request.getAttribute(headerName);
            if (attribute instanceof String attrValue) {
                headerValue = attrValue;
            }
        }
        return headerValue != null ? headerValue.trim() : null;
    }

    public record GatewayPrincipal(String username, List<String> roles) implements Serializable {

        private static final long serialVersionUID = 1L;

        public GatewayPrincipal {
            roles = roles == null ? List.of() : List.copyOf(roles);
        }

        public static GatewayPrincipal of(String username, String roleHeader) {
            List<String> parsedRoles = parseRoles(roleHeader);
            return new GatewayPrincipal(username, parsedRoles);
        }

        public Collection<SimpleGrantedAuthority> authorities() {
            if (roles == null || roles.isEmpty()) {
                return List.of();
            }
            return roles.stream()
                    .map(JwtAuthorizationFilter::normalizeRole)
                    .distinct()
                    .map(SimpleGrantedAuthority::new)
                    .toList();
        }

        public Optional<String> primaryRole() {
            if (roles == null || roles.isEmpty()) {
                return Optional.empty();
            }
            return Optional.of(roles.get(0));
        }

        public boolean hasRole(String role) {
            if (!StringUtils.hasText(role)) {
                return false;
            }
            String normalizedTarget = stripRolePrefix(normalizeRole(role));
            return roles.stream()
                    .map(JwtAuthorizationFilter::stripRolePrefix)
                    .anyMatch(normalizedTarget::equalsIgnoreCase);
        }

        private static List<String> parseRoles(String roleHeader) {
            if (!StringUtils.hasText(roleHeader)) {
                return List.of();
            }
            return Arrays.stream(roleHeader.split(","))
                    .map(String::trim)
                    .filter(StringUtils::hasText)
                    .map(JwtAuthorizationFilter::normalizeRole)
                    .toList();
        }
    }

    public static String normalizeRole(String role) {
        if (!StringUtils.hasText(role)) {
            return "";
        }
        String trimmed = role.trim();
        return trimmed.startsWith("ROLE_") ? trimmed : "ROLE_" + trimmed;
    }

    public static String stripRolePrefix(String role) {
        if (!StringUtils.hasText(role)) {
            return "";
        }
        return role.startsWith("ROLE_") ? role.substring("ROLE_".length()) : role;
    }
}
