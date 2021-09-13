package com.springbootpoc.springbootservices.springboot;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringBootConfiguration;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.data.mongo.MongoDataAutoConfiguration;
import org.springframework.boot.autoconfigure.data.mongo.MongoRepositoriesAutoConfiguration;
import org.springframework.boot.autoconfigure.mongo.MongoAutoConfiguration;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.web.context.support.AnnotationConfigWebApplicationContext;
import org.springframework.web.servlet.DispatcherServlet;

@Import(AppConfig.class)
@SpringBootConfiguration
@EnableAutoConfiguration(exclude = { MongoAutoConfiguration.class, MongoRepositoriesAutoConfiguration.class, MongoDataAutoConfiguration.class })
public class Application {

    private static final Logger LOGGER = LoggerFactory.getLogger(Application.class);

    @Autowired
    ApplicationContext applicationContext;

    @Bean
    public DispatcherServlet dispatcherServlet() {
        LOGGER.info("init bean dispatcherServlet.");
        AnnotationConfigWebApplicationContext context = new AnnotationConfigWebApplicationContext();
        context.register(AppConfig.class);
        return new DispatcherServlet(context);
    }
}
