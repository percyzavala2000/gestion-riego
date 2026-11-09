package pe.vichaval.gestionriego.agricultor.repository;

import org.springframework.data.domain.*;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;
import pe.vichaval.gestionriego.agricultor.entity.Agricultor;

public interface AgricultorRepository extends JpaRepository<Agricultor, Long> {
    @Query("select a from Agricultor a where (:q='' or lower(concat(a.nombres,' ',a.apellidos)) like lower(concat('%',:q,'%')) or a.dni like concat('%',:q,'%')) and (:estado='' or a.estado=:estado)")
    Page<Agricultor> buscar(@Param("q") String q, @Param("estado") String estado, Pageable p);

    boolean existsByDni(String d);

    boolean existsByDniAndIdNot(String d, Long id);

    long countByEstado(String e);
}
