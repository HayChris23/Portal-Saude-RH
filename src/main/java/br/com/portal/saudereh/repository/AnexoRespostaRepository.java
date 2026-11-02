package br.com.portal.saudereh.repository;

import br.com.portal.saudereh.model.AnexoResposta;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface AnexoRespostaRepository
        extends JpaRepository<AnexoResposta, Long> {

    List<AnexoResposta> findByRespostaIdOrderByDataUploadDesc(
            Long respostaId);

    Optional<AnexoResposta> findByIdAndRespostaId(
            Long anexoId,
            Long respostaId);
}
