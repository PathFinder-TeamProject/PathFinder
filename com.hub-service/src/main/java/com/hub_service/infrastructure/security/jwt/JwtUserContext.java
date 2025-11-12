package com.hub_service.infrastructure.security.jwt;
// 허브라우터 서비스일 경우: com.hub_route_service.infrastructure.config.security.jwt

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import java.util.Base64;
import java.util.Collections;

@Component
public class JwtUserContext {

    private static final String SECRET_KEY = "c3BhcnRhX3Rpa2l0YWthX2JhcHppcF8xMjM0NTY3ODk=";

    /**
     * JWT 토큰에서 username 추출
     */
    public static String getUsernameFromToken(String token) {
        Claims claims = Jwts.parser()
                .setSigningKey(Base64.getDecoder().decode(SECRET_KEY))
                .parseClaimsJws(token)
                .getBody();
        return claims.getSubject();
    }

    /**
     * 현재 인증된 사용자의 이름을 반환
     */
    public static String getUsernameFromHeader() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        return (auth != null) ? auth.getName() : "system";
    }

    /**
     * Username 기반 인증 객체 생성
     */
    public static Authentication buildAuthentication(String username) {
        return new UsernamePasswordAuthenticationToken(
                username,
                null,
                Collections.singletonList(new SimpleGrantedAuthority("ROLE_USER"))
        );
    }
}
