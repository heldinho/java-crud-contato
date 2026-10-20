package com.example.contatos.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public record ContatoRequest(
        @NotBlank(message = "nome é obrigatório")
                @Size(min = 3, max = 100, message = "nome deve ter entre 3 e 100 caracteres")
                String nome,
        @NotBlank(message = "email é obrigatório")
                @Email(message = "email inválido")
                @Size(max = 150, message = "email deve ter no máximo 150 caracteres")
                String email,
        @Pattern(
                regexp = "^[0-9+()\\s-]{8,20}$",
                message = "telefone inválido (use apenas dígitos, espaço, +, - e parênteses)")
                String telefone) {
}
