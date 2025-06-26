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

        String kakaoUniqueId = String.valueOf(oauthUser.getName());

        Optional<UserEntity> optionalUser = userRepository.findByEmail(email);
        UserEntity userEntity;

        if (optionalUser.isPresent()) {
            userEntity = optionalUser.get();
            userEntity.setName(nickname);
            userEntity.setProfileimg(profileImage);
            userEntity.setEmail(email);
            userRepository.save(userEntity); // 기존 유저는 업데이트 후 저장
        } else {
            userEntity = new UserEntity();
            userEntity.setUserLoginId(kakaoUniqueId); // 카카오 고유 ID
            userEntity.setEmail(email);
            userEntity.setName(nickname);
            userEntity.setProfileimg(profileImage);
            userEntity.setUserRole("USER");
            userEntity.setGrapeCount(0);
            userEntity.setRank("일반회원");
            // 신규 유저는 여기서 userRepository.save(userEntity); 를 호출하지 않습니다.
            // 저장은 signUpKakao 페이지에서 추가 정보 입력 후 진행됩니다.
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


