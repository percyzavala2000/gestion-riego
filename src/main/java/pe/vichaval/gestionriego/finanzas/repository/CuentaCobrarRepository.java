package pe.vichaval.gestionriego.finanzas.repository;

import java.math.BigDecimal;
import java.util.*;

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
}
