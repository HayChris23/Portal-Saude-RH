package br.com.portal.saudereh.repository;

import br.com.portal.saudereh.model.Solicitacao;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface DashboardRepository extends JpaRepository<Solicitacao, Long> {

    // Total de solicitações ativas
    @Query("""
        SELECT COUNT(s)
        FROM Solicitacao s
        WHERE s.ativo = true
    """)
    long contarSolicitacoesAtivas();

    // Quantidade de solicitações por status
    @Query("""
        SELECT s.status, COUNT(s)
        FROM Solicitacao s
        WHERE s.ativo = true
        GROUP BY s.status
        ORDER BY s.status
    """)
    List<Object[]> contarPorStatus();

    // Quantidade de solicitações por categoria
    @Query("""
        SELECT s.categoria.nome, COUNT(s)
        FROM Solicitacao s
        WHERE s.ativo = true
        GROUP BY s.categoria.nome
        ORDER BY COUNT(s) DESC
    """)
    List<Object[]> contarPorCategoria();

    // Quantidade de solicitações por status específico
    @Query("""
        SELECT COUNT(s)
        FROM Solicitacao s
        WHERE s.ativo = true
        AND s.status = :status
    """)
    long contarPorStatusEspecifico(@Param("status") String status);

    // Dados das solicitações concluídas para calcular o tempo de atendimento
    @Query("""
        SELECT s.id, s.dataAbertura, MIN(h.dataHora)
        FROM Solicitacao s, HistoricoSolicitacao h
        WHERE h.solicitacao.id = s.id
        AND LOWER(h.novoStatus) = 'concluída'
        AND h.acao = 'Alteração de status'
        AND s.ativo = true
        GROUP BY s.id, s.dataAbertura
        ORDER BY MIN(h.dataHora) DESC
    """)
    List<Object[]> buscarDadosConclusao();

    // Diagnóstico: lista as conclusões registradas no histórico.
    @Query("""
        SELECT h.solicitacao.id, h.novoStatus,
               h.dataHora, h.solicitacao.status
        FROM HistoricoSolicitacao h
        WHERE LOWER(h.novoStatus) = 'concluída'
        AND h.acao = 'Alteração de status'
        AND h.solicitacao.ativo = true
        ORDER BY h.dataHora DESC
    """)
    List<Object[]> buscarConclusoesParaDiagnostico();

}
