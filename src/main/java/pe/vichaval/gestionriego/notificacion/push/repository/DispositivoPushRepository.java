package pe.vichaval.gestionriego.notificacion.push.repository;
import java.time.OffsetDateTime; import java.util.*;
import org.springframework.data.jpa.repository.JpaRepository;
import pe.vichaval.gestionriego.notificacion.push.entity.DispositivoPush;
public interface DispositivoPushRepository extends JpaRepository<DispositivoPush,Long>{
 Optional<DispositivoPush> findByCuentaIdAndInstallationId(Long cuentaId,String installationId);
 Optional<DispositivoPush> findByIdAndCuentaId(Long id,Long cuentaId);
 List<DispositivoPush> findByCuentaIdOrderByFechaUltimoUsoDesc(Long cuentaId);
 List<DispositivoPush> findByCuentaAgricultorIdAndActivoTrue(Long agricultorId);
 List<DispositivoPush> findByActivoTrueAndFechaUltimoUsoBefore(OffsetDateTime limite);
 long countByActivoTrue();
}
