package pe.vichaval.gestionriego.caja.repository;

import java.time.LocalDate;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import pe.vichaval.gestionriego.caja.entity.CierreCaja;

public interface CierreCajaRepository extends JpaRepository<CierreCaja, Long> {
    boolean existsByFecha(LocalDate fecha);

    boolean existsByFechaAndEstado(LocalDate fecha, String estado);

    Optional<CierreCaja> findByFecha(LocalDate fecha);

    List<CierreCaja> findAllByOrderByFechaDesc();

    @Query("""
            select c from CierreCaja c
            where (:estado = '' or c.estado = :estado)
              and (:desde is null or c.fecha >= :desde)
              and (:hasta is null or c.fecha <= :hasta)
            """)
    Page<CierreCaja> buscar(
            @Param("estado") String estado,
            @Param("desde") LocalDate desde,
            @Param("hasta") LocalDate hasta,
            Pageable pageable
    );
}
