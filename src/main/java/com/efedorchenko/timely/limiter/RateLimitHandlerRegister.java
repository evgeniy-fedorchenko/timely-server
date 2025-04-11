package com.efedorchenko.timely.limiter;

import io.undertow.Undertow;
import io.undertow.servlet.api.DeploymentManager;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.boot.web.embedded.undertow.HttpHandlerFactory;
import org.springframework.boot.web.embedded.undertow.UndertowBuilderCustomizer;
import org.springframework.boot.web.embedded.undertow.UndertowDeploymentInfoCustomizer;
import org.springframework.boot.web.embedded.undertow.UndertowServletWebServer;
import org.springframework.boot.web.embedded.undertow.UndertowServletWebServerFactory;
import org.springframework.boot.web.embedded.undertow.UndertowWebServer;
import org.springframework.stereotype.Component;
import org.springframework.util.ReflectionUtils;

import java.lang.reflect.Field;
import java.util.List;

@Component
@RateLimiterEnabledOnCondition
public class RateLimitHandlerRegister extends UndertowServletWebServerFactory {

    private final RateLimitHandler rtHandler;

    public RateLimitHandlerRegister(
            ObjectProvider<UndertowDeploymentInfoCustomizer> deploymentInfoCustomizers,
            ObjectProvider<UndertowBuilderCustomizer> builderCustomizers,
            RateLimitHandler rtHandler) {

        this.getDeploymentInfoCustomizers().addAll(deploymentInfoCustomizers.orderedStream().toList());
        this.getBuilderCustomizers().addAll(builderCustomizers.orderedStream().toList());
        this.rtHandler = rtHandler;
    }

    @Override
    @SuppressWarnings("unchecked")
    protected UndertowServletWebServer getUndertowWebServer(
            Undertow.Builder builder, DeploymentManager manager, int port) {

        var webServer = super.getUndertowWebServer(builder, manager, port);
        Field factoriesField = ReflectionUtils.findField(UndertowWebServer.class, "httpHandlerFactories");
        if (factoriesField == null) {
            throw new IllegalStateException("Unable to create undertow web server: no httpHandlerFactories field in UndertowWebServer class");
        }
        ReflectionUtils.makeAccessible(factoriesField);
        var factories = (List<HttpHandlerFactory>) ReflectionUtils.getField(factoriesField, webServer);

        if (factories == null) {
            throw new IllegalStateException("Unable to create undertow web server: HttpHandlerFactories in UndertowWebServer class is null");
        }
        factories.add(next -> {
            rtHandler.setNext(next);
            return rtHandler;
        });

        return new UndertowServletWebServer(builder, factories, getContextPath(), port >= 0);
    }
}
