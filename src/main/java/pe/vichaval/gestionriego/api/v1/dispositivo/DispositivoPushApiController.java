package pe.vichaval.gestionriego.api.v1.dispositivo;
import jakarta.validation.Valid; import jakarta.validation.constraints.*; import java.util.List;
import org.springframework.http.ResponseEntity; import org.springframework.security.core.annotation.AuthenticationPrincipal; import org.springframework.web.bind.annotation.*;
import pe.vichaval.gestionriego.notificacion.push.entity.DispositivoPush; import pe.vichaval.gestionriego.notificacion.push.service.DispositivoPushService; import pe.vichaval.gestionriego.security.agricultor.auth.AgricultorPrincipal;
@RestController @RequestMapping("/api/v1/agricultor/me/dispositivos")
public class DispositivoPushApiController {
 private final DispositivoPushService service; public DispositivoPushApiController(DispositivoPushService s){service=s;}
 @PutMapping("/{installationId}") public Respuesta registrar(@AuthenticationPrincipal AgricultorPrincipal p,@PathVariable @Size(max=180)String installationId,@Valid @RequestBody Solicitud x){return Respuesta.desde(service.registrar(p.cuentaId(),installationId,x.tokenRegistro(),x.plataforma(),x.nombreDispositivo(),x.versionAplicacion(),x.versionSistema(),x.modeloDispositivo(),x.idioma(),x.zonaHoraria()));}
 @GetMapping public List<Respuesta> listar(@AuthenticationPrincipal AgricultorPrincipal p){return service.listar(p.cuentaId()).stream().map(Respuesta::desde).toList();}
 @DeleteMapping("/{id}") public ResponseEntity<Void> eliminar(@AuthenticationPrincipal AgricultorPrincipal p,@PathVariable Long id){service.desactivar(p.cuentaId(),id);return ResponseEntity.noContent().build();}
 public record Solicitud(@NotBlank @Size(max=4096)String tokenRegistro,@NotBlank @Pattern(regexp="ANDROID|IOS")String plataforma,@Size(max=120)String nombreDispositivo,@Size(max=30)String versionAplicacion,@Size(max=40)String versionSistema,@Size(max=100)String modeloDispositivo,@Size(max=20)String idioma,@Size(max=60)String zonaHoraria){}
 public record Respuesta(Long id,String installationId,String plataforma,String nombreDispositivo,String versionAplicacion,boolean activo){static Respuesta desde(DispositivoPush d){return new Respuesta(d.getId(),d.getInstallationId(),d.getPlataforma(),d.getNombreDispositivo(),d.getVersionAplicacion(),d.isActivo());}}
}
