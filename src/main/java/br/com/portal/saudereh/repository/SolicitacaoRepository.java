package br.com.portal.saudereh.repository;

import br.com.portal.saudereh.model.Solicitacao;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;
import java.util.Optional;

@Repository
public interface SolicitacaoRepository extends JpaRepository<Solicitacao, Long> {

    Optional<Solicitacao> findByProtocolo(String protocolo);

    List<Solicitacao> findByUsuarioId(Long usuarioId);

    Optional<Solicitacao> findByProtocoloAndUsuarioId(
            String protocolo,
            Long usuarioId);

    List<Solicitacao> findByStatusIgnoreCase(String status);

    List<Solicitacao> findByUsuarioIdAndStatusIgnoreCase(
            Long usuarioId,
            String status);

    @Query("""
        SELECT s.status AS status, COUNT(s) AS quantidade
        FROM Solicitacao s
        GROUP BY s.status
        ORDER BY s.status
        """)
    List<Map<String, Object>> contarSolicitacoesPorStatus();

    @Query("""
        SELECT c.nome AS categoria, COUNT(s) AS quantidade
        FROM Solicitacao s
        JOIN s.categoria c
        GROUP BY c.nome
        ORDER BY c.nome
        """)
    List<Map<String, Object>> contarSolicitacoesPorCategoria();
}