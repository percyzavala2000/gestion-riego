package pe.vichaval.gestionriego.riego.entity;

import jakarta.persistence.*;

import java.time.*;

import pe.vichaval.gestionriego.parcela.entity.Parcela;

@Entity
@Table(name = "turnos_riego")
public class TurnoRiego {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(nullable = false, unique = true)
    private String codigo;
    @Column(nullable = false)
    private LocalDate fecha;
    @Column(name = "hora_inicio", nullable = false)
    private LocalTime horaInicio;
    @Column(name = "hora_fin", nullable = false)
    private LocalTime horaFin;
    @Column(nullable = false)
    private String estado = "PROGRAMADO";
    private String observaciones;
    @ManyToOne(fetch = FetchType.LAZY,optional = false)
    @JoinColumn(name = "parcela_id", nullable = false   )
    private Parcela parcela;

    protected TurnoRiego() {
    }

    public TurnoRiego(String c, LocalDate f, LocalTime i, LocalTime fin, String o, Parcela p) {
        codigo = c;
        fecha = f;
        horaInicio = i;
        horaFin = fin;
        observaciones = o;
        parcela = p;
    }

    public String getCodigo() {
        return codigo;
    }

    public LocalDate getFecha() {
        return fecha;
    }

    public LocalTime getHoraInicio() {
        return horaInicio;
    }

    public LocalTime getHoraFin() {
        return horaFin;
    }

    public String getEstado() {
        return estado;
    }

    public Parcela getParcela() {
        return parcela;
    }
}