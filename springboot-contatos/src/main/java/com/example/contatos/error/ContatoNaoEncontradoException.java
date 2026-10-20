package com.example.contatos.error;

public class ContatoNaoEncontradoException extends RuntimeException {

    public ContatoNaoEncontradoException(Long id) {
        super("contato " + id + " não encontrado");
    }
}
