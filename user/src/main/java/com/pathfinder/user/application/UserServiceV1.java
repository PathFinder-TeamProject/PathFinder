package com.pathfinder.user.application;

import com.pathfinder.user.application.dto.request.*;
import com.pathfinder.user.application.excpetion.*;
import com.pathfinder.user.domain.entity.UserEntity;
import com.pathfinder.user.domain.enums.UserRoleEnum;
import com.pathfinder.user.domain.repository.UserRepository;
import com.pathfinder.user.infrastructure.client.DeliveryManagerClient;
import com.pathfinder.user.presentation.dto.response.*;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.Instant;

@Service
@RequiredArgsConstructor
public class UserServiceV1 {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final DeliveryManagerClient deliveryManagerClient;
    public SignupResponseDto signup(SignupRequestDto requestDto) {
        // 유저네임 중복 확인
        String username = requestDto.getUsername();
        if (userRepository.findByUsername(username).isPresent()) {
            throw new DuplicateUserException(UserErrorCode.DUPLICATE_USER,
                UserErrorCode.DUPLICATE_USER.getFormattedMessage("유저네임"));

        }
        // 이메일 중복 확인
        String email = requestDto.getEmail();
        if (userRepository.findByEmail(email).isPresent()) {
            throw new DuplicateUserException(UserErrorCode.DUPLICATE_USER,
                    UserErrorCode.DUPLICATE_USER.getFormattedMessage("이메일"));
        }
        String slackId = requestDto.getSlackId();
        if (userRepository.findBySlackId(slackId).isPresent()) {

            throw new DuplicateUserException(UserErrorCode.DUPLICATE_USER,
                    UserErrorCode.DUPLICATE_USER.getFormattedMessage("슬랙 계정"));
        }
        // 권한 확인
/*        UserRoleEnum role = requestDto.getRole();
        if (role.equals(UserRoleEnum.MASTER)) {
            throw new RuntimeException("마스터 계정 중복");
            throw new U(UserErrorCode.DUPLICATE_USER);
        }*/

        UserEntity user = UserEntity.create(requestDto, passwordEncoder.encode(requestDto.getPassword()));
        UserEntity saveUser = userRepository.save(user);
        /*if (role == UserRoleEnum.DELIVERY_MANAGER) {
            NewDeliveryManagerEvent event = NewDeliveryManagerEvent.builder()
                    .username(saveUser.getUsername())
                    .email(saveUser.getEmail())
                    .hubId(requestDto.getHubId())
                    .deliveryManagerType(requestDto.getDeliveryManagerType().name())
                    .build();

            userEventProducer.publishNewDeliveryManagerEvent(event);
        }*/
        if(requestDto.getRole()==UserRoleEnum.DELIVERY_MANAGER) {
            deliveryManagerClient.createDeliveryManager(
                    new DeliveryManagerRequestDto(user.getUsername(), user.getHubId(), requestDto.getDeliveryManagerType()));
        }
        return SignupResponseDto.of(saveUser);
    }

    public Page<UserResponseDto> getUserList(int page, int size, String sortBy, boolean isAsc) {
        if(size != 10 && size != 30 && size != 50) {
            size = 10;
        }

        Sort.Direction direction = isAsc ? Sort.Direction.ASC : Sort.Direction.DESC;
        Sort sort = Sort.by(direction, sortBy);
        Pageable pageable = PageRequest.of(page-1, size, sort);

        Page<UserEntity> userList = userRepository.findAll(pageable);
        return userList.map(UserResponseDto::of);
    }
    //관리자 기준 유저 조회
    @Cacheable(value = "users", key = "#username")
    public UserResponseDto getUser(String username, UserEntity user) {
        // 토큰 유저의 role이 MASTER인지 판별 후 유저 정보 반환
        UserRoleEnum role = user.getRole();
        if (role.equals(UserRoleEnum.MASTER)) {
            return UserResponseDto.of(findUser(username));
        }

        // 토큰 유저의 role이 MASTER와 MANAGER가 아닐 경우 exception 반환
        throw new UnauthorizedUserException(UserErrorCode.UNAUTHORIZED_USER);
    }

    public UserStatusUpdateResponseDto updateUserStatus(String username, UserStatusUpdateRequestDto userStatusChangeRequestDto, UserEntity user) {

        UserEntity targetUser = findUser(username);
        targetUser.updateStatus(userStatusChangeRequestDto.getStatus());
        targetUser.setModified(Instant.now(), user.getUsername());
        return UserStatusUpdateResponseDto.of(targetUser);
    }

    public UserRoleUpdateResponseDto userRoleUpdate (String username, UserRoleUpdateRequestDto userRoleChangeRequestDto, UserEntity user) {
        UserEntity targetUser = findUser(username);
        targetUser.updateRole(userRoleChangeRequestDto.getRole());
        targetUser.setModified(Instant.now(), user.getUsername());

        return UserRoleUpdateResponseDto.of(targetUser);
    }

    @CacheEvict(value = "users", key = "#username")
    public UserUpdateResponseDto updateUser(String username, UserUpdateRequestDto userUpdateRequestDto, UserEntity user) {
        // 비밀번호가 일치 하는지 확인
        UserEntity targetUser = findUser(username);
        matchPassword(userUpdateRequestDto.getPassword(), targetUser.getPassword());
        if(targetUser.getUsername().equals(user.getUsername())) {
            // 비밀번호가 일치하면 유저 이름과 변경할 패스워드 업데이트
            targetUser.update(userUpdateRequestDto, passwordEncoder);
        }
        user.setModified(Instant.now(), user.getUsername());
        UserEntity saveUser = userRepository.save(user);

        return UserUpdateResponseDto.of(saveUser);
    }

    @CacheEvict(value = "users", key = "#username")
    public UserDeleteResponseDto deleteUser(UserDeleteRequestDto userDeleteRequestDto, UserEntity user) {
        matchPassword(userDeleteRequestDto.getPassword(), user.getPassword());
        user.softDelete(Instant.now(), user.getUsername());
        UserEntity saveUser = userRepository.save(user);
        return UserDeleteResponseDto.of(saveUser);
    }

    @Cacheable(value = "users", key = "#username")
    public UserEntity findUser(String username) {
        return userRepository.findByUsername(username).orElseThrow(
                () -> new UserNotFoundException(UserErrorCode.USER_NOT_FOUND)
        );
    }

    private void matchPassword(String rowPassword, String encodedPassword) {
        if (!passwordEncoder.matches(rowPassword, encodedPassword)) {
            throw new PasswordNotMatchException(UserErrorCode.PASSWORD_NOT_MATCH);
        }
    }
    public void checkActive(String username) {
        UserEntity user = findUser(username);
        if(user.getStatus() != null &&
                user.getStatus() != com.pathfinder.user.domain.enums.UserStatusEnum.ACTIVE) {
            throw new NotActiveUser(UserErrorCode.NOT_ACTIVE_USER);
        }
    }
}
