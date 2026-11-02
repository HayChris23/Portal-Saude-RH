package br.com.portal.saudereh.repository;

import br.com.portal.saudereh.model.RespostaSolicitacao;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface RespostaSolicitacaoRepository
        extends JpaRepository<RespostaSolicitacao, Long> {

    List<RespostaSolicitacao>
            findBySolicitacaoIdOrderByDataRespostaDesc(Long solicitacaoId);

    Optional<RespostaSolicitacao>
            findByIdAndSolicitacaoId(Long id, Long solicitacaoId);
}