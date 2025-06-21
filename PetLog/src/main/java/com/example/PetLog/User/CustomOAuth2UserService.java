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

        String email = kakaoAccount != null ? (String) kakaoAccount.get("email") : null;
        String nickname = (String) ((Map<String, Object>) attributes.get("properties")).get("nickname");
        String profileImage = (String) ((Map<String, Object>) attributes.get("properties")).get("profile_image");

        // 확인용 로그
        System.out.println("✅ 카카오 이메일: " + email);
        System.out.println("✅ 카카오 닉네임: " + nickname);
        System.out.println("✅ 카카오 이미지: " + profileImage);

        // 세션 저장
        HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.currentRequestAttributes()).getRequest();
        HttpSession session = request.getSession();
        session.setAttribute("kakao_email", email);
        session.setAttribute("kakao_name", nickname);
        session.setAttribute("kakao_profile", profileImage);
        session.setAttribute("loginType", "kakao");
        return new CustomOAuth2User(oauthUser.getAttributes());
    }

}
