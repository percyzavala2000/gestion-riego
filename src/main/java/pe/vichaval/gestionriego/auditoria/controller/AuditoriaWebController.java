package pe.vichaval.gestionriego.auditoria.controller;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import pe.vichaval.gestionriego.auditoria.service.AuditoriaService;

@Controller
@RequestMapping("/auditoria")
@PreAuthorize("hasRole('ADMINISTRADOR')")
public class AuditoriaWebController {
    private final AuditoriaService auditoria;

    public AuditoriaWebController(AuditoriaService auditoria) {
        this.auditoria = auditoria;
    }

    @GetMapping
    public String listar(@RequestParam(defaultValue = "") String q,
                         @RequestParam(defaultValue = "0") int page, Model model) {
        model.addAttribute("pagina", auditoria.buscar(q, page));
        model.addAttribute("q", q);
        return "auditoria/index";
    }
}
