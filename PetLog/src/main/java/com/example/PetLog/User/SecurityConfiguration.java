package com.example.PetLog.User;

import com.example.PetLog.Diary.DiaryEntity;
import com.example.PetLog.Diary.DiaryRepository;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.spring6.SpringTemplateEngine;
import org.thymeleaf.templatemode.TemplateMode;
import org.thymeleaf.templateresolver.ClassLoaderTemplateResolver;

import java.io.IOException;

@Configuration
@EnableWebSecurity
@AllArgsConstructor
@Slf4j
public class SecurityConfiguration {

    @Autowired
    private OAuth2SuccessHandler oAuth2SuccessHandler;
    private final UserDetailsService userDetailsService;
    private final CustomOAuth2UserService customOAuth2UserService;

    @Bean
    public static BCryptPasswordEncoder bCryptPasswordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public DaoAuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider();
        authProvider.setUserDetailsService(userDetailsService);
        authProvider.setPasswordEncoder(bCryptPasswordEncoder());
        return authProvider;
    }

    @Bean
    public AuthenticationManager authManager(HttpSecurity http) throws Exception {
        return http.getSharedObject(AuthenticationManagerBuilder.class)
                .authenticationProvider(authenticationProvider())
                .build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                .csrf().disable()
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/", "/login", "/signUp", "/signUpSave","/findId", "/findIdSave", "/findPw", "/findPwSave", "/idCheck").permitAll()
                        .requestMatchers("/image/**").permitAll()  //로그인이 필요없는 url 작성
                        .anyRequest().authenticated()
                )
                .formLogin(form -> form
                        .loginPage("/login")
                        .loginProcessingUrl("/loginProcess")
                        .usernameParameter("userLoginId")
                        .passwordParameter("password")
                        .successHandler((request, response, authentication) -> {
                            CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();
                            UserEntity loginUser = userDetails.getUser();

                            // 세션에 사용자 정보 저장
                            request.getSession().setAttribute("userRole", loginUser.getUserRole());
                            request.getSession().setAttribute("userId", loginUser.getUserId());  // user_id로 세션에 저장
                            request.getSession().setAttribute("userLoginId", loginUser.getUserLoginId());
                            request.getSession().setAttribute("name", loginUser.getName());
                            request.getSession().setAttribute("grapeCount", loginUser.getGrapeCount());
                            request.getSession().setAttribute("rank", loginUser.getRank());
                            request.getSession().setAttribute("profileimgName", loginUser.getProfileimgName());
                            request.getSession().setAttribute("profileimg", loginUser.getProfileimg());
                            request.getSession().setAttribute("loginUser", loginUser);
                            request.getSession().setAttribute("loginType", "local");

                            // 소셜 로그인 처리 (카카오 등)
                            Boolean socialSignup = (Boolean) request.getSession().getAttribute("social_signup");
                            if (socialSignup != null && socialSignup) {
                                request.getSession().removeAttribute("social_signup");
                                response.sendRedirect("/signUpKakao"); // 카카오 전용 회원가입 창
                                return;
                            }
                            response.sendRedirect("/");  // 기본 페이지로 리디렉션
                        })

                        .failureUrl("/login?error=true")
                        .permitAll()
                )
                .oauth2Login(oauth2 -> oauth2
                        .loginPage("/login")
                        .userInfoEndpoint(userInfo -> userInfo
                                .userService(customOAuth2UserService) //주입 받아서 사용
                        )
                        .successHandler(oAuth2SuccessHandler)  //
                )
                .logout(logout -> logout
                        .logoutUrl("/logout")
                        .logoutRequestMatcher(new AntPathRequestMatcher("/logout"))
                        .invalidateHttpSession(true)
                        .deleteCookies("JSESSIONID")
                        .clearAuthentication(true)
                );

        return http.build();
    }

    @Configuration
    public class WebConfig implements WebMvcConfigurer {
        @Override
        public void addResourceHandlers(ResourceHandlerRegistry registry) {
            registry.addResourceHandler("/images/**")
                    .addResourceLocations("file:///C:/upload/image/"); // 실제 이미지 파일 경로
        }
    }
}
