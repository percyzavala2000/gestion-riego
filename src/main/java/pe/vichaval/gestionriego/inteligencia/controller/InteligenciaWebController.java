package pe.vichaval.gestionriego.inteligencia.controller;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import pe.vichaval.gestionriego.inteligencia.service.InteligenciaService;

@Controller
@RequestMapping("/inteligencia")
public class InteligenciaWebController {
    private final InteligenciaService inteligencia;

    public InteligenciaWebController(InteligenciaService inteligencia) {
        this.inteligencia = inteligencia;
    }

    @PreAuthorize("hasAnyRole('ADMINISTRADOR','OPERADOR')")
    @GetMapping
    public String operativa(Model model) {
        model.addAttribute("resumen", inteligencia.operativa());
        model.addAttribute("tipo", "operativa");
        return "inteligencia/index";
    }

    @PreAuthorize("hasAnyRole('ADMINISTRADOR','TESORERIA')")
    @GetMapping("/financiera")
    public String financiera(Model model) {
        model.addAttribute("resumen", inteligencia.financiera());
        model.addAttribute("tipo", "financiera");
        return "inteligencia/index";
    }
}
