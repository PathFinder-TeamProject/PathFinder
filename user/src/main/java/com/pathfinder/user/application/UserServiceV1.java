package com.pathfinder.user.application;

import com.pathfinder.global.event.NewDeliveryManagerEvent;
import com.pathfinder.user.application.dto.request.*;
import com.pathfinder.user.application.exception.*;
import com.pathfinder.user.domain.entity.UserEntity;
import com.pathfinder.user.domain.enums.UserRoleEnum;
import com.pathfinder.user.domain.repository.UserRepository;
import com.pathfinder.user.infrastructure.client.DeliveryManagerClient;
import com.pathfinder.user.jwt.JwtUserContext;
import com.pathfinder.user.kafka.UserEventProducer;
import com.pathfinder.user.presentation.dto.response.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
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
    private final UserEventProducer userEventProducer;

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
        if (requestDto.getRole() == UserRoleEnum.DELIVERY_MANAGER) {

            NewDeliveryManagerEvent event = NewDeliveryManagerEvent.builder()
                    .username(user.getUsername())
                    .deliveryManagerType(requestDto.getDeliveryManagerType().name())  // HUB or COMPANY
                    .hubId(requestDto.getHubId())        // 허브 ID
                    .build();

            // Kafka로 이벤트 발행
            userEventProducer.publishNewDeliveryManagerEvent(event);

            log.info("[User Service] 배송 담당자 등록 이벤트 발행 - Username: {}",
                    user.getUsername());
        }
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
    public UserResponseDto getUser(String username, UserEntity user) {
        log.info("유저 조회 - 대상: {}, 요청자: {}, 요청자 권한: {}", username, user.getUsername(), user.getRole());

        // 토큰 유저의 role이 MASTER인지 판별 후 유저 정보 반환
        UserRoleEnum role = user.getRole();
        if (role.equals(UserRoleEnum.MASTER)) {
            UserResponseDto response = UserResponseDto.of(findUser(username));
            log.info("유저 조회 성공 - 캐시에서 조회: {}", username);
            return response;
        }

        // 토큰 유저의 role이 MASTER와 MANAGER가 아닐 경우 exception 반환
        log.warn("유저 조회 실패 - 권한 없음. 요청자: {}, 대상: {}", user.getUsername(), username);
        throw new UnauthorizedUserException(UserErrorCode.UNAUTHORIZED_USER);
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
    @CacheEvict(value = "users", key = "#username")
    public UserUpdateResponseDto updateUser(String username, UserUpdateRequestDto userUpdateRequestDto, UserEntity user) {
        log.info("유저 정보 수정 - 대상: {}, 요청자: {}", username, user.getUsername());

        // 비밀번호가 일치 하는지 확인
        UserEntity targetUser = findUser(username);
        matchPassword(userUpdateRequestDto.getPassword(), targetUser.getPassword());

        if(targetUser.getUsername().equals(user.getUsername())) {
            log.debug("비밀번호 검증 완료 - username: {}", username);
            // 비밀번호가 일치하면 유저 이름과 변경할 패스워드 업데이트
            targetUser.update(userUpdateRequestDto, passwordEncoder);
        }

        user.setModified(Instant.now(), user.getUsername());
        UserEntity saveUser = userRepository.save(user);

        log.info("유저 정보 수정 완료 및 캐시 무효화 - username: {}", username);
        return UserUpdateResponseDto.of(saveUser);
    }

    @Transactional
    @CacheEvict(value = "users", key = "#user.username")
    public UserDeleteResponseDto deleteUser(UserDeleteRequestDto userDeleteRequestDto, UserEntity user) {
        log.info("유저 삭제 시작 - username: {}, 요청 시각: {}", user.getUsername(), Instant.now());

        try {
            // 비밀번호 검증
            log.debug("비밀번호 검증 중 - username: {}", user.getUsername());
            matchPassword(userDeleteRequestDto.getPassword(), user.getPassword());
            log.debug("비밀번호 검증 완료 - username: {}", user.getUsername());

            // Soft Delete 수행
            Instant deleteTime = Instant.now();
            user.softDelete(deleteTime, user.getUsername());
            UserEntity saveUser = userRepository.save(user);

            log.info("유저 삭제 완료 (Soft Delete) - username: {}, 삭제 시각: {}, 삭제자: {}",
                    user.getUsername(), deleteTime, user.getUsername());
            log.info("캐시 무효화 완료 - username: {}", user.getUsername());

            return UserDeleteResponseDto.of(saveUser);

        } catch (PasswordNotMatchException e) {
            log.warn("유저 삭제 실패 - 비밀번호 불일치. username: {}", user.getUsername());
            throw e;
        } catch (Exception e) {
            log.error("유저 삭제 중 예외 발생 - username: {}, error: {}", user.getUsername(), e.getMessage(), e);
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