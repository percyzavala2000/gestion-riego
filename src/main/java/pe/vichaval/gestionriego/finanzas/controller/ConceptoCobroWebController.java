package pe.vichaval.gestionriego.finanzas.controller;

import jakarta.validation.Valid;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import pe.vichaval.gestionriego.finanzas.dto.ConceptoCobroForm;
import pe.vichaval.gestionriego.finanzas.service.FinanzasService;

@Controller
@RequestMapping("/tesoreria/conceptos")
public class ConceptoCobroWebController {

    private final FinanzasService finanzasService;

    public ConceptoCobroWebController(
            FinanzasService finanzasService
    ) {
        this.finanzasService = finanzasService;
    }

    @GetMapping
    public String mostrarConceptos(Model model) {

        if (!model.containsAttribute("form")) {
            model.addAttribute(
                    "form",
                    new ConceptoCobroForm()
            );
        }

        cargarConceptos(model);

        return "finanzas/conceptos";
    }

    @PostMapping
    public String guardarConcepto(
            @Valid
            @ModelAttribute("form")
            ConceptoCobroForm form,

            BindingResult bindingResult,

            Model model,

            RedirectAttributes redirectAttributes
    ) {
        /*
         * Normaliza el código antes de validarlo y persistirlo.
         */
        if (form.getCodigo() != null) {
            form.setCodigo(
                    form.getCodigo()
                            .trim()
                            .toUpperCase()
            );
        }

        if (form.getNombre() != null) {
            form.setNombre(
                    form.getNombre().trim()
            );
        }

        if (
                form.getDescripcion() != null
                        && form.getDescripcion().isBlank()
        ) {
            form.setDescripcion(null);
        }

        /*
         * Si existen errores de validación, se vuelve a mostrar
         * el formulario junto con la tabla de conceptos.
         */
        if (bindingResult.hasErrors()) {

            cargarConceptos(model);

            return "finanzas/conceptos";
        }

        try {
            finanzasService.crearConcepto(form);

            redirectAttributes.addFlashAttribute(
                    "exito",
                    "Concepto de cobro creado correctamente."
            );

            return "redirect:/tesoreria/conceptos";

        } catch (RuntimeException exception) {

            bindingResult.reject(
                    "errorNegocio",
                    exception.getMessage()
            );

            cargarConceptos(model);

            return "finanzas/conceptos";
        }
    }

    private void cargarConceptos(Model model) {

        model.addAttribute(
                "conceptos",
                finanzasService.listarConceptos()
        );
    }
}