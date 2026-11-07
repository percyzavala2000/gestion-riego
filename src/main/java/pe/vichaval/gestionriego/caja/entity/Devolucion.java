package pe.vichaval.gestionriego.caja.entity;

import jakarta.persistence.*;

import java.math.BigDecimal;
import java.time.LocalDate;

import pe.vichaval.gestionriego.tesoreria.entity.Pago;
import pe.vichaval.gestionriego.usuario.entity.Usuario;

@Entity
@Table(name = "devoluciones")
public class Devolucion {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String codigo;
    @ManyToOne
    @JoinColumn(name = "pago_id")
    private Pago pago;
    private BigDecimal monto;
    private String motivo;
    private String estado = "SOLICITADA";
    private LocalDate fecha = LocalDate.now();
    @ManyToOne
    @JoinColumn(name = "usuario_id")
    private Usuario usuario;

    protected Devolucion() {
    }

    public Devolucion(String c, Pago p, BigDecimal m, String motivo, Usuario u) {
        codigo = c;
        pago = p;
        monto = m;
        this.motivo = motivo;
        usuario = u;
    }

    public void aprobar() {
        if (!"SOLICITADA".equals(estado)) throw new IllegalStateException("Solo solicitudes");
        estado = "APROBADA";
    }

    public void ejecutar() {
        if (!"APROBADA".equals(estado)) throw new IllegalStateException("Solo devoluciones aprobadas");
        estado = "EJECUTADA";
    }

    public void rechazar() {
        if (!"SOLICITADA".equals(estado)) throw new IllegalStateException("Solo solicitudes");
        estado = "RECHAZADA";
    }

    public Long getId() {
        return id;
    }

    public String getCodigo() {
        return codigo;
    }

    public Pago getPago() {
        return pago;
    }

    public BigDecimal getMonto() {
        return monto;
    }

    public String getMotivo() {
        return motivo;
    }

    public String getEstado() {
        return estado;
    }

    public LocalDate getFecha() {
        return fecha;
    }
}
