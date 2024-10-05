package com.devsu.ws_customer.config;

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
                .packagesToScan("com.devsu.ws_customer")
                .addOpenApiCustomiser(openApi -> {
                    openApi.info(new Info()
                            .title("WS-Customer Microservices Project")
                            .version("v1")
                            .description("El proyecto WS-Customer es un sistema de microservicios desarrollado con Spring Boot 3.3.1 y Java 17, que implementa una arquitectura hexagonal (clean architecture) para separar la lógica del dominio y asegurar la mantenibilidad y escalabilidad. El proyecto está diseñado para gestionar clientes, cuentas y movimientos bancarios, y se comunica de manera asíncrona a través de RabbitMQ. La base de datos utilizada es PostgreSQL, y todo el sistema está preparado para ser ejecutado en contenedores Docker."));
                })
                .build();
    }

}
