package com.example.PetLog.User;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import java.util.Optional;

public interface UserService {

    Long signUpInsert(@Valid UserDTO userDTO);

    //pet에서 추가
    UserEntity findById(Long id);

    //diary에서 추가
    Long findUserIdByLoginId(String loginId);

    //메뉴바 포도알 업데이트
    Optional<UserEntity> findUserByLoginId(String loginId);

    void save(UserDTO dto);

    void deleteUser(Long userId);

    boolean processPasswordReset(String name, String userLoginId, String email, String phone);

    boolean idCheck(String userLoginId);

    boolean changePassword(Long userId, String currentPw, String newPw, String newPwConfirm);

    String findLoginIdByInfo(@NotBlank(message = "이름을 입력해주세요.") String name, @NotBlank(message = "이메일 아이디를 입력해주세요.") String email, @NotBlank(message = "전화번호를 입력해주세요.") @Pattern(regexp = "^010\\d{8}$", message = "전화번호는 010으로 시작하는 숫자 11자리여야 합니다.") String phone);

    void updateUser(UserEntity entity);

    UserEntity login(String userLoginId, String password);

    UserEntity updateById(Long userId);

    UserDTO getUserDTOById(Long userId);

    UserDTO getUserProfileWithEquippedFrame(Long userId); //프로필 프레임 용

    UserEntity findByuserId(Long userId);

    void addGrapes(Long userId, int i);

    Optional<UserEntity> findUserById(Long userId);

    void addGrapeCount(Long userId, int i); //커뮤니티 포도알 +1

    int calculateUserScore(Long userId);

    String getUserRank(int score);

    int getPointsToNextRank(int score);
}