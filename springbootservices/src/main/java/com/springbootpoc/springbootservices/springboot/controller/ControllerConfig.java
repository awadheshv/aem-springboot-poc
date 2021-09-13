package com.springbootpoc.springbootservices.springboot.controller;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ControllerConfig {

    @Bean
    DefaultController defaultController() {
        return new DefaultController();
    }

}
