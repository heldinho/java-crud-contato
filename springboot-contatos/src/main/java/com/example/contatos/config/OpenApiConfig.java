package com.example.contatos.config;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Contact;
import io.swagger.v3.oas.annotations.info.Info;
import org.springframework.context.annotation.Configuration;

@Configuration
@OpenAPIDefinition(
        info =
                @Info(
                        title = "API de Contatos",
                        version = "1.0.0",
                        description = "CRUD de cadastro de contatos - versão Spring Boot",
                        contact = @Contact(name = "Equipe de Back-end")))
public class OpenApiConfig {
}
