package com.example.contatos.config;

import com.example.contatos.domain.Contato;
import com.example.contatos.repository.ContatoRepository;
import io.quarkus.runtime.StartupEvent;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.event.Observes;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import org.jboss.logging.Logger;

/** Popula a base com dados de exemplo quando app.seed.enabled=true. */
@ApplicationScoped
public class SeedContatos {

    private static final Logger log = Logger.getLogger(SeedContatos.class);

    @Inject ContatoRepository repository;
    @Inject AppConfig config;

    @Transactional
    void onStart(@Observes StartupEvent event) {
        if (!config.seed().enabled() || repository.count() > 0) {
            return;
        }
        repository.persist(new Contato("Ana Souza", "ana.souza@example.com", "+55 11 98888-0001"));
        repository.persist(new Contato("Bruno Lima", "bruno.lima@example.com", "+55 21 97777-0002"));
        repository.persist(new Contato("Carla Menezes", "carla.menezes@example.com", null));
        log.infof("dados de exemplo inseridos: %d contatos", repository.count());
    }}
