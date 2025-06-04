package com.example.PetLog.User;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class UserServiceImp implements UserService{

    @Autowired
    UserRepository userRepository;
    @Autowired
    BCryptPasswordEncoder bCryptPasswordEncoder;

    @Override
    public void signUpInsert(UserDTO userDTO) {
        userDTO.setPassword(bCryptPasswordEncoder.encode(userDTO.getPassword())); //패스워드 암호화
        userRepository.save(userDTO.toEntity());
    }

    @Override
    public UserEntity findById(Long id) {
        return userRepository.findById(id).orElse(null);
    }

    @Override
    public UserEntity updateById(Long userId) {
        return userRepository.findById(userId).orElse(null);
    }

    @Override
    public void updatesave(UserEntity userEntity) {
        userRepository.save(userEntity);
    }


}
