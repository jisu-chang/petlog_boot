package com.example.PetLog.User;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {
    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        // /images/** 요청을 C:/upload/image/ 폴더로 매핑
        registry.addResourceHandler("/images/**")
                .addResourceLocations("file:///C:/upload/image/"); // 실제 이미지 파일 경로
    }
}