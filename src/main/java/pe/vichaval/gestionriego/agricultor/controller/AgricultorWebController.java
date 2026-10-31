package pe.vichaval.gestionriego.agricultor.controller;

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
import pe.vichaval.gestionriego.agricultor.dto.AgricultorForm;
import pe.vichaval.gestionriego.agricultor.service.AgricultorService;

@Controller
@RequestMapping("/agricultores")
public class AgricultorWebController {

    private final AgricultorService agricultorService;

    public AgricultorWebController(
            AgricultorService agricultorService
    ) {
        this.agricultorService = agricultorService;
    }

    @GetMapping
    public String listar(
            @RequestParam(defaultValue = "") String q,
            @RequestParam(defaultValue = "0") int page,
            Model model
    ) {
        model.addAttribute(
                "pagina",
                agricultorService.listar(q, page)
        );

        model.addAttribute("q", q);

        return "agricultor/list";
    }

    @GetMapping("/nuevo")
    public String mostrarFormularioNuevo(Model model) {

        model.addAttribute(
                "form",
                new AgricultorForm()
        );

        model.addAttribute("id", null);

        return "agricultor/form";
    }

    @GetMapping("/{id}/editar")
    public String mostrarFormularioEditar(
            @PathVariable Long id,
            Model model
    ) {
        var agricultor = agricultorService.get(id);

        AgricultorForm form = new AgricultorForm();

        form.setDni(agricultor.getDni());
        form.setNombres(agricultor.getNombres());
        form.setApellidos(agricultor.getApellidos());
        form.setTelefono(agricultor.getTelefono());
        form.setSector(agricultor.getSector());
        form.setEstado(agricultor.getEstado());

        model.addAttribute("form", form);
        model.addAttribute("id", id);

        return "agricultor/form";
    }

    @PostMapping("/guardar")
    public String guardar(
            @RequestParam(required = false) Long id,
            @Valid @ModelAttribute("form") AgricultorForm form,
            BindingResult bindingResult,
            Model model,
            RedirectAttributes redirectAttributes
    ) {
        model.addAttribute("id", id);

        if (bindingResult.hasErrors()) {
            return "agricultor/form";
        }

        try {
            agricultorService.save(form, id);

            if (id == null) {
                redirectAttributes.addFlashAttribute(
                        "mensajeExito",
                        "Agricultor registrado correctamente."
                );
            } else {
                redirectAttributes.addFlashAttribute(
                        "mensajeExito",
                        "Agricultor actualizado correctamente."
                );
            }

            return "redirect:/agricultores";

        } catch (IllegalArgumentException exception) {

            bindingResult.reject(
                    "errorNegocio",
                    exception.getMessage()
            );

            return "agricultor/form";
        }
    }
}