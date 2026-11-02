package br.com.portal.saudereh.service;

import br.com.portal.saudereh.model.AnexoSolicitacao;
import br.com.portal.saudereh.model.Solicitacao;
import br.com.portal.saudereh.repository.AnexoSolicitacaoRepository;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.UUID;

@Service
public class AnexoSolicitacaoService {

    private static final long TAMANHO_MAXIMO = 5 * 1024 * 1024;

    private final AnexoSolicitacaoRepository anexoRepository;
    private final Path diretorio;

    public AnexoSolicitacaoService(
            AnexoSolicitacaoRepository anexoRepository,
            @Value("${portal.arquivos.diretorio:./arquivos-privados}")
            String diretorio) {

        this.anexoRepository = anexoRepository;

        this.diretorio = Paths.get(diretorio)
                .toAbsolutePath()
                .normalize();
    }

    @Transactional
    public List<AnexoSolicitacao> armazenarArquivos(
            Solicitacao solicitacao,
            MultipartFile[] arquivos) {

        if (solicitacao == null || solicitacao.getId() == null) {
            throw new IllegalArgumentException(
                    "A solicitação precisa estar salva antes dos anexos.");
        }

        if (arquivos == null || arquivos.length == 0) {
            return List.of();
        }

        List<AnexoSolicitacao> anexosSalvos = new ArrayList<>();
        List<Path> arquivosCriados = new ArrayList<>();

        try {
            Files.createDirectories(diretorio);

            for (MultipartFile arquivo : arquivos) {

                if (arquivo == null || arquivo.isEmpty()) {
                    continue;
                }

                if (arquivo.getSize() > TAMANHO_MAXIMO) {
                    throw new IllegalArgumentException(
                            "Cada arquivo deve ter no máximo 5 MB.");
                }

                String nomeOriginal = arquivo.getOriginalFilename();

                if (nomeOriginal == null || nomeOriginal.isBlank()) {
                    throw new IllegalArgumentException(
                            "Um dos arquivos não possui nome válido.");
                }

                nomeOriginal = Paths.get(nomeOriginal)
                        .getFileName()
                        .toString();

                String tipo = detectarTipo(arquivo);

                String nomeArmazenado =
                        UUID.randomUUID().toString()
                                + extensaoPermitida(tipo);

                Path destino = diretorio.resolve(nomeArmazenado)
                        .normalize();

                if (!destino.getParent().equals(diretorio)) {
                    throw new IllegalArgumentException(
                            "Caminho de arquivo inválido.");
                }

                try (InputStream entrada = arquivo.getInputStream()) {
                    Files.copy(
                            entrada,
                            destino,
                            StandardCopyOption.REPLACE_EXISTING);
                }

                arquivosCriados.add(destino);

                AnexoSolicitacao anexo = new AnexoSolicitacao();

                anexo.setSolicitacao(solicitacao);
                anexo.setNomeOriginal(nomeOriginal);
                anexo.setNomeArmazenado(nomeArmazenado);
                anexo.setTipoConteudo(tipo);
                anexo.setTamanhoBytes(arquivo.getSize());

                anexosSalvos.add(anexoRepository.save(anexo));
            }

            return anexosSalvos;

        } catch (IOException | RuntimeException e) {

            for (Path caminho : arquivosCriados) {
                try {
                    Files.deleteIfExists(caminho);
                } catch (IOException ignored) {
                    // Mantém o erro original.
                }
            }

            throw new IllegalArgumentException(
                    "Não foi possível armazenar os anexos: "
                            + e.getMessage(), e);
        }
    }

    @Transactional(readOnly = true)
    public List<AnexoSolicitacao> listarPorSolicitacao(
            Long solicitacaoId) {

        return anexoRepository
                .findBySolicitacaoIdOrderByDataUploadDesc(solicitacaoId);
    }

    @Transactional(readOnly = true)
    public AnexoSolicitacao buscarAnexo(
            Long anexoId,
            Long solicitacaoId) {

        return anexoRepository
                .findByIdAndSolicitacaoId(anexoId, solicitacaoId)
                .orElseThrow(() ->
                        new IllegalArgumentException(
                                "Anexo não encontrado para esta solicitação."));
    }

    @Transactional(readOnly = true)
    public Path localizarArquivo(AnexoSolicitacao anexo)
            throws IOException {

        if (anexo == null || anexo.getNomeArmazenado() == null) {
            throw new IllegalArgumentException("Anexo inválido.");
        }

        String nome = anexo.getNomeArmazenado();

        if (!nome.matches("[a-fA-F0-9-]{36}\\.(pdf|jpg|png)")) {
            throw new IllegalArgumentException(
                    "Nome interno do arquivo inválido.");
        }

        Path caminho = diretorio.resolve(nome).normalize();

        if (!caminho.getParent().equals(diretorio)
                || !Files.isRegularFile(caminho)) {
            throw new IOException("Arquivo não encontrado.");
        }

        return caminho;
    }

    private String detectarTipo(MultipartFile arquivo)
            throws IOException {

        try (InputStream entrada = arquivo.getInputStream()) {

            byte[] assinatura = entrada.readNBytes(8);

            if (assinatura.length >= 5
                    && assinatura[0] == '%'
                    && assinatura[1] == 'P'
                    && assinatura[2] == 'D'
                    && assinatura[3] == 'F'
                    && assinatura[4] == '-') {
                return "application/pdf";
            }

            if (assinatura.length >= 3
                    && (assinatura[0] & 0xFF) == 0xFF
                    && (assinatura[1] & 0xFF) == 0xD8
                    && (assinatura[2] & 0xFF) == 0xFF) {
                return "image/jpeg";
            }

            if (assinatura.length >= 8
                    && (assinatura[0] & 0xFF) == 0x89
                    && assinatura[1] == 'P'
                    && assinatura[2] == 'N'
                    && assinatura[3] == 'G'
                    && (assinatura[4] & 0xFF) == 0x0D
                    && (assinatura[5] & 0xFF) == 0x0A
                    && (assinatura[6] & 0xFF) == 0x1A
                    && (assinatura[7] & 0xFF) == 0x0A) {
                return "image/png";
            }
        }

        throw new IllegalArgumentException(
                "Formato inválido. Envie um PDF, JPG ou PNG válido.");
    }

    private String extensaoPermitida(String tipo) {

        return switch (tipo) {
            case "application/pdf" -> ".pdf";
            case "image/jpeg" -> ".jpg";
            case "image/png" -> ".png";
            default -> throw new IllegalArgumentException(
                    "Tipo de arquivo não permitido.");
        };
    }
}