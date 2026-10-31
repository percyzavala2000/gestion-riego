package pe.vichaval.gestionriego.security.service;

import java.util.Collection;
import java.util.List;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import pe.vichaval.gestionriego.usuario.entity.Usuario;

public class UsuarioPrincipal implements UserDetails {

    private final Long id;
    private final String username;
    private final String password;
    private final String nombres;
    private final String apellidos;
    private final String rol;
    private final boolean activo;

    public UsuarioPrincipal(Usuario usuario) {
        this.id = usuario.getId();
        this.username = usuario.getUsername();
        this.password = usuario.getPasswordHash();
        this.nombres = usuario.getNombres();
        this.apellidos = usuario.getApellidos();
        this.rol = usuario.getRol().getCodigo().name();
        this.activo = usuario.isActivo();
    }

    public Long getId() {
        return id;
    }

    public String getNombres() {
        return nombres;
    }

    public String getApellidos() {
        return apellidos;
    }

    public String getNombreCompleto() {
        return nombres + " " + apellidos;
    }

    public String getRol() {
        return rol;
    }

    public String getRolLegible() {
        return switch (rol) {
            case "ADMINISTRADOR" -> "Administrador";
            case "TESORERIA" -> "Tesorería";
            case "OPERADOR" -> "Operador de riego";
            default -> rol;
        };
    }

    public String getIniciales() {
        String inicialNombre = nombres == null || nombres.isBlank()
                ? ""
                : nombres.substring(0, 1).toUpperCase();

        String inicialApellido = apellidos == null || apellidos.isBlank()
                ? ""
                : apellidos.substring(0, 1).toUpperCase();

        return inicialNombre + inicialApellido;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return List.of(
                new SimpleGrantedAuthority("ROLE_" + rol)
        );
    }

    @Override
    public String getPassword() {
        return password;
    }

    @Override
    public String getUsername() {
        return username;
    }

    @Override
    public boolean isAccountNonExpired() {
        return activo;
    }

    @Override
    public boolean isAccountNonLocked() {
        return activo;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return activo;
    }

    @Override
    public boolean isEnabled() {
        return activo;
    }
}