package com.logistics;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;

/**
 * Entry point for the Logistics and Delivery Tracking System.
 *
 * @SpringBootApplication combines:
 *   @Configuration      – marks this class as a bean definition source
 *   @EnableAutoConfiguration – activates Spring Boot's auto-config magic
 *   @ComponentScan      – scans com.logistics and all sub-packages
 */
@SpringBootApplication
@EnableConfigurationProperties
public class LogisticsApplication {

    public static void main(String[] args) {
        SpringApplication.run(LogisticsApplication.class, args);
    }
}
