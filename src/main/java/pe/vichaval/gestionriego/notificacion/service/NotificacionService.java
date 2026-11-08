package pe.vichaval.gestionriego.notificacion.service;
import java.time.OffsetDateTime; import org.springframework.data.domain.*; import org.springframework.stereotype.Service; import org.springframework.transaction.annotation.Transactional;
import pe.vichaval.gestionriego.agricultor.repository.AgricultorRepository; import pe.vichaval.gestionriego.notificacion.entity.Notificacion; import pe.vichaval.gestionriego.notificacion.repository.NotificacionRepository; import pe.vichaval.gestionriego.usuario.repository.UsuarioRepository;
@Service @Transactional
public class NotificacionService {
 private final NotificacionRepository repo; private final AgricultorRepository agricultores; private final UsuarioRepository usuarios;
 public NotificacionService(NotificacionRepository r,AgricultorRepository a,UsuarioRepository u){repo=r;agricultores=a;usuarios=u;}
 public Notificacion crearAgricultor(Long agricultorId,String tipo,String titulo,String mensaje,String prioridad,String refTipo,Long refId,String url,String clave,OffsetDateTime exp){if(repo.existsByClaveIdempotencia(clave))return null;var a=agricultores.findById(agricultorId).orElseThrow(()->new IllegalArgumentException("Agricultor no encontrado"));return repo.save(Notificacion.paraAgricultor(a,tipo,titulo,mensaje,prioridad,refTipo,refId,url,clave,exp));}
 public Notificacion crearUsuario(Long usuarioId,String tipo,String titulo,String mensaje,String prioridad,String refTipo,Long refId,String url,String clave,OffsetDateTime exp){if(repo.existsByClaveIdempotencia(clave))return null;var u=usuarios.findById(usuarioId).orElseThrow(()->new IllegalArgumentException("Usuario no encontrado"));return repo.save(Notificacion.paraUsuario(u,tipo,titulo,mensaje,prioridad,refTipo,refId,url,clave,exp));}
 @Transactional(readOnly=true) public Page<Notificacion> agricultor(Long id,int page){return repo.findByAgricultorIdAndEstadoNotOrderByFechaCreacionDesc(id,"ARCHIVADA",PageRequest.of(Math.max(0,page),20));}
 @Transactional(readOnly=true) public Page<Notificacion> usuario(Long id,int page){return repo.findByUsuarioIdAndEstadoNotOrderByFechaCreacionDesc(id,"ARCHIVADA",PageRequest.of(Math.max(0,page),20));}
 @Transactional(readOnly=true) public long noLeidasAgricultor(Long id){return repo.countByAgricultorIdAndEstado(id,"NO_LEIDA");} @Transactional(readOnly=true) public long noLeidasUsuario(Long id){return repo.countByUsuarioIdAndEstado(id,"NO_LEIDA");}
 public Notificacion leerAgricultor(Long nid,Long aid){var n=repo.findByIdAndAgricultorId(nid,aid).orElseThrow(()->new IllegalArgumentException("Notificación no encontrada"));n.leer();return n;}
 public Notificacion archivarAgricultor(Long nid,Long aid){var n=repo.findByIdAndAgricultorId(nid,aid).orElseThrow(()->new IllegalArgumentException("Notificación no encontrada"));n.archivar();return n;}
 public int leerTodasAgricultor(Long id){return repo.leerTodasAgricultor(id,OffsetDateTime.now());}
 public Notificacion leerUsuario(Long nid,Long uid){var n=repo.findByIdAndUsuarioId(nid,uid).orElseThrow(()->new IllegalArgumentException("Notificación no encontrada"));n.leer();return n;}
 public Notificacion archivarUsuario(Long nid,Long uid){var n=repo.findByIdAndUsuarioId(nid,uid).orElseThrow(()->new IllegalArgumentException("Notificación no encontrada"));n.archivar();return n;}
 public int leerTodasUsuario(Long id){return repo.leerTodasUsuario(id,OffsetDateTime.now());}
}
