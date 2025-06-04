package com.example.PetLog.User.dto;

import com.example.PetLog.User.UserEntity;
import lombok.Data;

//안드로이드 통신용 - 로그인 성공 후 앱에 보내줄 정보 전달용
@Data
public class UserResponseDTO {
    Long userId;
    String userLoginId;
    String name;
    String userRole;

    public UserResponseDTO(UserEntity user) {
        this.userId = user.getUserId();
        this.userLoginId = user.getUserLoginId();
        this.name = user.getName();
        this.userRole = user.getUserRole();
    }
}
