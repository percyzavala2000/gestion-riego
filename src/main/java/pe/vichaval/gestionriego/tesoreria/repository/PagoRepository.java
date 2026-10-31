package pe.vichaval.gestionriego.tesoreria.repository;

import java.math.BigDecimal;
import java.util.List;

import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import pe.vichaval.gestionriego.tesoreria.entity.Pago;

public interface PagoRepository
        extends JpaRepository<Pago, Long> {

    boolean existsByComprobante(String comprobante);

    @Query("""
        select coalesce(sum(p.monto), 0)
        from Pago p
        """)
    BigDecimal total();

    @Override
    @EntityGraph(attributePaths = "agricultor")
    List<Pago> findAll();
}