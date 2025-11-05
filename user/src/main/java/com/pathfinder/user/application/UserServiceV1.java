package com.pathfinder.user.application;

import com.pathfinder.user.application.dto.request.SignupRequestDto;
import com.pathfinder.user.application.dto.request.UserUpdateRequestDto;
import com.pathfinder.user.application.excpetion.DuplicateUserException;
import com.pathfinder.user.application.excpetion.ErrorCode;
import com.pathfinder.user.application.excpetion.UnauthorizedUserException;
import com.pathfinder.user.domain.entity.UserEntity;
import com.pathfinder.user.domain.enums.UserRoleEnum;
import com.pathfinder.user.domain.repository.UserRepository;
import com.pathfinder.user.presentation.dto.response.SignupResponseDto;
import com.pathfinder.user.presentation.dto.response.UserResponseDto;
import jakarta.validation.Valid;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserServiceV1 {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    public SignupResponseDto signup(@Valid SignupRequestDto requestDto) {
        // 권한 확인
/*        UserRoleEnum role = requestDto.getRole();
        if (role.equals(UserRoleEnum.MASTER)) {
            throw new UnauthorizedUserException(ErrorCode.UNAUTHORIZED_USER);
        }*/

        /*// 이메일 중복 확인
        String email = requestDto.getEmail();
        if (userRepository.findByEmail(email).isPresent()) {
            throw new RuntimeException();
        }*/

        UserEntity user = UserEntity.create(requestDto, passwordEncoder.encode(requestDto.getPassword()));
        UserEntity saveUser = userRepository.save(user);
        return SignupResponseDto.of(saveUser);
    }
    public SignupResponseDto updateUser(@Valid SignupRequestDto requestDto) {
        return null;
    }

    public Page<UserResponseDto> getUserList(int i, int size, String sortBy, boolean isAsc) {
        return null;
    }

    public Object updateUser(@Valid UserUpdateRequestDto userUpdateRequestDto, Object user) {
        return null;
    }
}
