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
    public Long findUserIdByLoginId(String loginId) {
        UserEntity user = userRepository.findByUserLoginId(loginId);
        if (user != null) {
            return user.getUserId();
        }
        return null;  // 로그인 정보 없으면 null 반환하거나 예외 처리
    }
}
