package pe.vichaval.gestionriego.auditoria.repository;

import java.time.OffsetDateTime;
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
                or lower(e.correlacionId) like lower(concat('%', :q, '%'))
                or lower(coalesce(e.direccionIp, '')) like lower(concat('%', :q, '%')))
              and (:usuario = '' or lower(e.usuario) like lower(concat('%', :usuario, '%')))
              and (:metodo = '' or e.metodo = :metodo)
              and (:ruta = '' or lower(e.ruta) like lower(concat('%', :ruta, '%')))
              and (:correlacionId = '' or lower(e.correlacionId) like lower(concat('%', :correlacionId, '%')))
              and (:filtrarCodigo = false or e.codigoHttp between :codigoDesde and :codigoHasta)
              and (:filtrarDesde = false or e.fechaHora >= :desde)
              and (:filtrarHasta = false or e.fechaHora < :hasta)
            """)
    Page<EventoAuditoria> buscarAvanzado(
            @Param("q") String q,
            @Param("usuario") String usuario,
            @Param("metodo") String metodo,
            @Param("ruta") String ruta,
            @Param("correlacionId") String correlacionId,
            @Param("filtrarCodigo") boolean filtrarCodigo,
            @Param("codigoDesde") int codigoDesde,
            @Param("codigoHasta") int codigoHasta,
            @Param("filtrarDesde") boolean filtrarDesde,
            @Param("desde") OffsetDateTime desde,
            @Param("filtrarHasta") boolean filtrarHasta,
            @Param("hasta") OffsetDateTime hasta,
            Pageable pageable
    );

    long deleteByFechaHoraBefore(OffsetDateTime limite);
}
