package com.example.PetLog.User;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.thymeleaf.spring6.SpringTemplateEngine;

import java.util.Optional;
import java.util.UUID;
import jakarta.mail.internet.MimeMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;

import org.thymeleaf.context.Context;
import org.thymeleaf.spring6.SpringTemplateEngine;


@Service
public class UserServiceImp implements UserService{

    @Autowired
    UserRepository userRepository;
    @Autowired
    BCryptPasswordEncoder bCryptPasswordEncoder;
    @Autowired
    PasswordEncoder passwordEncoder;
    @Autowired
    JavaMailSender mailSender;
    @Autowired
    private SpringTemplateEngine templateEngine;

    @Override
    public void signUpInsert(UserDTO userDTO) {
        userDTO.setPassword(bCryptPasswordEncoder.encode(userDTO.getPassword())); //패스워드 암호화
        //기본 이미지 파일 처리
        if (userDTO.getProfileimg() == null || userDTO.getProfileimg().isEmpty()) {
            userDTO.setProfileimgName("default.png");
        } else {
            userDTO.setProfileimgName(userDTO.getProfileimg().getOriginalFilename());
        }
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

    @Override
    public Long findUserIdByLoginId(String loginId) {
        UserEntity user = userRepository.findByUserLoginId(loginId);
        if (user != null) {
            return user.getUserId();
        }
        return null;  // 로그인 정보 없으면 null 반환하거나 예외 처리
    }

    @Override
    public void save(UserDTO dto) {
        userRepository.save(dto.toEntity());
    }

    @Override
    public void deleteUser(Long userId) {
        userRepository.deleteById(userId);
    }

    @Override
    public String findLoginIdByNameAndEmail(String name, String email) {
        return userRepository.findByNameAndEmail(name, email)
                .map(UserEntity::getUserLoginId)
                .orElse(null);
    }

    @Override
    public boolean processPasswordReset(String name, String userLoginId, String email, String phone) {
        Optional<UserEntity> userOpt = userRepository.findByNameAndUserLoginIdAndEmailAndPhone(name, userLoginId, email, phone);

        if (userOpt.isPresent()) {
            String tempPw = UUID.randomUUID().toString().substring(0, 10);
            String encPw = passwordEncoder.encode(tempPw);
            UserEntity user = userOpt.get();
            user.setPassword(encPw);
            userRepository.save(user);

            try {
                // 이메일 내용 구성
                Context context = new Context();
                context.setVariable("name", user.getName());
                context.setVariable("tempPw", tempPw);  // 생성한 임시 비밀번호

                String htmlContent = templateEngine.process("User/PasswordMail", context);

                MimeMessage mimeMessage = mailSender.createMimeMessage();
                MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, true, "UTF-8");

                helper.setTo(user.getEmail());
                helper.setSubject("[PetLog] 임시 비밀번호 안내");
                helper.setText(htmlContent, true);  // HTML 형식 전송

                mailSender.send(mimeMessage);

                return true;
            } catch (Exception e) {
                e.printStackTrace(); // 또는 로그로 대체 가능
                return false;
            }
        }

        return false;
    }

    @Override
    public String changePw(Long userId, String currentPw, String newPw, String newPwConfirm) {
        Optional<UserEntity> userOpt = userRepository.findById(userId);
        if (userOpt.isPresent()){
            UserEntity user = userOpt.get();
            //현재 비밀번호 확인
            if (!passwordEncoder.matches(currentPw, user.getPassword())){
                return "wrong_current";
            }
            //새 비밀번호와 일치여부 확인
            if (!newPw.equals(newPwConfirm)){
                return "mismatch_confirm";
            }
            // 현재 비밀번호와 새 비밀번호가 같을 경우 변경 불가
            if (passwordEncoder.matches(newPw, user.getPassword())) {
                return "same_as_old";
            }
            //비밀번호 변경 후 저장
            String encryptedNewPw = passwordEncoder.encode(newPw);
            user.setPassword(encryptedNewPw);
            userRepository.save(user);
            return "success";
        }
        return "not_found";
    }

    @Override
    public boolean idCheck(String userLoginId) {
        return userRepository.existsByUserLoginIdNative(userLoginId) == 1;
    }



}
