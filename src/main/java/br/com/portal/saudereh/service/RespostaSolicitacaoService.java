package br.com.portal.saudereh.service;

import br.com.portal.saudereh.model.RespostaSolicitacao;
import br.com.portal.saudereh.model.Solicitacao;
import br.com.portal.saudereh.model.Usuario;
import br.com.portal.saudereh.repository.RespostaSolicitacaoRepository;
import br.com.portal.saudereh.repository.SolicitacaoRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class RespostaSolicitacaoService {

    private final RespostaSolicitacaoRepository respostaRepository;
    private final SolicitacaoRepository solicitacaoRepository;

    public RespostaSolicitacaoService(
            RespostaSolicitacaoRepository respostaRepository,
            SolicitacaoRepository solicitacaoRepository) {
        this.respostaRepository = respostaRepository;
        this.solicitacaoRepository = solicitacaoRepository;
    }

    @Transactional
    public RespostaSolicitacao registrarResposta(
            Long solicitacaoId,
            Usuario usuarioResponsavel,
            String comentario) {

        if (usuarioResponsavel == null
                || usuarioResponsavel.getId() == null) {
            throw new IllegalArgumentException(
                    "Usuário responsável pela resposta não identificado.");
        }

        String perfil = usuarioResponsavel.getPerfil();

        if (perfil == null
                || !(perfil.trim().equalsIgnoreCase("ADM_PLANO_SAUDE")
                || perfil.trim().equalsIgnoreCase("RH_GESTOR"))) {
            throw new IllegalArgumentException(
                    "Você não tem permissão para responder solicitações.");
        }

        if (comentario == null || comentario.isBlank()) {
            throw new IllegalArgumentException(
                    "O comentário da resposta é obrigatório.");
        }

        Solicitacao solicitacao = solicitacaoRepository
                .findById(solicitacaoId)
                .filter(s -> Boolean.TRUE.equals(s.getAtivo()))
                .orElseThrow(() -> new IllegalArgumentException(
                        "Solicitação não encontrada ou inativa."));

        RespostaSolicitacao resposta = new RespostaSolicitacao();
        resposta.setSolicitacao(solicitacao);
        resposta.setUsuarioResposta(usuarioResponsavel);
        resposta.setComentario(comentario.trim());
        resposta.setDataResposta(LocalDateTime.now());

        return respostaRepository.save(resposta);
    }

    @Transactional(readOnly = true)
    public List<RespostaSolicitacao> listarRespostas(Long solicitacaoId) {

        if (solicitacaoId == null) {
            throw new IllegalArgumentException(
                    "O identificador da solicitação é obrigatório.");
        }

        return respostaRepository
                .findBySolicitacaoIdOrderByDataRespostaDesc(solicitacaoId);
    }

    @Transactional(readOnly = true)
    public RespostaSolicitacao buscarResposta(
            Long respostaId,
            Long solicitacaoId) {

        if (respostaId == null || solicitacaoId == null) {
            throw new IllegalArgumentException(
                    "Os identificadores da resposta e da solicitação são obrigatórios.");
        }

        return respostaRepository
                .findByIdAndSolicitacaoId(respostaId, solicitacaoId)
                .orElseThrow(() -> new IllegalArgumentException(
                        "Resposta não encontrada para esta solicitação."));
    }
}