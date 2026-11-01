package pe.vichaval.gestionriego.auditoria.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import pe.vichaval.gestionriego.auditoria.entity.EventoAuditoria;

public interface EventoAuditoriaRepository extends JpaRepository<EventoAuditoria, Long> {
    @Query("""
        select e from EventoAuditoria e
        where (:q = ''
            or lower(e.usuario) like lower(concat('%', :q, '%'))
            or lower(e.ruta) like lower(concat('%', :q, '%'))
            or lower(e.correlacionId) like lower(concat('%', :q, '%')))
        order by e.fechaHora desc
        """)
    Page<EventoAuditoria> buscar(@Param("q") String q, Pageable pageable);
}
