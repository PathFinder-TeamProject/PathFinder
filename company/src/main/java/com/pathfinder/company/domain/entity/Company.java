package com.pathfinder.company.domain.entity;

import java.util.UUID;

import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.UuidGenerator;
import org.hibernate.annotations.Where;

import com.pathfinder.global.infrastructure.entity.BaseEntity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "p_company")
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Getter
@SQLDelete(sql = "UPDATE p_company SET deleted_at = NOW(), deleted_by = ? WHERE company_id = ?")
@Where(clause = "deleted_at IS NULL") // 조회 시 삭제 제외
public class Company extends BaseEntity {

	@Id
	@GeneratedValue
	@UuidGenerator
	@Column(name = "company_id", nullable = false)
	private UUID companyId;

	@Column(name = "company_name", nullable = false)
	private String companyName;

	@Column(name = "business_no", nullable = false, length = 50)
	private String businessNo; // 사업자 번호

	@Column(nullable = false, length = 200)
	private String address; // 주소

	@Column(nullable = false, length = 100)
	private String manager; // 담당자 ID (user username)

	@Column(name = "hub_id", nullable = false)
	private UUID hubId; // 허브 ID (연결된 허브)

	@Column(name = "is_producer", nullable = false)
	private boolean producer; // 생산업체 여부

	@Column(name = "is_receiver", nullable = false)
	private boolean receiver; // 수령업체 여부

	public void update(String companyName,  String address, String manager, String businessNo) {
		this.companyName = companyName;
		this.address = address;
		this.manager = manager;
		this.businessNo = businessNo;
	}
}
