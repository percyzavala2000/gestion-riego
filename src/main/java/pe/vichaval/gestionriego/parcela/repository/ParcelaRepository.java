package pe.vichaval.gestionriego.parcela.repository;

import java.util.List;

import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import pe.vichaval.gestionriego.parcela.entity.Parcela;

public interface ParcelaRepository
        extends JpaRepository<Parcela, Long> {

    boolean existsByCodigoIgnoreCase(String codigo);

    long countByAgricultorId(Long agricultorId);

    @Override
    @EntityGraph(attributePaths = "agricultor")
    List<Parcela> findAll();
}