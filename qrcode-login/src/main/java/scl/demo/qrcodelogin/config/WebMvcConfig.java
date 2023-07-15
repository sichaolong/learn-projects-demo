package scl.demo.qrcodelogin.config;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import scl.demo.qrcodelogin.controller.interceptor.ConfirmInterceptor;
import scl.demo.qrcodelogin.controller.interceptor.LoginInterceptor;

@Configuration
public class WebMvcConfig implements WebMvcConfigurer {

    @Autowired
    private LoginInterceptor loginInterceptor;

    @Autowired
    private ConfirmInterceptor confirmInterceptor;

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(loginInterceptor)
                .excludePathPatterns("/static/**")
                .addPathPatterns("/getUser", "/login/scan");
        registry.addInterceptor(confirmInterceptor)
                .excludePathPatterns("/static/**")
                .addPathPatterns("/login/confirm");
    }
}
