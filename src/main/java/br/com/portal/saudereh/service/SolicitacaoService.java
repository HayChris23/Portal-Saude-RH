package br.com.portal.saudereh.service;

import br.com.portal.saudereh.model.Categoria;
import br.com.portal.saudereh.model.Solicitacao;
import br.com.portal.saudereh.model.Usuario;
import br.com.portal.saudereh.repository.CategoriaRepository;
import br.com.portal.saudereh.repository.SolicitacaoRepository;
import br.com.portal.saudereh.repository.UsuarioRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class SolicitacaoService {

    private final SolicitacaoRepository solicitacaoRepository;
    private final UsuarioRepository usuarioRepository;
    private final CategoriaRepository categoriaRepository;
    private final AutenticacaoService autenticacaoService;
    

    public SolicitacaoService(
            SolicitacaoRepository solicitacaoRepository,
            UsuarioRepository usuarioRepository,
            CategoriaRepository categoriaRepository, 
            AutenticacaoService autenticacaoService) {

        this.solicitacaoRepository = solicitacaoRepository;
        this.usuarioRepository = usuarioRepository;
        this.categoriaRepository = categoriaRepository;
        this.autenticacaoService = autenticacaoService;
    }

    public Solicitacao criarSolicitacao(
            String categoriaNome,
            String descricao) {

        Usuario usuario = usuarioRepository
                .findByMatricula("HAYANE")
                .orElseGet(() -> {
                    Usuario novoUsuario = new Usuario();

                    novoUsuario.setNome("Hayane");
                    novoUsuario.setMatricula("HAYANE");
                    novoUsuario.setEmail("hayane@findes.org.br");
                    novoUsuario.setSenha(autenticacaoService.criptografarSenha("123456"));
                    novoUsuario.setAtivo(true);

                    return usuarioRepository.save(novoUsuario);
                });

        Categoria categoria = categoriaRepository
                .findAll()
                .stream()
                .filter(c -> c.getNome().equalsIgnoreCase(categoriaNome))
                .findFirst()
                .orElseGet(() -> {
                    Categoria novaCategoria = new Categoria();

                    novaCategoria.setNome(categoriaNome);
                    novaCategoria.setDescricao("Categoria de solicitação");
                    novaCategoria.setAtivo(true);

                    return categoriaRepository.save(novaCategoria);
                });

        String ano = String.valueOf(LocalDateTime.now().getYear());

        long numero = solicitacaoRepository.count() + 1;

        String protocolo = String.format(
                "PSRH-%s-%06d",
                ano,
                numero
        );

        Solicitacao solicitacao = new Solicitacao();

        solicitacao.setProtocolo(protocolo);
        solicitacao.setUsuario(usuario);
        solicitacao.setCategoria(categoria);
        solicitacao.setDescricao(descricao);
        solicitacao.setStatus("Aberta");
        solicitacao.setDataAbertura(LocalDateTime.now());

        return solicitacaoRepository.save(solicitacao);
    }

    public List<Solicitacao> listarSolicitacoes() {
        return solicitacaoRepository.findAll();
    }
}