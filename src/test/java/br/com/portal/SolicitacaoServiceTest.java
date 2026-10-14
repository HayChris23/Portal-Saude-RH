package br.com.portal.saudereh;

import br.com.portal.saudereh.model.Categoria;
import br.com.portal.saudereh.model.HistoricoSolicitacao;
import br.com.portal.saudereh.model.Solicitacao;
import br.com.portal.saudereh.model.Usuario;
import br.com.portal.saudereh.repository.CategoriaRepository;
import br.com.portal.saudereh.repository.HistoricoSolicitacaoRepository;
import br.com.portal.saudereh.repository.SolicitacaoRepository;
import br.com.portal.saudereh.repository.UsuarioRepository;
import br.com.portal.saudereh.service.SolicitacaoService;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class SolicitacaoServiceTest {

    @Mock
    private SolicitacaoRepository solicitacaoRepository;

    @Mock
    private HistoricoSolicitacaoRepository historicoSolicitacaoRepository;

    @Mock
    private UsuarioRepository usuarioRepository;

    @Mock
    private CategoriaRepository categoriaRepository;

    @InjectMocks
    private SolicitacaoService solicitacaoService;

    private Usuario usuario;
    private Categoria categoria;

    @BeforeEach
    void configurar() {
        usuario = new Usuario();
        usuario.setId(1L);
        usuario.setNome("Usuário de Teste");
        usuario.setAtivo(true);

        categoria = new Categoria();
        categoria.setId(1L);
        categoria.setNome("Autorização");
        categoria.setAtivo(true);
    }

    @Test
    void deveCriarSolicitacaoComDadosValidos() {
        when(usuarioRepository.findById(1L))
                .thenReturn(Optional.of(usuario));

        when(categoriaRepository.findByNomeIgnoreCase("Autorização"))
                .thenReturn(Optional.of(categoria));

        when(solicitacaoRepository.count()).thenReturn(0L);

        when(solicitacaoRepository.save(any(Solicitacao.class)))
                .thenAnswer(invocacao -> invocacao.getArgument(0));

        Solicitacao resultado = solicitacaoService.criarSolicitacao(
                usuario,
                "Autorização",
                "  Solicito autorização para procedimento médico.  "
        );

        assertNotNull(resultado);
        assertNotNull(resultado.getProtocolo());
        assertTrue(resultado.getProtocolo().matches("PSRH-\\d{4}-\\d{6}"));
        assertEquals("Aberta", resultado.getStatus());
        assertEquals(
                "Solicito autorização para procedimento médico.",
                resultado.getDescricao()
        );
        assertTrue(resultado.getAtivo());

        verify(solicitacaoRepository).save(any(Solicitacao.class));
    }

    @Test
    void naoDeveCriarSolicitacaoComDescricaoVazia() {
        when(usuarioRepository.findById(1L))
                .thenReturn(Optional.of(usuario));

        assertThrows(
                IllegalArgumentException.class,
                () -> solicitacaoService.criarSolicitacao(
                        usuario,
                        "Autorização",
                        "   "
                )
        );

        verify(solicitacaoRepository, never())
                .save(any(Solicitacao.class));
    }

    @Test
    void naoDeveCriarSolicitacaoComCategoriaInativa() {
        categoria.setAtivo(false);

        when(usuarioRepository.findById(1L))
                .thenReturn(Optional.of(usuario));

        when(categoriaRepository.findByNomeIgnoreCase("Autorização"))
                .thenReturn(Optional.of(categoria));

        assertThrows(
                IllegalArgumentException.class,
                () -> solicitacaoService.criarSolicitacao(
                        usuario,
                        "Autorização",
                        "Solicito atendimento."
                )
        );

        verify(solicitacaoRepository, never())
                .save(any(Solicitacao.class));
    }

    @Test
    void deveAtualizarStatusERegistrarHistorico() {
        Solicitacao solicitacao = new Solicitacao();
        solicitacao.setId(10L);
        solicitacao.setProtocolo("PSRH-2026-000010");
        solicitacao.setUsuario(usuario);
        solicitacao.setCategoria(categoria);
        solicitacao.setDescricao("Solicitação de teste");
        solicitacao.setStatus("Aberta");
        solicitacao.setAtivo(true);

        when(solicitacaoRepository.findById(10L))
                .thenReturn(Optional.of(solicitacao));

        when(solicitacaoRepository.save(any(Solicitacao.class)))
                .thenAnswer(invocacao -> invocacao.getArgument(0));

        Solicitacao resultado = solicitacaoService.atualizarStatus(
                10L,
                "Em análise",
                usuario
        );

        assertEquals("Em análise", resultado.getStatus());

        verify(solicitacaoRepository).save(solicitacao);
        verify(historicoSolicitacaoRepository)
                .save(any(HistoricoSolicitacao.class));
    }

    @Test
    void naoDeveAceitarStatusInvalido() {
        assertThrows(
                IllegalArgumentException.class,
                () -> solicitacaoService.atualizarStatus(
                        10L,
                        "Status inexistente",
                        usuario
                )
        );

        verify(solicitacaoRepository, never())
                .save(any(Solicitacao.class));
    }

    @Test
    void deveExcluirLogicamenteESalvarHistorico() {
        Solicitacao solicitacao = new Solicitacao();
        solicitacao.setId(10L);
        solicitacao.setProtocolo("PSRH-2026-000010");
        solicitacao.setUsuario(usuario);
        solicitacao.setCategoria(categoria);
        solicitacao.setDescricao("Solicitação de teste");
        solicitacao.setStatus("Aberta");
        solicitacao.setAtivo(true);

        when(solicitacaoRepository.findById(10L))
                .thenReturn(Optional.of(solicitacao));

        when(solicitacaoRepository.save(any(Solicitacao.class)))
                .thenAnswer(invocacao -> invocacao.getArgument(0));

        solicitacaoService.excluirLogicamente(10L, usuario);

        assertFalse(solicitacao.getAtivo());

        verify(solicitacaoRepository).save(solicitacao);
        verify(historicoSolicitacaoRepository)
                .save(any(HistoricoSolicitacao.class));
    }
}