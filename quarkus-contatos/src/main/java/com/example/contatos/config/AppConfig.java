package com.example.contatos.config;

import io.smallrye.config.ConfigMapping;

/** Configuração tipada da aplicação (substitui @Value por um contrato forte). */
@ConfigMapping(prefix = "app")
public interface AppConfig {

    Seed seed();

    interface Seed {
        boolean enabled();
    }
}
