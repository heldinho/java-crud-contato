package com.example.contatos.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import com.example.contatos.domain.Contato;
import com.example.contatos.dto.ContatoRequest;
import com.example.contatos.dto.ContatoResponse;
import com.example.contatos.error.ContatoNaoEncontradoException;
import com.example.contatos.error.EmailDuplicadoException;
import com.example.contatos.repository.ContatoRepository;
import io.quarkus.test.InjectMock;
import io.quarkus.test.junit.QuarkusTest;
import jakarta.inject.Inject;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

@QuarkusTest
class ContatoServiceTest {

    @InjectMock ContatoRepository repository;

    @Inject ContatoService service;

    @Test
    void deveNormalizarEmailAoCriar() {
        when(repository.buscarPorEmailIgnoreCase("ana.souza@example.com")).thenReturn(null);

        ContatoResponse resposta =
                service.criar(new ContatoRequest("  Ana Souza  ", " Ana.Souza@Example.COM ", null));

        assertThat(resposta.email()).isEqualTo("ana.souza@example.com");
        assertThat(resposta.nome()).isEqualTo("Ana Souza");
    }

    @Test
    void deveRejeitarEmailDuplicado() {
        Contato existente = new Contato("Outra", "ana.souza@example.com", null);
        when(repository.buscarPorEmailIgnoreCase("ana.souza@example.com")).thenReturn(existente);

        assertThatThrownBy(() -> service.criar(new ContatoRequest("Ana Souza", "ana.souza@example.com", null)))
                .isInstanceOf(EmailDuplicadoException.class);
    }

    @Test
    void deveFalharAoBuscarInexistente() {
        when(repository.findByIdOptional(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.buscarPorId(99L))
                .isInstanceOf(ContatoNaoEncontradoException.class);
    }

    @Test
    void deveRemoverContatoExistente() {
        Contato existente = new Contato("Ana Souza", "ana.souza@example.com", null);
        when(repository.findByIdOptional(10L)).thenReturn(Optional.of(existente));

        service.remover(10L);

        Mockito.verify(repository).delete(existente);
        Mockito.verify(repository, Mockito.never()).persist(Mockito.any(Contato.class));
    }
}
