package br.com.portal.saudereh.service;

import br.com.portal.saudereh.model.Categoria;
import br.com.portal.saudereh.model.HistoricoSolicitacao;
import br.com.portal.saudereh.model.Solicitacao;
import br.com.portal.saudereh.model.Usuario;
import br.com.portal.saudereh.repository.CategoriaRepository;
import br.com.portal.saudereh.repository.HistoricoSolicitacaoRepository;
import br.com.portal.saudereh.repository.SolicitacaoRepository;
import br.com.portal.saudereh.repository.UsuarioRepository;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class SolicitacaoService {

    private final SolicitacaoRepository solicitacaoRepository;
    private final HistoricoSolicitacaoRepository historicoSolicitacaoRepository;
    private final UsuarioRepository usuarioRepository;
    private final CategoriaRepository categoriaRepository;

    public SolicitacaoService(
            SolicitacaoRepository solicitacaoRepository,
            HistoricoSolicitacaoRepository historicoSolicitacaoRepository,
            UsuarioRepository usuarioRepository,
            CategoriaRepository categoriaRepository) {

        this.solicitacaoRepository = solicitacaoRepository;
        this.historicoSolicitacaoRepository = historicoSolicitacaoRepository;
        this.usuarioRepository = usuarioRepository;
        this.categoriaRepository = categoriaRepository;
    }

   
        @Transactional 
        public Solicitacao criarSolicitacao(
        Usuario usuarioAutenticado,
        String categoriaNome,
        String descricao) {

    if (usuarioAutenticado == null
            || usuarioAutenticado.getId() == null) {
        throw new IllegalArgumentException(
                "Usuário autenticado não identificado.");
    }

    Usuario usuario = usuarioRepository
            .findById(usuarioAutenticado.getId())
            .filter(u -> Boolean.TRUE.equals(u.getAtivo()))
            .orElseThrow(() -> new IllegalArgumentException(
                    "Usuário não encontrado ou inativo."));

    if (descricao == null || descricao.isBlank()) {
        throw new IllegalArgumentException(
                "A descrição da solicitação é obrigatória.");
    }

    if (categoriaNome == null || categoriaNome.isBlank()) {
        throw new IllegalArgumentException(
                "Selecione uma categoria válida.");
    }

    Categoria categoria = categoriaRepository
            .findByNomeIgnoreCase(categoriaNome.trim())
            .filter(c -> Boolean.TRUE.equals(c.getAtivo()))
            .orElseThrow(() -> new IllegalArgumentException(
                    "Categoria não encontrada ou inativa."));

    String ano = String.valueOf(LocalDateTime.now().getYear());
    long numero = solicitacaoRepository.count() + 1;

    String protocolo = String.format(
            "PSRH-%s-%06d",
            ano,
            numero
    );

    LocalDateTime agora = LocalDateTime.now();

    Solicitacao solicitacao = new Solicitacao();

    solicitacao.setProtocolo(protocolo);
    solicitacao.setUsuario(usuario);
    solicitacao.setCategoria(categoria);
    solicitacao.setDescricao(descricao.trim());
    solicitacao.setStatus("Aberta");
    solicitacao.setDataAbertura(agora);
    solicitacao.setAtivo(true);

    // Salva a solicitação primeiro para obter seu identificador.
    Solicitacao solicitacaoSalva =
            solicitacaoRepository.save(solicitacao);

    // Registra a abertura no histórico.
    HistoricoSolicitacao historico = new HistoricoSolicitacao();

    historico.setSolicitacao(solicitacaoSalva);
    historico.setUsuario(usuario);
    historico.setDataHora(agora);
    historico.setAcao("Solicitação criada");
    historico.setStatusAnterior(null);
    historico.setNovoStatus("Aberta");
    historico.setObservacao(
            "Solicitação aberta pelo colaborador.");

    historicoSolicitacaoRepository.save(historico);

    return solicitacaoSalva;
}

    public List<Solicitacao> listarSolicitacoes() {
        return solicitacaoRepository.findAll()
                .stream()
                .filter(s -> Boolean.TRUE.equals(s.getAtivo()))
                .toList();
    }

    public List<Solicitacao> listarSolicitacoesDoUsuario(Long usuarioId) {
        validarUsuarioId(usuarioId);

        return solicitacaoRepository.findByUsuarioId(usuarioId)
                .stream()
                .filter(s -> Boolean.TRUE.equals(s.getAtivo()))
                .toList();
    }

    public List<Solicitacao> listarSolicitacoesDoUsuarioPorStatus(
            Long usuarioId,
            String status) {

        validarUsuarioId(usuarioId);

        return listarSolicitacoesDoUsuario(usuarioId)
                .stream()
                .filter(s ->
                        status == null
                        || status.isBlank()
                        || s.getStatus().equalsIgnoreCase(status.trim()))
                .toList();
    }

    public Solicitacao buscarPorId(Long id) {
        return solicitacaoRepository.findById(id)
                .filter(s -> Boolean.TRUE.equals(s.getAtivo()))
                .orElseThrow(() -> new IllegalArgumentException(
                        "Solicitação não encontrada ou inativa."));
    }

    public Solicitacao buscarPorProtocolo(String protocolo) {
        validarProtocolo(protocolo);

        return solicitacaoRepository.findByProtocolo(protocolo.trim())
                .filter(s -> Boolean.TRUE.equals(s.getAtivo()))
                .orElseThrow(() -> new IllegalArgumentException(
                        "Nenhuma solicitação ativa encontrada para esse protocolo."));
    }

    public Solicitacao buscarPorProtocoloDoUsuario(
            String protocolo,
            Long usuarioId) {

        validarProtocolo(protocolo);
        validarUsuarioId(usuarioId);

        return solicitacaoRepository
                .findByProtocoloAndUsuarioId(protocolo.trim(), usuarioId)
                .filter(s -> Boolean.TRUE.equals(s.getAtivo()))
                .orElseThrow(() -> new IllegalArgumentException(
                        "Nenhuma solicitação ativa encontrada para esse protocolo."));
    }

    public List<Solicitacao> listarPorStatus(String status) {
        return listarSolicitacoes()
                .stream()
                .filter(s ->
                        status == null
                        || status.isBlank()
                        || s.getStatus().equalsIgnoreCase(status.trim()))
                .toList();
    }

    /**
     * Pesquisa solicitações usando os filtros informados.
     *
     * Valores enviados ao repositório:
     * - Protocolo vazio: ""
     * - Status vazio: ""
     * - Categoria não selecionada: -1
     * - Usuário sem restrição: -1
     */


        @Transactional(readOnly = true)
        public List<Solicitacao> pesquisarAvancado(
        String protocolo,
        String status,
        Long categoriaId,
        LocalDate dataInicial,
        LocalDate dataFinal,
        Long usuarioId) {

    if (usuarioId != null) {
        validarUsuarioId(usuarioId);
    }

    if (dataInicial != null
            && dataFinal != null
            && dataInicial.isAfter(dataFinal)) {

        throw new IllegalArgumentException(
                "A data inicial não pode ser posterior à data final."
        );
    }

        String protocoloFiltro =
                protocolo == null || protocolo.isBlank()
                        ? ""
                        : protocolo.trim();

        String statusFiltro =
                status == null || status.isBlank()
                        ? ""
                        : status.trim();

        Long categoriaFiltro =
                categoriaId == null ? -1L : categoriaId;

        Long usuarioFiltro =
                usuarioId == null ? -1L : usuarioId;

    LocalDateTime inicio = dataInicial == null
        ? LocalDateTime.of(1900, 1, 1, 0, 0)
        : dataInicial.atStartOfDay();

     LocalDateTime fimExclusivo = dataFinal == null
        ? LocalDateTime.of(9999, 12, 31, 23, 59, 59)
        : dataFinal.plusDays(1).atStartOfDay();

    return solicitacaoRepository.pesquisarAvancado(
            protocoloFiltro,
            statusFiltro,
            categoriaFiltro,
            inicio,
            fimExclusivo,
            usuarioFiltro
    );
}

    @Transactional
    public void excluirLogicamente(
            Long id,
            Usuario usuarioResponsavel) {

        if (usuarioResponsavel == null
                || usuarioResponsavel.getId() == null) {

            throw new IllegalArgumentException(
                    "Usuário responsável não identificado.");
        }

        Solicitacao solicitacao = buscarPorId(id);

        solicitacao.setAtivo(false);

        Solicitacao solicitacaoSalva =
                solicitacaoRepository.save(solicitacao);

        HistoricoSolicitacao historico = new HistoricoSolicitacao();

        historico.setSolicitacao(solicitacaoSalva);
        historico.setUsuario(usuarioResponsavel);
        historico.setDataHora(LocalDateTime.now());
        historico.setAcao("Exclusão lógica");
        historico.setStatusAnterior(solicitacaoSalva.getStatus());
        historico.setNovoStatus(solicitacaoSalva.getStatus());
        historico.setObservacao(
                "Solicitação desativada logicamente. "
                + "O registro foi mantido no banco de dados.");

        historicoSolicitacaoRepository.save(historico);
    }

    @Transactional
    public Solicitacao atualizarStatus(
            Long id,
            String novoStatus,
            Usuario usuarioResponsavel) {

        if (novoStatus == null || novoStatus.isBlank()) {
            throw new IllegalArgumentException(
                    "Informe o status da solicitação.");
        }

        if (usuarioResponsavel == null
                || usuarioResponsavel.getId() == null) {

            throw new IllegalArgumentException(
                    "Usuário responsável não identificado.");
        }

        List<String> statusPermitidos = List.of(
                "Aberta",
                "Em análise",
                "Pendente",
                "Concluída",
                "Cancelada"
        );

        String statusNormalizado = statusPermitidos.stream()
                .filter(s -> s.equalsIgnoreCase(novoStatus.trim()))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException(
                        "Status inválido."));

        Solicitacao solicitacao = buscarPorId(id);

        String statusAnterior = solicitacao.getStatus();

        // Impede a reabertura de solicitações concluídas.
        if ("Concluída".equalsIgnoreCase(statusAnterior)) {
                throw new IllegalArgumentException(
                "Esta solicitação já foi concluída e não pode ser reaberta. "
        + "Se precisar de um novo atendimento, abra outra solicitação."
    );
}

        if (statusNormalizado.equalsIgnoreCase(statusAnterior)) {
        return solicitacao;
}


        solicitacao.setStatus(statusNormalizado);

        Solicitacao solicitacaoSalva =
                solicitacaoRepository.save(solicitacao);

        HistoricoSolicitacao historico = new HistoricoSolicitacao();

        historico.setSolicitacao(solicitacaoSalva);
        historico.setUsuario(usuarioResponsavel);
        historico.setDataHora(LocalDateTime.now());
        historico.setAcao("Alteração de status");
        historico.setStatusAnterior(statusAnterior);
        historico.setNovoStatus(statusNormalizado);
        historico.setObservacao(
                "Status alterado de "
                + statusAnterior
                + " para "
                + statusNormalizado
                + ".");

        historicoSolicitacaoRepository.save(historico);

        return solicitacaoSalva;
    }

    private void validarUsuarioId(Long usuarioId) {
        if (usuarioId == null) {
            throw new IllegalArgumentException(
                    "Usuário não identificado.");
        }
    }

    private void validarProtocolo(String protocolo) {
        if (protocolo == null || protocolo.isBlank()) {
            throw new IllegalArgumentException(
                    "Informe um protocolo para pesquisa.");
        }
    }
}