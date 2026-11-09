package pe.vichaval.gestionriego.notificacion.push.service;
import java.time.OffsetDateTime; import java.util.List;
import org.springframework.beans.factory.annotation.Value; import org.springframework.scheduling.annotation.Scheduled; import org.springframework.stereotype.Service; import org.springframework.transaction.annotation.Transactional;
import pe.vichaval.gestionriego.notificacion.push.entity.DispositivoPush; import pe.vichaval.gestionriego.notificacion.push.repository.DispositivoPushRepository; import pe.vichaval.gestionriego.security.agricultor.repository.CuentaAgricultorRepository;
@Service @Transactional
public class DispositivoPushService {
 private final DispositivoPushRepository repo; private final CuentaAgricultorRepository cuentas; private final int staleDays;
 public DispositivoPushService(DispositivoPushRepository r,CuentaAgricultorRepository c,@Value("${vichayal.firebase.stale-days:90}")int staleDays){repo=r;cuentas=c;this.staleDays=staleDays;}
 public DispositivoPush registrar(Long cuentaId,String fid,String token,String plataforma,String nombre,String app,String sistema,String modelo,String idioma,String zona){
  DispositivoPush d=repo.findByCuentaIdAndInstallationId(cuentaId,fid).orElseGet(()->new DispositivoPush(cuentas.findById(cuentaId).orElseThrow(),fid));
  d.actualizar(token,plataforma,nombre,app,sistema,modelo,idioma,zona); return repo.save(d);
 }
 @Transactional(readOnly=true) public List<DispositivoPush> listar(Long cuentaId){return repo.findByCuentaIdOrderByFechaUltimoUsoDesc(cuentaId);}
 public void desactivar(Long cuentaId,Long id){repo.findByIdAndCuentaId(id,cuentaId).orElseThrow(()->new IllegalArgumentException("Dispositivo no encontrado")).desactivar();}
 @Scheduled(cron="0 10 3 * * *",zone="America/Lima") public int desactivarObsoletos(){int[] n={0};repo.findByActivoTrueAndFechaUltimoUsoBefore(OffsetDateTime.now().minusDays(staleDays)).forEach(d->{d.desactivar();n[0]++;});return n[0];}
}
