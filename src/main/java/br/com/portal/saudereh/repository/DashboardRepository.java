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
}
