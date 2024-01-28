package com.ll.mb.global.webMvc;

import com.ll.mb.global.app.AppConfig;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebMvcConfig implements WebMvcConfigurer {

    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/api/**")
                .allowedOrigins("https://cdpn.io", "http://localhost:5173")
                .allowedMethods("*")
                .allowedHeaders("*")
                .allowCredentials(true);
    }


    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        // gen으로 시작하는 폴더랑 temp/mb폴더를 연결해준다. 그러면 사용자 이미지가 잘 나온다.
        registry.addResourceHandler("/gen/**")
                .addResourceLocations("file:///" + AppConfig.getGenFileDirPath() + "/");
    }
}
