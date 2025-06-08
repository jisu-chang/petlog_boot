package com.example.PetLog.User;

import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.Optional;
@Service
@RequiredArgsConstructor
public class CustomOAuth2UserService extends DefaultOAuth2UserService {

    private final UserRepository userRepository;
    private final HttpSession session;

    @Override
    public OAuth2User loadUser(OAuth2UserRequest request) throws OAuth2AuthenticationException {
        OAuth2User oauthUser = super.loadUser(request);

        // 1. 카카오 사용자 정보 파싱
        Map<String, Object> kakaoAccount = (Map<String, Object>) oauthUser.getAttributes().get("kakao_account");
        Map<String, Object> profile = (Map<String, Object>) kakaoAccount.get("profile");

        String nickname = (String) profile.get("nickname"); // 이제 이름으로 사용
        String email = kakaoAccount.containsKey("email") ? (String) kakaoAccount.get("email") : null;
        String profileImgUrl = (String) profile.get("profile_image_url");
//        String phone = kakaoAccount.containsKey("phone_number") ? (String) kakaoAccount.get("phone_number") : null;

        // 2. 닉네임을 이름으로, 로그인 아이디는 나중에 입력받기
        session.setAttribute("kakao_name", nickname); // nickname → name
        session.setAttribute("kakao_email", email);
        session.setAttribute("kakao_profile", profileImgUrl);
//        session.setAttribute("kakao_phone", phone);
        session.setAttribute("social_signup", true);

        // 3. DB에 동일 이메일 있는지 확인
        Optional<UserEntity> userOpt = userRepository.findOptionalByEmail(email);

        if (userOpt.isPresent()) {
            UserEntity user = userOpt.get();
            session.setAttribute("user_id", user.getUserId());
            session.setAttribute("user_login_id", user.getUserLoginId());
            session.setAttribute("user_role", user.getUserRole());
            session.setAttribute("loginUser", user);
            session.removeAttribute("social_signup"); // 중복 회원가입 방지
        }

        return oauthUser;
    }
}
