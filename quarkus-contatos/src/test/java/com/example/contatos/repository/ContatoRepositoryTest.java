package com.example.contatos.repository;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.example.contatos.domain.Contato;
import io.quarkus.narayana.jta.QuarkusTransaction;
import io.quarkus.panache.common.Page;
import io.quarkus.test.junit.QuarkusTest;
import jakarta.inject.Inject;
import jakarta.persistence.PersistenceException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

@QuarkusTest
class ContatoRepositoryTest {

    @Inject ContatoRepository repository;

    @BeforeEach
    void limparBase() {
        QuarkusTransaction.requiringNew()
                .run(
                        () -> {
                            repository.deleteAll();
                        });
    }

    private void salvar(Contato contato) {
        QuarkusTransaction.requiringNew()
                .run(
                        () -> {
                            repository.persist(contato);
                        });
    }

    @Test
    void deveBuscarPorNomeOuEmail() {
        salvar(new Contato("Ana Souza", "ana@example.com", null));
        salvar(new Contato("Bruno Lima", "bruno@example.com", null));

        assertThat(repository.buscarPorNomeOuEmail("ANA").count()).isEqualTo(1);
        assertThat(repository.buscarPorNomeOuEmail("bruno@").count()).isEqualTo(1);
        assertThat(repository.buscarPorNomeOuEmail("qualquer").count()).isZero();
    }

    @Test
    void devePaginarResultados() {
        salvar(new Contato("Ana Souza", "ana@example.com", null));
        salvar(new Contato("Bruno Lima", "bruno@example.com", null));

        var query = repository.findAll().page(Page.of(0, 1));

        assertThat(query.list()).hasSize(1);
        assertThat(query.count()).isEqualTo(2);
        assertThat(query.pageCount()).isEqualTo(2);
        assertThat(query.page().index).isZero();
    }

    @Test
    void deveEncontrarPorEmailIgnorandoMaiusculas() {
        salvar(new Contato("Ana Souza", "ana@example.com", null));

        assertThat(repository.buscarPorEmailIgnoreCase("ANA@EXAMPLE.COM")).isNotNull();
        assertThat(repository.buscarPorEmailIgnoreCase("nao-existe@example.com")).isNull();
    }

    @Test
    void deveRejeitarEmailDuplicado() {
        salvar(new Contato("Ana Souza", "ana@example.com", null));

        assertThatThrownBy(
                        () ->
                                QuarkusTransaction.requiringNew()
                                        .run(
                                                () -> {
                                                    repository.persist(new Contato("Outra Ana", "ana@example.com", null));
                                                    repository.flush();
                                                }))
                .isInstanceOf(PersistenceException.class);
    }
}
