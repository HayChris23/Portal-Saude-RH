package br.com.portal.saudereh;

import br.com.portal.saudereh.model.Categoria;
import br.com.portal.saudereh.repository.CategoriaRepository;
import br.com.portal.saudereh.service.CategoriaService;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CategoriaServiceTest {

    @Mock
    private CategoriaRepository categoriaRepository;

    @InjectMocks
    private CategoriaService categoriaService;

    private Categoria categoria;

    @BeforeEach
    void configurar() {
        categoria = new Categoria();
        categoria.setId(1L);
        categoria.setNome("Autorização");
        categoria.setDescricao("Solicitações de autorização");
        categoria.setAtivo(true);
    }

    @Test
    void deveListarCategorias() {
        when(categoriaRepository.findAll())
                .thenReturn(List.of(categoria));

        List<Categoria> resultado =
                categoriaService.listarTodas();

        assertEquals(1, resultado.size());
        assertEquals("Autorização", resultado.get(0).getNome());

        verify(categoriaRepository).findAll();
    }

    @Test
    void deveBuscarCategoriaPorId() {
        when(categoriaRepository.findById(1L))
                .thenReturn(Optional.of(categoria));

        Categoria resultado =
                categoriaService.buscarPorId(1L);

        assertNotNull(resultado);
        assertEquals(1L, resultado.getId());
        assertEquals("Autorização", resultado.getNome());
    }

    @Test
    void deveLancarErroQuandoCategoriaNaoExiste() {
        when(categoriaRepository.findById(99L))
                .thenReturn(Optional.empty());

        assertThrows(
                IllegalArgumentException.class,
                () -> categoriaService.buscarPorId(99L)
        );
    }

    @Test
    void deveCadastrarCategoriaValida() {
        Categoria nova = new Categoria();
        nova.setNome("  Renovação de guia  ");
        nova.setDescricao("  Renovação de guias médicas  ");

        when(categoriaRepository.findByNomeIgnoreCase(
                "Renovação de guia"))
                .thenReturn(Optional.empty());

        when(categoriaRepository.save(any(Categoria.class)))
                .thenAnswer(invocacao -> invocacao.getArgument(0));

        Categoria resultado = categoriaService.salvar(nova);

        assertEquals("Renovação de guia", resultado.getNome());
        assertEquals(
                "Renovação de guias médicas",
                resultado.getDescricao()
        );
        assertTrue(resultado.getAtivo());

        verify(categoriaRepository).save(nova);
    }

    @Test
    void naoDeveCadastrarCategoriaSemNome() {
        Categoria nova = new Categoria();
        nova.setNome("   ");

        assertThrows(
                IllegalArgumentException.class,
                () -> categoriaService.salvar(nova)
        );

        verify(categoriaRepository, never())
                .save(any(Categoria.class));
    }

    @Test
    void naoDeveCadastrarCategoriaDuplicada() {
        Categoria nova = new Categoria();
        nova.setNome("Autorização");

        when(categoriaRepository.findByNomeIgnoreCase("Autorização"))
                .thenReturn(Optional.of(categoria));

        assertThrows(
                IllegalArgumentException.class,
                () -> categoriaService.salvar(nova)
        );

        verify(categoriaRepository, never())
                .save(any(Categoria.class));
    }

    @Test
    void deveEditarCategoriaExistente() {
        Categoria alteracao = new Categoria();
        alteracao.setId(1L);
        alteracao.setNome("Autorização atualizada");
        alteracao.setDescricao("Descrição atualizada");

        when(categoriaRepository.findById(1L))
                .thenReturn(Optional.of(categoria));

        when(categoriaRepository.existsByNomeIgnoreCaseAndIdNot(
                "Autorização atualizada", 1L))
                .thenReturn(false);

        when(categoriaRepository.save(any(Categoria.class)))
                .thenAnswer(invocacao -> invocacao.getArgument(0));

        Categoria resultado = categoriaService.salvar(alteracao);

        assertEquals("Autorização atualizada", resultado.getNome());
        assertEquals("Descrição atualizada", resultado.getDescricao());

        verify(categoriaRepository).save(categoria);
    }

    @Test
    void naoDeveEditarCategoriaComNomeDuplicado() {
        Categoria alteracao = new Categoria();
        alteracao.setId(1L);
        alteracao.setNome("Renovação de guia");

        when(categoriaRepository.findById(1L))
                .thenReturn(Optional.of(categoria));

        when(categoriaRepository.existsByNomeIgnoreCaseAndIdNot(
                "Renovação de guia", 1L))
                .thenReturn(true);

        assertThrows(
                IllegalArgumentException.class,
                () -> categoriaService.salvar(alteracao)
        );

        verify(categoriaRepository, never())
                .save(any(Categoria.class));
    }

    @Test
    void deveDesativarCategoriaSemExcluirRegistro() {
        when(categoriaRepository.findById(1L))
                .thenReturn(Optional.of(categoria));

        when(categoriaRepository.save(any(Categoria.class)))
                .thenAnswer(invocacao -> invocacao.getArgument(0));

        categoriaService.desativar(1L);

        assertFalse(categoria.getAtivo());

        verify(categoriaRepository).save(categoria);
    }
}