package pe.vichaval.gestionriego.parcela.dto;

import jakarta.validation.constraints.*;

import java.math.BigDecimal;

public class ParcelaForm {
    @NotBlank
    private String codigo;
    @NotBlank
    private String nombre;
    @NotNull
    @DecimalMin("0.01")
    private BigDecimal hectareas;
    private String cultivo;
    @NotNull
    private Long agricultorId;
    @NotBlank
    private String estado = "ACTIVA";

    public String getCodigo() {
        return codigo;
    }

    public void setCodigo(String v) {
        codigo = v;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String v) {
        nombre = v;
    }

    public BigDecimal getHectareas() {
        return hectareas;
    }

    public void setHectareas(BigDecimal v) {
        hectareas = v;
    }

    public String getCultivo() {
        return cultivo;
    }

    public void setCultivo(String v) {
        cultivo = v;
    }

    public Long getAgricultorId() {
        return agricultorId;
    }

    public void setAgricultorId(Long v) {
        agricultorId = v;
    }

    public String getEstado() {
        return estado;
    }

    public void setEstado(String v) {
        estado = v;
    }
}
