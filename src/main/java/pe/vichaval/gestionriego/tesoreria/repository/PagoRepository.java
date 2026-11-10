package pe.vichaval.gestionriego.tesoreria.repository;

import java.math.BigDecimal;
import java.util.List;
import java.time.LocalDate;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;
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

    @EntityGraph(attributePaths = {"agricultor", "cuentaCobrar"})
    @Query("""
            select p from Pago p
            where (:q = ''
                or lower(p.codigo) like lower(concat('%', :q, '%'))
                or lower(p.comprobante) like lower(concat('%', :q, '%'))
                or lower(p.concepto) like lower(concat('%', :q, '%'))
                or lower(concat(p.agricultor.nombres, ' ', p.agricultor.apellidos)) like lower(concat('%', :q, '%')))
              and (:estado = '' or p.estado = :estado)
              and (:metodo = '' or p.metodo = :metodo)
            """)
    Page<Pago> buscar(@Param("q") String q,
                      @Param("estado") String estado,
                      @Param("metodo") String metodo,
                      Pageable pageable);

    @EntityGraph(attributePaths = {"agricultor", "cuentaCobrar"})
    @Query("""
            select p from Pago p
            where (:q='' or lower(p.codigo) like lower(concat('%',:q,'%')) or lower(p.comprobante) like lower(concat('%',:q,'%')) or lower(p.concepto) like lower(concat('%',:q,'%')) or lower(concat(p.agricultor.nombres,' ',p.agricultor.apellidos)) like lower(concat('%',:q,'%')))
              and (:estado='' or p.estado=:estado)
              and (:metodo='' or p.metodo=:metodo)
              and (:agricultorId is null or p.agricultor.id=:agricultorId)
              and (:desde is null or p.fecha>=:desde)
              and (:hasta is null or p.fecha<=:hasta)
            """)
    Page<Pago> buscarReporte(@Param("q") String q,@Param("estado") String estado,@Param("metodo") String metodo,@Param("agricultorId") Long agricultorId,@Param("desde") LocalDate desde,@Param("hasta") LocalDate hasta,Pageable pageable);
}
