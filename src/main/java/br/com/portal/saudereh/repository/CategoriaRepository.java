package br.com.portal.saudereh.repository;

import br.com.portal.saudereh.model.Categoria;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CategoriaRepository extends JpaRepository<Categoria, Long> {
}
