package com.efedorchenko.timely.configuration;

import org.slf4j.MDC;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.function.Function;

@Configuration
public class MainConfig {

    @Bean
    public ExecutorService executorOfVirtual() {
        return Executors.newThreadPerTaskExecutor(
                srcRunnable -> Thread.ofVirtual().unstarted(mdcDecorator.apply(srcRunnable))
        );
    }

    @Bean
    public static ScheduledExecutorService singleThreadScheduler() {
        return Executors.newScheduledThreadPool(1,
                srcRunnable -> Thread.ofPlatform().unstarted(mdcDecorator.apply(srcRunnable))
        );
    }

    private static final Function<Runnable, Runnable> mdcDecorator = srcRunnable -> {
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
