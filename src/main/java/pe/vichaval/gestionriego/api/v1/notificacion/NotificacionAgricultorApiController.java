package pe.vichaval.gestionriego.api.v1.notificacion;
import java.util.Map; import org.springframework.security.core.annotation.AuthenticationPrincipal; import org.springframework.web.bind.annotation.*;
import pe.vichaval.gestionriego.api.v1.common.PaginaRespuesta; import pe.vichaval.gestionriego.notificacion.dto.NotificacionRespuesta; import pe.vichaval.gestionriego.notificacion.service.NotificacionService; import pe.vichaval.gestionriego.security.agricultor.auth.AgricultorPrincipal;
@RestController @RequestMapping("/api/v1/agricultor/me/notificaciones")
public class NotificacionAgricultorApiController {
 private final NotificacionService service; public NotificacionAgricultorApiController(NotificacionService s){service=s;}
 @GetMapping public PaginaRespuesta<NotificacionRespuesta> listar(@AuthenticationPrincipal AgricultorPrincipal p,@RequestParam(defaultValue="0")int page){return PaginaRespuesta.desde(service.agricultor(p.agricultorId(),page).map(NotificacionRespuesta::desde));}
 @GetMapping("/no-leidas") public Map<String,Long> noLeidas(@AuthenticationPrincipal AgricultorPrincipal p){return Map.of("noLeidas",service.noLeidasAgricultor(p.agricultorId()));}
 @PatchMapping("/{id}/leer") public NotificacionRespuesta leer(@AuthenticationPrincipal AgricultorPrincipal p,@PathVariable Long id){return NotificacionRespuesta.desde(service.leerAgricultor(id,p.agricultorId()));}
 @PatchMapping("/leer-todas") public Map<String,Integer> leerTodas(@AuthenticationPrincipal AgricultorPrincipal p){return Map.of("actualizadas",service.leerTodasAgricultor(p.agricultorId()));}
 @PatchMapping("/{id}/archivar") public NotificacionRespuesta archivar(@AuthenticationPrincipal AgricultorPrincipal p,@PathVariable Long id){return NotificacionRespuesta.desde(service.archivarAgricultor(id,p.agricultorId()));}
}
