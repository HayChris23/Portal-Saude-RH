package br.com.portal.saudereh.repository;

import br.com.portal.saudereh.model.HistoricoSolicitacao;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface HistoricoSolicitacaoRepository
        extends JpaRepository<HistoricoSolicitacao, Long> {

    List<HistoricoSolicitacao> findBySolicitacaoIdOrderByDataHoraDesc(Long solicitacaoId);
}