package pe.vichaval.gestionriego.caja.repository;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;
import pe.vichaval.gestionriego.caja.entity.Devolucion;

public interface DevolucionRepository extends JpaRepository<Devolucion, Long> {
    List<Devolucion> findAllByOrderByFechaDescIdDesc();

    long countByEstado(String estado);

    @Query("select coalesce(sum(d.monto),0) from Devolucion d where d.pago.id=:id and d.estado in ('APROBADA','EJECUTADA')")
    BigDecimal totalPorPago(@Param("id") Long id);

    @Query("select coalesce(sum(d.monto),0) from Devolucion d where d.fecha=:f and d.estado='EJECUTADA'")
    BigDecimal totalFecha(@Param("f") LocalDate f);
}
