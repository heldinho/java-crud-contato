package com.example.contatos.web;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.notNullValue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.example.contatos.domain.Contato;
import com.example.contatos.repository.ContatoRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.Map;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

@SpringBootTest
@AutoConfigureMockMvc
class ContatoControllerIT {

    @Autowired private MockMvc mockMvc;
    @Autowired private ObjectMapper objectMapper;
    @Autowired private ContatoRepository repository;

    @BeforeEach
    void limparBase() {
        repository.deleteAll();
    }

    @Test
    @DisplayName("GET /api/contatos deve retornar lista paginada vazia")
    void deveListarVazio() throws Exception {
        mockMvc.perform(get("/api/contatos"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content", hasSize(0)))
                .andExpect(jsonPath("$.totalElements").value(0))
                .andExpect(jsonPath("$.page").value(0));
    }

    @Test
    @DisplayName("POST /api/contatos deve criar e retornar 201 com Location")
    void deveCriarContato() throws Exception {
        String payload =
                objectMapper.writeValueAsString(
                        Map.of("nome", "Ana Souza", "email", "ana@example.com", "telefone", "+55 11 98888-0001"));

        mockMvc.perform(post("/api/contatos").contentType(MediaType.APPLICATION_JSON).content(payload))
                .andExpect(status().isCreated())
                .andExpect(header().string("Location", notNullValue()))
                .andExpect(jsonPath("$.id", notNullValue()))
                .andExpect(jsonPath("$.nome").value("Ana Souza"))
                .andExpect(jsonPath("$.email").value("ana@example.com"))
                .andExpect(jsonPath("$.criadoEm", notNullValue()));
    }

    @Test
    @DisplayName("POST /api/contatos deve retornar 400 com erros de campo")
    void deveRetornarErroDeValidacao() throws Exception {
        String payload = objectMapper.writeValueAsString(Map.of("nome", "A", "email", "email-invalido"));

        mockMvc.perform(post("/api/contatos").contentType(MediaType.APPLICATION_JSON).content(payload))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.fieldErrors.nome").exists())
                .andExpect(jsonPath("$.fieldErrors.email").exists());
    }

    @Test
    @DisplayName("POST /api/contatos deve retornar 409 para e-mail duplicado")
    void deveRetornarConflitoParaEmailDuplicado() throws Exception {
        repository.save(new Contato("Ana Souza", "ana@example.com", null));

        String payload =
                objectMapper.writeValueAsString(Map.of("nome", "Outra Ana", "email", "ana@example.com"));

        mockMvc.perform(post("/api/contatos").contentType(MediaType.APPLICATION_JSON).content(payload))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.message", notNullValue()));
    }

    @Test
    @DisplayName("GET /api/contatos/{id} deve retornar 404 quando não existe")
    void deveRetornarNotFound() throws Exception {
        mockMvc.perform(get("/api/contatos/999"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status").value(404));
    }

    @Test
    @DisplayName("PUT /api/contatos/{id} deve atualizar o contato")
    void deveAtualizarContato() throws Exception {
        Contato salvo = repository.save(new Contato("Ana Souza", "ana@example.com", null));

        String payload =
                objectMapper.writeValueAsString(
                        Map.of("nome", "Ana Souza Atualizada", "email", "ana@example.com", "telefone", "11988880001"));

        mockMvc.perform(put("/api/contatos/" + salvo.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(payload))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.nome").value("Ana Souza Atualizada"))
                .andExpect(jsonPath("$.telefone").value("11988880001"));
    }

    @Test
    @DisplayName("DELETE /api/contatos/{id} deve remover e retornar 204")
    void deveRemoverContato() throws Exception {
        Contato salvo = repository.save(new Contato("Ana Souza", "ana@example.com", null));

        mockMvc.perform(delete("/api/contatos/" + salvo.getId())).andExpect(status().isNoContent());

        mockMvc.perform(get("/api/contatos/" + salvo.getId())).andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("GET /api/contatos?q= deve filtrar por nome ou e-mail")
    void deveFiltrarContatos() throws Exception {
        repository.save(new Contato("Ana Souza", "ana@example.com", null));
        repository.save(new Contato("Bruno Lima", "bruno@example.com", null));

        mockMvc.perform(get("/api/contatos").param("q", "bruno"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content", hasSize(1)))
                .andExpect(jsonPath("$.content[0].nome").value("Bruno Lima"));
    }
}
