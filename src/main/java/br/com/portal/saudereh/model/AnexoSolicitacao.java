package br.com.portal.saudereh.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "anexo_solicitacao")
public class AnexoSolicitacao {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "solicitacao_id", nullable = false)
    private Solicitacao solicitacao;

    @Column(name = "nome_original", nullable = false, length = 255)
    private String nomeOriginal;

    @Column(name = "nome_armazenado", nullable = false,
            unique = true, length = 255)
    private String nomeArmazenado;

    @Column(name = "tipo_conteudo", nullable = false, length = 150)
    private String tipoConteudo;

    @Column(name = "tamanho_bytes", nullable = false)
    private Long tamanhoBytes;

    @Column(name = "data_upload", nullable = false)
    private LocalDateTime dataUpload = LocalDateTime.now();

    public AnexoSolicitacao() {
    }

    public Long getId() {
        return id;
    }

    public Solicitacao getSolicitacao() {
        return solicitacao;
    }

    public void setSolicitacao(Solicitacao solicitacao) {
        this.solicitacao = solicitacao;
    }

    public String getNomeOriginal() {
        return nomeOriginal;
    }

    public void setNomeOriginal(String nomeOriginal) {
        this.nomeOriginal = nomeOriginal;
    }

    public String getNomeArmazenado() {
        return nomeArmazenado;
    }

    public void setNomeArmazenado(String nomeArmazenado) {
        this.nomeArmazenado = nomeArmazenado;
    }

    public String getTipoConteudo() {
        return tipoConteudo;
    }

    public void setTipoConteudo(String tipoConteudo) {
        this.tipoConteudo = tipoConteudo;
    }

    public Long getTamanhoBytes() {
        return tamanhoBytes;
    }

    public void setTamanhoBytes(Long tamanhoBytes) {
        this.tamanhoBytes = tamanhoBytes;
    }

    public LocalDateTime getDataUpload() {
        return dataUpload;
    }

    public void setDataUpload(LocalDateTime dataUpload) {
        this.dataUpload = dataUpload;
    }
}