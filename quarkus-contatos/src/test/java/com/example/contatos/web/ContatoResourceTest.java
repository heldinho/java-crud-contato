package com.example.contatos.web;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;

import com.example.contatos.domain.Contato;
import com.example.contatos.repository.ContatoRepository;
import io.quarkus.narayana.jta.QuarkusTransaction;
import io.quarkus.test.junit.QuarkusTest;
import io.restassured.http.ContentType;
import jakarta.inject.Inject;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

/**
 * As requisições HTTP são processadas em outra thread (e portanto em outra transação),
 * por isso os dados de teste são gravados em transações já confirmadas.
 */
@QuarkusTest
class ContatoResourceTest {

    @Inject ContatoRepository repository;

    @BeforeEach
    void limparBase() {
        QuarkusTransaction.requiringNew()
                .run(
                        () -> {
                            repository.deleteAll();
                        });
    }

    private void salvar(Contato contato) {
        QuarkusTransaction.requiringNew()
                .run(
                        () -> {
                            repository.persist(contato);
                        });
    }

    @Test
    void deveListarContatos() {
        salvar(new Contato("Ana Souza", "ana@example.com", null));

        given()
                .when()
                .get("/api/contatos")
                .then()
                .statusCode(200)
                .body("content", hasSize(1))
                .body("content[0].nome", is("Ana Souza"))
                .body("totalElements", is(1));
    }

    @Test
    void deveCriarContato() {
        given()
                .contentType(ContentType.JSON)
                .body("{\"nome\":\"Ana Souza\",\"email\":\"ana@example.com\",\"telefone\":\"11988880000\"}")
                .when()
                .post("/api/contatos")
                .then()
                .statusCode(201)
                .header("Location", notNullValue())
                .body("id", notNullValue())
                .body("nome", is("Ana Souza"))
                .body("email", is("ana@example.com"));
    }

    @Test
    void deveRejeitarPayloadInvalido() {
        given()
                .contentType(ContentType.JSON)
                .body("{\"nome\":\"A\",\"email\":\"email-invalido\"}")
                .when()
                .post("/api/contatos")
                .then()
                .statusCode(400)
                .body("status", is(400))
                .body("fieldErrors.nome", notNullValue())
                .body("fieldErrors.email", notNullValue());
    }

    @Test
    void deveRetornarConflitoParaEmailDuplicado() {
        salvar(new Contato("Ana Souza", "ana@example.com", null));

        given()
                .contentType(ContentType.JSON)
                .body("{\"nome\":\"Outra Ana\",\"email\":\"ana@example.com\"}")
                .when()
                .post("/api/contatos")
                .then()
                .statusCode(409);
    }

    @Test
    void deveRetornarNotFound() {
        given().when().get("/api/contatos/999").then().statusCode(404).body("status", is(404));
    }

    @Test
    void deveAtualizarContato() {
        Contato contato = new Contato("Ana Souza", "ana@example.com", null);
        salvar(contato);

        given()
                .contentType(ContentType.JSON)
                .body("{\"nome\":\"Ana Atualizada\",\"email\":\"ana@example.com\"}")
                .when()
                .put("/api/contatos/" + contato.getId())
                .then()
                .statusCode(200)
                .body("nome", is("Ana Atualizada"));
    }

    @Test
    void deveRemoverContato() {
        Contato contato = new Contato("Ana Souza", "ana@example.com", null);
        salvar(contato);

        given().when().delete("/api/contatos/" + contato.getId()).then().statusCode(204);

        given().when().get("/api/contatos/" + contato.getId()).then().statusCode(404);
    }

    @Test
    void deveFiltrarPorTermo() {
        salvar(new Contato("Ana Souza", "ana@example.com", null));
        salvar(new Contato("Bruno Lima", "bruno@example.com", null));

        given()
                .queryParam("q", "bruno")
                .when()
                .get("/api/contatos")
                .then()
                .statusCode(200)
                .body("content", hasSize(1))
                .body("content[0].nome", is("Bruno Lima"));
    }

    @Test
    void deveRejeitarPaginacaoInvalida() {
        given().queryParam("size", 0).when().get("/api/contatos").then().statusCode(400);
    }
}
