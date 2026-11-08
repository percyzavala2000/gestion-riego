package pe.vichaval.gestionriego.security.agricultor.entity;

import jakarta.persistence.*;
import java.time.OffsetDateTime;
import pe.vichaval.gestionriego.agricultor.entity.Agricultor;

@Entity
@Table(name = "cuentas_agricultor")
public class CuentaAgricultor {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @OneToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "agricultor_id", nullable = false, unique = true)
    private Agricultor agricultor;
    @Column(nullable = false, unique = true, length = 80)
    private String usuario;
    @Column(name = "password_hash", nullable = false, length = 255)
    private String passwordHash;
    @Column(nullable = false)
    private boolean activo = true;
    @Column(name = "intentos_fallidos", nullable = false)
    private int intentosFallidos;
    @Column(name = "bloqueado_hasta")
    private OffsetDateTime bloqueadoHasta;
    @Column(name = "ultimo_acceso")
    private OffsetDateTime ultimoAcceso;
    @Column(name = "fecha_creacion", nullable = false)
    private OffsetDateTime fechaCreacion = OffsetDateTime.now();
    @Column(name = "fecha_actualizacion", nullable = false)
    private OffsetDateTime fechaActualizacion = OffsetDateTime.now();

    protected CuentaAgricultor() {}
    public CuentaAgricultor(Agricultor agricultor, String usuario, String passwordHash) {
        this.agricultor = agricultor;
        this.usuario = usuario.trim().toLowerCase();
        this.passwordHash = passwordHash;
    }
    public void registrarFallo(int maximo, OffsetDateTime bloqueadoHasta) {
        intentosFallidos++;
        if (intentosFallidos >= maximo) this.bloqueadoHasta = bloqueadoHasta;
        fechaActualizacion = OffsetDateTime.now();
    }
    public void registrarAcceso() {
        intentosFallidos = 0; bloqueadoHasta = null; ultimoAcceso = OffsetDateTime.now(); fechaActualizacion = ultimoAcceso;
    }
    public boolean estaBloqueada(OffsetDateTime ahora) { return bloqueadoHasta != null && bloqueadoHasta.isAfter(ahora); }
    public void cambiarPassword(String hash) { passwordHash = hash; fechaActualizacion = OffsetDateTime.now(); }
    public void activar() { activo = true; fechaActualizacion = OffsetDateTime.now(); }
    public void desactivar() { activo = false; fechaActualizacion = OffsetDateTime.now(); }
    public Long getId() { return id; }
    public Agricultor getAgricultor() { return agricultor; }
    public String getUsuario() { return usuario; }
    public String getPasswordHash() { return passwordHash; }
    public boolean isActivo() { return activo; }
    public int getIntentosFallidos() { return intentosFallidos; }
    public OffsetDateTime getBloqueadoHasta() { return bloqueadoHasta; }
    public OffsetDateTime getUltimoAcceso() { return ultimoAcceso; }
}
