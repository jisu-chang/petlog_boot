package com.example.PetLog.User;

import jakarta.validation.constraints.AssertTrue;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class UserPasswordUpdateDTO {
    @NotBlank(message = "현재 비밀번호를 입력해주세요.")
    private String currentPw;

    @NotBlank(message = "새 비밀번호를 입력해주세요.")
    @Size(min = 8, max = 20, message = "비밀번호는 8자 이상 20자 이하로 입력하세요.")
    @Pattern(
            regexp = "^(?=.*[A-Za-z])(?=.*\\d)[A-Za-z\\d!@#$%^&*()_+\\-=\\[\\]{}|;:',.<>/?]{8,}$",
            message = "비밀번호는 영문자와 숫자를 포함한 8자 이상이어야 합니다."
    )
    private String newPw;

    @NotBlank(message = "비밀번호를 한번 더 입력해주세요.")
    private String newPwConfirm;

    // 두 비밀번호가 일치하는지 확인하는 유효성 검사 추가
    @AssertTrue(message = "새 비밀번호와 비밀번호 확인이 일치하지 않습니다.")
    public boolean isNewPwMatching() {
        return newPw != null && newPw.equals(newPwConfirm);
    }
}