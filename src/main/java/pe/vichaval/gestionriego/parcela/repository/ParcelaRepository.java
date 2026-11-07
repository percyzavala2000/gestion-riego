package pe.vichaval.gestionriego.parcela.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.*;
import pe.vichaval.gestionriego.parcela.entity.Parcela;

public interface ParcelaRepository extends JpaRepository<Parcela, Long> {
    boolean existsByCodigoIgnoreCase(String c);

    boolean existsByCodigoIgnoreCaseAndIdNot(String c, Long id);

    long countByAgricultorId(Long id);

    @Override
    @EntityGraph(attributePaths = "agricultor")
    List<Parcela> findAll();

    @Override
    @EntityGraph(attributePaths = "agricultor")
    Optional<Parcela> findById(Long id);

    @EntityGraph(attributePaths = "agricultor")
    List<Parcela> findByEstadoOrderByCodigo(String estado);
}
