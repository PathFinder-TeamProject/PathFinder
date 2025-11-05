package com.pathfinder.user.application;

import com.pathfinder.user.application.dto.request.SignupRequestDto;
import com.pathfinder.user.application.dto.request.UserUpdateRequestDto;
import com.pathfinder.user.presentation.dto.response.SignupResponseDto;
import com.pathfinder.user.presentation.dto.response.UserResponseDto;
import jakarta.validation.Valid;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;

@Service
//@RequiredArgsConstructor
public class UserServiceV1 {
    public UserServiceV1() {
    }

    public SignupResponseDto signup(@Valid SignupRequestDto requestDto) {
    }
    public SignupResponseDto updateUser(@Valid SignupRequestDto requestDto) {
    }

    public Page<UserResponseDto> getUserList(int i, int size, String sortBy, boolean isAsc) {
    }

    public Object updateUser(@Valid UserUpdateRequestDto userUpdateRequestDto, Object user) {
    }
}
