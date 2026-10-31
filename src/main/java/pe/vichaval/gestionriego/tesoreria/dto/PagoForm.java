package pe.vichaval.gestionriego.tesoreria.dto;

import jakarta.validation.constraints.*;

import java.math.BigDecimal;
import java.time.LocalDate;

public class PagoForm {
    @NotNull
    private Long agricultorId;
    @NotNull
    @DecimalMin("0.01")
    private BigDecimal monto;
    @NotNull
    private LocalDate fecha = LocalDate.now();
    @NotBlank
    private String concepto;
    @NotBlank
    private String comprobante;
    @NotBlank
    private String metodo = "EFECTIVO";

    public Long getAgricultorId() {
        return agricultorId;
    }

    public void setAgricultorId(Long v) {
        agricultorId = v;
    }

    public BigDecimal getMonto() {
        return monto;
    }

    public void setMonto(BigDecimal v) {
        monto = v;
    }

    public LocalDate getFecha() {
        return fecha;
    }

    public void setFecha(LocalDate v) {
        fecha = v;
    }

    public String getConcepto() {
        return concepto;
    }

    public void setConcepto(String v) {
        concepto = v;
    }

    public String getComprobante() {
        return comprobante;
    }

    public void setComprobante(String v) {
        comprobante = v;
    }

    public String getMetodo() {
        return metodo;
    }

    public void setMetodo(String v) {
        metodo = v;
    }
}
