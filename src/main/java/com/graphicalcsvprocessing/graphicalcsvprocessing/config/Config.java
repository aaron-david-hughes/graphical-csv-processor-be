package com.graphicalcsvprocessing.graphicalcsvprocessing.config;

import com.graphicalcsvprocessing.graphicalcsvprocessing.resolvers.RequestParamDeserializingArgumentResolver;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.util.List;

/**
 * controls any alteration or addition to spring boiler plate code
 */
@Configuration
public class Config implements WebMvcConfigurer {

    //allows on startup setting of which urls are allowed to request the API using CORS
    @Value("${app.frontend}")
    String frontendCors;

    //add the custom argument resolver used for deserializing complex objects using the spring framework
    @Override
    public void addArgumentResolvers(List<HandlerMethodArgumentResolver> resolvers) {
        resolvers.add(new RequestParamDeserializingArgumentResolver());
    }

    //the above variable frontendCors as a space separated list
    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/process").allowedOrigins(frontendCors.split(" "));
    }
}
