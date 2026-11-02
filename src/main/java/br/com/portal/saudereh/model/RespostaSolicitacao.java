package br.com.portal.saudereh.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "resposta_solicitacao")
public class RespostaSolicitacao {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "solicitacao_id", nullable = false)
    private Solicitacao solicitacao;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "usuario_resposta_id", nullable = false)
    private Usuario usuarioResposta;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String comentario;

    @Column(name = "data_resposta", nullable = false)
    private LocalDateTime dataResposta = LocalDateTime.now();

    @Column(name = "nome_original_arquivo", length = 255)
    private String nomeOriginalArquivo;

    @Column(name = "nome_armazenado_arquivo", length = 255)
    private String nomeArmazenadoArquivo;

    @Column(name = "tipo_arquivo", length = 150)
    private String tipoArquivo;

    @Column(name = "tamanho_arquivo")
    private Long tamanhoArquivo;

    public RespostaSolicitacao() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Solicitacao getSolicitacao() {
        return solicitacao;
    }

    public void setSolicitacao(Solicitacao solicitacao) {
        this.solicitacao = solicitacao;
    }

    public Usuario getUsuarioResposta() {
        return usuarioResposta;
    }

    public void setUsuarioResposta(Usuario usuarioResposta) {
        this.usuarioResposta = usuarioResposta;
    }

    public String getComentario() {
        return comentario;
    }

    public void setComentario(String comentario) {
        this.comentario = comentario;
    }

    public LocalDateTime getDataResposta() {
        return dataResposta;
    }

    public void setDataResposta(LocalDateTime dataResposta) {
        this.dataResposta = dataResposta;
    }

    public String getNomeOriginalArquivo() {
        return nomeOriginalArquivo;
    }

    public void setNomeOriginalArquivo(String nomeOriginalArquivo) {
        this.nomeOriginalArquivo = nomeOriginalArquivo;
    }

    public String getNomeArmazenadoArquivo() {
        return nomeArmazenadoArquivo;
    }

    public void setNomeArmazenadoArquivo(String nomeArmazenadoArquivo) {
        this.nomeArmazenadoArquivo = nomeArmazenadoArquivo;
    }

    public String getTipoArquivo() {
        return tipoArquivo;
    }

    public void setTipoArquivo(String tipoArquivo) {
        this.tipoArquivo = tipoArquivo;
    }

    public Long getTamanhoArquivo() {
        return tamanhoArquivo;
    }

    public void setTamanhoArquivo(Long tamanhoArquivo) {
        this.tamanhoArquivo = tamanhoArquivo;
    }
}