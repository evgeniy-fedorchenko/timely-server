package com.efedorchenko.timely;

import jakarta.annotation.PostConstruct;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.util.Locale;

@SpringBootApplication
public class TimelyServerApplication {

    public static void main(String[] args) {
        SpringApplication.run(TimelyServerApplication.class, args);
    }

    /** Для сообщений валидации */
    @PostConstruct
    public void init() {
        Locale.setDefault(Locale.ENGLISH);
    }
}
