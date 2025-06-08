package com.example.PetLog.User;

import jakarta.validation.Valid;

public interface UserService {

    void signUpInsert(@Valid UserDTO userDTO);

    //pet에서 추가
    UserEntity findById(Long id);

    UserEntity updateById(Long userId);

    void updatesave(UserEntity userEntity);

    //diary에서 추가
    Long findUserIdByLoginId(String loginId);

    void save(UserDTO dto);
}
