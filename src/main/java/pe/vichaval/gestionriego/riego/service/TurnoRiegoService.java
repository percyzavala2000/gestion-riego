package pe.vichaval.gestionriego.riego.service;

import java.time.*;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pe.vichaval.gestionriego.parcela.repository.ParcelaRepository;
import pe.vichaval.gestionriego.riego.dto.TurnoForm;
import pe.vichaval.gestionriego.riego.entity.TurnoRiego;
import pe.vichaval.gestionriego.riego.repository.TurnoRiegoRepository;

@Service
@Transactional
public class TurnoRiegoService {
    private final TurnoRiegoRepository r;
    private final ParcelaRepository p;

    public TurnoRiegoService(TurnoRiegoRepository r, ParcelaRepository p) {
        this.r = r;
        this.p = p;
    }

    @Transactional(readOnly = true)
    public TurnoRiego get(Long id) {
        return r.findById(id).orElseThrow(() -> new IllegalArgumentException("Turno no encontrado"));
    }

    public TurnoRiego guardar(TurnoForm f, Long id) {
        var parcela = p.findById(f.getParcelaId())
                .orElseThrow(() -> new IllegalArgumentException("Parcela no encontrada"));

        if (!"ACTIVA".equals(parcela.getEstado())) {
            throw new IllegalStateException("Parcela no activa");
        }
        if (!"ACTIVO".equals(parcela.getAgricultor().getEstado())) {
            throw new IllegalStateException("Agricultor no activo");
        }

        LocalTime fin = f.getHoraInicio()
                .plusMinutes(f.getDuracionMinutos());

        if (r.hayCruce(f.getFecha(), f.getHoraInicio(), fin, id)) {
            throw new IllegalStateException(
                    "Existe otro turno en ese horario"
            );
        }

        if (id == null) {
            TurnoRiego nuevo = new TurnoRiego(
                    "TR-%04d".formatted(r.count() + 1),
                    f,
                    fin,
                    parcela
            );
            return r.save(nuevo);
        }

        TurnoRiego existente = get(id);
        if (!"PROGRAMADO".equals(existente.getEstado())
                && !"PENDIENTE".equals(existente.getEstado())) {
            throw new IllegalStateException(
                    "Solo se editan turnos pendientes o programados"
            );
        }

        existente.actualizar(f, fin, parcela);
        return r.save(existente);
    }

    public void iniciar(Long id) {
        get(id).iniciar();
    }

    public void completar(Long id) {
        get(id).completar();
    }

    public void cancelar(Long id, String motivo) {
        get(id).cancelar(motivo);
    }

    public void actualizarVencidos() {
        r.findByEstadoAndFechaBefore("PROGRAMADO", LocalDate.now()).forEach(TurnoRiego::vencer);
    }
}
