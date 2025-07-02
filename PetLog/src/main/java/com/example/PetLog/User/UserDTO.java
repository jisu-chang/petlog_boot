package com.example.PetLog.User;

import jakarta.persistence.Transient;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.web.multipart.MultipartFile;
import jakarta.validation.constraints.NotBlank;

import java.io.File;
import java.io.IOException;
import java.util.UUID;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class UserDTO {
    Long userId;

    @NotBlank(message = "아이디는 필수 입력 값입니다.")
    @Size(min = 4, max = 20, message = "아이디는 4자 이상 20자 이하로 입력하세요.")
    @Pattern(regexp = "[a-zA-Z0-o_]+$", message = "아이디는 영문, 숫자, _만 사용 가능합니다.")
    String userLoginId;

    @NotBlank(message = "비밀번호를 입력해주세요.")
    @Pattern(
            regexp = "^(?=.*[A-Za-z])(?=.*\\d)[A-Za-z\\d!@#$%^&*()_+\\-=\\[\\]{}|;:',.<>/?]{8,20}$",
            message = "비밀번호는 영문자와 숫자를 포함한 8자 이상 20자 이하이어야 합니다."
    )
    String password;

    @Transient // DB저장 없이 화면에서만 받음
    @NotBlank(message = "비밀번호를 한번 더 입력해주세요.")
    String passwordCheck;

    @NotBlank(message = "이름에 공백이 포함될 수 없습니다.")
    String name;

    @Pattern(regexp = "^010-\\d{4}-\\d{4}$", message = "전화번호 형식은 010-1234-5678 이어야 합니다.")
    @NotBlank(message = "전화번호를 입력해주세요.")
    String phone;

    @NotBlank(message = "이메일을 입력해주세요.")
    @Email(message = "이메일 형식이 올바르지 않습니다.")
    String email;
    String emailId;

    @NotBlank(message = "이메일 도메인을 선택 또는 입력해주세요.")
    String emailDomain;

    @Email(message = "도메인 형식이 올바르지 않습니다.")
    String emailDomainCustom;

    MultipartFile profileimg;
    String profileimgName;

    String rank;
    String userRole;
    int grapeCount;

    //프로필 프레임 용
    String equippedFrameImageName;
    String equippedFrameName;

    //회원등급 보여주기 용
    int pointsToNext;
    int score;

    public UserDTO(UserEntity userEntity) {
    }

    public String getProfileimgName() {
        return (profileimgName != null && !profileimgName.isEmpty()) ? profileimgName : "default.png";
    }

    // 이메일을 아이디와 도메인으로 분리 - 회원정보 수정 시 보여주는 용도
    public void splitEmail() {
        if (this.email != null && this.email.contains("@")) {
            String[] parts = this.email.split("@");
            this.emailId = parts[0];  // 이메일 아이디 추출
            String domainPart = parts[1];
            this.emailDomain = domainPart; // 도메인 부분 추출
        }
    }

    public String getFullEmail() {
        if ("direct".equals(emailDomain)) {
            return emailId + "@" + emailDomainCustom;
        }
        return emailId + "@" + emailDomain;
    }

    public UserEntity toEntity() {
        // 카카오 로그인으로 들어오면 email 필드에 이미 전체 이메일이 들어가 있으므로 우선 사용
        String finalEmail = (email != null && !email.isEmpty()) ? email : getFullEmail();

        return UserEntity.builder()
                .userId(userId)
                .userLoginId(userLoginId)
                .password(password)
                .email(finalEmail)
                .name(name)
                .phone(phone)
                .profileimg(profileimgName)
                .rank("일반회원")
                .userRole("USER")
                .grapeCount(0)
                .build();
    }

}
