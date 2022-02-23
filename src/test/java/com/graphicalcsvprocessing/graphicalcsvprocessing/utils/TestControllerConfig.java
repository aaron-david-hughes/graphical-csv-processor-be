package com.graphicalcsvprocessing.graphicalcsvprocessing.utils;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.multipart.MultipartResolver;
import org.springframework.web.multipart.commons.CommonsMultipartResolver;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurationSupport;

@Configuration
@ComponentScan({ "com.graphicalcsvprocessing.graphicalcsvprocessing.controller" })
@EnableWebMvc
public class TestControllerConfig extends WebMvcConfigurationSupport {

    @Bean
    public MultipartResolver multipartResolver() {
        return new CommonsMultipartResolver();
    }
}

