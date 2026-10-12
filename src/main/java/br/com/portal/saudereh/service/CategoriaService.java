
package br.com.portal.saudereh.service;

import br.com.portal.saudereh.model.Categoria;
import br.com.portal.saudereh.repository.CategoriaRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class CategoriaService {

    private final CategoriaRepository categoriaRepository;

    public CategoriaService(CategoriaRepository categoriaRepository) {
        this.categoriaRepository = categoriaRepository;
    }

    public List<Categoria> listarTodas() {
        return categoriaRepository.findAll();
    }

    public Categoria buscarPorId(Long id) {
        return categoriaRepository.findById(id)
                .orElseThrow(() ->
                        new IllegalArgumentException(
                                "Categoria não encontrada."));
    }

    @Transactional
    public Categoria salvar(Categoria categoria) {
        if (categoria.getNome() == null
                || categoria.getNome().isBlank()) {
            throw new IllegalArgumentException(
                    "O nome da categoria é obrigatório.");
        }

        String nome = categoria.getNome().trim();
        categoria.setNome(nome);

        if (categoria.getDescricao() != null) {
            categoria.setDescricao(
                    categoria.getDescricao().trim());
        }

        if (categoria.getId() == null) {
            if (categoriaRepository
                    .findByNomeIgnoreCase(nome).isPresent()) {
                throw new IllegalArgumentException(
                        "Já existe uma categoria com esse nome.");
            }

            categoria.setAtivo(true);
            return categoriaRepository.save(categoria);
        }

        Categoria existente = buscarPorId(categoria.getId());

        boolean nomeDuplicado =
                categoriaRepository
                        .existsByNomeIgnoreCaseAndIdNot(
                                nome, categoria.getId());

        if (nomeDuplicado) {
            throw new IllegalArgumentException(
                    "Já existe outra categoria com esse nome.");
        }

        existente.setNome(nome);
        existente.setDescricao(categoria.getDescricao());

        return categoriaRepository.save(existente);
    }

    @Transactional
    public void desativar(Long id) {
        Categoria categoria = buscarPorId(id);
        categoria.setAtivo(false);
        categoriaRepository.save(categoria);
    }
}
