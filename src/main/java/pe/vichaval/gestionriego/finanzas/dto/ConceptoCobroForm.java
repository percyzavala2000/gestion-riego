package pe.vichaval.gestionriego.finanzas.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public class ConceptoCobroForm {

    @NotBlank(
            message = "El código es obligatorio."
    )
    @Size(
            min = 2,
            max = 30,
            message = "El código debe tener entre 2 y 30 caracteres."
    )
    @Pattern(
            regexp = "^[A-Za-z0-9_-]+$",
            message = "El código solo puede contener letras, números, guion o guion bajo."
    )
    private String codigo;

    @NotBlank(
            message = "El nombre es obligatorio."
    )
    @Size(
            min = 3,
            max = 120,
            message = "El nombre debe tener entre 3 y 120 caracteres."
    )
    private String nombre;

    @Size(
            max = 500,
            message = "La descripción no puede superar los 500 caracteres."
    )
    private String descripcion;

    private boolean activo = true;

    public String getCodigo() {
        return codigo;
    }

    public void setCodigo(String codigo) {
        this.codigo = codigo;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(
            String descripcion
    ) {
        this.descripcion = descripcion;
    }

    public boolean isActivo() {
        return activo;
    }

    public void setActivo(boolean activo) {
        this.activo = activo;
    }
}
