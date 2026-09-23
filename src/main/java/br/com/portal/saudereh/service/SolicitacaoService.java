package br.com.portal.saudereh.service;

import br.com.portal.saudereh.model.Solicitacao;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
public class SolicitacaoService {

    private final List<Solicitacao> solicitacoes = new ArrayList<>();

    private int proximoNumero = 1;

    public Solicitacao criarSolicitacao(
            String categoria,
            String descricao) {

        String ano = String.valueOf(LocalDateTime.now().getYear());

        String protocolo = String.format(
                "PSRH-%s-%06d",
                ano,
                proximoNumero
        );

        proximoNumero++;

        Solicitacao solicitacao = new Solicitacao();

        solicitacao.setProtocolo(protocolo);

        solicitacao.setColaborador("Hayane");

        solicitacao.setCategoria(categoria);

        solicitacao.setDescricao(descricao);

        solicitacao.setStatus("Aberta");

        solicitacao.setDataAbertura(LocalDateTime.now());

        solicitacoes.add(solicitacao);

        return solicitacao;
    }

    public List<Solicitacao> listarSolicitacoes() {
        return solicitacoes;
    }
}
