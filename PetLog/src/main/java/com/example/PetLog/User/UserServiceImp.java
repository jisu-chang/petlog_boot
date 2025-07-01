package com.example.PetLog.User;

import jakarta.mail.internet.MimeMessage;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
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

import com.example.PetLog.ItemUser.ItemUserRepository;
import com.example.PetLog.ItemUser.ItemUserEntity;
import java.util.List;

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

    @Autowired
    ItemUserRepository itemUserRepository;//프로필 프레임 용

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
    public void updateUser(UserEntity userEntity) {
        userRepository.save(userEntity);  // 수정된 데이터를 DB에 저장
    }

    public UserEntity login(String userLoginId, String password) {
        UserEntity user = userRepository.findByUserLoginId(userLoginId).orElse(null);

        if (user != null && user.getPassword().equals(password)) {
            return user;
        }
        return null;
    }

    @Override
    public UserEntity updateById(Long userId) {
        // userId가 null인지 체크
        if (userId == null) {
            throw new IllegalArgumentException("User ID는 null일 수 없습니다.");
        }

        // UserRepository를 사용하여 userId에 해당하는 UserEntity를 조회
        Optional<UserEntity> userEntityOptional = userRepository.findById(userId);

        // UserEntity가 존재하면 반환, 없으면 예외를 던짐
        if (userEntityOptional.isPresent()) {
            return userEntityOptional.get();
        } else {
            // UserEntity가 존재하지 않으면 예외를 던짐
            throw new EntityNotFoundException("ID가 " + userId + "인 사용자가 존재하지 않습니다.");
        }
    }

    @Override
    public UserDTO getUserDTOById(Long userId) {
        // DB에서 UserEntity를 가져온다.
        Optional<UserEntity> userEntityOptional = userRepository.findById(userId);

        if (userEntityOptional.isPresent()) {
            UserEntity userEntity = userEntityOptional.get();

            // UserEntity를 UserDTO로 변환하는 로직
            UserDTO userDTO = new UserDTO();
            userDTO.setUserId(userEntity.getUserId());
            userDTO.setUserLoginId(userEntity.getUserLoginId());
            userDTO.setName(userEntity.getName());
            userDTO.setPhone(userEntity.getPhone());
            userDTO.setEmail(userEntity.getEmail());
            userDTO.setProfileimgName(userEntity.getProfileimg());  // 여기서 이미지 값을 제대로 전달

            // 나머지 필드 설정
            return userDTO;
        }
        return null;  // 유저가 없다면 null 반환
    }

    private UserDTO convertToDTO(UserEntity userEntity) {
        UserDTO userDTO = new UserDTO();

        userDTO.setUserId(userEntity.getUserId());
        userDTO.setUserLoginId(userEntity.getUserLoginId());
        userDTO.setName(userEntity.getName());
        userDTO.setPhone(userEntity.getPhone());
        userDTO.setEmail(userEntity.getEmail());

        // 프로필 이미지 처리 (DB에 저장된 이미지 경로)
        userDTO.setProfileimgName(userEntity.getProfileimg());  // 저장된 파일명 또는 경로를 DTO에 설정

        userDTO.setRank(userEntity.getRank());
        userDTO.setUserRole(userEntity.getUserRole());
        userDTO.setGrapeCount(userEntity.getGrapeCount());

        // 필요한 다른 필드도 추가

        return userDTO;
    }

    @Override
    public Long findUserIdByLoginId(String loginId) {
        Optional<UserEntity> optionalUser = userRepository.findByUserLoginId(loginId);
        if (optionalUser.isPresent()) {
            return optionalUser.get().getUserId();
        }
        return null;
        // 로그인 정보 없으면 null 반환하거나 예외 처리
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


    // 프로필 프레임
    @Override
    public UserDTO getUserProfileWithEquippedFrame(Long userId) {
        Optional<UserEntity> userEntityOptional = userRepository.findById(userId);
        if (userEntityOptional.isEmpty()) {
            return null;
        }
        UserEntity userEntity = userEntityOptional.get();

        UserDTO userDTO = new UserDTO();
        userDTO.setUserId(userEntity.getUserId());
        userDTO.setUserLoginId(userEntity.getUserLoginId());
        userDTO.setName(userEntity.getName());
        userDTO.setPhone(userEntity.getPhone());
        userDTO.setEmail(userEntity.getEmail());
        userDTO.setProfileimgName(userEntity.getProfileimg());
        userDTO.setGrapeCount(userEntity.getGrapeCount());
        userDTO.setRank(userEntity.getRank());

        List<ItemUserEntity> equippedFrames = itemUserRepository.findByUserIdAndUsertemEquipAndItem_ItemCategory(userId, "Y", "프레임");

        if (!equippedFrames.isEmpty()) {
            ItemUserEntity equippedFrame = equippedFrames.get(0);
            if (equippedFrame.getItem() != null) {
                userDTO.setEquippedFrameImageName(equippedFrame.getItem().getItemImage());
                userDTO.setEquippedFrameName(equippedFrame.getItem().getItemName());
            }
        }
        return userDTO;
    }

    @Override
    public UserEntity findByuserId(Long userId) {
        return userRepository.findById(userId).orElseThrow(() -> new EntityNotFoundException("해당 유저가 존재하지 않습니다.")); //null 처리 예외
    }


    @Override
    public Optional<UserEntity> findUserByLoginId(String loginId) {
        return userRepository.findByUserLoginId(loginId);
    }

    @Override
    @Transactional
    public void addGrapes(Long userId, int i) { //quiz 포도알 +3
        Optional<UserEntity> optionalUser = userRepository.findById(userId);

        if (optionalUser.isPresent()) {
            UserEntity user = optionalUser.get();

            int currentGrapes = user.getGrapeCount();
            user.setGrapeCount(currentGrapes + i);

            userRepository.save(user);

            userRepository.flush();
        }
    }

    @Override
    public Optional<UserEntity> findUserById(Long userId) {
        // userRepository.findById는 이미 Optional<UserEntity>를 반환합니다.
        return userRepository.findById(userId);
    }
}