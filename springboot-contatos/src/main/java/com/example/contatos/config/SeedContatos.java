package com.example.contatos.config;

import com.example.contatos.domain.Contato;
import com.example.contatos.repository.ContatoRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@ConditionalOnProperty(name = "app.seed.enabled", havingValue = "true")
public class SeedContatos implements CommandLineRunner {

    private static final Logger log = LoggerFactory.getLogger(SeedContatos.class);

    private final ContatoRepository repository;

    public SeedContatos(ContatoRepository repository) {
        this.repository = repository;
    }

    @Override
    @Transactional
    public void run(String... args) {
        if (repository.count() > 0) {
            return;
        }
        repository.save(new Contato("Ana Souza", "ana.souza@example.com", "+55 11 98888-0001"));
        repository.save(new Contato("Bruno Lima", "bruno.lima@example.com", "+55 21 97777-0002"));
        repository.save(new Contato("Carla Menezes", "carla.menezes@example.com", null));
        log.info("dados de exemplo inseridos: {} contatos", repository.count());
    }
}
