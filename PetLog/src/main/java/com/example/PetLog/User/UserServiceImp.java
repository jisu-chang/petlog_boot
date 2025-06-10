package com.example.PetLog.User;

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
import org.thymeleaf.context.Context;

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
    public UserUpdateDTO updateById(Long userId) {
        // 유저가 존재하는지 확인하고, 존재하면 UserEntity를 가져옴
        System.out.println("updateById() called with userId: " + userId);  // userId가 들어오는지 확인
        UserEntity userEntity = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        System.out.println("UserEntity retrieved: " + userEntity);  // 유저가 제대로 조회되었는지 확인

        // UserEntity를 UserUpdateDTO로 변환
        UserUpdateDTO userUpdateDTO = new UserUpdateDTO();

        // UserEntity에서 값을 가져와서 UserUpdateDTO로 세팅
        userUpdateDTO.setUserId(userEntity.getUserId());  // userId 설정
        userUpdateDTO.setName(userEntity.getName());

        System.out.println("UserUpdateDTO created: " + userUpdateDTO);  // DTO 생성된 후 로그

        // email과 emailDomain 분리하여 세팅
        String email = userEntity.getEmail();
        String emailDomain = email.substring(email.indexOf('@') + 1);
        String emailPrefix = email.substring(0, email.indexOf('@'));

        userUpdateDTO.setEmail(emailPrefix);  // email의 앞부분
        if ("self".equals(emailDomain)) {
            userUpdateDTO.setEmailDomain("self");
            userUpdateDTO.setEmailDomainCustom(emailDomain);  // custom domain을 추가
        } else {
            userUpdateDTO.setEmailDomain(emailDomain);
        }

        userUpdateDTO.setPhone(userEntity.getPhone());
        userUpdateDTO.setProfileimg(userEntity.getProfileimg());  // profileimg 추가

        // 반환
        return userUpdateDTO;
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

    @Override
    public boolean changePassword(Long userId, String currentPw, String newPw, String newPwConfirm) {
        if (!newPw.equals(newPwConfirm)) return false;

        Optional<UserEntity> optionalUser = userRepository.findById(userId);
        if (optionalUser.isEmpty()) return false;

        UserEntity user = optionalUser.get();

        // 현재 비밀번호 일치 확인
        if (!passwordEncoder.matches(currentPw, user.getPassword())) return false;

        // 새 비밀번호 암호화 후 저장
        String encodedNewPw = passwordEncoder.encode(newPw);
        user.setPassword(encodedNewPw);
        userRepository.save(user);

        return true;
    }

    @Override
    public String findLoginIdByInfo(String name, String email, String phone) {
        Optional<UserEntity> user = userRepository.findByNameAndEmailAndPhone(name, email, phone);
        return user.map(UserEntity::getUserLoginId).orElse(null);
    }

    @Override
    public void updateUser(UserEntity entity) {
        // 유저가 존재하는지 확인하고, 존재하지 않으면 예외를 던짐
        UserEntity userToUpdate = userRepository.findById(entity.getUserId())
                .orElseThrow(() -> new RuntimeException("User not found"));

        // 필요한 업데이트 필드를 설정
        userToUpdate.setEmail(entity.getEmail());
        userToUpdate.setName(entity.getName());
        userToUpdate.setPhone(entity.getPhone());
        userToUpdate.setProfileimg(entity.getProfileimg());  // 이미지 업데이트

        // 비밀번호는 기존 값을 그대로 유지 (암호화된 상태로)
        userToUpdate.setPassword(entity.getPassword());

        // 유저 정보를 DB에 저장 (업데이트)
        userRepository.save(userToUpdate);
    }

}
