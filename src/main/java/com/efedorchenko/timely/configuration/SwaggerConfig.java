package com.efedorchenko.timely.configuration;

import com.efedorchenko.timely.configuration.properties.ApplicationProperties;
import org.springdoc.core.models.GroupedOpenApi;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

@Profile("dev")
@Configuration
public class SwaggerConfig {

    @Bean
    public GroupedOpenApi publicApi(@Value("${app.version}") String applicationVersion,
                                    @Value("${spring.application.name}") String applicationName) {
        return GroupedOpenApi.builder()
                .group(applicationName + " " + applicationVersion)
                .pathsToMatch(ApplicationProperties.BASE_PATH + "/**")
                .build();
    }

    @Bean
    public GroupedOpenApi actuatorApi() {
        return GroupedOpenApi.builder().group("actuator").pathsToMatch("/actuator/**").build();
    }

}
