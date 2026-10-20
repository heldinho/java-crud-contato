package com.example.contatos.repository;

import com.example.contatos.domain.Contato;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface ContatoRepository extends JpaRepository<Contato, Long> {

    Optional<Contato> findByEmailIgnoreCase(String email);

    boolean existsByEmailIgnoreCase(String email);

    @Query("""
            select c from Contato c
            where lower(c.nome) like lower(concat('%', :q, '%'))
               or lower(c.email) like lower(concat('%', :q, '%'))
            """)
    Page<Contato> buscar(@Param("q") String termo, Pageable pageable);
}
