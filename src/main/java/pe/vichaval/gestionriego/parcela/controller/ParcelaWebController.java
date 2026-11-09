package pe.vichaval.gestionriego.parcela.controller;

import jakarta.validation.Valid;

import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.security.access.prepost.PreAuthorize;
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
import pe.vichaval.gestionriego.parcela.dto.ParcelaForm;
import pe.vichaval.gestionriego.parcela.repository.ParcelaRepository;
import pe.vichaval.gestionriego.parcela.service.ParcelaService;
import pe.vichaval.gestionriego.riego.repository.TurnoRiegoRepository;

@Controller
@RequestMapping("/parcelas")
public class ParcelaWebController {

    private final ParcelaRepository parcelaRepository;
    private final AgricultorRepository agricultorRepository;
    private final ParcelaService parcelaService;
    private final TurnoRiegoRepository turnoRiegoRepository;

    public ParcelaWebController(
            ParcelaRepository parcelaRepository,
            AgricultorRepository agricultorRepository,
            ParcelaService parcelaService,
            TurnoRiegoRepository turnoRiegoRepository
    ) {
        this.parcelaRepository = parcelaRepository;
        this.agricultorRepository = agricultorRepository;
        this.parcelaService = parcelaService;
        this.turnoRiegoRepository = turnoRiegoRepository;
    }

    @GetMapping
    public String listar(
            @RequestParam(defaultValue = "") String q,
            @RequestParam(defaultValue = "") String estado,
            @RequestParam(defaultValue = "") String sector,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            Model model
    ) {
        var pagina = parcelaService.listar(q, estado, sector, page, size);
        Set<Long> parcelasConTurnos = pagina.getContent().stream()
                .filter(parcela -> turnoRiegoRepository.existsByParcelaId(parcela.getId()))
                .map(parcela -> parcela.getId())
                .collect(Collectors.toSet());

        model.addAttribute("pagina", pagina);
        model.addAttribute("parcelasConTurnos", parcelasConTurnos);
        model.addAttribute("q", q);
        model.addAttribute("estado", estado);
        model.addAttribute("sector", sector);
        model.addAttribute("size", pagina.getSize());
        return "parcela/list";
    }

    @GetMapping("/nueva")
    public String mostrarFormularioNuevo(Model model) {

        model.addAttribute(
                "form",
                new ParcelaForm()
        );

        model.addAttribute(
                "id",
                null
        );

        cargarAgricultoresActivos(model);

        return "parcela/form";
    }

    @GetMapping("/{id}/editar")
    public String mostrarFormularioEditar(
            @PathVariable Long id,
            Model model,
            RedirectAttributes redirectAttributes
    ) {
        try {
            var parcela = parcelaService.get(id);

            ParcelaForm form = new ParcelaForm();

            form.setCodigo(
                    parcela.getCodigo()
            );

            form.setNombre(
                    parcela.getNombre()
            );

            form.setHectareas(
                    parcela.getHectareas()
            );

            form.setCultivo(
                    parcela.getCultivo()
            );

            form.setAgricultorId(
                    parcela.getAgricultor().getId()
            );

            form.setEstado(
                    parcela.getEstado()
            );

            model.addAttribute(
                    "form",
                    form
            );

            model.addAttribute(
                    "id",
                    id
            );

            cargarAgricultoresActivos(model);

            return "parcela/form";

        } catch (RuntimeException exception) {

            redirectAttributes.addFlashAttribute(
                    "error",
                    exception.getMessage()
            );

            return "redirect:/parcelas";
        }
    }

    @PostMapping("/guardar")
    public String guardar(
            @RequestParam(required = false) Long id,
            @Valid @ModelAttribute("form") ParcelaForm form,
            BindingResult bindingResult,
            Model model,
            RedirectAttributes redirectAttributes
    ) {
        model.addAttribute(
                "id",
                id
        );

        if (bindingResult.hasErrors()) {

            cargarAgricultoresActivos(model);

            return "parcela/form";
        }

        try {
            parcelaService.guardar(
                    form,
                    id
            );

            String mensaje = id == null
                    ? "Parcela registrada correctamente."
                    : "Parcela actualizada correctamente.";

            redirectAttributes.addFlashAttribute(
                    "exito",
                    mensaje
            );

            return "redirect:/parcelas";

        } catch (RuntimeException exception) {

            bindingResult.reject(
                    "errorNegocio",
                    exception.getMessage()
            );

            cargarAgricultoresActivos(model);

            return "parcela/form";
        }
    }

    @PreAuthorize("hasRole('ADMINISTRADOR')")
    @PostMapping("/{id}/eliminar")
    public String eliminar(
            @PathVariable Long id,
            RedirectAttributes redirectAttributes
    ) {
        try {
            parcelaService.eliminar(id);

            redirectAttributes.addFlashAttribute(
                    "exito",
                    "Parcela eliminada correctamente."
            );

        } catch (RuntimeException exception) {

            redirectAttributes.addFlashAttribute(
                    "error",
                    exception.getMessage()
            );
        }

        return "redirect:/parcelas";
    }

    private void cargarAgricultoresActivos(
            Model model
    ) {
        var agricultoresActivos = agricultorRepository
                .findAll()
                .stream()
                .filter(agricultor ->
                        "ACTIVO".equals(
                                agricultor.getEstado()
                        )
                )
                .toList();

        model.addAttribute(
                "agricultores",
                agricultoresActivos
        );
    }
}