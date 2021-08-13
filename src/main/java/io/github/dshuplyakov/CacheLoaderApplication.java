package io.github.dshuplyakov;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;

@SpringBootApplication
@EnableConfigurationProperties
public class CacheLoaderApplication {
    public static void main(String[] args) {
        SpringApplication.run(CacheLoaderApplication.class, args);
    }
}