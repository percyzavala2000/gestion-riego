package pe.vichaval.gestionriego.agricultor.dto;

import jakarta.validation.constraints.*;

public class AgricultorForm {
    @NotBlank(message = "El DNI es obligatorio")
    @Pattern(regexp = "[0-9]{8}", message = "El DNI debe contener 8 digitos")
    private String dni;
    @NotBlank(message = "Los nombres son obligatorios")
    @Size(max = 100)
    private String nombres;
    @NotBlank(message = "Los apellidos son obligatorios")
    @Size(max = 120)
    private String apellidos;
    @Pattern(regexp = "^$|^[0-9+ -]{7,20}$", message = "Telefono invalido")
    private String telefono;
    @NotBlank
    private String sector;
    @NotBlank
    private String estado = "ACTIVO";

    public String getDni() {
        return dni;
    }

    public void setDni(String v) {
        dni = v;
    }

    public String getNombres() {
        return nombres;
    }

    public void setNombres(String v) {
        nombres = v;
    }

    public String getApellidos() {
        return apellidos;
    }

    public void setApellidos(String v) {
        apellidos = v;
    }

    public String getTelefono() {
        return telefono;
    }

    public void setTelefono(String v) {
        telefono = v;
    }

    public String getSector() {
        return sector;
    }

    public void setSector(String v) {
        sector = v;
    }

    public String getEstado() {
        return estado;
    }

    public void setEstado(String v) {
        estado = v;
    }
}