package com.efedorchenko.timely.configuration;

import org.slf4j.MDC;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;

import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ThreadFactory;
import java.util.function.Function;

@EnableAsync
@EnableCaching
@EnableScheduling
@Configuration
public class ExecutorConfig {

    private static final String VIRTUAL_THREAD_PREFIX = "vtly-";   // virtual-timely-thread
    private static final String SCHEDULED_PLATFORM_THREAD_PREFIX = "sptly-";   // scheduled-platform-timely-thread

    @Bean
    public ExecutorService executorOfVirtual() {
        ThreadFactory f = new DecoratedThreadFactory(mdcDecorator(), true, VIRTUAL_THREAD_PREFIX);
        return Executors.newThreadPerTaskExecutor(f);
    }

    @Bean
    public ScheduledExecutorService singleThreadScheduler() {
        ThreadFactory f = new DecoratedThreadFactory(mdcDecorator(), false, SCHEDULED_PLATFORM_THREAD_PREFIX);
        return Executors.newScheduledThreadPool(1, f);
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
