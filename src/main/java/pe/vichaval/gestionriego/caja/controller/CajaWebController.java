package pe.vichaval.gestionriego.caja.controller;

import jakarta.validation.Valid;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import pe.vichaval.gestionriego.caja.dto.*;
import pe.vichaval.gestionriego.caja.service.CajaService;
import pe.vichaval.gestionriego.security.service.UsuarioPrincipal;
import pe.vichaval.gestionriego.tesoreria.repository.PagoRepository;

@Controller
@RequestMapping("/tesoreria/caja")
public class CajaWebController {
    private final CajaService s;
    private final PagoRepository pagos;

    public CajaWebController(CajaService s, PagoRepository p) {
        this.s = s;
        pagos = p;
    }

    @GetMapping
    String inicio(
            @RequestParam(defaultValue = "") String eQ,
            @RequestParam(defaultValue = "") String eEstado,
            @RequestParam(defaultValue = "0") int ePage,
            @RequestParam(defaultValue = "20") int eSize,
            @RequestParam(defaultValue = "") String dQ,
            @RequestParam(defaultValue = "") String dEstado,
            @RequestParam(defaultValue = "0") int dPage,
            @RequestParam(defaultValue = "20") int dSize,
            Model m
    ) {
        cargar(m, eQ, eEstado, ePage, eSize, dQ, dEstado, dPage, dSize);
        return "caja/index";
    }

    @PostMapping("/egresos")
    String egreso(@Valid @ModelAttribute("egresoForm") EgresoForm f, BindingResult b, @AuthenticationPrincipal UsuarioPrincipal u, Model m, RedirectAttributes r) {
        if (b.hasErrors()) {
            cargar(m, "", "", 0, 20, "", "", 0, 20);
            return "caja/index";
        }
        return ejecutar(() -> s.crearEgreso(f, u.getId()), r, "Egreso registrado");
    }

    @PostMapping("/egresos/{id}/anular")
    String anular(@PathVariable Long id, @RequestParam String motivo, RedirectAttributes r) {
        return ejecutar(() -> s.anularEgreso(id, motivo), r, "Egreso anulado");
    }

    @PostMapping("/devoluciones")
    String devolver(@Valid @ModelAttribute("devolucionForm") DevolucionForm f, BindingResult b, @AuthenticationPrincipal UsuarioPrincipal u, Model m, RedirectAttributes r) {
        if (b.hasErrors()) {
            cargar(m, "", "", 0, 20, "", "", 0, 20);
            return "caja/index";
        }
        return ejecutar(() -> s.solicitar(f, u.getId()), r, "Devolución solicitada");
    }

    @PostMapping("/devoluciones/{id}/{accion}")
    String accion(@PathVariable Long id, @PathVariable String accion, RedirectAttributes r) {
        return ejecutar(() -> {
            switch (accion) {
                case "aprobar" -> s.aprobar(id);
                case "ejecutar" -> s.ejecutar(id);
                case "rechazar" -> s.rechazar(id);
                default -> throw new IllegalArgumentException("Acción inválida");
            }
        }, r, "Devolución actualizada");
    }

    @PostMapping("/cierres")
    String cerrar(@Valid @ModelAttribute("cierreForm") CierreCajaForm f, BindingResult b, @AuthenticationPrincipal UsuarioPrincipal u, Model m, RedirectAttributes r) {
        if (b.hasErrors()) {
            cargar(m, "", "", 0, 20, "", "", 0, 20);
            return "caja/index";
        }
        return ejecutar(() -> s.cerrar(f, u.getId()), r, "Caja cerrada");
    }

    @PreAuthorize("hasRole('ADMINISTRADOR')")
    @PostMapping("/cierres/{id}/reabrir")
    String reabrir(@PathVariable Long id, @RequestParam String motivo, @AuthenticationPrincipal UsuarioPrincipal u, RedirectAttributes r) {
        return ejecutar(() -> s.reabrir(id, motivo, u.getId()), r, "Caja reabierta");
    }

    private String ejecutar(Runnable op, RedirectAttributes r, String ok) {
        try {
            op.run();
            r.addFlashAttribute("exito", ok);
        } catch (RuntimeException e) {
            r.addFlashAttribute("error", e.getMessage());
        }
        return "redirect:/tesoreria/caja";
    }

    private void cargar(Model m, String eQ, String eEstado, int ePage, int eSize, String dQ, String dEstado, int dPage, int dSize) {
        cargarBase(m);
        var egresosPagina = s.egresos(eQ, eEstado, ePage, eSize);
        m.addAttribute("egresosPagina", egresosPagina);
        m.addAttribute("eQ", eQ);
        m.addAttribute("eEstado", eEstado);
        m.addAttribute("eSize", egresosPagina.getSize());

        var devolucionesPagina = s.devoluciones(dQ, dEstado, dPage, dSize);
        m.addAttribute("devolucionesPagina", devolucionesPagina);
        m.addAttribute("dQ", dQ);
        m.addAttribute("dEstado", dEstado);
        m.addAttribute("dSize", devolucionesPagina.getSize());
    }

    private void cargarBase(Model m) {
        m.addAttribute("egresoForm", new EgresoForm());
        m.addAttribute("devolucionForm", new DevolucionForm());
        m.addAttribute("cierreForm", new CierreCajaForm());
        m.addAttribute("cierres", s.cierres());
        m.addAttribute("pagos", pagos.findAll().stream().filter(p -> "CONFIRMADO".equals(p.getEstado())).toList());
    }
}
