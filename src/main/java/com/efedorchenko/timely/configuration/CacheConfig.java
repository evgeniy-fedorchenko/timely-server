package com.efedorchenko.timely.configuration;

import com.efedorchenko.timely.model.auth.RoleType;
import org.springframework.cache.CacheManager;
import org.springframework.cache.concurrent.ConcurrentMapCache;
import org.springframework.cache.support.SimpleCacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

@Configuration
public class CacheConfig {

    public static final String ROLES_CACHE_NAME = "roles";

    @Bean
    public CacheManager cacheManager() {
        SimpleCacheManager cacheManager = new SimpleCacheManager();
        ConcurrentMap<Object, Object> store = new ConcurrentHashMap<>(RoleType.values().length);
        cacheManager.setCaches(List.of(new ConcurrentMapCache(ROLES_CACHE_NAME, store, false)));
        return cacheManager;
    }
}
