package pe.vichaval.gestionriego.usuario.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import pe.vichaval.gestionriego.usuario.enums.CodigoRol;

public class UsuarioForm {
    @NotBlank(message = "El nombre de usuario es obligatorio")
    @Pattern(regexp = "^[A-Za-z0-9._-]{4,50}$", message = "Usa entre 4 y 50 caracteres: letras, numeros, punto, guion o guion bajo")
    private String username;

    @NotBlank(message = "Los nombres son obligatorios")
    @Size(max = 100)
    private String nombres;

    @NotBlank(message = "Los apellidos son obligatorios")
    @Size(max = 120)
    private String apellidos;

    @NotNull(message = "Selecciona un rol")
    private CodigoRol rol;

    private boolean activo = true;

    @Size(max = 100)
    private String password;

    @Size(max = 100)
    private String confirmarPassword;

    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }
    public String getNombres() { return nombres; }
    public void setNombres(String nombres) { this.nombres = nombres; }
    public String getApellidos() { return apellidos; }
    public void setApellidos(String apellidos) { this.apellidos = apellidos; }
    public CodigoRol getRol() { return rol; }
    public void setRol(CodigoRol rol) { this.rol = rol; }
    public boolean isActivo() { return activo; }
    public void setActivo(boolean activo) { this.activo = activo; }
    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }
    public String getConfirmarPassword() { return confirmarPassword; }
    public void setConfirmarPassword(String confirmarPassword) { this.confirmarPassword = confirmarPassword; }
}
