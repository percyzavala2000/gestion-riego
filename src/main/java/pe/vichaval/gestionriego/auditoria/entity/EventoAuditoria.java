package pe.vichaval.gestionriego.auditoria.entity;

import jakarta.persistence.*;
import java.time.OffsetDateTime;

@Entity
@Table(name = "auditoria_eventos")
public class EventoAuditoria {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(name = "fecha_hora", nullable = false)
    private OffsetDateTime fechaHora = OffsetDateTime.now();
    @Column(name = "correlacion_id", nullable = false, length = 36)
    private String correlacionId;
    @Column(nullable = false, length = 50)
    private String usuario;
    @Column(length = 30)
    private String rol;
    @Column(nullable = false, length = 10)
    private String metodo;
    @Column(nullable = false, length = 255)
    private String ruta;
    @Column(name = "codigo_http", nullable = false)
    private int codigoHttp;
    @Column(nullable = false)
    private boolean exitoso;
    @Column(name = "direccion_ip", length = 64)
    private String direccionIp;
    @Column(name = "agente_usuario", length = 500)
    private String agenteUsuario;

    protected EventoAuditoria() {}

    public EventoAuditoria(String correlacionId, String usuario, String rol, String metodo,
            String ruta, int codigoHttp, String direccionIp, String agenteUsuario) {
        this.correlacionId = limitar(correlacionId, 36);
        this.usuario = limitar(usuario == null ? "ANONIMO" : usuario, 50);
        this.rol = limitar(rol, 30);
        this.metodo = limitar(metodo, 10);
        this.ruta = limitar(ruta, 255);
        this.codigoHttp = codigoHttp;
        this.exitoso = codigoHttp < 400;
        this.direccionIp = limitar(direccionIp, 64);
        this.agenteUsuario = limitar(agenteUsuario, 500);
    }

    private static String limitar(String valor, int maximo) {
        if (valor == null) return null;
        return valor.length() <= maximo ? valor : valor.substring(0, maximo);
    }

    public Long getId() { return id; }
    public OffsetDateTime getFechaHora() { return fechaHora; }
    public String getCorrelacionId() { return correlacionId; }
    public String getUsuario() { return usuario; }
    public String getRol() { return rol; }
    public String getMetodo() { return metodo; }
    public String getRuta() { return ruta; }
    public int getCodigoHttp() { return codigoHttp; }
    public boolean isExitoso() { return exitoso; }
    public String getDireccionIp() { return direccionIp; }
}
