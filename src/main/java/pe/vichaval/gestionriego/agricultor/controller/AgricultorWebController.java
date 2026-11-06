package pe.vichaval.gestionriego.agricultor.controller;

import jakarta.validation.Valid;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import pe.vichaval.gestionriego.agricultor.dto.AgricultorForm;
import pe.vichaval.gestionriego.agricultor.service.AgricultorService;

@Controller
@RequestMapping("/agricultores")
public class AgricultorWebController {
    private final AgricultorService s;

    public AgricultorWebController(AgricultorService s) {
        this.s = s;
    }

    @GetMapping
    String listar(@RequestParam(defaultValue = "") String q, @RequestParam(defaultValue = "0") int page, Model m) {
        m.addAttribute("pagina", s.listar(q, page));
        m.addAttribute("q", q);
        return "agricultor/list";
    }

    @GetMapping("/nuevo")
    String nuevo(Model m) {
        m.addAttribute("form", new AgricultorForm());
        m.addAttribute("id", null);
        return "agricultor/form";
    }

    @GetMapping("/{id}/editar")
    String editar(@PathVariable Long id, Model m) {
        var a = s.get(id);
        var f = new AgricultorForm();
        f.setDni(a.getDni());
        f.setNombres(a.getNombres());
        f.setApellidos(a.getApellidos());
        f.setTelefono(a.getTelefono());
        f.setSector(a.getSector());
        f.setEstado(a.getEstado());
        m.addAttribute("form", f);
        m.addAttribute("id", id);
        return "agricultor/form";
    }

    @PostMapping("/guardar")
    String guardar(@RequestParam(required = false) Long id, @Valid @ModelAttribute("form") AgricultorForm f, BindingResult b, Model m, RedirectAttributes ra) {
        m.addAttribute("id", id);
        if (b.hasErrors()) return "agricultor/form";
        try {
            s.save(f, id);
            ra.addFlashAttribute("exito", "Agricultor guardado");
            return "redirect:/agricultores";
        } catch (RuntimeException e) {
            b.reject("negocio", e.getMessage());
            return "agricultor/form";
        }
    }

    @PostMapping("/{id}/estado")
    String estado(@PathVariable Long id, RedirectAttributes ra) {
        s.cambiarEstado(id);
        ra.addFlashAttribute("exito", "Estado actualizado");
        return "redirect:/agricultores";
    }

    @PreAuthorize("hasRole('ADMINISTRADOR')")
    @PostMapping("/{id}/eliminar")
    String eliminar(@PathVariable Long id, RedirectAttributes ra) {
        try {
            s.eliminar(id);
            ra.addFlashAttribute("exito", "Agricultor eliminado");
        } catch (RuntimeException e) {
            ra.addFlashAttribute("error", e.getMessage());
        }
        return "redirect:/agricultores";
    }
}
