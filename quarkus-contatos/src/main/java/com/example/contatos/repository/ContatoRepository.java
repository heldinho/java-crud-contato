package com.example.contatos.repository;

import com.example.contatos.domain.Contato;
import io.quarkus.hibernate.orm.panache.PanacheQuery;
import io.quarkus.hibernate.orm.panache.PanacheRepository;
import jakarta.enterprise.context.ApplicationScoped;

/**
 * Repositório no estilo "repository pattern" (equivalente ao Spring Data).
 * As queries HQL ficam aqui, encapsulando o acesso a dados do serviço.
 */
@ApplicationScoped
public class ContatoRepository implements PanacheRepository<Contato> {

    private static final String BUSCA_POR_NOME_OU_EMAIL =
            """
            lower(nome) like lower(concat('%', ?1, '%'))
            or lower(email) like lower(concat('%', ?1, '%'))
            """;

    public Contato buscarPorEmailIgnoreCase(String email) {
        return find("lower(email) = lower(?1)", email).firstResultOptional().orElse(null);
    }

    public PanacheQuery<Contato> buscarPorNomeOuEmail(String termo) {
        return find(BUSCA_POR_NOME_OU_EMAIL, termo);
    }
}
