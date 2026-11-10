package pe.vichaval.gestionriego.riego.repository;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import pe.vichaval.gestionriego.riego.entity.TurnoRiego;

public interface TurnoRiegoRepository
        extends JpaRepository<TurnoRiego, Long> {

    @Query("""
            SELECT COUNT(t) > 0
            FROM TurnoRiego t
            WHERE t.fecha = :fecha
              AND (
                  :ignorarId IS NULL
                  OR t.id <> :ignorarId
              )
              AND t.estado NOT IN (
                  'CANCELADO',
                  'VENCIDO'
              )
              AND t.horaInicio < :horaFin
              AND t.horaFin > :horaInicio
            """)
    boolean hayCruce(
            @Param("fecha") LocalDate fecha,
            @Param("horaInicio") LocalTime horaInicio,
            @Param("horaFin") LocalTime horaFin,
            @Param("ignorarId") Long ignorarId
    );

    long countByFecha(LocalDate fecha);

    long countByFechaAndEstado(
            LocalDate fecha,
            String estado
    );

    long countByParcelaId(Long parcelaId);

    boolean existsByParcelaId(Long parcelaId);

    List<TurnoRiego> findByEstadoAndFechaBefore(
            String estado,
            LocalDate fecha
    );

    @Override
    @EntityGraph(
            attributePaths = {
                    "parcela",
                    "parcela.agricultor"
            }
    )
    List<TurnoRiego> findAll();

    @Override
    @EntityGraph(
            attributePaths = {
                    "parcela",
                    "parcela.agricultor"
            }
    )
    Optional<TurnoRiego> findById(Long id);

    @EntityGraph(attributePaths = {"parcela", "parcela.agricultor"})
    @Query("""
            select t from TurnoRiego t
            where (:q = ''
                or lower(t.codigo) like lower(concat('%', :q, '%'))
                or lower(t.parcela.nombre) like lower(concat('%', :q, '%'))
                or lower(concat(
                    t.parcela.agricultor.nombres,
                    ' ',
                    t.parcela.agricultor.apellidos
                )) like lower(concat('%', :q, '%')))
              and (:estado = '' or t.estado = :estado)
            """)
    Page<TurnoRiego> buscar(
            @Param("q") String q,
            @Param("estado") String estado,
            Pageable pageable
    );

    @EntityGraph(attributePaths = {"parcela", "parcela.agricultor"})
    @Query("""
            select t from TurnoRiego t
            where (:q='' or lower(t.codigo) like lower(concat('%',:q,'%')) or lower(t.parcela.nombre) like lower(concat('%',:q,'%')) or lower(concat(t.parcela.agricultor.nombres,' ',t.parcela.agricultor.apellidos)) like lower(concat('%',:q,'%')))
              and (:estado='' or t.estado=:estado)
              and (:sector='' or lower(t.parcela.agricultor.sector)=lower(:sector))
              and (:agricultorId is null or t.parcela.agricultor.id=:agricultorId)
              and (:parcelaId is null or t.parcela.id=:parcelaId)
              and (:desde is null or t.fecha>=:desde)
              and (:hasta is null or t.fecha<=:hasta)
            """)
    Page<TurnoRiego> buscarReporte(@Param("q") String q,@Param("estado") String estado,@Param("sector") String sector,@Param("agricultorId") Long agricultorId,@Param("parcelaId") Long parcelaId,@Param("desde") LocalDate desde,@Param("hasta") LocalDate hasta,Pageable pageable);
}