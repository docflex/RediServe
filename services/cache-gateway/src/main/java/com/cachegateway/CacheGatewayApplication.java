package com.cachegateway;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication(scanBasePackages = {
        "com.cachegateway",
        "common.cache",
})
public class CacheGatewayApplication {
    public static void main(String[] args) {
        SpringApplication.run(CacheGatewayApplication.class, args);
    }
}