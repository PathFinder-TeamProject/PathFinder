package com.pathfinder.user.application;

import com.pathfinder.user.application.dto.request.*;
import com.pathfinder.user.domain.entity.UserEntity;
import com.pathfinder.user.domain.enums.UserRoleEnum;
import com.pathfinder.user.domain.enums.UserStatusEnum;
import com.pathfinder.user.domain.repository.UserRepository;
import com.pathfinder.user.presentation.dto.response.*;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserServiceV1 {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public SignupResponseDto signup(SignupRequestDto requestDto) {
        // 유저네임 중복 확인
        String username = requestDto.getUsername();
        if (userRepository.findByUsername(username).isPresent()) {
            throw new RuntimeException("유저네임 중복");
        }
        // 이메일 중복 확인
        String email = requestDto.getEmail();
        if (userRepository.findByEmail(email).isPresent()) {
            throw new RuntimeException("이메일 중복");
        }
        String slackId = requestDto.getSlackId();
        if (userRepository.findBySlackId(slackId).isPresent()) {
            throw new RuntimeException("슬랙계정 중복");
        }
        // 권한 확인
        UserRoleEnum role = requestDto.getRole();
        if (role.equals(UserRoleEnum.MASTER)) {
            throw new RuntimeException("마스터 계정 중복");
        }

        UserEntity user = UserEntity.create(requestDto, passwordEncoder.encode(requestDto.getPassword()));
        UserEntity saveUser = userRepository.save(user);
        return SignupResponseDto.of(saveUser);
    }

    public Page<UserResponseDto> getUserList(int page, int size, String sortBy, boolean isAsc) {
        if(size != 10 && size != 30 && size != 50) {
            size = 10;
        }

        Sort.Direction direction = isAsc ? Sort.Direction.ASC : Sort.Direction.DESC;
        Sort sort = Sort.by(direction, sortBy);
        Pageable pageable = PageRequest.of(page, size, sort);

        Page<UserEntity> userList = userRepository.findAll(pageable);
        return userList.map(UserResponseDto::of);
    }
    //관리자 기준 유저 조회
    public UserResponseDto getUser(String username, UserEntity user) {
        // 요청 유저 id와 토큰 유저의 id가 같을 경우 유저 정보 반환
        if (username.equals(user.getUsername())) {
            return UserResponseDto.of(user);
        }

        // 요청 유저 id와 토큰 유저의 id가 다를 경우
        // 토큰 유저의 role이 MASTER인지 판별 후 유저 정보 반환
        UserRoleEnum role = user.getRole();
        if (role.equals(UserRoleEnum.MASTER)) {
            return UserResponseDto.of(findUser(username));
        }

        // 토큰 유저의 role이 MASTER와 MANAGER가 아닐 경우 exception 반환
//        throw new UnauthorizedUserException(ErrorCode.UNAUTHORIZED_USER);
        throw new RuntimeException();
    }

    public UserStatusUpdateResponseDto updateUserStatus(String username, UserStatusUpdateRequestDto userStatusChangeRequestDto, UserEntity user) {

        UserEntity targetUser = findUser(username);
        targetUser.updateStatus(userStatusChangeRequestDto.getStatus());
//        targetUser.markUpdated(user.getUsername());

        return UserStatusUpdateResponseDto.of(targetUser);
    }

    public UserRoleUpdateResponseDto userRoleUpdate (String username, UserRoleUpdateRequestDto userRoleChangeRequestDto, UserEntity user) {
        UserEntity targetUser = findUser(username);
        targetUser.updateRole(userRoleChangeRequestDto.getRole());
//        targetUser.markUpdated(user.getUsername());

        return UserRoleUpdateResponseDto.of(targetUser);
    }

    public UserUpdateResponseDto updateUser(String username, UserUpdateRequestDto userUpdateRequestDto, UserEntity user) {
        // 비밀번호가 일치 하는지 확인
        UserEntity targetUser = findUser(username);
        matchPassword(userUpdateRequestDto.getPassword(), targetUser.getPassword());
        if((targetUser.getUsername().equals(user.getUsername()) && targetUser.getUsername().equals(user.getUsername()))
                || user.getRole().equals(UserRoleEnum.MASTER)) {
            // 비밀번호가 일치하면 유저 이름과 변경할 패스워드 업데이트
            user.update(userUpdateRequestDto, passwordEncoder);
        }
//        user.markUpdated(user.getUsername());
        UserEntity saveUser = userRepository.save(user);

        return UserUpdateResponseDto.of(saveUser);
    }

    public UserDeleteResponseDto deleteUser(UserDeleteRequestDto userDeleteRequestDto, UserEntity user) {
        matchPassword(userDeleteRequestDto.getPassword(), user.getPassword());
//        user.markDeleted(user.getUsername());
        UserEntity saveUser = userRepository.save(user);
        return UserDeleteResponseDto.of(saveUser);
    }

    public UserEntity findUser(String username) {
        return userRepository.findByUsername(username).orElseThrow(
                () -> new RuntimeException()
        );
    }

    private void matchPassword(String rowPassword, String encodedPassword) {
        if (!passwordEncoder.matches(rowPassword, encodedPassword)) {
            throw new RuntimeException();
        }
    }
}
