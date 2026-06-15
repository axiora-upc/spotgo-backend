package com.axiora.spotgo.shared.infrastructure.documentation.openapi.configuration;

import io.swagger.v3.oas.models.ExternalDocumentation;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.servers.Server;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
public class OpenApiConfiguration {
    // Properties
    @Value("${spring.application.name}")
    String applicationName;

    @Value("${documentation.application.description}")
    String applicationDescription;

    @Value("${documentation.application.version}")
    String applicationVersion;

    // Methods

    /**
     * Builds the OpenApi document used by Swagger UI and client generation tools.
     *
     * @return configured OpenApi descriptor
     */
    @Bean
    public OpenAPI spotgoOpenApi() {

        // General configuration
        var openApi = new OpenAPI();
        openApi
                .info(new Info()
                        .title(this.applicationName)
                        .description(this.applicationDescription)
                        .version(this.applicationVersion)
                        .contact(new Contact()
                                .name("SpotGo Support")
                                .email("support@spotgo.com")
                                .url("https://www.spotgo.com/support"))
                        .license(new License()
                                .name("Apache 2.0")
                                .url("https://www.apache.org/licenses/LICENSE-2.0.HTML")))
                .externalDocs(new ExternalDocumentation()
                        .description("SpotGo API Documentation")
                        .url("https://www.spotgo.com/api-docs"));

        // Add server configurations
        openApi.servers(List.of(
                new Server()
                        .url("http://localhost:8080")
                        .description("Local Development Environment"),
                new Server()
                        .url("http://staging-api.spotgo.com")
                        .description("Staging Environment"),
                new Server()
                        .url("http://api.spotgo.com")
                        .description("Production Environment")
        ));

        return openApi;


    }
}
