package com.example.PetLog.User;

import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.core.GrantedAuthority;
import java.util.Collection;
import java.util.Collections;
import java.util.Map;

public class CustomOAuth2User implements OAuth2User {

    private final Map<String, Object> attributes;

    public CustomOAuth2User(Map<String, Object> attributes) {
        this.attributes = attributes;
    }

    public String getEmail() {
        Map<String, Object> kakaoAccount = (Map<String, Object>) attributes.get("kakao_account");
        return kakaoAccount != null ? (String) kakaoAccount.get("email") : null;
    }

    public String getProfileImage() {
        Map<String, Object> properties = (Map<String, Object>) attributes.get("properties");
        return properties != null ? (String) properties.get("profile_image") : null;
    }

    @Override
    public Map<String, Object> getAttributes() {
        return attributes;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return Collections.emptyList();  // 권한이 없다면 비워도 됨
    }

    @Override
    public String getName() {
        Map<String, Object> properties = (Map<String, Object>) attributes.get("properties");
        return properties != null ? (String) properties.get("nickname") : null;
    }
}
