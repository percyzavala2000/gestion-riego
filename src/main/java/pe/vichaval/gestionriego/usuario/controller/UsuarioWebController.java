package pe.vichaval.gestionriego.usuario.controller;

import jakarta.validation.Valid;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
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
import pe.vichaval.gestionriego.security.service.UsuarioPrincipal;
import pe.vichaval.gestionriego.usuario.dto.CambioPasswordForm;
import pe.vichaval.gestionriego.usuario.dto.UsuarioForm;
import pe.vichaval.gestionriego.usuario.enums.CodigoRol;
import pe.vichaval.gestionriego.usuario.service.UsuarioService;

@Controller
@RequestMapping("/usuarios")
@PreAuthorize("hasRole('ADMINISTRADOR')")
public class UsuarioWebController {
    private final UsuarioService service;

    public UsuarioWebController(UsuarioService service) {
        this.service = service;
    }

    @GetMapping
    public String listar(
            @RequestParam(defaultValue = "") String q,
            @RequestParam(required = false) CodigoRol rol,
            @RequestParam(defaultValue = "") String estado,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            Model model,
            @AuthenticationPrincipal UsuarioPrincipal principal
    ) {
        var pagina = service.listar(q, rol, estado, page, size);
        model.addAttribute("pagina", pagina);
        model.addAttribute("q", q);
        model.addAttribute("rol", rol);
        model.addAttribute("estado", estado);
        model.addAttribute("size", pagina.getSize());
        model.addAttribute("usuarioActualId", principal.getId());
        return "usuario/list";
    }

    @GetMapping("/nuevo")
    public String nuevo(Model model) {
        model.addAttribute("form", new UsuarioForm());
        model.addAttribute("id", null);
        cargarRoles(model);
        return "usuario/form";
    }

    @GetMapping("/{id}/editar")
    public String editar(@PathVariable Long id, Model model) {
        var usuario = service.get(id);
        UsuarioForm form = new UsuarioForm();
        form.setUsername(usuario.getUsername());
        form.setNombres(usuario.getNombres());
        form.setApellidos(usuario.getApellidos());
        form.setRol(usuario.getRol().getCodigo());
        form.setActivo(usuario.isActivo());
        model.addAttribute("form", form);
        model.addAttribute("id", id);
        cargarRoles(model);
        return "usuario/form";
    }

    @PostMapping("/guardar")
    public String guardar(
            @RequestParam(required = false) Long id,
            @Valid @ModelAttribute("form") UsuarioForm form,
            BindingResult result,
            Model model,
            @AuthenticationPrincipal UsuarioPrincipal principal,
            RedirectAttributes redirectAttributes) {
        model.addAttribute("id", id);
        if (result.hasErrors()) {
            cargarRoles(model);
            return "usuario/form";
        }
        try {
            service.guardar(form, id, principal.getId());
            redirectAttributes.addFlashAttribute("exito", id == null ? "Usuario creado correctamente" : "Usuario actualizado correctamente");
            return "redirect:/usuarios";
        } catch (RuntimeException ex) {
            result.reject("negocio", ex.getMessage());
            cargarRoles(model);
            return "usuario/form";
        }
    }

    @PostMapping("/{id}/estado")
    public String cambiarEstado(
            @PathVariable Long id,
            @AuthenticationPrincipal UsuarioPrincipal principal,
            RedirectAttributes redirectAttributes) {
        try {
            service.cambiarEstado(id, principal.getId());
            redirectAttributes.addFlashAttribute("exito", "Estado actualizado correctamente");
        } catch (RuntimeException ex) {
            redirectAttributes.addFlashAttribute("error", ex.getMessage());
        }
        return "redirect:/usuarios";
    }

    @GetMapping("/{id}/password")
    public String password(@PathVariable Long id, Model model) {
        model.addAttribute("usuario", service.get(id));
        model.addAttribute("form", new CambioPasswordForm());
        return "usuario/password";
    }

    @PostMapping("/{id}/password")
    public String cambiarPassword(
            @PathVariable Long id,
            @Valid @ModelAttribute("form") CambioPasswordForm form,
            BindingResult result,
            Model model,
            RedirectAttributes redirectAttributes) {
        var usuario = service.get(id);
        model.addAttribute("usuario", usuario);
        if (result.hasErrors()) return "usuario/password";
        try {
            service.cambiarPassword(id, form);
            redirectAttributes.addFlashAttribute("exito", "Contraseña actualizada correctamente");
            return "redirect:/usuarios";
        } catch (RuntimeException ex) {
            result.reject("negocio", ex.getMessage());
            return "usuario/password";
        }
    }

    private void cargarRoles(Model model) {
        model.addAttribute("roles", service.listarRoles());
    }
}
