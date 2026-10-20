package com.example.contatos.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.example.contatos.domain.Contato;
import com.example.contatos.dto.ContatoRequest;
import com.example.contatos.dto.ContatoResponse;
import com.example.contatos.error.ContatoNaoEncontradoException;
import com.example.contatos.error.EmailDuplicadoException;
import com.example.contatos.repository.ContatoRepository;
import java.util.Optional;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

@ExtendWith(MockitoExtension.class)
class ContatoServiceTest {

    @Mock private ContatoRepository repository;

    @InjectMocks private ContatoService service;

    @Test
    @DisplayName("deve normalizar o e-mail (trim + minúsculas) ao criar")
    void deveNormalizarEmailAoCriar() {
        when(repository.findByEmailIgnoreCase("ana.souza@example.com")).thenReturn(Optional.empty());
        when(repository.save(any(Contato.class))).thenAnswer(invocacao -> invocacao.getArgument(0));

        ContatoResponse resposta =
                service.criar(new ContatoRequest("  Ana Souza  ", " Ana.Souza@Example.COM ", null));

        assertThat(resposta.email()).isEqualTo("ana.souza@example.com");
        assertThat(resposta.nome()).isEqualTo("Ana Souza");
    }

    @Test
    @DisplayName("deve rejeitar e-mail já cadastrado")
    void deveRejeitarEmailDuplicado() {
        Contato existente = new Contato("Outro", "ana.souza@example.com", null);
        ReflectionTestUtils.setField(existente, "id", 10L);
        when(repository.findByEmailIgnoreCase("ana.souza@example.com")).thenReturn(Optional.of(existente));

        assertThatThrownBy(() -> service.criar(new ContatoRequest("Ana Souza", "ana.souza@example.com", null)))
                .isInstanceOf(EmailDuplicadoException.class)
                .hasMessageContaining("ana.souza@example.com");
    }

    @Test
    @DisplayName("deve permitir manter o próprio e-mail ao atualizar")
    void devePermitirManterEmailAoAtualizar() {
        Contato existente = new Contato("Ana Souza", "ana.souza@example.com", null);
        ReflectionTestUtils.setField(existente, "id", 10L);
        when(repository.findById(10L)).thenReturn(Optional.of(existente));
        when(repository.findByEmailIgnoreCase("ana.souza@example.com")).thenReturn(Optional.of(existente));
        when(repository.save(any(Contato.class))).thenAnswer(invocacao -> invocacao.getArgument(0));

        ContatoResponse resposta =
                service.atualizar(10L, new ContatoRequest("Ana Souza Alterada", "ana.souza@example.com", null));

        assertThat(resposta.nome()).isEqualTo("Ana Souza Alterada");
    }

    @Test
    @DisplayName("deve falhar ao buscar contato inexistente")
    void deveFalharAoBuscarInexistente() {
        when(repository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.buscarPorId(99L))
                .isInstanceOf(ContatoNaoEncontradoException.class)
                .hasMessageContaining("99");
    }

    @Test
    @DisplayName("deve remover contato existente")
    void deveRemoverContatoExistente() {
        Contato existente = new Contato("Ana Souza", "ana.souza@example.com", null);
        ReflectionTestUtils.setField(existente, "id", 10L);
        when(repository.findById(10L)).thenReturn(Optional.of(existente));

        service.remover(10L);

        verify(repository).delete(existente);
    }

    @Test
    @DisplayName("não deve salvar quando o contato não existe")
    void naoDeveSalvarQuandoContatoNaoExiste() {
        when(repository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.atualizar(99L, new ContatoRequest("X", "x@example.com", null)))
                .isInstanceOf(ContatoNaoEncontradoException.class);

        verify(repository, never()).save(any());
    }
}
