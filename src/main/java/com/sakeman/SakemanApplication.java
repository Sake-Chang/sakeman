package com.sakeman;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;

@SpringBootApplication
@EnableCaching
public class SakemanApplication {
    public static void main(String[] args) {
        SpringApplication.run(SakemanApplication.class, args);
    }
}
