package com.dovakin0007.notes_service.config;

import com.dovakin0007.notes_service.ratelimiter.RateLimitInterceptor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class BucketConfig implements WebMvcConfigurer {
    private final RateLimitInterceptor interceptor;
    private static final Logger log = LoggerFactory.getLogger(BucketConfig.class);

    public BucketConfig(RateLimitInterceptor interceptor) {
        this.interceptor = interceptor;
    }

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        // Intercept the GraphQL endpoint
        registry.addInterceptor(interceptor)
                .addPathPatterns("/api/project-managment");
        log.info("RateLimitInterceptor registered for /api/project-managment");
    }
}
