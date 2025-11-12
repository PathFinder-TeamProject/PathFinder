package com.pathfinder.order.infrastructure.global.security.auth;

import com.auth0.jwt.interfaces.DecodedJWT;
import lombok.*;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Getter
@Builder
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class CustomUserDetails implements UserDetails {

    private UUID id;
    private String username;
    private String nickname;
    private String email;
    private String accessJwt;
    private List<String> roleList;

    public static CustomUserDetails of(DecodedJWT decodedJwt) {
        String idStr = decodedJwt.getClaim("id").asString();
        String username = decodedJwt.getClaim("username").asString();
        String nickname = decodedJwt.getClaim("nickname").asString();
        String email = decodedJwt.getClaim("email").asString();
        List<String> roles = decodedJwt.getClaim("roleList").asList(String.class);
        
        if (idStr == null || username == null) {
            throw new IllegalArgumentException("JWT must contain 'id' and 'username' claims");
        }
        
        return CustomUserDetails.builder()
                .id(UUID.fromString(idStr))
                .username(username)
                .nickname(nickname != null ? nickname : "")
                .email(email != null ? email : "")
                .accessJwt(decodedJwt.getToken())
                .roleList(roles == null ? List.of() : List.copyOf(roles))
                .build();
    }

    public List<String> getRoleList() {
        return roleList == null ? List.of() : Collections.unmodifiableList(roleList);
    }

    public String getAccessJwt() {
        return accessJwt;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return getRoleList()
                .stream()
                .map(role -> role.startsWith("ROLE_") ? role : "ROLE_" + role)
                .map(SimpleGrantedAuthority::new)
                .collect(Collectors.toUnmodifiableList());
    }

    @Override
    public String getUsername() {
        return username;
    }

    @Override
    public String getPassword() {
        return null;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }
}
