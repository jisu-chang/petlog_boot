package com.example.PetLog.User;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

import java.io.IOException;

@Configuration
@AllArgsConstructor
@EnableWebSecurity
public class SecurityConfiguration {

    private final UserDetailsService userDetailsService;

    @Bean
    public static BCryptPasswordEncoder bCryptPasswordEncoder() {
        return new BCryptPasswordEncoder();
    }
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {

        /* @formatter:off */
        http
                .authorizeRequests()
                .requestMatchers("/", "/login","/signUp","/signUpSave").permitAll() //로그인이 필요없는 url 작성, 액션 부분 url 작성하면 됨
                .requestMatchers("/image/**").permitAll()
                .anyRequest().authenticated()//위의 url 외에는 로그인하라는 뜻
                .and()
                .formLogin()
                .permitAll()
                .loginPage("/login")
                .loginProcessingUrl("/loginProcess")
                .usernameParameter("userLoginId")
                .passwordParameter("password")
                .defaultSuccessUrl("/")

                //로그인 인증 됨
                .successHandler(new AuthenticationSuccessHandler() {
                    @Override public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication)throws IOException, ServletException {
                        System.out.println("로그인중 아이디 : "+authentication.getName()); //authentication.getName())
                        response.sendRedirect("/");
                    }
                })

                //로그인 안됨
                .failureHandler(new AuthenticationFailureHandler() {
                    @Override
                    public void onAuthenticationFailure(HttpServletRequest request, HttpServletResponse response, AuthenticationException exception) throws IOException, ServletException {
                        response.sendRedirect("/login");
                    }
                })

                .and()
                .logout()
                .permitAll()
                .logoutUrl("/logout")
                .logoutRequestMatcher(new AntPathRequestMatcher("/logout"))
                .deleteCookies("JSESSIONID")
                .invalidateHttpSession(true)
                .clearAuthentication(true);
        return http.build();
        /* @formatter:on */
    }
    @Autowired
    public void configureGlobal(AuthenticationManagerBuilder auth) throws Exception {
        auth.userDetailsService(userDetailsService).passwordEncoder(bCryptPasswordEncoder());
    }
}
