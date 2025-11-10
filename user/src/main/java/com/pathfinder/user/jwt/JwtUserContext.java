package com.pathfinder.user.jwt;

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
}