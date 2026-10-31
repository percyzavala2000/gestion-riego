package pe.vichaval.gestionriego.tesoreria.entity;

import jakarta.persistence.*;

import java.math.*;
import java.time.*;

import pe.vichaval.gestionriego.agricultor.entity.Agricultor;
import pe.vichaval.gestionriego.tesoreria.dto.PagoForm;

@Entity
@Table(name = "pagos")
public class Pago {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(nullable = false, unique = true, length = 30)
    private String codigo;
    @Column(nullable = false)
    private BigDecimal monto;
    @Column(nullable = false)
    private LocalDate fecha;
    @Column(nullable = false, length = 150)
    private String concepto;
    @Column(nullable = false, unique = true, length = 50)
    private String comprobante;
    @Column(nullable = false, length = 30)
    private String metodo;
    @Column(nullable = false, length = 30)
    private String estado;
    @Column(name = "motivo_rechazo", length = 500)
    private String motivoRechazo;
    @Column(name = "motivo_anulacion", length = 500)
    private String motivoAnulacion;
    @Column(name = "fecha_creacion", nullable = false)
    private OffsetDateTime fechaCreacion = OffsetDateTime.now();
    @Column(name = "fecha_actualizacion", nullable = false)
    private OffsetDateTime fechaActualizacion = OffsetDateTime.now();
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "agricultor_id", nullable = false)
    private Agricultor agricultor;

    protected Pago() {
    }

    public Pago(String c, PagoForm f, Agricultor a) {
        codigo = c;
        estado = "EFECTIVO".equals(f.getMetodo()) ? "CONFIRMADO" : "PENDIENTE";
        cargar(f, a);
    }

    private void cargar(PagoForm f, Agricultor a) {
        monto = f.getMonto();
        fecha = f.getFecha();
        concepto = f.getConcepto();
        comprobante = f.getComprobante();
        metodo = f.getMetodo();
        agricultor = a;
        fechaActualizacion = OffsetDateTime.now();
    }

    public void confirmar() {
        if (!"PENDIENTE".equals(estado)) throw new IllegalStateException("Solo pagos pendientes");
        estado = "CONFIRMADO";
    }

    public void rechazar(String m) {
        if (!"PENDIENTE".equals(estado)) throw new IllegalStateException("Solo pagos pendientes");
        if (m == null || m.isBlank()) throw new IllegalArgumentException("Motivo obligatorio");
        estado = "RECHAZADO";
        motivoRechazo = m;
    }

    public void anular(String m) {
        if (!"CONFIRMADO".equals(estado)) throw new IllegalStateException("Solo pagos confirmados");
        if (m == null || m.isBlank()) throw new IllegalArgumentException("Motivo obligatorio");
        estado = "ANULADO";
        motivoAnulacion = m;
    }

    public Long getId() {
        return id;
    }

    public String getCodigo() {
        return codigo;
    }

    public BigDecimal getMonto() {
        return monto;
    }

    public LocalDate getFecha() {
        return fecha;
    }

    public String getConcepto() {
        return concepto;
    }

    public String getComprobante() {
        return comprobante;
    }

    public String getMetodo() {
        return metodo;
    }

    public String getEstado() {
        return estado;
    }

    public Agricultor getAgricultor() {
        return agricultor;
    }
}
