package com.pathfinder.delivery.infrastructure.external.security.jwt;

import static com.pathfinder.delivery.infrastructure.external.security.filter.JwtAuthorizationFilter.PRINCIPAL_REQUEST_ATTRIBUTE;
import static com.pathfinder.delivery.infrastructure.external.security.filter.JwtAuthorizationFilter.ROLE_HEADER;
import static com.pathfinder.delivery.infrastructure.external.security.filter.JwtAuthorizationFilter.USERNAME_HEADER;
import static com.pathfinder.delivery.infrastructure.external.security.filter.JwtAuthorizationFilter.stripRolePrefix;

import com.pathfinder.delivery.infrastructure.external.security.filter.JwtAuthorizationFilter;
import com.pathfinder.delivery.infrastructure.external.security.filter.JwtAuthorizationFilter.GatewayPrincipal;
import java.util.Optional;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

@Component
public class JwtUserContext {

    private static final String DEFAULT_SUBJECT = "system";

    public static String getUsername() {
        return currentPrincipal()
                .map(GatewayPrincipal::username)
                .filter(StringUtils::hasText)
                .orElseGet(() -> headerValue(USERNAME_HEADER).orElse(DEFAULT_SUBJECT));
    }

    public static String getUsernameFromHeader() {
        return getUsername();
    }

    public static String getRole() {
        return currentPrincipal()
                .flatMap(GatewayPrincipal::primaryRole)
                .map(JwtAuthorizationFilter::stripRolePrefix)
                .orElseGet(() -> headerValue(ROLE_HEADER)
                        .map(JwtAuthorizationFilter::stripRolePrefix)
                        .orElse(DEFAULT_SUBJECT));
    }

    public static String getRoleFromHeader() {
        return getRole();
    }

    public static boolean hasRole(String role) {
        if (!StringUtils.hasText(role)) {
            return false;
        }
        return currentPrincipal()
                .map(principal -> principal.hasRole(role))
                .orElseGet(() -> headerValue(ROLE_HEADER)
                        .map(actual -> rolesEqual(actual, role))
                        .orElse(false));
    }

    public static boolean isMaster() {
        return hasRole("MASTER");
    }

    public static boolean isHubManager() {
        return hasRole("HUB_MANAGER");
    }

    private static Optional<GatewayPrincipal> currentPrincipal() {
        Optional<GatewayPrincipal> fromSecurityContext = Optional
                .ofNullable(SecurityContextHolder.getContext().getAuthentication())
                .map(Authentication::getPrincipal)
                .filter(GatewayPrincipal.class::isInstance)
                .map(GatewayPrincipal.class::cast);
        if (fromSecurityContext.isPresent()) {
            return fromSecurityContext;
        }

        return currentRequest()
                .map(request -> request.getAttribute(PRINCIPAL_REQUEST_ATTRIBUTE))
                .filter(GatewayPrincipal.class::isInstance)
                .map(GatewayPrincipal.class::cast);
    }

    private static Optional<ServletRequestAttributes> requestAttributes() {
        return Optional.ofNullable(RequestContextHolder.getRequestAttributes())
                .filter(ServletRequestAttributes.class::isInstance)
                .map(ServletRequestAttributes.class::cast);
    }

    private static Optional<jakarta.servlet.http.HttpServletRequest> currentRequest() {
        return requestAttributes().map(ServletRequestAttributes::getRequest);
    }

    private static Optional<String> headerValue(String headerName) {
        return currentRequest()
                .map(request -> Optional.ofNullable(request.getHeader(headerName))
                        .filter(StringUtils::hasText)
                        .orElseGet(() -> {
                            Object attribute = request.getAttribute(headerName);
                            return attribute instanceof String attr ? attr : null;
                        }))
                .filter(StringUtils::hasText);
    }

    private static boolean rolesEqual(String actualRole, String targetRole) {
        if (!StringUtils.hasText(actualRole) || !StringUtils.hasText(targetRole)) {
            return false;
        }
        String normalizedActual = stripRolePrefix(actualRole);
        String normalizedTarget = stripRolePrefix(targetRole);
        return normalizedActual.equalsIgnoreCase(normalizedTarget);
    }
}