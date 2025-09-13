package com.dbfetcher;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@SpringBootApplication(scanBasePackages = {"com.dbfetcher", "commonlibs"})
@EntityScan(basePackages = {"commonlibs.models", "com.dbfetcher.models"})
@EnableJpaRepositories(basePackages = {"com.dbfetcher.repository"})
public class DbFetcherApplication {
    public static void main(String[] args) {
        SpringApplication.run(DbFetcherApplication.class, args);
    }
}