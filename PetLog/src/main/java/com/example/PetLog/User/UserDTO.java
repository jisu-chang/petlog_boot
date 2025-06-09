package com.example.PetLog.User;

import jakarta.persistence.Transient;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.web.multipart.MultipartFile;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class UserDTO {
    Long user_id;

    @NotBlank(message = "아이디는 필수 입력 값입니다.")
    @Size(min = 4, max = 20, message = "아이디는 4자 이상 20자 이하로 입력하세요.")
    @Pattern(regexp = "[a-zA-Z0-o_]+$", message = "아이디는 영문, 숫자, _만 사용 가능합니다.")
    String user_login_id;

    @NotBlank(message = "비밀번호를 입력해주세요.")
    @Size(min = 8, max = 20, message = "비밀번호는 8자 이상 20자 이하로 입력하세요.")
    @Pattern(
            regexp = "^(?=.*[A-Za-z])(?=.*\\d)[A-Za-z\\d!@#$%^&*()_+\\-=\\[\\]{}|;:',.<>/?]{8,}$",
            message = "비밀번호는 영문자와 숫자를 포함한 8자 이상이어야 합니다."
    )
    String password;

    @Transient //DB저장 없이 화면에서만 받음, @Transient는 JPA가 DB 컬럼으로 만들지 않게 함
    @NotBlank(message = "비밀번호를 한번 더 입력해주세요.")
    String passwordCheck;

    @NotBlank(message = "이름에 공백이 포함될 수 없습니다.")
    String name;

    String phone;

    String email;

    MultipartFile profileimg; //실제 저장되는 파일명 or 카카오 URL

    String profileimgName;
    String rank;
    String user_role;
    int grape_count;

    public UserEntity toEntity() {
        String filename = (profileimg != null && !profileimg.isEmpty())
                ? profileimg.getOriginalFilename()
                : "default.png";

        return UserEntity.builder()
                .userId(user_id)
                .userLoginId(user_login_id)
                .password(password)
                .name(name)
                .phone(phone)
                .email(email)
                .profileimg(profileimgName) // 여기서 직접 url, 파일명 추출
                .rank("일반회원")  //null 혹은 "일반, 우수"
                .userRole("USER") //일반회원 고정
                .grapeCount(0)
                .build();
    }
}
