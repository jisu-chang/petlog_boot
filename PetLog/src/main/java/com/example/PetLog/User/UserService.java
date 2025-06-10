package com.example.PetLog.User;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;

public interface UserService {

    void signUpInsert(@Valid UserDTO userDTO);

    //pet에서 추가
    UserEntity findById(Long id);

    UserUpdateDTO updateById(Long userId);

    void updatesave(UserEntity userEntity);

    //diary에서 추가
    Long findUserIdByLoginId(String loginId);

    void save(UserDTO dto);

    void deleteUser(Long userId);

    String findLoginIdByNameAndEmail(String name, String email);

    boolean processPasswordReset(String name, String userLoginId, String email, String phone);

    String changePw(Long userId, String currentPw, String newPw, String newPwConfirm);

    boolean idCheck(String userLoginId);

    boolean changePassword(Long userId, String currentPw, String newPw, String newPwConfirm);

    String findLoginIdByInfo(@NotBlank(message = "이름을 입력해주세요.") String name, @NotBlank(message = "이메일 아이디를 입력해주세요.") String email, @NotBlank(message = "전화번호를 입력해주세요.") @Pattern(regexp = "^010\\d{8}$", message = "전화번호는 010으로 시작하는 숫자 11자리여야 합니다.") String phone);

    void updateUser(UserEntity entity);
}