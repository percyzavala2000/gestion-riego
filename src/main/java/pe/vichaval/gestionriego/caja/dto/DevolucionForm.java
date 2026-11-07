package pe.vichaval.gestionriego.caja.dto;

import jakarta.validation.constraints.*;

import java.math.BigDecimal;

public class DevolucionForm {
    @NotNull
    private Long pagoId;
    @NotNull
    @DecimalMin("0.01")
    private BigDecimal monto;
    @NotBlank
    private String motivo;

    public Long getPagoId() {
        return pagoId;
    }

    public void setPagoId(Long v) {
        pagoId = v;
    }

    public BigDecimal getMonto() {
        return monto;
    }

    public void setMonto(BigDecimal v) {
        monto = v;
    }

    public String getMotivo() {
        return motivo;
    }

    public void setMotivo(String v) {
        motivo = v;
    }
}
