package pe.vichaval.gestionriego.tesoreria.dto;

import jakarta.validation.constraints.*;

import java.math.*;
import java.time.*;

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

    public void setAgricultorId(Long x) {
        agricultorId = x;
    }

    public BigDecimal getMonto() {
        return monto;
    }

    public void setMonto(BigDecimal x) {
        monto = x;
    }

    public LocalDate getFecha() {
        return fecha;
    }

    public void setFecha(LocalDate x) {
        fecha = x;
    }

    public String getConcepto() {
        return concepto;
    }

    public void setConcepto(String x) {
        concepto = x;
    }

    public String getComprobante() {
        return comprobante;
    }

    public void setComprobante(String x) {
        comprobante = x;
    }

    public String getMetodo() {
        return metodo;
    }

    public void setMetodo(String x) {
        metodo = x;
    }
}