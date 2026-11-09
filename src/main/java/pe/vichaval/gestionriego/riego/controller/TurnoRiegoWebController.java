package pe.vichaval.gestionriego.riego.controller;

import jakarta.validation.Valid;
import java.time.Duration;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import pe.vichaval.gestionriego.parcela.repository.ParcelaRepository;
import pe.vichaval.gestionriego.riego.dto.TurnoForm;
import pe.vichaval.gestionriego.riego.repository.TurnoRiegoRepository;
import pe.vichaval.gestionriego.riego.service.TurnoRiegoService;

@Controller
@RequestMapping("/turnos")
public class TurnoRiegoWebController {

    private final TurnoRiegoRepository turnoRepository;
    private final ParcelaRepository parcelaRepository;
    private final TurnoRiegoService turnoService;

    public TurnoRiegoWebController(
            TurnoRiegoRepository turnoRepository,
            ParcelaRepository parcelaRepository,
            TurnoRiegoService turnoService
    ) {
        this.turnoRepository = turnoRepository;
        this.parcelaRepository = parcelaRepository;
        this.turnoService = turnoService;
    }

    @GetMapping
    public String listar(
            @RequestParam(defaultValue = "") String q,
            @RequestParam(defaultValue = "") String estado,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            Model model
    ) {
        turnoService.actualizarVencidos();

        var pagina = turnoService.listar(
                q,
                estado,
                page,
                size
        );

        model.addAttribute("pagina", pagina);
        model.addAttribute("q", q);
        model.addAttribute("estado", estado);
        model.addAttribute("size", pagina.getSize());

        return "riego/list";
    }

    @GetMapping("/nuevo")
    public String mostrarFormularioNuevo(Model model) {

        model.addAttribute(
                "form",
                new TurnoForm()
        );

        model.addAttribute("id", null);

        cargarParcelasActivas(model);

        return "riego/form";
    }

    @GetMapping("/{id}/editar")
    public String mostrarFormularioEditar(
            @PathVariable Long id,
            Model model,
            RedirectAttributes redirectAttributes
    ) {
        try {
            var turno = turnoService.get(id);

            if (
                    !"PROGRAMADO".equals(turno.getEstado())
                            && !"PENDIENTE".equals(turno.getEstado())
            ) {
                redirectAttributes.addFlashAttribute(
                        "error",
                        "Solo pueden editarse turnos pendientes "
                                + "o programados."
                );

                return "redirect:/turnos";
            }

            TurnoForm form = crearFormularioDesdeTurno(
                    turno
            );

            model.addAttribute("form", form);
            model.addAttribute("id", id);

            cargarParcelasActivas(model);

            return "riego/form";

        } catch (RuntimeException exception) {

            redirectAttributes.addFlashAttribute(
                    "error",
                    exception.getMessage()
            );

            return "redirect:/turnos";
        }
    }

    @GetMapping("/{id}/reprogramar")
    public String reprogramar(
            @PathVariable Long id,
            Model model,
            RedirectAttributes redirectAttributes
    ) {
        try {
            var turno = turnoService.get(id);

            if (
                    !"CANCELADO".equals(turno.getEstado())
                            && !"VENCIDO".equals(turno.getEstado())
            ) {
                redirectAttributes.addFlashAttribute(
                        "error",
                        "Solo pueden reprogramarse turnos "
                                + "cancelados o vencidos."
                );

                return "redirect:/turnos";
            }

            TurnoForm form = new TurnoForm();

            form.setParcelaId(
                    turno.getParcela().getId()
            );

            form.setDuracionMinutos(
                    calcularDuracion(turno)
            );

            form.setObservaciones(
                    "Reprogramación del turno "
                            + turno.getCodigo()
            );

            /*
             * La fecha y la hora quedan vacías para obligar
             * a seleccionar una nueva programación.
             */
            form.setFecha(null);
            form.setHoraInicio(null);

            model.addAttribute("form", form);

            /*
             * id nulo significa que se creará un turno nuevo.
             * No se sobrescribe el turno cancelado o vencido.
             */
            model.addAttribute("id", null);

            cargarParcelasActivas(model);

            return "riego/form";

        } catch (RuntimeException exception) {

            redirectAttributes.addFlashAttribute(
                    "error",
                    exception.getMessage()
            );

            return "redirect:/turnos";
        }
    }

    @PostMapping("/guardar")
    public String guardar(
            @RequestParam(required = false) Long id,
            @Valid @ModelAttribute("form") TurnoForm form,
            BindingResult bindingResult,
            Model model,
            RedirectAttributes redirectAttributes
    ) {
        model.addAttribute("id", id);

        if (bindingResult.hasErrors()) {
            cargarParcelasActivas(model);
            return "riego/form";
        }

        try {
            turnoService.guardar(form, id);

            redirectAttributes.addFlashAttribute(
                    "exito",
                    id == null
                            ? "Turno registrado correctamente."
                            : "Turno actualizado correctamente."
            );

            return "redirect:/turnos";

        } catch (RuntimeException exception) {

            bindingResult.reject(
                    "errorNegocio",
                    exception.getMessage()
            );

            cargarParcelasActivas(model);

            return "riego/form";
        }
    }

    @PostMapping("/{id}/iniciar")
    public String iniciar(
            @PathVariable Long id,
            RedirectAttributes redirectAttributes
    ) {
        return ejecutarOperacion(
                () -> turnoService.iniciar(id),
                redirectAttributes,
                "Turno iniciado correctamente."
        );
    }

    @PostMapping("/{id}/completar")
    public String completar(
            @PathVariable Long id,
            RedirectAttributes redirectAttributes
    ) {
        return ejecutarOperacion(
                () -> turnoService.completar(id),
                redirectAttributes,
                "Turno completado correctamente."
        );
    }

    @PostMapping("/{id}/cancelar")
    public String cancelar(
            @PathVariable Long id,
            @RequestParam String motivo,
            RedirectAttributes redirectAttributes
    ) {
        return ejecutarOperacion(
                () -> turnoService.cancelar(id, motivo),
                redirectAttributes,
                "Turno cancelado correctamente."
        );
    }

    private TurnoForm crearFormularioDesdeTurno(
            pe.vichaval.gestionriego.riego.entity.TurnoRiego turno
    ) {
        TurnoForm form = new TurnoForm();

        form.setParcelaId(
                turno.getParcela().getId()
        );

        form.setFecha(
                turno.getFecha()
        );

        form.setHoraInicio(
                turno.getHoraInicio()
        );

        form.setDuracionMinutos(
                calcularDuracion(turno)
        );

        form.setObservaciones(
                turno.getObservaciones()
        );

        return form;
    }

    private int calcularDuracion(
            pe.vichaval.gestionriego.riego.entity.TurnoRiego turno
    ) {
        return (int) Duration
                .between(
                        turno.getHoraInicio(),
                        turno.getHoraFin()
                )
                .toMinutes();
    }

    private String ejecutarOperacion(
            Runnable operacion,
            RedirectAttributes redirectAttributes,
            String mensajeExito
    ) {
        try {
            operacion.run();

            redirectAttributes.addFlashAttribute(
                    "exito",
                    mensajeExito
            );

        } catch (RuntimeException exception) {

            redirectAttributes.addFlashAttribute(
                    "error",
                    exception.getMessage()
            );
        }

        return "redirect:/turnos";
    }

    private void cargarParcelasActivas(Model model) {
        model.addAttribute(
                "parcelas",
                parcelaRepository
                        .findByEstadoOrderByCodigo("ACTIVA")
        );
    }
}