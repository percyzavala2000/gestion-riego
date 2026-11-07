package pe.vichaval.gestionriego.usuario.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

import java.time.OffsetDateTime;

@Entity
@Table(name = "usuarios")
public class Usuario {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true, length = 50)
    private String username;

    @Column(name = "password_hash", nullable = false, length = 255)
    private String passwordHash;

    @Column(nullable = false, length = 100)
    private String nombres;

    @Column(nullable = false, length = 120)
    private String apellidos;

    @Column(nullable = false)
    private boolean activo = true;

    @ManyToOne(fetch = FetchType.EAGER, optional = false)
    @JoinColumn(name = "rol_id", nullable = false)
    private Rol rol;

    @Column(name = "fecha_creacion", nullable = false)
    private OffsetDateTime fechaCreacion;

    protected Usuario() {
    }

    public Usuario(String username, String passwordHash, String nombres, String apellidos, Rol rol) {
        this.username = username;
        this.passwordHash = passwordHash;
        this.nombres = nombres;
        this.apellidos = apellidos;
        this.rol = rol;
        this.activo = true;
        this.fechaCreacion = OffsetDateTime.now();
    }

    public void actualizar(String username, String nombres, String apellidos, Rol rol, boolean activo) {
        this.username = username;
        this.nombres = nombres;
        this.apellidos = apellidos;
        this.rol = rol;
        this.activo = activo;
    }

    public void cambiarPassword(String passwordHash) {
        this.passwordHash = passwordHash;
    }

    public void activar() {
        this.activo = true;
    }

    public void desactivar() {
        this.activo = false;
    }

    public Long getId() {
        return id;
    }

    public String getNombres() {
        return nombres;
    }

    public String getApellidos() {
        return apellidos;
    }

    public String getUsername() {
        return username;
    }

    public String getPasswordHash() {
        return passwordHash;
    }

    public boolean isActivo() {
        return activo;
    }

    public Rol getRol() {
        return rol;
    }

    public OffsetDateTime getFechaCreacion() {
        return fechaCreacion;
    }
}
