package br.com.portal.saudereh.repository;

import br.com.portal.saudereh.model.Solicitacao;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface SolicitacaoRepository extends JpaRepository<Solicitacao, Long> {

    Optional<Solicitacao> findByProtocolo(String protocolo);
}