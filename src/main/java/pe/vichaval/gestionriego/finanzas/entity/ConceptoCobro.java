package pe.vichaval.gestionriego.finanzas.entity;

import jakarta.persistence.*;
import pe.vichaval.gestionriego.finanzas.dto.ConceptoCobroForm;

@Entity
@Table(name = "conceptos_cobro")
public class ConceptoCobro {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(nullable = false, unique = true, length = 30)
    private String codigo;
    @Column(nullable = false, length = 120)
    private String nombre;
    @Column(length = 500)
    private String descripcion;
    @Column(nullable = false)
    private boolean activo = true;

    protected ConceptoCobro() {
    }

    public ConceptoCobro(ConceptoCobroForm f) {
        actualizar(f);
    }

    public void actualizar(ConceptoCobroForm f) {
        codigo = f.getCodigo().trim().toUpperCase();
        nombre = f.getNombre().trim();
        descripcion = f.getDescripcion();
        activo = f.isActivo();
    }

    public Long getId() {
        return id;
    }

    public String getCodigo() {
        return codigo;
    }

    public String getNombre() {
        return nombre;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public boolean isActivo() {
        return activo;
    }
}
