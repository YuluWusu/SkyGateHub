package com.example.skygatehub.common.config;

import com.example.skygatehub.common.security.AuthInterceptor;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * Cấu hình Spring MVC:
 * 1. Gắn AuthInterceptor bảo vệ /admin/** và /profile/**.
 * 2. Ánh xạ tài nguyên tĩnh (static/) và thư mục ảnh upload động (uploads/, /images/rooms/**).
 * 3. Đăng ký view lỗi tĩnh /403, /404, /500.
 */
@Configuration
public class WebMvcConfig implements WebMvcConfigurer {

    private final AuthInterceptor authInterceptor;

    public WebMvcConfig(AuthInterceptor authInterceptor) {
        this.authInterceptor = authInterceptor;
    }

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(authInterceptor)
                .addPathPatterns("/admin/**", "/profile/**")
                .excludePathPatterns("/login", "/css/**", "/js/**", "/images/**", "/error", "/403", "/404", "/500", "/favicon.ico");
    }

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        registry.addResourceHandler("/uploads/**")
                .addResourceLocations("file:uploads/");
        registry.addResourceHandler("/images/rooms/**")
                .addResourceLocations("file:uploads/rooms/", "file:src/main/resources/static/images/rooms/", "classpath:/static/images/rooms/");
        registry.addResourceHandler("/**")
                .addResourceLocations("classpath:/static/");
    }

    @Override
    public void addViewControllers(org.springframework.web.servlet.config.annotation.ViewControllerRegistry registry) {
        registry.addViewController("/403").setViewName("error/403");
        registry.addViewController("/404").setViewName("error/404");
        registry.addViewController("/500").setViewName("error/500");
    }
}
