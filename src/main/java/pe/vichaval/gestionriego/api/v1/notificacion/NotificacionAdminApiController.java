package pe.vichaval.gestionriego.api.v1.notificacion;
import jakarta.validation.Valid; import jakarta.validation.constraints.*; import java.time.OffsetDateTime; import org.springframework.web.bind.annotation.*;
import pe.vichaval.gestionriego.notificacion.dto.NotificacionRespuesta; import pe.vichaval.gestionriego.notificacion.service.NotificacionService;
@RestController @RequestMapping("/api/v1/notificaciones/administrativas")
public class NotificacionAdminApiController {
 private final NotificacionService s; public NotificacionAdminApiController(NotificacionService s){this.s=s;}
 @PostMapping public NotificacionRespuesta crear(@Valid @RequestBody Solicitud x){var n=s.crearAgricultor(x.agricultorId(),"AVISO_ADMINISTRATIVO",x.titulo(),x.mensaje(),x.prioridad(),"ADMINISTRATIVO",null,x.referenciaUrl(),"ADMIN:"+x.clave(),x.fechaExpiracion());if(n==null)throw new IllegalStateException("La notificación ya existe");return NotificacionRespuesta.desde(n);}
 public record Solicitud(@NotNull Long agricultorId,@NotBlank @Size(max=160)String titulo,@NotBlank @Size(max=1000)String mensaje,@Pattern(regexp="BAJA|MEDIA|ALTA|CRITICA")String prioridad,@NotBlank @Size(max=120)String clave,@Size(max=255)String referenciaUrl,OffsetDateTime fechaExpiracion){}
}
