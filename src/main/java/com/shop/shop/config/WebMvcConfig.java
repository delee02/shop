package com.shop.shop.config;


import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebMvcConfig implements WebMvcConfigurer {
    @Value("${uploadPath}") //application.properties에 설정한 uploadpath
    String uploadPath;

    //절대경로를 상대경로로 바꾼다
    //uploadPath = "C:/shop" -> images/item/**.
    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry){
        registry.addResourceHandler("/images/**")
                // /images로 시작하는 경우 uploadPath에 설정한 폴더를기준으로 파일을 읽어오도록 설정
                .addResourceLocations(uploadPath); //local에서 root경로 설정
    }
}
