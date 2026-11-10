package pe.vichaval.gestionriego.parcela.repository;

import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.query.Param;

import org.springframework.data.jpa.repository.*;
import pe.vichaval.gestionriego.parcela.entity.Parcela;

public interface ParcelaRepository extends JpaRepository<Parcela, Long> {
    boolean existsByCodigoIgnoreCase(String c);

    boolean existsByCodigoIgnoreCaseAndIdNot(String c, Long id);

    long countByAgricultorId(Long id);

    @Override
    @EntityGraph(attributePaths = "agricultor")
    List<Parcela> findAll();

    @Override
    @EntityGraph(attributePaths = "agricultor")
    Optional<Parcela> findById(Long id);

    @EntityGraph(attributePaths = "agricultor")
    List<Parcela> findByEstadoOrderByCodigo(String estado);

    @EntityGraph(attributePaths = "agricultor")
    @Query("""
            select p from Parcela p
            where (:q = ''
                or lower(p.codigo) like lower(concat('%', :q, '%'))
                or lower(p.nombre) like lower(concat('%', :q, '%'))
                or lower(coalesce(p.cultivo, '')) like lower(concat('%', :q, '%'))
                or lower(concat(p.agricultor.nombres, ' ', p.agricultor.apellidos)) like lower(concat('%', :q, '%')))
              and (:estado = '' or p.estado = :estado)
              and (:sector = '' or p.agricultor.sector = :sector)
            """)
    Page<Parcela> buscar(
            @Param("q") String q,
            @Param("estado") String estado,
            @Param("sector") String sector,
            Pageable pageable
    );

    @EntityGraph(attributePaths = "agricultor")
    @Query("""
            select p from Parcela p
            where (:q='' or lower(p.codigo) like lower(concat('%',:q,'%')) or lower(p.nombre) like lower(concat('%',:q,'%')) or lower(coalesce(p.cultivo,'')) like lower(concat('%',:q,'%')) or lower(concat(p.agricultor.nombres,' ',p.agricultor.apellidos)) like lower(concat('%',:q,'%')))
              and (:estado='' or p.estado=:estado)
              and (:sector='' or lower(p.agricultor.sector)=lower(:sector))
              and (:cultivo='' or lower(coalesce(p.cultivo,'')) like lower(concat('%',:cultivo,'%')))
              and (:agricultorId is null or p.agricultor.id=:agricultorId)
            """)
    Page<Parcela> buscarReporte(@Param("q") String q,@Param("estado") String estado,@Param("sector") String sector,@Param("cultivo") String cultivo,@Param("agricultorId") Long agricultorId,Pageable pageable);
}
