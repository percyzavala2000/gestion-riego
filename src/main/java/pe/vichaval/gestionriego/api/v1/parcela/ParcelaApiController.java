package pe.vichaval.gestionriego.api.v1.parcela;

import java.util.List;

import org.springframework.web.bind.annotation.*;
import pe.vichaval.gestionriego.parcela.repository.ParcelaRepository;
import pe.vichaval.gestionriego.parcela.service.ParcelaService;

@RestController
@RequestMapping("/api/v1/parcelas")
public class ParcelaApiController {
    private final ParcelaRepository repository;
    private final ParcelaService service;

    public ParcelaApiController(ParcelaRepository repository, ParcelaService service) {
        this.repository = repository;
        this.service = service;
    }

    @GetMapping
    public List<ParcelaRespuesta> listar() {
        return repository.findAll().stream().map(ParcelaRespuesta::desde).toList();
    }

    @GetMapping("/{id}")
    public ParcelaRespuesta obtener(@PathVariable Long id) {
        return ParcelaRespuesta.desde(service.get(id));
    }
}
