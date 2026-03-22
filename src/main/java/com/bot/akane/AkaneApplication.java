package com.bot.akane;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;

@SpringBootApplication
@EnableCaching
public class AkaneApplication {

    public static void main(String[] args) {
        SpringApplication.run(AkaneApplication.class, args);
    }

}
