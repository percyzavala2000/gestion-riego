package pe.vichaval.gestionriego.caja.entity;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.OffsetDateTime;
import pe.vichaval.gestionriego.usuario.entity.Usuario;

@Entity @Table(name="cierres_caja")
public class CierreCaja {
    @Id @GeneratedValue(strategy=GenerationType.IDENTITY) private Long id;
    private LocalDate fecha;
    @Column(name="saldo_inicial") private BigDecimal saldoInicial;
    private BigDecimal ingresos; private BigDecimal egresos; private BigDecimal devoluciones;
    @Column(name="saldo_teorico") private BigDecimal saldoTeorico;
    @Column(name="saldo_contado") private BigDecimal saldoContado;
    private BigDecimal diferencia; private String observaciones; private String estado="CERRADO";
    @ManyToOne @JoinColumn(name="usuario_id") private Usuario usuario;
    @Column(name="fecha_cierre") private OffsetDateTime fechaCierre=OffsetDateTime.now();
    @Column(name="motivo_reapertura") private String motivoReapertura;
    @Column(name="fecha_reapertura") private OffsetDateTime fechaReapertura;
    @ManyToOne @JoinColumn(name="usuario_reapertura_id") private Usuario usuarioReapertura;
    protected CierreCaja(){}
    public CierreCaja(LocalDate f,BigDecimal si,BigDecimal i,BigDecimal e,BigDecimal d,BigDecimal sc,String o,Usuario u){fecha=f;cerrar(si,i,e,d,sc,o,u);}
    public void reabrir(String motivo,Usuario usuario){if(!"CERRADO".equals(estado))throw new IllegalStateException("El cierre ya está reabierto");if(motivo==null||motivo.isBlank())throw new IllegalArgumentException("El motivo de reapertura es obligatorio");estado="REABIERTO";motivoReapertura=motivo.trim();fechaReapertura=OffsetDateTime.now();usuarioReapertura=usuario;}
    public void cerrar(BigDecimal si,BigDecimal i,BigDecimal e,BigDecimal d,BigDecimal sc,String o,Usuario u){if(si==null||sc==null)throw new IllegalArgumentException("Los saldos son obligatorios");saldoInicial=si;ingresos=i;egresos=e;devoluciones=d;saldoTeorico=si.add(i).subtract(e).subtract(d);saldoContado=sc;diferencia=sc.subtract(saldoTeorico);observaciones=o;usuario=u;estado="CERRADO";fechaCierre=OffsetDateTime.now();}
    public Long getId(){return id;} public LocalDate getFecha(){return fecha;} public BigDecimal getSaldoTeorico(){return saldoTeorico;} public BigDecimal getSaldoContado(){return saldoContado;} public BigDecimal getDiferencia(){return diferencia;} public String getEstado(){return estado;} public String getMotivoReapertura(){return motivoReapertura;}
}
