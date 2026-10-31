package pe.vichaval.gestionriego.parcela.entity;

import jakarta.persistence.*;

import java.math.BigDecimal;

import pe.vichaval.gestionriego.agricultor.entity.Agricultor;
import pe.vichaval.gestionriego.parcela.dto.ParcelaForm;

@Entity
@Table(name = "parcelas")
public class Parcela {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(nullable = false, unique = true)
    private String codigo;
    @Column(nullable = false)
    private String nombre;
    @Column(nullable = false)
    private BigDecimal hectareas;
    private String cultivo;
    @ManyToOne(fetch = FetchType.LAZY,optional = false)
    @JoinColumn(name = "agricultor_id", nullable = false)
    private Agricultor agricultor;

    protected Parcela() {
    }

    public Parcela(ParcelaForm f, Agricultor a) {
        set(f, a);
    }

    public void set(ParcelaForm f, Agricultor a) {
        codigo = f.getCodigo();
        nombre = f.getNombre();
        hectareas = f.getHectareas();
        cultivo = f.getCultivo();
        agricultor = a;
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

    public BigDecimal getHectareas() {
        return hectareas;
    }

    public String getCultivo() {
        return cultivo;
    }

    public Agricultor getAgricultor() {
        return agricultor;
    }
}