package pe.vichaval.gestionriego.mantenimiento;

import java.time.OffsetDateTime;

import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.MeterRegistry;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import pe.vichaval.gestionriego.security.agricultor.repository.SesionAgricultorRepository;

@Service
public class MantenimientoSesionesAgricultorService {

    private static final Logger log = LoggerFactory.getLogger(
            MantenimientoSesionesAgricultorService.class
    );

    private final SesionAgricultorRepository sesiones;
    private final int retencionDias;
    private final Counter eliminadas;

    public MantenimientoSesionesAgricultorService(
            SesionAgricultorRepository sesiones,
            MeterRegistry registry,
            @Value("${vichayal.auth.session-retention-days:30}")
            int retencionDias
    ) {
        if (retencionDias < 1) {
            throw new IllegalArgumentException(
                    "La retención de sesiones debe ser al menos un día"
            );
        }
        this.sesiones = sesiones;
        this.retencionDias = retencionDias;
        this.eliminadas = Counter.builder(
                        "vichayal_sesiones_agricultor_eliminadas_total"
                )
                .description("Sesiones móviles expiradas eliminadas")
                .register(registry);
    }

    @Scheduled(
            cron = "${vichayal.auth.session-cleanup-cron:0 20 3 * * *}",
            zone = "America/Lima"
    )
    @Transactional
    public long depurar() {
        OffsetDateTime limite = OffsetDateTime.now()
                .minusDays(retencionDias);
        long cantidad = sesiones.deleteExpiradasORevocadasAntes(limite);
        eliminadas.increment(cantidad);
        log.info(
                "Mantenimiento de sesiones móviles finalizado: eliminadas={}, limite={}",
                cantidad,
                limite
        );
        return cantidad;
    }
}
