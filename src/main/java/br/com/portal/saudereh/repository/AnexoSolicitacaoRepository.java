package br.com.portal.saudereh.repository;

import br.com.portal.saudereh.model.AnexoSolicitacao;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface AnexoSolicitacaoRepository
        extends JpaRepository<AnexoSolicitacao, Long> {

    List<AnexoSolicitacao> findBySolicitacaoIdOrderByDataUploadDesc(
            Long solicitacaoId);

    Optional<AnexoSolicitacao> findByIdAndSolicitacaoId(
            Long anexoId,
            Long solicitacaoId);
}