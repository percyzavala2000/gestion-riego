package pe.vichaval.gestionriego.caja.repository;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;
import pe.vichaval.gestionriego.caja.entity.Egreso;

public interface EgresoRepository extends JpaRepository<Egreso, Long> {
    boolean existsByComprobanteIgnoreCase(String c);

    List<Egreso> findAllByOrderByFechaDescIdDesc();

    @Query("select coalesce(sum(e.monto),0) from Egreso e where e.fecha=:f and e.estado='REGISTRADO'")
    BigDecimal totalFecha(@Param("f") LocalDate f);

    @Query("""
            select e from Egreso e
            where (:q = ''
                or lower(e.codigo) like lower(concat('%', :q, '%'))
                or lower(e.concepto) like lower(concat('%', :q, '%'))
                or lower(e.comprobante) like lower(concat('%', :q, '%')))
              and (:estado = '' or e.estado = :estado)
            """)
    Page<Egreso> buscar(
            @Param("q") String q,
            @Param("estado") String estado,
            Pageable pageable
    );
}
