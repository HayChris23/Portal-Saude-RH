package br.com.portal.saudereh.repository;

import br.com.portal.saudereh.model.Solicitacao;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Repository
public interface SolicitacaoRepository
        extends JpaRepository<Solicitacao, Long> {

    Optional<Solicitacao> findByProtocolo(String protocolo);

    List<Solicitacao> findByUsuarioId(Long usuarioId);

    Optional<Solicitacao> findByProtocoloAndUsuarioId(
            String protocolo,
            Long usuarioId
    );

    List<Solicitacao> findByStatusIgnoreCase(String status);

    List<Solicitacao> findByUsuarioIdAndStatusIgnoreCase(
            Long usuarioId,
            String status
    );

    @Query("""
        SELECT s
        FROM Solicitacao s
        WHERE s.ativo = true
          AND (
              :protocolo = ''
              OR LOWER(s.protocolo)
                 LIKE CONCAT('%', LOWER(:protocolo), '%')
          )
          AND (
              :status = ''
              OR LOWER(s.status) = LOWER(:status)
          )
          AND (
              :categoriaId = -1
              OR s.categoria.id = :categoriaId
          )
          AND s.dataAbertura >= :dataInicial
          AND s.dataAbertura < :dataFinal
          AND (
              :usuarioId = -1
              OR s.usuario.id = :usuarioId
          )
        ORDER BY s.dataAbertura DESC
        """)
    List<Solicitacao> pesquisarAvancado(
            @Param("protocolo") String protocolo,
            @Param("status") String status,
            @Param("categoriaId") Long categoriaId,
            @Param("dataInicial") LocalDateTime dataInicial,
            @Param("dataFinal") LocalDateTime dataFinal,
            @Param("usuarioId") Long usuarioId
    );

    @Query("""
        SELECT s.status AS status, COUNT(s) AS quantidade
        FROM Solicitacao s
        WHERE s.ativo = true
        GROUP BY s.status
        ORDER BY s.status
        """)
    List<Map<String, Object>> contarSolicitacoesPorStatus();

    @Query("""
        SELECT c.nome AS categoria, COUNT(s) AS quantidade
        FROM Solicitacao s
        JOIN s.categoria c
        WHERE s.ativo = true
        GROUP BY c.nome
        ORDER BY c.nome
        """)
    List<Map<String, Object>> contarSolicitacoesPorCategoria();
}