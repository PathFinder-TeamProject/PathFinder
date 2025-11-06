package com.pathfinder.user.domain.enums;

public enum UserRoleEnum {
    DELIVERY_MANAGER(Authority.DELIVERY_MANAGER),  // 사용자 권한
    COMPANY_MANAGER(Authority.COMPANY_MANAGER),  // 가게 주인 권한
    HUB_MANAGER(Authority.HUB_MANAGER),
    MASTER(Authority.MASTER);

    private final String authority;

    UserRoleEnum(String authority) {
        this.authority = authority;
    }

    public String getAuthority() {
        return this.authority;
    }

    public static class Authority {
        public static final String DELIVERY_MANAGER = "ROLE_DELIVERY_MANAGER";
        public static final String COMPANY_MANAGER = "ROLE_COMPANY_MANAGER";
        public static final String HUB_MANAGER = "ROLE_HUB_MANAGER";
        public static final String MASTER = "ROLE_MASTER";
    }
}