package pe.vichaval.gestionriego.usuario.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import pe.vichaval.gestionriego.usuario.enums.CodigoRol;

@Entity
@Table(name = "roles")
public class Rol {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, unique = true, length = 30)
    private CodigoRol codigo;

    @Column(nullable = false, length = 80)
    private String nombre;

    protected Rol() {
    }

    public Long getId() {
        return id;
    }

    public CodigoRol getCodigo() {
        return codigo;
    }

    public String getNombre() {
        return nombre;
    }
}
