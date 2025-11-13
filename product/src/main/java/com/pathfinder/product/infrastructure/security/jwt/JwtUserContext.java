package com.pathfinder.product.infrastructure.security.jwt;

import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.util.Optional;

@Component
public class JwtUserContext {

    public static String getUsernameFromHeader() {
        return Optional.ofNullable(RequestContextHolder.getRequestAttributes())
                .map(attr -> ((ServletRequestAttributes) attr).getRequest())
                .map(req -> req.getHeader("X-User-Username"))
                .orElse("system");
    }
    public static String getRoleFromHeader() {
        return Optional.ofNullable(RequestContextHolder.getRequestAttributes())
                .map(attr -> ((ServletRequestAttributes) attr).getRequest())
                .map(req -> req.getHeader("X-User-Role"))
                .orElse("system");
    }
    public static boolean isMaster(){
        return JwtUserContext.getRoleFromHeader().equals("MASTER")||JwtUserContext.getRoleFromHeader().equals("ROLE_MASTER");
    }
    public static boolean isHubManager(){
        return JwtUserContext.getRoleFromHeader().equals("HUB_MANAGER")||JwtUserContext.getRoleFromHeader().equals("ROLE_HUB_MANAGER");
    }
}
