package pe.vichaval.gestionriego.api.v1.agricultor;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
import pe.vichaval.gestionriego.agricultor.service.AgricultorService;
import pe.vichaval.gestionriego.api.v1.common.PaginaRespuesta;

@Validated
@RestController
@RequestMapping("/api/v1/agricultores")
public class AgricultorApiController {
    private final AgricultorService service;

    public AgricultorApiController(AgricultorService service) {
        this.service = service;
    }

    @GetMapping
    public PaginaRespuesta<AgricultorRespuesta> listar(
            @RequestParam(defaultValue = "") String q,
            @RequestParam(defaultValue = "0")
            @Min(value = 0, message = "La pagina no puede ser negativa") int page) {
        return PaginaRespuesta.desde(
                service.listar(q, page).map(AgricultorRespuesta::desde)
        );
    }

    @GetMapping("/{id}")
    public AgricultorRespuesta obtener(@PathVariable Long id) {
        return AgricultorRespuesta.desde(service.get(id));
    }

    @PostMapping
    public ResponseEntity<AgricultorRespuesta> crear(
            @Valid @RequestBody AgricultorSolicitud solicitud) {
        var creado = service.save(solicitud.aForm(), null);
        var uri = ServletUriComponentsBuilder.fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(creado.getId())
                .toUri();
        return ResponseEntity.created(uri)
                .body(AgricultorRespuesta.desde(creado));
    }

    @PutMapping("/{id}")
    public AgricultorRespuesta actualizar(
            @PathVariable Long id,
            @Valid @RequestBody AgricultorSolicitud solicitud) {
        return AgricultorRespuesta.desde(
                service.save(solicitud.aForm(), id)
        );
    }

    @PatchMapping("/{id}/estado")
    public AgricultorRespuesta cambiarEstado(
            @PathVariable Long id,
            @Valid @RequestBody CambioEstadoAgricultorSolicitud solicitud) {
        return AgricultorRespuesta.desde(
                service.cambiarEstado(id, solicitud.estado())
        );
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable Long id) {
        service.eliminar(id);
        return ResponseEntity.noContent().build();
    }
}
