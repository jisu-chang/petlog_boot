package com.example.PetLog.User;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.Collections;

// 기존 UserEntity를 그대로 들고 있는 최소한의 구현체!
public class CustomUserDetails implements UserDetails {

    private final UserEntity user;

    public CustomUserDetails(UserEntity user) {
        this.user = user;
    }

    // 이걸 통해 로그인한 UserEntity를 그대로 꺼낼 수 있음
    public UserEntity getUser() {
        return user;
    }

    //user_role을 가져오는 메소드
    public String getUserRole() {
        return user.getUserRole();
    }

    //유저 아이디 가져오는 메소드
    public Long getUserId() {
        return user.getUserId();
    }


    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return Collections.singleton(new SimpleGrantedAuthority("ROLE_" + user.getUserRole().toUpperCase()));
    }

    @Override
    public String getPassword() {
        return user.getPassword();
    }

    @Override
    public String getUsername() {
        return user.getUserLoginId();
    }

    @Override public boolean isAccountNonExpired() { return true; }
    @Override public boolean isAccountNonLocked() { return true; }
    @Override public boolean isCredentialsNonExpired() { return true; }
    @Override public boolean isEnabled() { return true; }
}

