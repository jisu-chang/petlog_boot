package com.example.PetLog.User;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.util.Map;
import java.util.Optional;
@Service
@RequiredArgsConstructor
public class CustomOAuth2UserService extends DefaultOAuth2UserService {

    private final UserRepository userRepository;
    private final HttpSession session;

    @Override
    public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {
        OAuth2User oauthUser = super.loadUser(userRequest);

        Map<String, Object> attributes = oauthUser.getAttributes();
        Map<String, Object> kakaoAccount = (Map<String, Object>) attributes.get("kakao_account");
        Map<String, Object> properties = (Map<String, Object>) attributes.get("properties");

        String email = kakaoAccount != null ? (String) kakaoAccount.get("email") : null;
        String nickname = (String) properties.get("nickname");
        String profileImage = (String) properties.get("profile_image");

        // 카카오에서 제공하는 사용자 고유 ID를 userLoginId로 사용합니다.
        String kakaoUniqueId = String.valueOf(oauthUser.getName());

        // 사용자를 'email' 기준으로 조회합니다. (이전에 findByEmail에서 에러가 났었으므로)
        // Email은 유일하다고 가정합니다.
        Optional<UserEntity> optionalUser = userRepository.findByEmail(email);
        UserEntity userEntity;

        if (optionalUser.isPresent()) {
            userEntity = optionalUser.get();
            // 기존 사용자: 정보 업데이트 (닉네임, 프로필 이미지, 이메일 업데이트)
            // userLoginId는 카카오 고유 ID로 이미 설정되어 있으므로, 업데이트하지 않습니다.
            userEntity.setName(nickname);        // NAME 필드에 카카오 닉네임 저장
            userEntity.setProfileimg(profileImage);
            // userEntity.setEmail(email);          // EMAIL은 그대로 둡니다. (카카오 계정의 이메일)
            userRepository.save(userEntity);
        } else {
            // 새 사용자: UserEntity 생성 및 저장
            userEntity = new UserEntity();
            userEntity.setUserLoginId(kakaoUniqueId); // USER_LOGIN_ID에 카카오 고유 ID 저장
            userEntity.setEmail(email);                // EMAIL 필드에 카카오 이메일 저장
            userEntity.setName(nickname);              // NAME 필드에 카카오 닉네임 저장
            userEntity.setProfileimg(profileImage);
            userEntity.setUserRole("USER");
            userEntity.setGrapeCount(0);
            userEntity.setRank("일반회원");
            // 소셜 로그인으로 가입했음을 나타내는 필드를 추가하면, OAuth2SuccessHandler에서 신규 유저 구분이 더 명확해집니다.
            // 예시: userEntity.setLoginProvider("KAKAO"); (UserEntity에 loginProvider 필드가 있다면)
            userRepository.save(userEntity);
        }

        HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.currentRequestAttributes()).getRequest();
        HttpSession currentSession = request.getSession();
        currentSession.setAttribute("kakao_email", email);
        currentSession.setAttribute("kakao_name", nickname);
        currentSession.setAttribute("kakao_profile", profileImage);
        currentSession.setAttribute("loginType", "kakao");

        return new CustomOAuth2User(attributes, userEntity);
    }
}


