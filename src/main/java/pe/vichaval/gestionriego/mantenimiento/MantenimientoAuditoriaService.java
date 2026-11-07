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
import pe.vichaval.gestionriego.auditoria.repository.EventoAuditoriaRepository;

@Service
public class MantenimientoAuditoriaService {
    private static final Logger log = LoggerFactory.getLogger(MantenimientoAuditoriaService.class);
    private final EventoAuditoriaRepository repository;
    private final Counter eliminados;
    private final int retentionDays;

    public MantenimientoAuditoriaService(EventoAuditoriaRepository repository, MeterRegistry registry,
                                         @Value("${vichayal.audit.retention-days:365}") int retentionDays) {
        if (retentionDays < 30)
            throw new IllegalArgumentException("La retención de auditoría no puede ser menor a 30 días");
        this.repository = repository;
        this.retentionDays = retentionDays;
        this.eliminados = Counter.builder("vichayal_auditoria_eventos_eliminados_total")
                .description("Eventos eliminados por la política de retención").register(registry);
    }

    @Scheduled(cron = "${vichayal.audit.cleanup-cron:0 30 2 * * *}", zone = "America/Lima")
    @Transactional
    public long depurar() {
        OffsetDateTime limite = OffsetDateTime.now().minusDays(retentionDays);
        long cantidad = repository.deleteByFechaHoraBefore(limite);
        eliminados.increment(cantidad);
        log.info("Mantenimiento de auditoría finalizado: eliminados={}, limite={}", cantidad, limite);
        return cantidad;
    }
}
