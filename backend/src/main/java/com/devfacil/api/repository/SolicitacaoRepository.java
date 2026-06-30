package com.devfacil.api.repository;

import com.devfacil.api.model.Solicitacao;
import com.devfacil.api.model.StatusSolicitacao;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface SolicitacaoRepository extends JpaRepository<Solicitacao, Long> {
    List<Solicitacao> findByStatus(StatusSolicitacao status);

    List<Solicitacao> findByClienteId(Long clienteId);

    @Query("""
            select s from Solicitacao s
            where s.desenvolvedor.id = :desenvolvedorId
               or (s.status = :status and s.desenvolvedor is null)
            """)
    List<Solicitacao> findDisponiveisOuDoDesenvolvedor(
            @Param("desenvolvedorId") Long desenvolvedorId,
            @Param("status") StatusSolicitacao status
    );
}
