package pe.vichaval.gestionriego.riego.controller;

import jakarta.validation.Valid;

import java.time.Duration;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import pe.vichaval.gestionriego.parcela.repository.ParcelaRepository;
import pe.vichaval.gestionriego.riego.dto.TurnoForm;
import pe.vichaval.gestionriego.riego.repository.TurnoRiegoRepository;
import pe.vichaval.gestionriego.riego.service.TurnoRiegoService;

@Controller
@RequestMapping("/turnos")
public class TurnoRiegoWebController {
    private final TurnoRiegoRepository r;
    private final ParcelaRepository p;
    private final TurnoRiegoService s;

    public TurnoRiegoWebController(TurnoRiegoRepository r, ParcelaRepository p, TurnoRiegoService s) {
        this.r = r;
        this.p = p;
        this.s = s;
    }

    @GetMapping
    String listar(Model m) {
        s.actualizarVencidos();
        m.addAttribute("turnos", r.findAll());
        return "riego/list";
    }

    @GetMapping("/nuevo")
    String nuevo(Model m) {
        m.addAttribute("form", new TurnoForm());
        m.addAttribute("id", null);
        cargar(m);
        return "riego/form";
    }

    @GetMapping("/{id}/editar")
    String editar(@PathVariable Long id, Model m) {
        var x = s.get(id);
        var f = new TurnoForm();
        f.setParcelaId(x.getParcela().getId());
        f.setFecha(x.getFecha());
        f.setHoraInicio(x.getHoraInicio());
        f.setDuracionMinutos((int) Duration.between(x.getHoraInicio(), x.getHoraFin()).toMinutes());
        f.setObservaciones(x.getObservaciones());
        m.addAttribute("form", f);
        m.addAttribute("id", id);
        cargar(m);
        return "riego/form";
    }

    @PostMapping("/guardar")
    String guardar(@RequestParam(required = false) Long id, @Valid @ModelAttribute("form") TurnoForm f, BindingResult b, Model m, RedirectAttributes ra) {
        m.addAttribute("id", id);
        if (b.hasErrors()) {
            cargar(m);
            return "riego/form";
        }
        try {
            s.guardar(f, id);
            ra.addFlashAttribute("exito", "Turno guardado");
            return "redirect:/turnos";
        } catch (RuntimeException e) {
            b.reject("negocio", e.getMessage());
            cargar(m);
            return "riego/form";
        }
    }

    @PostMapping("/{id}/iniciar")
    String iniciar(@PathVariable Long id, RedirectAttributes ra) {
        return ejecutar(() -> s.iniciar(id), ra, "Turno iniciado");
    }

    @PostMapping("/{id}/completar")
    String completar(@PathVariable Long id, RedirectAttributes ra) {
        return ejecutar(() -> s.completar(id), ra, "Turno completado");
    }

    @PostMapping("/{id}/cancelar")
    String cancelar(@PathVariable Long id, @RequestParam String motivo, RedirectAttributes ra) {
        return ejecutar(() -> s.cancelar(id, motivo), ra, "Turno cancelado");
    }

    private String ejecutar(Runnable x, RedirectAttributes ra, String ok) {
        try {
            x.run();
            ra.addFlashAttribute("exito", ok);
        } catch (RuntimeException e) {
            ra.addFlashAttribute("error", e.getMessage());
        }
        return "redirect:/turnos";
    }

    private void cargar(Model m) {
        m.addAttribute("parcelas", p.findByEstadoOrderByCodigo("ACTIVA"));
    }
}
