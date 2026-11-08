package pe.vichaval.gestionriego.api.v1.agricultorpropio;
import java.util.List; import org.springframework.security.core.annotation.AuthenticationPrincipal; import org.springframework.web.bind.annotation.*;
import pe.vichaval.gestionriego.agricultor.service.AgricultorService; import pe.vichaval.gestionriego.api.v1.agricultor.AgricultorRespuesta;
import pe.vichaval.gestionriego.api.v1.parcela.ParcelaRespuesta; import pe.vichaval.gestionriego.api.v1.turno.TurnoRespuesta;
import pe.vichaval.gestionriego.parcela.repository.ParcelaRepository; import pe.vichaval.gestionriego.riego.repository.TurnoRiegoRepository;
import pe.vichaval.gestionriego.security.agricultor.auth.AgricultorPrincipal;
@RestController @RequestMapping("/api/v1/agricultor/me")
public class AgricultorPropioApiController {
 private final AgricultorService agricultores; private final ParcelaRepository parcelas; private final TurnoRiegoRepository turnos;
 public AgricultorPropioApiController(AgricultorService a,ParcelaRepository p,TurnoRiegoRepository t){agricultores=a;parcelas=p;turnos=t;}
 @GetMapping public AgricultorRespuesta perfil(@AuthenticationPrincipal AgricultorPrincipal p){return AgricultorRespuesta.desde(agricultores.get(p.agricultorId()));}
 @GetMapping("/parcelas") public List<ParcelaRespuesta> parcelas(@AuthenticationPrincipal AgricultorPrincipal p){return parcelas.findAll().stream().filter(x->x.getAgricultor().getId().equals(p.agricultorId())).map(ParcelaRespuesta::desde).toList();}
 @GetMapping("/turnos") public List<TurnoRespuesta> turnos(@AuthenticationPrincipal AgricultorPrincipal p){return turnos.findAll().stream().filter(x->x.getParcela().getAgricultor().getId().equals(p.agricultorId())).map(TurnoRespuesta::desde).toList();}
 @GetMapping("/turnos/{id}") public TurnoRespuesta turno(@AuthenticationPrincipal AgricultorPrincipal p,@PathVariable Long id){return turnos.findById(id).filter(x->x.getParcela().getAgricultor().getId().equals(p.agricultorId())).map(TurnoRespuesta::desde).orElseThrow(()->new IllegalArgumentException("Turno no encontrado"));}
}
