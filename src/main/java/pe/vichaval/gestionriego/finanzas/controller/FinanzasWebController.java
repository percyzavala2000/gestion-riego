package pe.vichaval.gestionriego.finanzas.controller;

import jakarta.validation.Valid;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import pe.vichaval.gestionriego.agricultor.repository.AgricultorRepository;
import pe.vichaval.gestionriego.finanzas.dto.*;
import pe.vichaval.gestionriego.finanzas.service.FinanzasService;

@Controller
@RequestMapping("/tesoreria/cuentas")
public class FinanzasWebController {
    private final FinanzasService s;
    private final AgricultorRepository a;

    public FinanzasWebController(FinanzasService s, AgricultorRepository a) {
        this.s = s;
        this.a = a;
    }

    @GetMapping
    String listar(Model m) {
        m.addAttribute("cuentas", s.listar());
        return "finanzas/cuentas";
    }

    @GetMapping("/nueva")
    String nueva(Model m) {
        m.addAttribute("form", new CuentaCobrarForm());
        cargar(m);
        return "finanzas/cuenta-form";
    }

    @PostMapping("/guardar")
    String guardar(@Valid @ModelAttribute("form") CuentaCobrarForm f, BindingResult b, Model m, RedirectAttributes ra) {
        if (b.hasErrors()) {
            cargar(m);
            return "finanzas/cuenta-form";
        }
        try {
            s.crearCuenta(f);
            ra.addFlashAttribute("exito", "Cuenta emitida");
            return "redirect:/tesoreria/cuentas";
        } catch (RuntimeException e) {
            b.reject("negocio", e.getMessage());
            cargar(m);
            return "finanzas/cuenta-form";
        }
    }

    @GetMapping("/{id}")
    String detalle(@PathVariable Long id, Model m) {
        m.addAttribute("cuenta", s.get(id));
        m.addAttribute("pagoForm", new PagoCuentaForm());
        return "finanzas/cuenta-detalle";
    }

    @PostMapping("/{id}/pagar")
    String pagar(@PathVariable Long id, @Valid @ModelAttribute("pagoForm") PagoCuentaForm f, BindingResult b, Model m, RedirectAttributes ra) {
        if (b.hasErrors()) {
            m.addAttribute("cuenta", s.get(id));
            return "finanzas/cuenta-detalle";
        }
        try {
            s.registrarPago(id, f);
            ra.addFlashAttribute("exito", "Pago aplicado");
            return "redirect:/tesoreria/cuentas/" + id;
        } catch (RuntimeException e) {
            b.reject("negocio", e.getMessage());
            m.addAttribute("cuenta", s.get(id));
            return "finanzas/cuenta-detalle";
        }
    }

    @PostMapping("/{id}/anular")
    String anular(@PathVariable Long id, @RequestParam String motivo, RedirectAttributes ra) {
        try {
            s.anular(id, motivo);
            ra.addFlashAttribute("exito", "Cuenta anulada");
        } catch (RuntimeException e) {
            ra.addFlashAttribute("error", e.getMessage());
        }
        return "redirect:/tesoreria/cuentas";
    }

    private void cargar(Model m) {
        m.addAttribute("agricultores", a.findAll().stream().filter(x -> "ACTIVO".equals(x.getEstado())).toList());
        m.addAttribute("conceptos", s.conceptosActivos());
    }
}
