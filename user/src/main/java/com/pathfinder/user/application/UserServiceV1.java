package com.pathfinder.user.application;

import com.pathfinder.global.event.NewDeliveryManagerEvent;
import com.pathfinder.user.application.dto.request.*;
import com.pathfinder.user.application.exception.*;
import com.pathfinder.user.domain.entity.UserEntity;
import com.pathfinder.user.domain.enums.UserRoleEnum;
import com.pathfinder.user.domain.repository.UserRepository;
import com.pathfinder.user.infrastructure.client.DeliveryManagerClient;
import com.pathfinder.user.jwt.JwtUserContext;
import com.pathfinder.user.presentation.dto.response.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.Caching;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;

@Slf4j  // 로그 추가
@Service
@RequiredArgsConstructor
public class UserServiceV1 {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final DeliveryManagerClient deliveryManagerClient;

    public SignupResponseDto signup(SignupRequestDto requestDto) {
        log.info("회원가입 시도 - username: {}, email: {}", requestDto.getUsername(), requestDto.getEmail());

        // 유저네임 중복 확인
        String username = requestDto.getUsername();
        if (userRepository.findByUsername(username).isPresent()) {
            log.warn("회원가입 실패 - 중복된 유저네임: {}", username);
            throw new DuplicateUserException(UserErrorCode.DUPLICATE_USER,
                    UserErrorCode.DUPLICATE_USER.getFormattedMessage("유저네임"));
        }

        // 이메일 중복 확인
        String email = requestDto.getEmail();
        if (userRepository.findByEmail(email).isPresent()) {
            log.warn("회원가입 실패 - 중복된 이메일: {}", email);
            throw new DuplicateUserException(UserErrorCode.DUPLICATE_USER,
                    UserErrorCode.DUPLICATE_USER.getFormattedMessage("이메일"));
        }

        String slackId = requestDto.getSlackId();
        if (userRepository.findBySlackId(slackId).isPresent()) {
            log.warn("회원가입 실패 - 중복된 슬랙 계정: {}", slackId);
            throw new DuplicateUserException(UserErrorCode.DUPLICATE_USER,
                    UserErrorCode.DUPLICATE_USER.getFormattedMessage("슬랙 계정"));
        }

        UserEntity user = UserEntity.create(requestDto, passwordEncoder.encode(requestDto.getPassword()));
        UserEntity saveUser = userRepository.save(user);

        log.info("[User Service] 회원가입 완료 - Username: {}, Role: {}",
                user.getUsername(), user.getRole());

        // 5. 배송 담당자 역할인 경우 Kafka 이벤트 발행
        /*if (requestDto.getRole() == UserRoleEnum.DELIVERY_MANAGER) {

            NewDeliveryManagerEvent event = NewDeliveryManagerEvent.builder()
                    .username(user.getUsername())
                    .deliveryManagerType(requestDto.getDeliveryManagerType().name())  // HUB or COMPANY
                    .hubId(requestDto.getHubId())        // 허브 ID
                    .build();

            // Kafka로 이벤트 발행
            userEventProducer.publishNewDeliveryManagerEvent(event);

            log.info("[User Service] 배송 담당자 등록 이벤트 발행 - Username: {}",
                    user.getUsername());
        }*/
        log.info("회원가입 성공 - username: {}, role: {}", saveUser.getUsername(), saveUser.getRole());
        return SignupResponseDto.of(saveUser);
    }

    public Page<UserResponseDto> getUserList(int page, int size, String sortBy, boolean isAsc) {
        log.debug("유저 목록 조회 - page: {}, size: {}, sortBy: {}, isAsc: {}", page, size, sortBy, isAsc);

        if(size != 10 && size != 30 && size != 50) {
            size = 10;
        }
        if(!sortBy.equals("modifiedAt") || !sortBy.isEmpty() && !sortBy.isBlank()) {
            sortBy = "createdAt";
        }

        Sort.Direction direction = isAsc ? Sort.Direction.ASC : Sort.Direction.DESC;
        Sort sort = Sort.by(direction, sortBy);
        Pageable pageable = PageRequest.of(page>0?page-1:page, size, sort);

        Page<UserEntity> userList = userRepository.findAll(pageable);
        log.info("유저 목록 조회 완료 - 총 {}건, 현재 페이지: {}", userList.getTotalElements(), page);

        return userList.map(UserResponseDto::of);
    }

    //관리자 기준 유저 조회
    @Cacheable(value = "users", key = "#username")
    public UserResponseDto getUser(String username) {
        log.info("유저 조회 - 대상: {}, 요청자: {}, 요청자 권한: {}", username, JwtUserContext.getUsernameFromHeader(), JwtUserContext.getRoleFromHeader());

        // 토큰 유저의 role이 MASTER인지 판별 후 유저 정보 반환
        return UserResponseDto.of(findUser(username));
    }

    @Transactional
    public UserStatusUpdateResponseDto updateUserStatus(String username, UserStatusUpdateRequestDto userStatusChangeRequestDto) {
        log.info("유저 상태 변경 - 대상: {}, 변경할 상태: {}, 요청자: {}",
                username, userStatusChangeRequestDto.getStatus(), JwtUserContext.getUsernameFromHeader());

        UserEntity targetUser = findUser(username);
        targetUser.updateStatus(userStatusChangeRequestDto.getStatus());
        targetUser.setModified(Instant.now(), JwtUserContext.getUsernameFromHeader());

        log.info("유저 상태 변경 완료 - username: {}, 새 상태: {}", username, userStatusChangeRequestDto.getStatus());
        return UserStatusUpdateResponseDto.of(targetUser);
    }

    @Transactional
    public UserRoleUpdateResponseDto userRoleUpdate(String username, UserRoleUpdateRequestDto userRoleChangeRequestDto, UserEntity user) {
        log.info("유저 권한 변경 - 대상: {}, 변경할 권한: {}, 요청자: {}",
                username, userRoleChangeRequestDto.getRole(), user.getUsername());

        UserEntity targetUser = findUser(username);
        targetUser.updateRole(userRoleChangeRequestDto.getRole());
        targetUser.setModified(Instant.now(), user.getUsername());

        log.info("유저 권한 변경 완료 - username: {}, 새 권한: {}", username, userRoleChangeRequestDto.getRole());
        return UserRoleUpdateResponseDto.of(targetUser);
    }

    @Transactional
    @Caching(evict = {
            @CacheEvict(value = "users", key = "#username"),  // 기존 캐시 제거
            @CacheEvict(value = "userList", allEntries = true) // 전체 목록 캐시도 무효화
    }, put = {
            @CachePut(value = "users", key = "#username")      // 수정 후 캐시 재저장
    })
    public UserUpdateResponseDto updateUser(String username, UserUpdateRequestDto userUpdateRequestDto, String loginUsername) {
        log.info("유저 정보 수정 - 대상: {}, 요청자: {}", username, loginUsername);

        UserEntity targetUser = findUser(username);

        // 이메일 중복 확인
        String email = userUpdateRequestDto.getEmail();
        if (email != null && !email.equals(targetUser.getEmail())) {
            userRepository.findByEmail(email).ifPresent(existingUser -> {
                throw new DuplicateUserException(
                        UserErrorCode.DUPLICATE_USER,
                        UserErrorCode.DUPLICATE_USER.getFormattedMessage("이메일")
                );
            });
        }

        // 슬랙 ID 중복 검증
        String slackId = userUpdateRequestDto.getSlackId();
        if (slackId != null && !slackId.equals(targetUser.getSlackId())) {
            userRepository.findBySlackId(slackId).ifPresent(existingUser -> {
                throw new DuplicateUserException(
                        UserErrorCode.DUPLICATE_USER,
                        UserErrorCode.DUPLICATE_USER.getFormattedMessage("슬랙 계정")
                );
            });
        }
        log.debug("JWT role value = {}", JwtUserContext.getRoleFromHeader());
        if (JwtUserContext.getRoleFromHeader().equals("MASTER")) {
            log.info("관리자에 의한 타인 정보 수정 - 대상: {}, 요청자: {}", username, loginUsername);
            targetUser.update(userUpdateRequestDto, passwordEncoder);
        } else if (targetUser.getUsername().equals(loginUsername)) {
            //만약 본인 인 경우
            matchPassword(userUpdateRequestDto.getPassword(), targetUser.getPassword());
            log.debug("비밀번호 검증 완료 - username: {}", username);
            // 비밀번호가 일치하면 유저 이름과 변경할 패스워드 업데이트
            targetUser.update(userUpdateRequestDto, passwordEncoder);
        } else{
            log.warn("권한 없는 유저 정보 수정 시도 - 대상: {}, 요청자: {}", username, loginUsername);
            throw new UnauthorizedUserException(UserErrorCode.UNAUTHORIZED_USER);
        }
        targetUser.setModified(Instant.now(), loginUsername);
        UserEntity saveUser = userRepository.save(targetUser);

        log.info("유저 정보 수정 완료 및 캐시 무효화 - username: {}", username);
        return UserUpdateResponseDto.of(saveUser);
    }

    @Transactional
    @Caching(evict = {
            @CacheEvict(value = "users", key = "#username"),
            @CacheEvict(value = "userList", allEntries = true)
    })
    public void deleteUser(String username, String masterUsername) {
        log.info("유저 삭제 시작 - username: {}, 요청 시각: {}", username, Instant.now());

        try {
            // Soft Delete 수행
            UserEntity targetUser = findUser(username);
            targetUser.softDelete( Instant.now(), masterUsername);
            UserEntity saveUser = userRepository.save(targetUser);

            log.info("유저 삭제 완료 (Soft Delete) - username: {}, 삭제 시각: {}, 삭제자: {}",
                    saveUser.getUsername(), saveUser.getDeletedAt(), masterUsername);
            log.info("캐시 무효화 완료 - username: {}", saveUser.getUsername());
            if(saveUser.getRole() == UserRoleEnum.DELIVERY_MANAGER) {
                log.info("[유저 서비스] 배송담당자 유저 삭제로 인한 배송담당자 서비스 연동 시작 - username: {}",
                        saveUser.getUsername());

                try {
                    // Feign Retryer 설정에 의해 실패 시 자동으로 최대 3회 재시도
                    deliveryManagerClient.deleteDeliveryManager(saveUser.getUsername());

                    log.info("[유저 서비스] 배송담당자 서비스에 삭제 요청 완료 - username: {}",
                            saveUser.getUsername());

                } catch (Exception e) {
                    // 3회 재시도 후에도 실패한 경우
                    log.error("========================================");
                    log.error("[유저 서비스] ⚠️ 배송담당자 서비스 연동 최종 실패 (Feign 재시도 3회 완료)");
                    log.error("[유저 서비스] Username: {}", saveUser.getUsername());
                    log.error("[유저 서비스] Error: {}", e.getMessage());
                    log.error("[유저 서비스] ⚠️ 수동 처리 필요: 배송담당자 서비스에서 username [{}]를 직접 삭제해주세요",
                            saveUser.getUsername());
                    log.error("========================================");
                }
            }
        } catch (Exception e) {
            log.error("유저 삭제 중 예외 발생 - username: {}, error: {}", username, e.getMessage(), e);
            throw e;
        }
    }

    @Cacheable(value = "users", key = "#username")
    public UserEntity findUser(String username) {
        log.debug("유저 검색 - username: {}", username);
        return userRepository.findByUsername(username).orElseThrow(() -> {
            log.warn("유저를 찾을 수 없음 - username: {}", username);
            return new UserNotFoundException(UserErrorCode.USER_NOT_FOUND);
        });
    }

    private void matchPassword(String rowPassword, String encodedPassword) {
        if (!passwordEncoder.matches(rowPassword, encodedPassword)) {
            log.warn("비밀번호 불일치");
            throw new PasswordNotMatchException(UserErrorCode.PASSWORD_NOT_MATCH);
        }
    }

    public void checkApproved(String username) {
        log.debug("유저 활성화 상태 확인 - username: {}", username);
        UserEntity user = findUser(username);

        if(user.getStatus() != null &&
                user.getStatus() != com.pathfinder.user.domain.enums.UserStatusEnum.APPROVED) {
            log.warn("비활성 유저 접근 시도 - username: {}, status: {}", username, user.getStatus());
            throw new NotActiveUserException(UserErrorCode.NOT_APPROVED_USER);
        }

        log.debug("유저 활성화 상태 확인 완료 - username: {}", username);
    }
}