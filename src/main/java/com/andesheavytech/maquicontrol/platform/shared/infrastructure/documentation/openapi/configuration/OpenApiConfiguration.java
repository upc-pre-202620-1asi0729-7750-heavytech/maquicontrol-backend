package com.andesheavytech.maquicontrol.platform.shared.infrastructure.documentation.openapi.configuration;

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

/**
 * Configures the OpenAPI specification exposed by the platform.
 */
@Configuration
public class OpenApiConfiguration {
    @Value("${spring.application.name}")
    String applicationName;

    @Value("${documentation.application.description}")
    String applicationDescription;

    @Value("${documentation.application.version}")
    String applicationVersion;

    @Bean
    public OpenAPI maquiControlPlatformOpenApi() {
        var openApi = new OpenAPI();
        openApi
                .info(new Info()
                        .title(this.applicationName)
                        .description(this.applicationDescription)
                        .version(this.applicationVersion)
                        .contact(new Contact()
                                .name("AndesHeavyTech - MaquiControl Support")
                                .email("andesheavytech@gmail.com")
                                .url("https://github.com/upc-pre-202620-1asi0729-7750-heavytech"))
                        .license(new License()
                                .name("MIT")
                                .url("https://opensource.org/licenses/MIT")))
                .externalDocs(new ExternalDocumentation()
                        .description("MaquiControl Platform Documentation")
                        .url("https://github.com/upc-pre-202620-1asi0729-7750-heavytech/maquicontrol-backend"));

        openApi.servers(List.of(
                new Server()
                        .url("http://localhost:8080")
                        .description("Local Development Environment"),
                new Server()
                        .url("https://maquicontrol-backend.azurewebsites.net")
                        .description("Production Environment")
        ));

        // final String securitySchemeName = "bearerAuth";
        //
        // openApi.addSecurityItem(new SecurityRequirement()
        //                 .addList(securitySchemeName))
        //         .components(new Components()
        //                 .addSecuritySchemes(securitySchemeName,
        //                         new SecurityScheme()
        //                                 .name(securitySchemeName)
        //                                 .type(SecurityScheme.Type.HTTP)
        //                                 .scheme("bearer")
        //                                 .bearerFormat("JWT")
        //                                 .description("JWT Bearer token for API authentication")));

        return openApi;
    }
}
