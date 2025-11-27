package com.postres.config;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Contact;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.info.License;
import org.springframework.context.annotation.Configuration;

@Configuration
@OpenAPIDefinition(
        info = @Info(
                title = "Postres API",
                version = "v1",
                description = "API para gestión de postres, pedidos, usuarios y repartidores",
                contact = @Contact(
                        name = "Postres API Team"
                ),
                license = @License(name = "Apache 2.0")
        )
)
public class OpenApiConfig {
}
