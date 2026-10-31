package pe.vichaval.gestionriego.tesoreria.controller;

import jakarta.validation.Valid;
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
import pe.vichaval.gestionriego.agricultor.repository.AgricultorRepository;
import pe.vichaval.gestionriego.tesoreria.dto.PagoForm;
import pe.vichaval.gestionriego.tesoreria.repository.PagoRepository;
import pe.vichaval.gestionriego.tesoreria.service.PagoService;

@Controller
@RequestMapping("/tesoreria")
public class TesoreriaWebController {

    private final PagoRepository pagoRepository;
    private final AgricultorRepository agricultorRepository;
    private final PagoService pagoService;

    public TesoreriaWebController(
            PagoRepository pagoRepository,
            AgricultorRepository agricultorRepository,
            PagoService pagoService
    ) {
        this.pagoRepository = pagoRepository;
        this.agricultorRepository = agricultorRepository;
        this.pagoService = pagoService;
    }

    @GetMapping
    public String mostrarTesoreria(Model model) {

        cargarDatos(model);

        if (!model.containsAttribute("form")) {
            model.addAttribute(
                    "form",
                    new PagoForm()
            );
        }

        return "tesoreria/index";
    }

    @PostMapping("/pagos/guardar")
    public String registrarPago(
            @Valid @ModelAttribute("form") PagoForm form,
            BindingResult bindingResult,
            Model model,
            RedirectAttributes redirectAttributes
    ) {
        /*
         * Si existe algún error de validación, se vuelve a mostrar
         * el formulario conservando los valores ingresados.
         */
        if (bindingResult.hasErrors()) {
            cargarDatos(model);
            return "tesoreria/index";
        }

        try {
            pagoService.registrar(form);

            redirectAttributes.addFlashAttribute(
                    "exito",
                    "Pago registrado correctamente."
            );

            return "redirect:/tesoreria";

        } catch (RuntimeException exception) {

            bindingResult.reject(
                    "errorNegocio",
                    exception.getMessage()
            );

            cargarDatos(model);

            return "tesoreria/index";
        }
    }

    @PostMapping("/pagos/{id}/confirmar")
    public String confirmarPago(
            @PathVariable Long id,
            RedirectAttributes redirectAttributes
    ) {
        return ejecutarOperacion(
                () -> pagoService.confirmar(id),
                redirectAttributes,
                "Pago confirmado correctamente."
        );
    }

    @PostMapping("/pagos/{id}/rechazar")
    public String rechazarPago(
            @PathVariable Long id,
            @RequestParam String motivo,
            RedirectAttributes redirectAttributes
    ) {
        return ejecutarOperacion(
                () -> pagoService.rechazar(id, motivo),
                redirectAttributes,
                "Pago rechazado correctamente."
        );
    }

    @PostMapping("/pagos/{id}/anular")
    public String anularPago(
            @PathVariable Long id,
            @RequestParam String motivo,
            RedirectAttributes redirectAttributes
    ) {
        return ejecutarOperacion(
                () -> pagoService.anular(id, motivo),
                redirectAttributes,
                "Pago anulado correctamente."
        );
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

        return "redirect:/tesoreria";
    }

    private void cargarDatos(Model model) {

        model.addAttribute(
                "pagos",
                pagoRepository.findAll()
        );

        model.addAttribute(
                "total",
                pagoRepository.total()
        );

        model.addAttribute(
                "agricultores",
                agricultorRepository
                        .findAll()
                        .stream()
                        .filter(agricultor ->
                                "ACTIVO".equals(
                                        agricultor.getEstado()
                                )
                        )
                        .toList()
        );
    }
}