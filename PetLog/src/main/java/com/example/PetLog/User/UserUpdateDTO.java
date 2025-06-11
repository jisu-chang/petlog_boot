package com.example.PetLog.User;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import org.springframework.web.multipart.MultipartFile;

@Data
public class UserUpdateDTO {
    private Long userId;

    @NotBlank(message = "이름은 필수입니다.")
    private String name;

    @NotBlank(message = "전화번호는 필수입니다.")
    private String phone;

    @Email(message = "이메일 형식이 올바르지 않습니다.")
    private String email;

    private MultipartFile profileimg;
    private String profileimgName;
}