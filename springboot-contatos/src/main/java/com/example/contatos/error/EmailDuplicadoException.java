package com.example.contatos.error;

public class EmailDuplicadoException extends RuntimeException {

    public EmailDuplicadoException(String email) {
        super("já existe um contato com o email " + email);
    }
}
