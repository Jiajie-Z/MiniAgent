package com.jagent.web;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication(scanBasePackages = "com.jagent")
public class MiniAgentApplication {
    public static void main(String[] args) {
        SpringApplication.run(MiniAgentApplication.class, args);
    }
}
