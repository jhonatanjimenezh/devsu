package com.devsu.ws_account.config;

import org.springdoc.core.GroupedOpenApi;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import io.swagger.v3.oas.models.info.Info;

@Configuration
public class SpringDocConfig {

    @Bean
    public GroupedOpenApi publicApi() {
        return GroupedOpenApi.builder()
                .group("Customer API")
                .packagesToScan("com.devsu.ws_account")
                .addOpenApiCustomiser(openApi -> {
                    openApi.info(new Info()
                            .title("Customer WS")
                            .version("v1")
                            .description("Customer WS es una aplicación Spring Boot "));
                })
                .build();
    }

}
