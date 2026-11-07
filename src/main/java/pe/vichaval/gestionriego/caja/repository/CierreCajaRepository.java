package pe.vichaval.gestionriego.caja.repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import pe.vichaval.gestionriego.caja.entity.CierreCaja;

public interface CierreCajaRepository extends JpaRepository<CierreCaja, Long> {
    boolean existsByFecha(LocalDate fecha);

    boolean existsByFechaAndEstado(LocalDate fecha, String estado);

    Optional<CierreCaja> findByFecha(LocalDate fecha);

    List<CierreCaja> findAllByOrderByFechaDesc();
}
