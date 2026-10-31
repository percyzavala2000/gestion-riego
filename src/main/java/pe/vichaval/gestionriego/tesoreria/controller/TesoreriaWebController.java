package pe.vichaval.gestionriego.tesoreria.controller;

import jakarta.validation.Valid;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import pe.vichaval.gestionriego.agricultor.repository.AgricultorRepository;
import pe.vichaval.gestionriego.tesoreria.dto.PagoForm;
import pe.vichaval.gestionriego.tesoreria.repository.PagoRepository;
import pe.vichaval.gestionriego.tesoreria.service.PagoService;

@Controller
@RequestMapping("/tesoreria")
public class TesoreriaWebController {
    private final PagoRepository r;
    private final AgricultorRepository a;
    private final PagoService s;

    public TesoreriaWebController(PagoRepository r, AgricultorRepository a, PagoService s) {
        this.r = r;
        this.a = a;
        this.s = s;
    }

    @GetMapping
    String index(Model m) {
        cargar(m);
        if (!m.containsAttribute("form")) m.addAttribute("form", new PagoForm());
        return "tesoreria/index";
    }

    @PostMapping("/pagos/guardar")
    String guardar(@Valid @ModelAttribute("form") PagoForm f, BindingResult b, Model m, RedirectAttributes ra) {
        if (b.hasErrors()) {
            cargar(m);
            return "tesoreria/index";
        }
        try {
            s.registrar(f);
            ra.addFlashAttribute("exito", "Pago registrado");
            return "redirect:/tesoreria";
        } catch (RuntimeException e) {
            b.reject("negocio", e.getMessage());
            cargar(m);
            return "tesoreria/index";
        }
    }

    @PostMapping("/pagos/{id}/confirmar")
    String confirmar(@PathVariable Long id, RedirectAttributes ra) {
        return ejecutar(() -> s.confirmar(id), ra, "Pago confirmado");
    }

    @PostMapping("/pagos/{id}/rechazar")
    String rechazar(@PathVariable Long id, @RequestParam String motivo, RedirectAttributes ra) {
        return ejecutar(() -> s.rechazar(id, motivo), ra, "Pago rechazado");
    }

    @PostMapping("/pagos/{id}/anular")
    String anular(@PathVariable Long id, @RequestParam String motivo, RedirectAttributes ra) {
        return ejecutar(() -> s.anular(id, motivo), ra, "Pago anulado");
    }

    private String ejecutar(Runnable x, RedirectAttributes ra, String ok) {
        try {
            x.run();
            ra.addFlashAttribute("exito", ok);
        } catch (RuntimeException e) {
            ra.addFlashAttribute("error", e.getMessage());
        }
        return "redirect:/tesoreria";
    }

    private void cargar(Model m) {
        m.addAttribute("pagos", r.findAll());
        m.addAttribute("total", r.total());
        m.addAttribute("agricultores", a.findAll().stream().filter(x -> "ACTIVO".equals(x.getEstado())).toList());
    }
}
