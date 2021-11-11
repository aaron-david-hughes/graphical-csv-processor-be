package com.graphicalcsvprocessing.graphicalcsvprocessing.config;

import com.graphicalcsvprocessing.graphicalcsvprocessing.resolvers.RequestParamDeserializingArgumentResolver;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.util.List;

@Configuration
public class Config implements WebMvcConfigurer {

    @Value("${app.frontend}")
    String frontendCors;

    @Override
    public void addArgumentResolvers(List<HandlerMethodArgumentResolver> resolvers) {
        resolvers.add(new RequestParamDeserializingArgumentResolver());
    }

    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/process").allowedOrigins(frontendCors.split(" "));
    }
}
