package com.example.PetLog.User;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;
import java.io.IOException;
import java.util.Optional;

@Component
public class OAuth2SuccessHandler extends SimpleUrlAuthenticationSuccessHandler {

    @Autowired
    private UserRepository userRepository;

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request,
                                        HttpServletResponse response,
                                        Authentication authentication) throws IOException, ServletException {

        CustomOAuth2User oauthUser = (CustomOAuth2User) authentication.getPrincipal();

        String email = oauthUser.getEmail();
        String name = oauthUser.getName();
        String profileImg = oauthUser.getProfileImage();

        HttpSession session = request.getSession();

        Optional<UserEntity> userOpt = userRepository.findByEmail(email);

        if (userOpt.isPresent()) {
            // 기존 유저 → 로그인 처리
            UserEntity user = userOpt.get();
            session.setAttribute("loginUser", user);
            session.setAttribute("userId", user.getUserId());
            session.setAttribute("userLoginId", user.getUserLoginId());
            session.setAttribute("userRole", user.getUserRole());
            session.setAttribute("name", user.getName());
            session.setAttribute("profileimg", user.getProfileimg());
            session.setAttribute("grapeCount", user.getGrapeCount());
            session.setAttribute("rank", user.getRank());
            session.setAttribute("loginType", "kakao");
            getRedirectStrategy().sendRedirect(request, response, "/");
        } else {
            // 신규 유저 → 회원가입 창으로 이동
            // CustomOAuth2UserService에서 아직 DB에 저장하지 않았으므로 여기로 돌아옴
            session.setAttribute("kakao_email", email);
            session.setAttribute("kakao_name", name);
            session.setAttribute("kakao_profile", profileImg);
            session.setAttribute("loginType", "kakao");
            getRedirectStrategy().sendRedirect(request, response, "/signUpKakao");
        }
    }
}