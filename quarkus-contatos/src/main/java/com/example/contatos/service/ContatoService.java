package com.example.contatos.service;

import com.example.contatos.domain.Contato;
import com.example.contatos.dto.ContatoRequest;
import com.example.contatos.dto.ContatoResponse;
import com.example.contatos.dto.PaginaResponse;
import com.example.contatos.error.ContatoNaoEncontradoException;
import com.example.contatos.error.EmailDuplicadoException;
import com.example.contatos.error.RequisicaoInvalidaException;
import com.example.contatos.repository.ContatoRepository;
import io.quarkus.hibernate.orm.panache.PanacheQuery;
import io.quarkus.panache.common.Page;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import java.util.List;
import java.util.Locale;

@ApplicationScoped
public class ContatoService {

    private static final int TAMANHO_MAXIMO_PAGINA = 100;

    @Inject ContatoRepository repository;

    @Transactional
    public PaginaResponse<ContatoResponse> listar(String termo, int page, int size) {
        validarPaginacao(page, size);

        PanacheQuery<Contato> query =
                (termo == null || termo.isBlank())
                        ? repository.findAll()
                        : repository.buscarPorNomeOuEmail(termo.trim());

        query.page(Page.of(page, size));

        List<ContatoResponse> content = query.list().stream().map(ContatoResponse::from).toList();

        return new PaginaResponse<>(
                content, query.page().index, query.page().size, query.count(), query.pageCount());
    }

    @Transactional
    public ContatoResponse buscarPorId(Long id) {
        return ContatoResponse.from(obrigatorio(repository.findByIdOptional(id), id));
    }

    @Transactional
    public ContatoResponse criar(ContatoRequest request) {
        String email = normalizarEmail(request.email());
        garantirEmailLivre(email, null);

        Contato contato = new Contato(request.nome().trim(), email, normalizarTelefone(request.telefone()));
        repository.persist(contato);

        return ContatoResponse.from(contato);
    }

    @Transactional
    public ContatoResponse atualizar(Long id, ContatoRequest request) {
        Contato contato = obrigatorio(repository.findByIdOptional(id), id);

        String email = normalizarEmail(request.email());
        garantirEmailLivre(email, id);

        contato.setNome(request.nome().trim());
        contato.setEmail(email);
        contato.setTelefone(normalizarTelefone(request.telefone()));

        return ContatoResponse.from(contato);
    }

    @Transactional
    public void remover(Long id) {
        repository.delete(obrigatorio(repository.findByIdOptional(id), id));
    }

    private Contato obrigatorio(java.util.Optional<Contato> contato, Long id) {
        return contato.orElseThrow(() -> new ContatoNaoEncontradoException(id));
    }

    private void validarPaginacao(int page, int size) {
        if (page < 0) {
            throw new RequisicaoInvalidaException("page deve ser >= 0");
        }
        if (size < 1 || size > TAMANHO_MAXIMO_PAGINA) {
            throw new RequisicaoInvalidaException("size deve ser entre 1 e " + TAMANHO_MAXIMO_PAGINA);
        }
    }

    private void garantirEmailLivre(String email, Long idIgnorado) {
        Contato existente = repository.buscarPorEmailIgnoreCase(email);

        if (existente != null && (idIgnorado == null || !idIgnorado.equals(existente.getId()))) {
            throw new EmailDuplicadoException(email);
        }
    }

    private String normalizarEmail(String email) {
        return email.trim().toLowerCase(Locale.ROOT);
    }

    private String normalizarTelefone(String telefone) {
        if (telefone == null || telefone.isBlank()) {
            return null;
        }
        return telefone.trim();
    }
}
