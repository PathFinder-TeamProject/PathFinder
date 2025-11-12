package com.pathfinder.product.infrastructure.security.jwt;

import lombok.Getter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

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
