package pe.vichaval.gestionriego.usuario.service;

import java.util.Comparator;
import java.util.List;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pe.vichaval.gestionriego.usuario.dto.CambioPasswordForm;
import pe.vichaval.gestionriego.usuario.dto.UsuarioForm;
import pe.vichaval.gestionriego.usuario.entity.Rol;
import pe.vichaval.gestionriego.usuario.entity.Usuario;
import pe.vichaval.gestionriego.usuario.enums.CodigoRol;
import pe.vichaval.gestionriego.usuario.repository.RolRepository;
import pe.vichaval.gestionriego.usuario.repository.UsuarioRepository;

@Service
@Transactional
public class UsuarioService {
    private final UsuarioRepository usuarios;
    private final RolRepository roles;
    private final PasswordEncoder passwordEncoder;

    public UsuarioService(UsuarioRepository usuarios, RolRepository roles, PasswordEncoder passwordEncoder) {
        this.usuarios = usuarios;
        this.roles = roles;
        this.passwordEncoder = passwordEncoder;
    }

    @Transactional(readOnly = true)
    public List<Usuario> listar() {
        return usuarios.findAll().stream()
                .sorted(Comparator.comparing(Usuario::getApellidos).thenComparing(Usuario::getNombres))
                .toList();
    }

    @Transactional(readOnly = true)
    public List<Rol> listarRoles() {
        return roles.findAllByOrderByNombreAsc();
    }

    @Transactional(readOnly = true)
    public Usuario get(Long id) {
        return usuarios.findById(id).orElseThrow(() -> new IllegalArgumentException("Usuario no encontrado"));
    }

    public void guardar(UsuarioForm form, Long id, Long usuarioActualId) {
        String username = form.getUsername().trim();
        validarUsername(username, id);
        Rol rol = roles.findByCodigo(form.getRol()).orElseThrow(() -> new IllegalArgumentException("Rol no encontrado"));

        if (id == null) {
            validarPasswordCreacion(form);
            Usuario nuevo = new Usuario(
                    username,
                    passwordEncoder.encode(form.getPassword()),
                    form.getNombres().trim(),
                    form.getApellidos().trim(),
                    rol
            );
            if (!form.isActivo()) nuevo.desactivar();
            usuarios.save(nuevo);
            return;
        }

        Usuario usuario = get(id);
        validarCambioSensible(usuario, form.getRol(), form.isActivo(), usuarioActualId);
        usuario.actualizar(username, form.getNombres().trim(), form.getApellidos().trim(), rol, form.isActivo());
    }

    public void cambiarEstado(Long id, Long usuarioActualId) {
        Usuario usuario = get(id);
        if (usuario.getId().equals(usuarioActualId)) {
            throw new IllegalStateException("No puedes desactivar tu propia cuenta");
        }
        if (usuario.isActivo()) {
            validarUltimoAdministrador(usuario);
            usuario.desactivar();
        } else {
            usuario.activar();
        }
    }

    public void cambiarPassword(Long id, CambioPasswordForm form) {
        if (!form.getNuevaPassword().equals(form.getConfirmarPassword())) {
            throw new IllegalArgumentException("Las contraseñas no coinciden");
        }
        get(id).cambiarPassword(passwordEncoder.encode(form.getNuevaPassword()));
    }

    private void validarUsername(String username, Long id) {
        boolean duplicado = id == null
                ? usuarios.existsByUsernameIgnoreCase(username)
                : usuarios.existsByUsernameIgnoreCaseAndIdNot(username, id);
        if (duplicado) throw new IllegalArgumentException("El nombre de usuario ya está registrado");
    }

    private void validarPasswordCreacion(UsuarioForm form) {
        if (form.getPassword() == null || form.getPassword().length() < 8) {
            throw new IllegalArgumentException("La contraseña debe tener al menos 8 caracteres");
        }
        if (!form.getPassword().equals(form.getConfirmarPassword())) {
            throw new IllegalArgumentException("Las contraseñas no coinciden");
        }
    }

    private void validarCambioSensible(Usuario usuario, CodigoRol nuevoRol, boolean nuevoActivo, Long usuarioActualId) {
        if (usuario.getId().equals(usuarioActualId) && !nuevoActivo) {
            throw new IllegalStateException("No puedes desactivar tu propia cuenta");
        }
        boolean dejaDeSerAdministrador = usuario.getRol().getCodigo() == CodigoRol.ADMINISTRADOR
                && (nuevoRol != CodigoRol.ADMINISTRADOR || !nuevoActivo);
        if (dejaDeSerAdministrador) validarUltimoAdministrador(usuario);
    }

    private void validarUltimoAdministrador(Usuario usuario) {
        if (usuario.getRol().getCodigo() == CodigoRol.ADMINISTRADOR
                && usuarios.countByRolCodigoAndActivoTrue(CodigoRol.ADMINISTRADOR) <= 1) {
            throw new IllegalStateException("No se puede desactivar o cambiar el rol del último administrador activo");
        }
    }
}
