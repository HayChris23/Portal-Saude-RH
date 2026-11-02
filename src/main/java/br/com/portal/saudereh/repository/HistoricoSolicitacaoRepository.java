package br.com.portal.saudereh.repository;

import br.com.portal.saudereh.model.HistoricoSolicitacao;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface HistoricoSolicitacaoRepository
        extends JpaRepository<HistoricoSolicitacao, Long> {

    List<HistoricoSolicitacao>
            findBySolicitacaoIdOrderByDataHoraDesc(Long solicitacaoId);

    @Query("""
        SELECT h
        FROM HistoricoSolicitacao h
        WHERE LOWER(h.novoStatus) = LOWER(:status)
        AND h.acao = 'Alteração de status'
        ORDER BY h.dataHora DESC
    """)
    List<HistoricoSolicitacao> buscarPorNovoStatus(
            @Param("status") String status);

    @Query("""
        SELECT h
        FROM HistoricoSolicitacao h
        WHERE LOWER(h.novoStatus) = 'concluída'
        AND h.acao = 'Alteração de status'
        ORDER BY h.dataHora DESC
    """)
    List<HistoricoSolicitacao> buscarConclusoes();
}
