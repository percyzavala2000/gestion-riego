package pe.vichaval.gestionriego.notificacion.push.repository;
import java.time.OffsetDateTime; import java.util.List;
import org.springframework.data.domain.Pageable; import org.springframework.data.jpa.repository.*; import org.springframework.data.repository.query.Param;
import pe.vichaval.gestionriego.notificacion.push.entity.EntregaPush;
public interface EntregaPushRepository extends JpaRepository<EntregaPush,Long>{
 boolean existsByNotificacionIdAndDispositivoId(Long notificacionId,Long dispositivoId);
 @EntityGraph(attributePaths={"notificacion","notificacion.agricultor","dispositivo"})
 @Query("select e from EntregaPush e where (e.estado='PENDIENTE' or e.estado='ERROR_TEMPORAL') and (e.proximoIntento is null or e.proximoIntento<=:ahora) order by e.fechaCreacion")
 List<EntregaPush> pendientes(@Param("ahora")OffsetDateTime ahora,Pageable pageable);
 long countByEstado(String estado);
}
