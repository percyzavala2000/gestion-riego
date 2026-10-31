package pe.vichaval.gestionriego.parcela.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import pe.vichaval.gestionriego.agricultor.repository.AgricultorRepository;
import pe.vichaval.gestionriego.parcela.dto.ParcelaForm;
import pe.vichaval.gestionriego.parcela.entity.Parcela;
import pe.vichaval.gestionriego.parcela.repository.ParcelaRepository;
import pe.vichaval.gestionriego.riego.repository.TurnoRiegoRepository;

@Service
@Transactional
public class ParcelaService {

    private final ParcelaRepository parcelaRepository;
    private final AgricultorRepository agricultorRepository;
    private final TurnoRiegoRepository turnoRiegoRepository;

    public ParcelaService(
            ParcelaRepository parcelaRepository,
            AgricultorRepository agricultorRepository,
            TurnoRiegoRepository turnoRiegoRepository
    ) {
        this.parcelaRepository = parcelaRepository;
        this.agricultorRepository = agricultorRepository;
        this.turnoRiegoRepository = turnoRiegoRepository;
    }

    @Transactional(readOnly = true)
    public Parcela get(Long id) {

        return parcelaRepository
                .findById(id)
                .orElseThrow(() ->
                        new IllegalArgumentException(
                                "La parcela solicitada no existe."
                        )
                );
    }

    public void guardar(
            ParcelaForm form,
            Long id
    ) {
        validarCodigo(
                form.getCodigo(),
                id
        );

        var agricultor = agricultorRepository
                .findById(form.getAgricultorId())
                .orElseThrow(() ->
                        new IllegalArgumentException(
                                "El agricultor seleccionado no existe."
                        )
                );

        if (!"ACTIVO".equals(agricultor.getEstado())) {
            throw new IllegalStateException(
                    "No se puede asociar la parcela "
                            + "a un agricultor inactivo."
            );
        }

        Parcela parcela;

        if (id == null) {

            parcela = new Parcela(
                    form,
                    agricultor
            );

        } else {

            parcela = get(id);

            /*
             * Cuando la parcela tiene historial, el código
             * debe conservarse como identificador estable.
             */
            boolean tieneTurnos =
                    turnoRiegoRepository.existsByParcelaId(id);

            if (
                    tieneTurnos
                            && !parcela.getCodigo()
                            .equalsIgnoreCase(
                                    form.getCodigo()
                                            .trim()
                            )
            ) {
                throw new IllegalStateException(
                        "No puedes cambiar el código de una "
                                + "parcela que tiene turnos asociados."
                );
            }

            parcela.actualizar(
                    form,
                    agricultor
            );
        }

        parcelaRepository.save(
                parcela
        );
    }

    public void eliminar(Long id) {

        Parcela parcela = get(id);

        boolean tieneTurnos =
                turnoRiegoRepository.existsByParcelaId(id);

        if (tieneTurnos) {

            throw new IllegalStateException(
                    "No se puede eliminar la parcela porque "
                            + "tiene turnos de riego asociados. "
                            + "Cámbiala a INACTIVA para conservar "
                            + "el historial."
            );
        }

        parcelaRepository.delete(
                parcela
        );
    }

    private void validarCodigo(
            String codigo,
            Long id
    ) {
        String codigoNormalizado =
                codigo.trim();

        if (
                id == null
                        && parcelaRepository
                        .existsByCodigoIgnoreCase(
                                codigoNormalizado
                        )
        ) {
            throw new IllegalArgumentException(
                    "El código de parcela ya está registrado."
            );
        }

        if (
                id != null
                        && parcelaRepository
                        .existsByCodigoIgnoreCaseAndIdNot(
                                codigoNormalizado,
                                id
                        )
        ) {
            throw new IllegalArgumentException(
                    "El código pertenece a otra parcela."
            );
        }
    }
}