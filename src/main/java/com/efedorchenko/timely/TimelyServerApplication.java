package com.efedorchenko.timely;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;

@EnableCaching
@SpringBootApplication
public class TimelyServerApplication {

    public static void main(String[] args) {
        SpringApplication.run(TimelyServerApplication.class, args);
    }

}
