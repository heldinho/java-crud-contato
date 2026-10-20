package com.example.contatos;

import jakarta.inject.Singleton;
import org.eclipse.microprofile.openapi.annotations.OpenAPIDefinition;
import org.eclipse.microprofile.openapi.annotations.info.Contact;
import org.eclipse.microprofile.openapi.annotations.info.Info;

@Singleton
@OpenAPIDefinition(
        info =
                @Info(
                        title = "API de Contatos",
                        version = "1.0.0",
                        description = "CRUD de cadastro de contatos - versão Quarkus",
                        contact = @Contact(name = "Equipe de Back-end")))
public class ContatosApplication {
}
