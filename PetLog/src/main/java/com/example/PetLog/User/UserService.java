package com.example.PetLog.User;

import jakarta.validation.Valid;

public interface UserService {

    void signUpInsert(@Valid UserDTO userDTO);

    UserEntity findByLoginId(String id);
}
