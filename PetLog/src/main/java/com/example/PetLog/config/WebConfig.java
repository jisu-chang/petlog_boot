package com.example.PetLog.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        // 커뮤니티 이미지 (community)
        registry.addResourceHandler("/communityimg/**")
                .addResourceLocations("file:" + System.getProperty("user.dir") + "/uploads/community");

        // 프로필 이미지 (profile)
        registry.addResourceHandler("/profileimg/**")
                .addResourceLocations("file:" + System.getProperty("user.dir") + "/uploads/profile/");

        // 간식 이미지
        registry.addResourceHandler("/snackimg/**")
                .addResourceLocations("file:" + System.getProperty("user.dir") + "/uploads/snack/");

        // 펫 이미지
        registry.addResourceHandler("/petimg/**")
                .addResourceLocations("file:" + System.getProperty("user.dir") + "/uploads/petprofile/");

    }
}
