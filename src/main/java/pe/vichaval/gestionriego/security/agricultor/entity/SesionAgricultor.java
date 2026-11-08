package pe.vichaval.gestionriego.security.agricultor.entity;

import jakarta.persistence.*;
import java.time.OffsetDateTime;

@Entity
@Table(name = "sesiones_agricultor")
public class SesionAgricultor {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "cuenta_id", nullable = false)
    private CuentaAgricultor cuenta;
    @Column(name = "token_hash", nullable = false, unique = true, length = 64)
    private String tokenHash;
    @Column(nullable = false, unique = true, length = 64)
    private String identificador;
    @Column(length = 200)
    private String dispositivo;
    @Column(name = "direccion_ip", length = 64)
    private String direccionIp;
    @Column(name = "fecha_creacion", nullable = false)
    private OffsetDateTime fechaCreacion = OffsetDateTime.now();
    @Column(name = "fecha_expiracion", nullable = false)
    private OffsetDateTime fechaExpiracion;
    @Column(name = "fecha_ultimo_uso")
    private OffsetDateTime fechaUltimoUso;
    @Column(nullable = false)
    private boolean revocado;
    @Column(name = "fecha_revocacion")
    private OffsetDateTime fechaRevocacion;
    @Column(name = "reemplazado_por", length = 64)
    private String reemplazadoPor;

    protected SesionAgricultor() {}
    public SesionAgricultor(CuentaAgricultor cuenta, String tokenHash, String identificador,
            String dispositivo, String direccionIp, OffsetDateTime fechaExpiracion) {
        this.cuenta = cuenta; this.tokenHash = tokenHash; this.identificador = identificador;
        this.dispositivo = limitar(dispositivo, 200); this.direccionIp = limitar(direccionIp, 64);
        this.fechaExpiracion = fechaExpiracion;
    }
    public boolean esUtilizable(OffsetDateTime ahora) { return !revocado && fechaExpiracion.isAfter(ahora); }
    public void registrarUso() { fechaUltimoUso = OffsetDateTime.now(); }
    public void revocar(String reemplazo) { revocado = true; fechaRevocacion = OffsetDateTime.now(); reemplazadoPor = reemplazo; }
    private static String limitar(String valor, int max) { return valor == null ? null : valor.substring(0, Math.min(valor.length(), max)); }
    public Long getId() { return id; }
    public CuentaAgricultor getCuenta() { return cuenta; }
    public String getTokenHash() { return tokenHash; }
    public String getIdentificador() { return identificador; }
    public OffsetDateTime getFechaExpiracion() { return fechaExpiracion; }
    public boolean isRevocado() { return revocado; }
}
