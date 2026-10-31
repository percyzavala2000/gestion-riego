package pe.vichaval.gestionriego.tesoreria.controller;

import jakarta.validation.Valid;
import org.springframework.stereotype.*;
import org.springframework.ui.*;
import org.springframework.validation.*;
import org.springframework.web.bind.annotation.*;
import pe.vichaval.gestionriego.agricultor.repository.*;
import pe.vichaval.gestionriego.tesoreria.dto.*;
import pe.vichaval.gestionriego.tesoreria.entity.*;
import pe.vichaval.gestionriego.tesoreria.repository.*;

import java.util.UUID;

@Controller
@RequestMapping("/tesoreria")
public class TesoreriaWebController {
    private final PagoRepository r;
    private final AgricultorRepository a;

    public TesoreriaWebController(PagoRepository r, AgricultorRepository a) {
        this.r = r;
        this.a = a;
    }

    @GetMapping
    String index(Model m) {
        load(m);
        m.addAttribute("form", new PagoForm());
        return "tesoreria/index";
    }

    @PostMapping
    String save(@Valid @ModelAttribute("form") PagoForm f, BindingResult b, Model m) {
        if (r.existsByComprobante(f.getComprobante())) b.rejectValue("comprobante", "dup", "Comprobante duplicado");
        if (b.hasErrors()) {
            load(m);
            return "tesoreria/index";
        }

        // Generar un código único para el pago antes de instanciarlo
        String codigo = "PAG-" + System.currentTimeMillis();

        r.save(new Pago(codigo, f, a.findById(f.getAgricultorId()).orElseThrow()));
        return "redirect:/tesoreria";
    }

    void load(Model m) {
        m.addAttribute("pagos", r.findAll());
        m.addAttribute("total", r.total());
        m.addAttribute("agricultores", a.findAll());
    }
}