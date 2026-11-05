package br.com.portal.saudereh.service;

import br.com.portal.saudereh.model.RespostaSolicitacao;
import br.com.portal.saudereh.model.Solicitacao;
import br.com.portal.saudereh.model.Usuario;
import br.com.portal.saudereh.repository.RespostaSolicitacaoRepository;
import br.com.portal.saudereh.repository.SolicitacaoRepository;
import br.com.portal.saudereh.model.HistoricoSolicitacao;
import br.com.portal.saudereh.repository.HistoricoSolicitacaoRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class RespostaSolicitacaoService {

    private final RespostaSolicitacaoRepository respostaRepository;
    private final SolicitacaoRepository solicitacaoRepository;
    private final AnexoRespostaService anexoRespostaService;
    private final HistoricoSolicitacaoRepository historicoRepository;

    public RespostaSolicitacaoService(
            RespostaSolicitacaoRepository respostaRepository,
            SolicitacaoRepository solicitacaoRepository,
            AnexoRespostaService anexoRespostaService,
            HistoricoSolicitacaoRepository historicoRepository) {

        this.respostaRepository = respostaRepository;
        this.solicitacaoRepository = solicitacaoRepository;
        this.anexoRespostaService = anexoRespostaService;
        this.historicoRepository = historicoRepository;
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
        || perfil.trim().equalsIgnoreCase("RH_GESTOR")
        || perfil.trim().equalsIgnoreCase("COLABORADOR"))) {

        throw new IllegalArgumentException(
            "Você não tem permissão para responder solicitações.");
}

        if (comentario == null || comentario.isBlank()) {
            throw new IllegalArgumentException(
                    "O comentário da resposta é obrigatório.");
        }

// Valida se o colaborador está respondendo à própria solicitação.
        Solicitacao solicitacao = solicitacaoRepository
                .findById(solicitacaoId)
                .filter(s -> Boolean.TRUE.equals(s.getAtivo()))
                .orElseThrow(() -> new IllegalArgumentException(
                        "Solicitação não encontrada ou inativa."));

// Valida se o colaborador está respondendo à própria solicitação.
        if (perfil.trim().equalsIgnoreCase("COLABORADOR")) {

        if (solicitacao.getUsuario() == null
            || !solicitacao.getUsuario().getId()
                    .equals(usuarioResponsavel.getId())) {

        throw new IllegalArgumentException(
                "Você só pode responder às suas próprias solicitações.");
        }
}

// O colaborador só pode responder enquanto a solicitação estiver pendente.
        if (perfil.trim().equalsIgnoreCase("COLABORADOR")
        && !"Pendente".equalsIgnoreCase(solicitacao.getStatus())) {

        throw new IllegalArgumentException(
            "Você só pode responder quando a solicitação estiver pendente.");
        }

        RespostaSolicitacao resposta = new RespostaSolicitacao();
        resposta.setSolicitacao(solicitacao);
        resposta.setUsuarioResposta(usuarioResponsavel);
        resposta.setComentario(comentario.trim());
        resposta.setDataResposta(LocalDateTime.now());

        RespostaSolicitacao respostaSalva =
        respostaRepository.save(resposta);

        // Registra automaticamente a resposta nas movimentações.
                HistoricoSolicitacao historico = new HistoricoSolicitacao();

        historico.setSolicitacao(solicitacao);
        historico.setUsuario(usuarioResponsavel);
        historico.setDataHora(LocalDateTime.now());
        historico.setAcao("Resposta registrada");
        historico.setStatusAnterior(null);
        historico.setNovoStatus(null);
        historico.setObservacao(
                "Uma resposta foi registrada no atendimento."
        );

        historicoRepository.save(historico);

        return respostaSalva;
    }

@Transactional
public RespostaSolicitacao registrarRespostaComAnexos(
        Long solicitacaoId,
        Usuario usuarioResponsavel,
        String comentario,
        MultipartFile[] arquivos) {

    RespostaSolicitacao resposta = registrarResposta(
            solicitacaoId,
            usuarioResponsavel,
            comentario
    );

    if (arquivos != null && arquivos.length > 0) {
        anexoRespostaService.armazenarArquivos(
                resposta,
                arquivos
        );
    }

    return resposta;
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