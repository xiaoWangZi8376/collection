package com.lin;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;

@SpringBootApplication
public class QuartzApp {

    public static void main(String[] args) {
        ConfigurableApplicationContext run = SpringApplication.run(QuartzApp.class, args);
    }
}
