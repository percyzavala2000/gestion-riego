package pe.vichaval.gestionriego.api.v1.agricultor;

import jakarta.validation.Valid;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
import pe.vichaval.gestionriego.agricultor.service.AgricultorService;
import pe.vichaval.gestionriego.api.v1.common.PaginaRespuesta;

@RestController
@RequestMapping("/api/v1/agricultores")
public class AgricultorApiController {
    private final AgricultorService service;

    public AgricultorApiController(AgricultorService service) {
        this.service = service;
    }

    @GetMapping
    public PaginaRespuesta<AgricultorRespuesta> listar(@RequestParam(defaultValue = "") String q, @RequestParam(defaultValue = "0") int page) {
        return PaginaRespuesta.desde(service.listar(q, page).map(AgricultorRespuesta::desde));
    }

    @GetMapping("/{id}")
    public AgricultorRespuesta obtener(@PathVariable Long id) {
        return AgricultorRespuesta.desde(service.get(id));
    }

    @PostMapping
    public ResponseEntity<AgricultorRespuesta> crear(@Valid @RequestBody AgricultorSolicitud r) {
        service.save(r.aForm(), null);
        var creado = service.listar(r.dni(), 0).getContent().stream().filter(a -> r.dni().equals(a.getDni())).findFirst().orElseThrow();
        var uri = ServletUriComponentsBuilder.fromCurrentRequest().path("/{id}").buildAndExpand(creado.getId()).toUri();
        return ResponseEntity.created(uri).body(AgricultorRespuesta.desde(creado));
    }

    @PutMapping("/{id}")
    public AgricultorRespuesta actualizar(@PathVariable Long id, @Valid @RequestBody AgricultorSolicitud r) {
        service.save(r.aForm(), id);
        return AgricultorRespuesta.desde(service.get(id));
    }

    @PatchMapping("/{id}/estado")
    public AgricultorRespuesta cambiarEstado(@PathVariable Long id) {
        service.cambiarEstado(id);
        return AgricultorRespuesta.desde(service.get(id));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable Long id) {
        service.eliminar(id);
        return ResponseEntity.noContent().build();
    }
}
