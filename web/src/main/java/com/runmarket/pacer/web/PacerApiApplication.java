package com.runmarket.pacer.web;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;

@EnableCaching
@SpringBootApplication(scanBasePackages = "com.runmarket.pacer")
public class PacerApiApplication {

    public static void main(String[] args) {
        SpringApplication.run(PacerApiApplication.class, args);
    }
}
