package pe.vichaval.gestionriego.caja.dto;

import jakarta.validation.constraints.*;

import java.math.BigDecimal;
import java.time.LocalDate;

public class CierreCajaForm {
    @NotNull
    private LocalDate fecha = LocalDate.now();
    @NotNull
    private BigDecimal saldoInicial = BigDecimal.ZERO;
    @NotNull
    private BigDecimal saldoContado = BigDecimal.ZERO;
    private String observaciones;

    public LocalDate getFecha() {
        return fecha;
    }

    public void setFecha(LocalDate v) {
        fecha = v;
    }

    public BigDecimal getSaldoInicial() {
        return saldoInicial;
    }

    public void setSaldoInicial(BigDecimal v) {
        saldoInicial = v;
    }

    public BigDecimal getSaldoContado() {
        return saldoContado;
    }

    public void setSaldoContado(BigDecimal v) {
        saldoContado = v;
    }

    public String getObservaciones() {
        return observaciones;
    }

    public void setObservaciones(String v) {
        observaciones = v;
    }
}
