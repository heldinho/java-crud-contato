package com.example.contatos.dto;

import com.example.contatos.domain.Contato;
import java.time.Instant;

public record ContatoResponse(
        Long id,
        String nome,
        String email,
        String telefone,
        Instant criadoEm,
        Instant atualizadoEm) {

    public static ContatoResponse from(Contato contato) {
        return new ContatoResponse(
                contato.getId(),
                contato.getNome(),
                contato.getEmail(),
                contato.getTelefone(),
                contato.getCriadoEm(),
                contato.getAtualizadoEm());
    }
}
