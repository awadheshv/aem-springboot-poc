package com.springbootpoc.springbootservices.springboot;

import com.springbootpoc.springbootservices.springboot.controller.ControllerConfig;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

@Configuration
@Import(ControllerConfig.class)
public class AppConfig {
}
