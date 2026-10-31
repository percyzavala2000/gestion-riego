package pe.vichaval.gestionriego.usuario.entity;

import jakarta.persistence.*;
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

    public CodigoRol getCodigo() {
        return codigo;
    }
}
