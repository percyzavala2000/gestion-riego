package pe.vichaval.gestionriego.finanzas.entity;

import jakarta.persistence.*;

import java.math.BigDecimal;
import java.time.*;

import pe.vichaval.gestionriego.agricultor.entity.Agricultor;
import pe.vichaval.gestionriego.finanzas.dto.CuentaCobrarForm;

@Entity
@Table(name = "cuentas_cobrar")
public class CuentaCobrar {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(nullable = false, unique = true, length = 30)
    private String codigo;
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "agricultor_id")
    private Agricultor agricultor;
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "concepto_id")
    private ConceptoCobro concepto;
    @Column(name = "monto_total", nullable = false)
    private BigDecimal montoTotal;
    @Column(name = "monto_pagado", nullable = false)
    private BigDecimal montoPagado = BigDecimal.ZERO;
    @Column(name = "fecha_emision", nullable = false)
    private LocalDate fechaEmision;
    @Column(name = "fecha_vencimiento", nullable = false)
    private LocalDate fechaVencimiento;
    @Column(nullable = false, length = 30)
    private String estado = "PENDIENTE";
    @Column(length = 500)
    private String observaciones;
    @Column(name = "motivo_anulacion", length = 500)
    private String motivoAnulacion;

    protected CuentaCobrar() {
    }

    public CuentaCobrar(String codigo, CuentaCobrarForm f, Agricultor a, ConceptoCobro c) {
        this.codigo = codigo;
        agricultor = a;
        concepto = c;
        montoTotal = f.getMontoTotal();
        fechaEmision = f.getFechaEmision();
        fechaVencimiento = f.getFechaVencimiento();
        observaciones = f.getObservaciones();
    }

    public BigDecimal getSaldoPendiente() {
        return montoTotal.subtract(montoPagado);
    }

    public void aplicarPago(BigDecimal monto) {
        if (monto.compareTo(getSaldoPendiente()) > 0)
            throw new IllegalStateException("El pago supera el saldo pendiente");
        montoPagado = montoPagado.add(monto);
        recalcular();
    }

    public void revertirPago(BigDecimal monto) {
        montoPagado = montoPagado.subtract(monto);
        if (montoPagado.signum() < 0) montoPagado = BigDecimal.ZERO;
        recalcular();
    }

    public void actualizarVencida() {
        if (("PENDIENTE".equals(estado) || "PAGO_PARCIAL".equals(estado)) && fechaVencimiento.isBefore(LocalDate.now()))
            estado = "VENCIDA";
    }

    public void anular(String motivo) {
        if ("PAGADA".equals(estado)) throw new IllegalStateException("No se puede anular una cuenta pagada");
        if (motivo == null || motivo.isBlank()) throw new IllegalArgumentException("Motivo obligatorio");
        estado = "ANULADA";
        motivoAnulacion = motivo;
    }

    private void recalcular() {
        if (getSaldoPendiente().signum() == 0) estado = "PAGADA";
        else if (montoPagado.signum() > 0)
            estado = fechaVencimiento.isBefore(LocalDate.now()) ? "VENCIDA" : "PAGO_PARCIAL";
        else estado = fechaVencimiento.isBefore(LocalDate.now()) ? "VENCIDA" : "PENDIENTE";
    }

    public Long getId() {
        return id;
    }

    public String getCodigo() {
        return codigo;
    }

    public Agricultor getAgricultor() {
        return agricultor;
    }

    public ConceptoCobro getConcepto() {
        return concepto;
    }

    public BigDecimal getMontoTotal() {
        return montoTotal;
    }

    public BigDecimal getMontoPagado() {
        return montoPagado;
    }

    public LocalDate getFechaEmision() {
        return fechaEmision;
    }

    public LocalDate getFechaVencimiento() {
        return fechaVencimiento;
    }

    public String getEstado() {
        return estado;
    }

    public String getObservaciones() {
        return observaciones;
    }
}
