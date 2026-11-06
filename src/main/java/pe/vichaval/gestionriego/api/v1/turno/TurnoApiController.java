package pe.vichaval.gestionriego.api.v1.turno;

import java.util.List;

import jakarta.validation.constraints.NotBlank;
import org.springframework.web.bind.annotation.*;
import pe.vichaval.gestionriego.riego.repository.TurnoRiegoRepository;
import pe.vichaval.gestionriego.riego.service.TurnoRiegoService;

@RestController
@RequestMapping("/api/v1/turnos")
public class TurnoApiController {
    private final TurnoRiegoRepository repository;
    private final TurnoRiegoService service;

    public TurnoApiController(TurnoRiegoRepository repository, TurnoRiegoService service) {
        this.repository = repository;
        this.service = service;
    }

    @GetMapping
    public List<TurnoRespuesta> listar() {
        service.actualizarVencidos();
        return repository.findAll().stream().map(TurnoRespuesta::desde).toList();
    }

    @GetMapping("/{id}")
    public TurnoRespuesta obtener(@PathVariable Long id) {
        return TurnoRespuesta.desde(service.get(id));
    }

    @PostMapping("/{id}/iniciar")
    public TurnoRespuesta iniciar(@PathVariable Long id) {
        service.iniciar(id);
        return TurnoRespuesta.desde(service.get(id));
    }

    @PostMapping("/{id}/completar")
    public TurnoRespuesta completar(@PathVariable Long id) {
        service.completar(id);
        return TurnoRespuesta.desde(service.get(id));
    }

    @PostMapping("/{id}/cancelar")
    public TurnoRespuesta cancelar(@PathVariable Long id, @RequestBody CancelacionSolicitud r) {
        service.cancelar(id, r.motivo());
        return TurnoRespuesta.desde(service.get(id));
    }

    public record CancelacionSolicitud(@NotBlank String motivo) {
    }
}
