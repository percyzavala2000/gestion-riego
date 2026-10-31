package pe.vichaval.gestionriego.usuario.entity;

import jakarta.persistence.*;

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

    public Usuario(String u, String p, String n, String a, Rol r) {
        username = u;
        passwordHash = p;
        nombres = n;
        apellidos = a;
        rol = r;
        fechaCreacion = OffsetDateTime.now();
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
    public Long getId() {
        return id;
    }
}
