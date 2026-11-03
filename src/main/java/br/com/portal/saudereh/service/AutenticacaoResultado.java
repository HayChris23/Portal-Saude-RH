package br.com.portal.saudereh.service;

import br.com.portal.saudereh.model.Usuario;

public record AutenticacaoResultado(
        Usuario usuario,
        String mensagemErro
) {

    public boolean autenticado() {
        return usuario != null;
    }

    public static AutenticacaoResultado sucesso(Usuario usuario) {
        return new AutenticacaoResultado(usuario, null);
    }

    public static AutenticacaoResultado falha(String mensagem) {
        return new AutenticacaoResultado(null, mensagem);
    }
}
