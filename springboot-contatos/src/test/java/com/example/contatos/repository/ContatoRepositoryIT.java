package com.example.contatos.repository;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.example.contatos.domain.Contato;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;

@DataJpaTest
class ContatoRepositoryIT {

    @Autowired private ContatoRepository repository;

    @Test
    @DisplayName("deve encontrar por e-mail ignorando maiúsculas")
    void deveEncontrarPorEmailIgnorandoMaiusculas() {
        repository.save(new Contato("Ana Souza", "ana@example.com", null));

        assertThat(repository.findByEmailIgnoreCase("ANA@EXAMPLE.COM")).isPresent();
        assertThat(repository.existsByEmailIgnoreCase("ana@example.com")).isTrue();
    }

    @Test
    @DisplayName("deve rejeitar e-mail duplicado por causa da restrição única")
    void deveRejeitarEmailDuplicado() {
        repository.saveAndFlush(new Contato("Ana Souza", "ana@example.com", null));

        assertThatThrownBy(() -> repository.saveAndFlush(new Contato("Outra Ana", "ana@example.com", null)))
                .isInstanceOf(DataIntegrityViolationException.class);
    }

    @Test
    @DisplayName("deve buscar por nome ou e-mail, ignorando maiúsculas")
    void deveBuscarPorNomeOuEmail() {
        repository.save(new Contato("Ana Souza", "ana@example.com", null));
        repository.save(new Contato("Bruno Lima", "bruno@example.com", null));

        Page<Contato> porNome = repository.buscar("ANA", PageRequest.of(0, 10));
        Page<Contato> porEmail = repository.buscar("bruno@", PageRequest.of(0, 10));

        assertThat(porNome.getTotalElements()).isEqualTo(1);
        assertThat(porEmail.getTotalElements()).isEqualTo(1);
    }
}
