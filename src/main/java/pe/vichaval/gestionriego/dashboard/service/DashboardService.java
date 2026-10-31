package pe.vichaval.gestionriego.dashboard.service;

import java.math.BigDecimal;
import java.time.LocalDate;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import pe.vichaval.gestionriego.agricultor.repository.AgricultorRepository;
import pe.vichaval.gestionriego.dashboard.dto.DashboardResumen;
import pe.vichaval.gestionriego.parcela.repository.ParcelaRepository;
import pe.vichaval.gestionriego.riego.repository.TurnoRiegoRepository;
import pe.vichaval.gestionriego.tesoreria.repository.PagoRepository;

@Service
public class DashboardService {

    private final AgricultorRepository agricultorRepository;
    private final ParcelaRepository parcelaRepository;
    private final TurnoRiegoRepository turnoRepository;
    private final PagoRepository pagoRepository;

    public DashboardService(
            AgricultorRepository agricultorRepository,
            ParcelaRepository parcelaRepository,
            TurnoRiegoRepository turnoRepository,
            PagoRepository pagoRepository
    ) {
        this.agricultorRepository = agricultorRepository;
        this.parcelaRepository = parcelaRepository;
        this.turnoRepository = turnoRepository;
        this.pagoRepository = pagoRepository;
    }

    @Transactional(readOnly = true)
    public DashboardResumen obtenerResumen() {

        LocalDate fechaActual = LocalDate.now();

        long turnosHoy =
                turnoRepository.countByFecha(fechaActual);

        long turnosProgramados =
                turnoRepository.countByFechaAndEstado(
                        fechaActual,
                        "PROGRAMADO"
                );

        long agricultoresActivos =
                agricultorRepository.countByEstado("ACTIVO");

        long parcelasRegistradas =
                parcelaRepository.count();

        BigDecimal totalIngresos =
                pagoRepository.total();

        long cantidadPagos =
                pagoRepository.count();

        /*
         * Estos valores serán sustituidos cuando se implemente
         * la entidad CuentaPorCobrar o Deuda.
         */
        BigDecimal totalDeuda =
                BigDecimal.ZERO;

        long agricultoresConDeuda =
                0L;

        return new DashboardResumen(
                turnosHoy,
                turnosProgramados,
                agricultoresActivos,
                parcelasRegistradas,
                totalIngresos,
                cantidadPagos,
                totalDeuda,
                agricultoresConDeuda
        );
    }
}