package pe.vichaval.gestionriego.tesoreria.repository;

import java.math.BigDecimal;
import java.util.List;

import org.springframework.data.jpa.repository.*;
import pe.vichaval.gestionriego.tesoreria.entity.Pago;

public interface PagoRepository extends JpaRepository<Pago, Long> {
    boolean existsByComprobante(String c);

    long countByAgricultorId(Long id);

    @Query("select coalesce(sum(p.monto),0) from Pago p where p.estado='CONFIRMADO'")
    BigDecimal total();

    long countByEstado(String e);

    @Override
    @EntityGraph(attributePaths = "agricultor")
    List<Pago> findAll();
}
