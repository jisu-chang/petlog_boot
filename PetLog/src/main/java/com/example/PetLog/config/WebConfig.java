package com.example.PetLog.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
       String basePath = "file:/C:/petlog-uploads/";

        // community 이미지
        registry.addResourceHandler("/communityimg/**")
                .addResourceLocations(basePath + "community/");

        // profile 이미지
        registry.addResourceHandler("/profileimg/**")
                .addResourceLocations(basePath + "profile/");

        // snack 이미지
        registry.addResourceHandler("/snackimg/**")
                .addResourceLocations(basePath + "snack/");

        // pet 이미지
        registry.addResourceHandler("/petimg/**")
                .addResourceLocations(basePath + "petprofile/");

        // Diary 이미지
        registry.addResourceHandler("/diaryimg/**")
                .addResourceLocations(basePath + "diary/");
    }
}
