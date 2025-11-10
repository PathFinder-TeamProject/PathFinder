package com.pathfinder.delivery.infrastructure.external.security.jwt;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import lombok.Getter;

@Getter
@Component
public class JwtProperties {

    private final String accessHeaderName;
    private final String headerPrefix;
    private final String accessSubject;

    public JwtProperties(
            @Value("${security.jwt.access-header-name:Authorization}") String accessHeaderName,
            @Value("${security.jwt.header-prefix:Bearer }") String headerPrefix,
            @Value("${security.jwt.access-subject:access}") String accessSubject
    ) {
        this.accessHeaderName = accessHeaderName;
        this.headerPrefix = headerPrefix;
        this.accessSubject = accessSubject;
    }
}
