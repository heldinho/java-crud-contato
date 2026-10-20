package com.example.contatos.service;

import com.example.contatos.domain.Contato;
import com.example.contatos.dto.ContatoRequest;
import com.example.contatos.dto.ContatoResponse;
import com.example.contatos.dto.PaginaResponse;
import com.example.contatos.error.ContatoNaoEncontradoException;
import com.example.contatos.error.EmailDuplicadoException;
import com.example.contatos.repository.ContatoRepository;
import java.util.Locale;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
public class ContatoService {

    private static final int TAMANHO_MAXIMO_PAGINA = 100;

    private final ContatoRepository repository;

    public ContatoService(ContatoRepository repository) {
        this.repository = repository;
    }

    public PaginaResponse<ContatoResponse> listar(String termo, int page, int size) {
        Pageable pageable = PageRequest.of(page, Math.min(size, TAMANHO_MAXIMO_PAGINA), Sort.by("id"));

        Page<Contato> resultado =
                (termo == null || termo.isBlank())
                        ? repository.findAll(pageable)
                        : repository.buscar(termo.trim(), pageable);

        return new PaginaResponse<>(
                resultado.getContent().stream().map(ContatoResponse::from).toList(),
                resultado.getNumber(),
                resultado.getSize(),
                resultado.getTotalElements(),
                resultado.getTotalPages());
    }

    public ContatoResponse buscarPorId(Long id) {
        return ContatoResponse.from(repository.findById(id).orElseThrow(() -> new ContatoNaoEncontradoException(id)));
    }

    @Transactional
    public ContatoResponse criar(ContatoRequest request) {
        String email = normalizarEmail(request.email());
        garantirEmailLivre(email, null);

        Contato contato = new Contato(request.nome().trim(), email, normalizarTelefone(request.telefone()));
        return ContatoResponse.from(repository.save(contato));
    }

    @Transactional
    public ContatoResponse atualizar(Long id, ContatoRequest request) {
        Contato contato = repository.findById(id).orElseThrow(() -> new ContatoNaoEncontradoException(id));

        String email = normalizarEmail(request.email());
        garantirEmailLivre(email, id);

        contato.setNome(request.nome().trim());
        contato.setEmail(email);
        contato.setTelefone(normalizarTelefone(request.telefone()));

        return ContatoResponse.from(repository.save(contato));
    }

    @Transactional
    public void remover(Long id) {
        Contato contato = repository.findById(id).orElseThrow(() -> new ContatoNaoEncontradoException(id));
        repository.delete(contato);
    }

    private void garantirEmailLivre(String email, Long idIgnorado) {
        boolean emailEmUso =
                repository.findByEmailIgnoreCase(email)
                        .map(existente -> idIgnorado == null || !idIgnorado.equals(existente.getId()))
                        .orElse(false);

        if (emailEmUso) {
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
