package com.example.PetLog.User.dto;

import lombok.Data;

//안드로이드와 통신용 - 아이디,비밀번호 전달용
@Data
public class LoginRequestDTO {
    String user_login_id;
    String password;
}
