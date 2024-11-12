package com.braidsbeautyByAngie.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.servers.Server;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SwaggerConfig {
    @Value("${swagger.server.url}")
    private String serverUrl;
    @Bean
    public OpenAPI customOpenApi(){
        return new OpenAPI()
                .info(new Info()
                        .title("APIs of the 'Services Service' for AngieBraidsBeauty Microservice")
                        .description("This API provides endpoints for managing services, reservations, schedule, and related data.")
                        .version("v1")
                        .contact(new Contact()
                                .name("PantaX Support")
                                .email("pantajefferson173@gmail.com")
                                .url("https://jefferson-panta.netlify.app/"))
                        .license(new License()
                                .name("Apache 2.0")
                                .url("https://www.apache.org/licenses/LICENSE-2.0.html")))
                .addServersItem(new Server().url(serverUrl));
    }
}
