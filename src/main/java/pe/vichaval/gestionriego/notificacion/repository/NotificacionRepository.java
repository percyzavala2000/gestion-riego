package pe.vichaval.gestionriego.notificacion.repository;
import java.time.OffsetDateTime; import java.util.Optional; import org.springframework.data.domain.*; import org.springframework.data.jpa.repository.*; import org.springframework.data.repository.query.Param;
import pe.vichaval.gestionriego.notificacion.entity.Notificacion;
public interface NotificacionRepository extends JpaRepository<Notificacion,Long>{
 @EntityGraph(attributePaths="agricultor") Page<Notificacion> findByAgricultorIdAndEstadoNotOrderByFechaCreacionDesc(Long id,String estado,Pageable p);
 @EntityGraph(attributePaths="usuario") Page<Notificacion> findByUsuarioIdAndEstadoNotOrderByFechaCreacionDesc(Long id,String estado,Pageable p);
 long countByAgricultorIdAndEstado(Long id,String estado); long countByUsuarioIdAndEstado(Long id,String estado);
 boolean existsByClaveIdempotencia(String clave); Optional<Notificacion> findByIdAndAgricultorId(Long id,Long agricultorId); Optional<Notificacion> findByIdAndUsuarioId(Long id,Long usuarioId);
 @Modifying @Query("update Notificacion n set n.estado='LEIDA',n.fechaLectura=:ahora where n.agricultor.id=:id and n.estado='NO_LEIDA'") int leerTodasAgricultor(@Param("id")Long id,@Param("ahora")OffsetDateTime ahora);
 @Modifying @Query("update Notificacion n set n.estado='LEIDA',n.fechaLectura=:ahora where n.usuario.id=:id and n.estado='NO_LEIDA'") int leerTodasUsuario(@Param("id")Long id,@Param("ahora")OffsetDateTime ahora);
 long deleteByEstadoAndFechaArchivadoBefore(String estado,OffsetDateTime limite); long deleteByEstadoAndFechaLecturaBefore(String estado,OffsetDateTime limite);
 long countByEstado(String estado);
}
