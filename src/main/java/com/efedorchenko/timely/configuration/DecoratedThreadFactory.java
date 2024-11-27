package com.efedorchenko.timely.configuration;

import lombok.AllArgsConstructor;
import org.springframework.lang.NonNull;
import org.springframework.lang.Nullable;

import java.util.concurrent.ThreadFactory;
import java.util.function.Function;

@AllArgsConstructor
class DecoratedThreadFactory implements ThreadFactory {

    @Nullable
    private final Function<Runnable, Runnable> decorator;

    private final boolean isVirtual;

    private final String prefix;

    @Override
    public Thread newThread(@NonNull Runnable srcRunnable) {
        Thread.Builder threadBuilder = isVirtual ? Thread.ofVirtual() : Thread.ofPlatform();

        Thread thread = decorator != null
                ? threadBuilder.unstarted(decorator.apply(srcRunnable))
                : threadBuilder.unstarted(srcRunnable);

        thread.setName(prefix + thread.threadId());
        return thread;
    }
}
