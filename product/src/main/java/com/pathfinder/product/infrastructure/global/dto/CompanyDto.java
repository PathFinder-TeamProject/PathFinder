package com.pathfinder.product.infrastructure.global.dto;

import jakarta.persistence.Column;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.UuidGenerator;

import java.util.UUID;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CompanyDto {

    private UUID companyId;
    private String companyName;
    private String businessNo; // 사업자 번호
    private String address; // 주소
    private String manager; // 담당자 ID (user username)
    private UUID hubId; // 허브 ID (연결된 허브)
    private boolean producer; // 생산업체 여부
    private boolean receiver; // 수령업체 여부
}

