package com.example.PetLog.User;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.web.multipart.MultipartFile;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class UserDTO {
    Long user_id;
    String user_login_id;
    String password;
    @NotBlank(message = "이름에 공백이 포함될 수 없습니다.")
    String name;
    String phone;
    String email;
    MultipartFile profileimg;
    String rank;
    String user_role;
    int grape_count;


    public UserEntity toEntity() {
        return UserEntity.builder()
                .userId(user_id)
                .userLoginId(user_login_id)
                .password(password)
                .name(name)
                .phone(phone)
                .email(email)
                .profileimg(profileimg.getOriginalFilename()) // 여기서 직접 파일명 추출
                .rank(null)  //null 혹은 0
                .userRole("USER") //일반회원 고정
                .grapeCount(0)
                .build();
    }
}
