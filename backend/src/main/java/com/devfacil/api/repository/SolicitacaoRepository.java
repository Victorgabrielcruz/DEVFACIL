package com.devfacil.api.repository;

import com.devfacil.api.model.Solicitacao;
import com.devfacil.api.model.StatusSolicitacao;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface SolicitacaoRepository extends JpaRepository<Solicitacao, Long> {
    List<Solicitacao> findByStatus(StatusSolicitacao status);
}
