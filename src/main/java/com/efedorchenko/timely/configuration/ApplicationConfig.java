package com.efedorchenko.timely.configuration;

 import com.efedorchenko.timely.configuration.properties.JwtProperties;
import com.efedorchenko.timely.configuration.properties.RateLimiterProperties;
import com.efedorchenko.timely.model.data.UserDataType;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.fasterxml.jackson.module.paramnames.ParameterNamesModule;
import org.slf4j.MDC;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.core.convert.converter.Converter;
import org.springframework.lang.NonNull;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;

import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;
import java.util.function.Function;

@EnableAsync
@EnableCaching
@EnableScheduling
@Configuration
@EnableConfigurationProperties({ JwtProperties.class, RateLimiterProperties.class })
public class ApplicationConfig {

    /*
     * Стандартный экзекутор Spring (виртуальный): vbase-...
     * Пользовательский виртуальный экзекутор: vtly-999
     */
    private static final String VIRTUAL_THREAD_PREFIX = "vtly-";   // virtual-timely-thread

    @Bean
    public ExecutorService executorOfVirtual() {
        ThreadFactory f = new DecoratedThreadFactory(mdcDecorator(), true, VIRTUAL_THREAD_PREFIX);
        return Executors.newThreadPerTaskExecutor(f);
    }

    @Bean
    @Primary
    public ObjectMapper objectMapper() {
        ObjectMapper objectMapper = new ObjectMapper();

        objectMapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);

        objectMapper.registerModule(new ParameterNamesModule(JsonCreator.Mode.PROPERTIES));
        objectMapper.registerModule(new JavaTimeModule());

        SimpleModule serializerModule = new SimpleModule();
        objectMapper.registerModule(serializerModule);

        return objectMapper;
    }

    @Bean
    public Converter<String, UserDataType> userDataTypeConverter() {
        return new Converter<>() {
            @Override
            public UserDataType convert(@NonNull String source) {
                return UserDataType.valueOf(source.toUpperCase());
            }
        };
    }

    private Function<Runnable, Runnable> mdcDecorator() {
        return srcRunnable -> {
            Map<String, String> parentContext = MDC.getCopyOfContextMap();
            return () -> {
                if (parentContext != null) {
                    MDC.setContextMap(parentContext);
                }
                try {
                    srcRunnable.run();
                } finally {
                    MDC.clear();
                }
            };
        };
    }
}
