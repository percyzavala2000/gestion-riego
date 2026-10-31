package pe.vichaval.gestionriego.riego.repository;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import pe.vichaval.gestionriego.riego.entity.TurnoRiego;

public interface TurnoRiegoRepository
        extends JpaRepository<TurnoRiego, Long> {

    @Query("""
        select count(t) > 0
        from TurnoRiego t
        where t.fecha = :fecha
          and t.horaInicio < :horaFin
          and t.horaFin > :horaInicio
        """)
    boolean hayCruce(
            @Param("fecha") LocalDate fecha,
            @Param("horaInicio") LocalTime horaInicio,
            @Param("horaFin") LocalTime horaFin
    );

    long countByFecha(LocalDate fecha);

    long countByFechaAndEstado(
            LocalDate fecha,
            String estado
    );

    @Override
    @EntityGraph(attributePaths = {
            "parcela",
            "parcela.agricultor"
    })
    List<TurnoRiego> findAll();
}