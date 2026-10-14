package br.com.portal.saudereh.service;

import br.com.portal.saudereh.model.HistoricoSolicitacao;
import br.com.portal.saudereh.repository.HistoricoSolicitacaoRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class HistoricoSolicitacaoService {

    private final HistoricoSolicitacaoRepository historicoRepository;

    public HistoricoSolicitacaoService(
            HistoricoSolicitacaoRepository historicoRepository) {
        this.historicoRepository = historicoRepository;
    }

    @Transactional(readOnly = true)
    public List<HistoricoSolicitacao> listarPorSolicitacao(
            Long solicitacaoId) {

        if (solicitacaoId == null || solicitacaoId <= 0) {
            throw new IllegalArgumentException(
                    "Identificador da solicitação inválido.");
        }

        return historicoRepository
                .findBySolicitacaoIdOrderByDataHoraDesc(solicitacaoId);
    }
}
