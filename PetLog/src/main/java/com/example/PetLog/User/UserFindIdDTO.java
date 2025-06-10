package com.example.PetLog.User;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.Data;

@Data
public class UserFindIdDTO {

    @NotBlank(message = "이름을 입력해주세요.")
    String name;

    @NotBlank(message = "이메일 아이디를 입력해주세요.")
    String email;

    @NotBlank(message = "이메일 도메인을 선택 또는 입력해주세요.")
    String emailDomain;
    @Email(message = "도메인 형식이 올바르지 않습니다.")
    String emailDomainCustom;

    @Pattern(regexp = "^010-\\d{4}-\\d{4}$", message = "전화번호 형식은 010-1234-5678 이어야 합니다.")
    String phone;

    // 이메일을 합쳐서 전체 주소로 반환하는 메서드
    public String getFullEmail() {
        if ("self".equals(emailDomain)) {
            return email + "@" + emailDomainCustom;
        }
        return email + "@" + emailDomain;
    }
}
