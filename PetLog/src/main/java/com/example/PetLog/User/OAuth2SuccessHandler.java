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
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException, ServletException {
        CustomOAuth2User oauthUser = (CustomOAuth2User) authentication.getPrincipal();

        String email = oauthUser.getEmail();
        String name = oauthUser.getName(); // 카카오 닉네임 (UserEntity의 name 필드에 저장됨)
        String profileImg = oauthUser.getProfileImage();

        HttpSession session = request.getSession();

        // CustomOAuth2UserService에서 이미 UserEntity를 만들거나 업데이트했으므로,
        // 여기서 다시 findByEmail을 할 필요 없이 CustomOAuth2User에서 UserEntity를 직접 가져오는 것이 좋습니다.
        // CustomOAuth2User에 getUser() 메소드가 있다고 가정합니다.
        UserEntity user = oauthUser.getUser(); // CustomOAuth2User에 UserEntity 필드가 직접 연결되어 있다면

        // 만약 user가 null일 가능성이 있다면 (예외 처리)
        if (user == null) {
            // 심각한 오류: UserEntity가 CustomOAuth2User에 없음. 로그인 실패 처리 또는 오류 페이지로
            getRedirectStrategy().sendRedirect(request, response, "/error");
            return;
        }

        // user.getLoginProvider()가 "KAKAO"이고,
        // 추가 정보로 받아야 할 필드(예: 전화번호)가 비어있는 경우, 신규 카카오 유저로 간주
        // (user.getPhoneNumber()는 UserEntity에 phoneNumber 필드가 있을 때 사용 가능)
//        if ("KAKAO".equals(user.getLoginProvider()) && (user.getPhone() == null || user.getPhone().isEmpty())) {
//            // 신규 카카오 유저이며, 추가 정보 입력이 필요함
//            session.setAttribute("kakao_email", email);
//            session.setAttribute("kakao_name", name);
//            session.setAttribute("kakao_profile", profileImg);
//            session.setAttribute("loginType", "kakao");
//            getRedirectStrategy().sendRedirect(request, response, "/signUpKakao"); // 추가 정보 입력 페이지
//        } else {
//            // 기존 유저 또는 이미 정보 입력 완료된 소셜 유저 → 로그인 처리
//            session.setAttribute("userId", user.getUserId());
//            session.setAttribute("userLoginId", user.getUserLoginId()); // 카카오 고유 ID
//            session.setAttribute("userRole", user.getUserRole());
//            session.setAttribute("name", user.getName()); // 카카오 닉네임
//            session.setAttribute("profileimg", user.getProfileimg());
//            session.setAttribute("loginType", "kakao");
//            getRedirectStrategy().sendRedirect(request, response, "/"); // 메인 페이지
//        }
    }
}