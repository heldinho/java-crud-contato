package com.example.contatos.web;

import com.example.contatos.dto.ContatoRequest;
import com.example.contatos.dto.ContatoResponse;
import com.example.contatos.dto.PaginaResponse;
import com.example.contatos.service.ContatoService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import java.net.URI;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/contatos")
@Validated
public class ContatoController {

    private final ContatoService service;

    public ContatoController(ContatoService service) {
        this.service = service;
    }

    @GetMapping
    public PaginaResponse<ContatoResponse> listar(
            @RequestParam(required = false) String q,
            @RequestParam(defaultValue = "0") @Min(value = 0, message = "page deve ser >= 0") int page,
            @RequestParam(defaultValue = "10") @Min(value = 1, message = "size deve ser >= 1")
                    @Max(value = 100, message = "size deve ser <= 100") int size) {
        return service.listar(q, page, size);
    }

    @GetMapping("/{id}")
    public ContatoResponse buscarPorId(@PathVariable Long id) {
        return service.buscarPorId(id);
    }

    @PostMapping
    public ResponseEntity<ContatoResponse> criar(@Valid @RequestBody ContatoRequest request) {
        ContatoResponse criado = service.criar(request);
        return ResponseEntity.created(URI.create("/api/contatos/" + criado.id())).body(criado);
    }

    @PutMapping("/{id}")
    public ContatoResponse atualizar(@PathVariable Long id, @Valid @RequestBody ContatoRequest request) {
        return service.atualizar(id, request);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> remover(@PathVariable Long id) {
        service.remover(id);
        return ResponseEntity.noContent().build();
    }
}
