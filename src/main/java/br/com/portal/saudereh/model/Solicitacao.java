package br.com.portal.saudereh.model;

import java.time.LocalDateTime;

public class Solicitacao {

    private String protocolo;
    private String colaborador;
    private String categoria;
    private String descricao;
    private String status;
    private LocalDateTime dataAbertura;

    public Solicitacao() {
    }

    public Solicitacao(
            String protocolo,
            String colaborador,
            String categoria,
            String descricao,
            String status,
            LocalDateTime dataAbertura) {

        this.protocolo = protocolo;
        this.colaborador = colaborador;
        this.categoria = categoria;
        this.descricao = descricao;
        this.status = status;
        this.dataAbertura = dataAbertura;
    }

    public String getProtocolo() {
        return protocolo;
    }

    public void setProtocolo(String protocolo) {
        this.protocolo = protocolo;
    }

    public String getColaborador() {
        return colaborador;
    }

    public void setColaborador(String colaborador) {
        this.colaborador = colaborador;
    }

    public String getCategoria() {
        return categoria;
    }

    public void setCategoria(String categoria) {
        this.categoria = categoria;
    }

    public String getDescricao() {
        return descricao;
    }

    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public LocalDateTime getDataAbertura() {
        return dataAbertura;
    }

    public void setDataAbertura(LocalDateTime dataAbertura) {
        this.dataAbertura = dataAbertura;
    }
}
