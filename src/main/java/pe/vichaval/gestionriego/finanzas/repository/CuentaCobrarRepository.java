package pe.vichaval.gestionriego.finanzas.repository;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.*;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.query.Param;

import org.springframework.data.jpa.repository.*;
import pe.vichaval.gestionriego.finanzas.entity.CuentaCobrar;

public interface CuentaCobrarRepository extends JpaRepository<CuentaCobrar, Long> {
    @Override
    @EntityGraph(attributePaths = {"agricultor", "concepto"})
    List<CuentaCobrar> findAll();

    @Override
    @EntityGraph(attributePaths = {"agricultor", "concepto"})
    Optional<CuentaCobrar> findById(Long id);

    @Query("select coalesce(sum(c.montoTotal-c.montoPagado),0) from CuentaCobrar c where c.estado not in ('PAGADA','ANULADA')")
    BigDecimal totalDeuda();

    @Query("select count(distinct c.agricultor.id) from CuentaCobrar c where c.estado not in ('PAGADA','ANULADA')")
    long agricultoresConDeuda();

    @EntityGraph(attributePaths = {"agricultor", "concepto"})
    @Query("""
            select c from CuentaCobrar c
            where (:q = ''
                or lower(c.codigo) like lower(concat('%', :q, '%'))
                or lower(c.concepto.nombre) like lower(concat('%', :q, '%'))
                or lower(concat(c.agricultor.nombres, ' ', c.agricultor.apellidos)) like lower(concat('%', :q, '%')))
              and (:estado = '' or c.estado = :estado)
              and (
                    :vencimiento = ''
                    or (:vencimiento = 'VENCIDA' and c.fechaVencimiento < :hoy and c.estado not in ('PAGADA','ANULADA'))
                    or (:vencimiento = 'PROXIMA' and c.fechaVencimiento between :hoy and :proxima and c.estado not in ('PAGADA','ANULADA'))
                    or (:vencimiento = 'VIGENTE' and c.fechaVencimiento >= :hoy and c.estado not in ('PAGADA','ANULADA'))
                  )
            """)
    Page<CuentaCobrar> buscar(
            @Param("q") String q,
            @Param("estado") String estado,
            @Param("vencimiento") String vencimiento,
            @Param("hoy") LocalDate hoy,
            @Param("proxima") LocalDate proxima,
            Pageable pageable
    );
}
