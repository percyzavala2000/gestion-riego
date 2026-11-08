package pe.vichaval.gestionriego.notificacion.controller;
import org.springframework.security.core.annotation.AuthenticationPrincipal; import org.springframework.stereotype.Controller; import org.springframework.ui.Model; import org.springframework.web.bind.annotation.*; import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import pe.vichaval.gestionriego.notificacion.service.NotificacionService; import pe.vichaval.gestionriego.security.service.UsuarioPrincipal;
@Controller @RequestMapping("/notificaciones")
public class NotificacionWebController {
 private final NotificacionService service; public NotificacionWebController(NotificacionService s){service=s;}
 @GetMapping String listar(@AuthenticationPrincipal UsuarioPrincipal p,@RequestParam(defaultValue="0")int page,Model m){m.addAttribute("pagina",service.usuario(p.getId(),page));m.addAttribute("noLeidas",service.noLeidasUsuario(p.getId()));return "notificacion/index";}
 @PostMapping("/{id}/leer") String leer(@AuthenticationPrincipal UsuarioPrincipal p,@PathVariable Long id){service.leerUsuario(id,p.getId());return "redirect:/notificaciones";}
 @PostMapping("/leer-todas") String todas(@AuthenticationPrincipal UsuarioPrincipal p,RedirectAttributes r){int n=service.leerTodasUsuario(p.getId());r.addFlashAttribute("exito",n+" notificación(es) marcadas como leídas");return "redirect:/notificaciones";}
 @PostMapping("/{id}/archivar") String archivar(@AuthenticationPrincipal UsuarioPrincipal p,@PathVariable Long id){service.archivarUsuario(id,p.getId());return "redirect:/notificaciones";}
}
