package pe.vichaval.gestionriego.security.agricultor.entity;

import jakarta.persistence.*;
import java.time.OffsetDateTime;

@Entity
@Table(name = "sesiones_agricultor")
public class SesionAgricultor {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY) private Long id;
    @ManyToOne(fetch = FetchType.LAZY, optional = false) @JoinColumn(name = "cuenta_id", nullable = false) private CuentaAgricultor cuenta;
    @Column(name = "token_hash", nullable = false, unique = true, length = 64) private String tokenHash;
    @Column(name = "access_token_hash", unique = true, length = 64) private String accessTokenHash;
    @Column(nullable = false, unique = true, length = 64) private String identificador;
    @Column(length = 200) private String dispositivo;
    @Column(name = "direccion_ip", length = 64) private String direccionIp;
    @Column(name = "fecha_creacion", nullable = false) private OffsetDateTime fechaCreacion = OffsetDateTime.now();
    @Column(name = "fecha_expiracion", nullable = false) private OffsetDateTime fechaExpiracion;
    @Column(name = "access_expiracion") private OffsetDateTime accessExpiracion;
    @Column(name = "fecha_ultimo_uso") private OffsetDateTime fechaUltimoUso;
    @Column(nullable = false) private boolean revocado;
    @Column(name = "fecha_revocacion") private OffsetDateTime fechaRevocacion;
    @Column(name = "reemplazado_por", length = 64) private String reemplazadoPor;
    protected SesionAgricultor() {}
    public SesionAgricultor(CuentaAgricultor cuenta, String refreshHash, String accessHash, String identificador,
            String dispositivo, String direccionIp, OffsetDateTime refreshExpira, OffsetDateTime accessExpira) {
        this.cuenta=cuenta; this.tokenHash=refreshHash; this.accessTokenHash=accessHash; this.identificador=identificador;
        this.dispositivo=limitar(dispositivo,200); this.direccionIp=limitar(direccionIp,64);
        this.fechaExpiracion=refreshExpira; this.accessExpiracion=accessExpira;
    }
    public boolean refreshUtilizable(OffsetDateTime ahora){ return !revocado && fechaExpiracion.isAfter(ahora); }
    public boolean accessUtilizable(OffsetDateTime ahora){ return !revocado && accessExpiracion!=null && accessExpiracion.isAfter(ahora); }
    public void registrarUso(){ fechaUltimoUso=OffsetDateTime.now(); }
    public void revocar(String reemplazo){ revocado=true; fechaRevocacion=OffsetDateTime.now(); reemplazadoPor=reemplazo; }
    private static String limitar(String v,int m){ return v==null?null:v.substring(0,Math.min(v.length(),m)); }
    public Long getId(){return id;} public CuentaAgricultor getCuenta(){return cuenta;} public String getTokenHash(){return tokenHash;}
    public String getAccessTokenHash(){return accessTokenHash;} public String getIdentificador(){return identificador;}
    public OffsetDateTime getFechaExpiracion(){return fechaExpiracion;} public OffsetDateTime getAccessExpiracion(){return accessExpiracion;}
    public boolean isRevocado(){return revocado;}
}
