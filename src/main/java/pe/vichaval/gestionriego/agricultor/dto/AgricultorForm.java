package pe.vichaval.gestionriego.agricultor.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public class AgricultorForm {

    @NotBlank(message = "El DNI es obligatorio")
    @Pattern(
            regexp = "[0-9]{8}",
            message = "El DNI debe contener exactamente 8 dígitos"
    )
    private String dni;

    @NotBlank(message = "Los nombres son obligatorios")
    @Size(
            max = 100,
            message = "Los nombres no deben superar 100 caracteres"
    )
    private String nombres;

    @NotBlank(message = "Los apellidos son obligatorios")
    @Size(
            max = 120,
            message = "Los apellidos no deben superar 120 caracteres"
    )
    private String apellidos;

    @Pattern(
            regexp = "^$|^[0-9+ -]{7,20}$",
            message = "El teléfono contiene un formato inválido"
    )
    private String telefono;

    @NotBlank(message = "El sector es obligatorio")
    private String sector;

    @NotBlank(message = "El estado es obligatorio")
    private String estado = "ACTIVO";

    public String getDni() {
        return dni;
    }

    public void setDni(String dni) {
        this.dni = dni;
    }

    public String getNombres() {
        return nombres;
    }

    public void setNombres(String nombres) {
        this.nombres = nombres;
    }

    public String getApellidos() {
        return apellidos;
    }

    public void setApellidos(String apellidos) {
        this.apellidos = apellidos;
    }

    public String getTelefono() {
        return telefono;
    }

    public void setTelefono(String telefono) {
        this.telefono = telefono;
    }

    public String getSector() {
        return sector;
    }

    public void setSector(String sector) {
        this.sector = sector;
    }

    public String getEstado() {
        return estado;
    }

    public void setEstado(String estado) {
        this.estado = estado;
    }
}