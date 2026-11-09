package pe.vichaval.gestionriego.caja.repository;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

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

    @EntityGraph(attributePaths = {"pago", "pago.agricultor"})
    @Query("""
            select d from Devolucion d
            where (:q = ''
                or lower(d.codigo) like lower(concat('%', :q, '%'))
                or lower(d.pago.codigo) like lower(concat('%', :q, '%'))
                or lower(d.motivo) like lower(concat('%', :q, '%'))
                or lower(concat(d.pago.agricultor.nombres, ' ', d.pago.agricultor.apellidos)) like lower(concat('%', :q, '%')))
              and (:estado = '' or d.estado = :estado)
            """)
    Page<Devolucion> buscar(
            @Param("q") String q,
            @Param("estado") String estado,
            Pageable pageable
    );
}
