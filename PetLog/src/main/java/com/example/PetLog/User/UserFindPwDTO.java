package com.example.PetLog.User;

import jakarta.validation.constraints.*;
import lombok.Data;
import org.springframework.web.multipart.MultipartFile;

@Data
public class UserFindPwDTO {

    @NotBlank(message = "이름을 입력해주세요.")
    String name;

    @NotBlank(message = "아이디를 입력해주세요.")
    String userLoginId;

    @NotBlank(message = "이메일을 입력해주세요.")
    @Email(message = "이메일 형식이 올바르지 않습니다.")
    String email;

    @NotBlank(message = "이메일 도메인을 선택 또는 입력해주세요.")
    String emailDomain;

    @Email(message = "도메인 형식이 올바르지 않습니다.")
    String emailDomainCustom;

    @Pattern(regexp = "^010-\\d{4}-\\d{4}$", message = "전화번호 형식은 010-1234-5678 이어야 합니다.")
    String phone;

    // 이메일을 아이디와 도메인으로 분리 - 회원정보 수정 시 보여주는 용도
    public void splitEmail() {
        if (this.email != null && this.email.contains("@")) {
            String[] parts = this.email.split("@");
            this.emailId = parts[0];  // 이메일 아이디 추출
            String domainPart = parts[1];
            this.emailDomain = domainPart; // 도메인 부분 추출
        }
    }

    // 이메일을 아이디와 도메인으로 합치는 메서드
    public String getFullEmail() {
        // emailDomain이 "direct"일 때만 커스텀 도메인 사용
        if ("direct".equals(emailDomain) && emailDomainCustom != null && !emailDomainCustom.isEmpty()) {
            return emailId + "@" + emailDomainCustom;
        }
        return emailId + "@" + emailDomain;
    }

    // 이메일 아이디와 도메인 필드
    String emailId;

    public UserEntity toEntity() {
        // 이메일 합치기: 카카오 로그인 등 다른 방식에서는 email 필드 사용
        String finalEmail = (email != null && !email.isEmpty()) ? email : getFullEmail();

        return UserEntity.builder()
                .userLoginId(userLoginId)
                .email(finalEmail)
                .name(name)
                .phone(phone)
                .rank("일반회원")
                .userRole("USER")
                .grapeCount(0)
                .build();
    }
}
