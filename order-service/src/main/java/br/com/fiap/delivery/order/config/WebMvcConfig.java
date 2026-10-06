package br.com.fiap.delivery.order.config;

import br.com.fiap.delivery.order.limiter.OrderRateLimitInterceptor;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebMvcConfig implements WebMvcConfigurer {

    private final OrderRateLimitInterceptor orderRateLimitInterceptor;

    public WebMvcConfig(OrderRateLimitInterceptor orderRateLimitInterceptor) {
        this.orderRateLimitInterceptor = orderRateLimitInterceptor;
    }

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(orderRateLimitInterceptor)
                .addPathPatterns("/orders");
    }

    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/**")
                .allowedOrigins("*")
                .allowedMethods("*");
    }
}
