package com.cafe.api;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableAsync;

@EnableAsync
@SpringBootApplication
public class CafeManagementSystemApiApplication {

    public static void main(String[] args) {
        SpringApplication.run(CafeManagementSystemApiApplication.class, args);
    }
}