package pe.vichaval.gestionriego.mantenimiento;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.mock;
import org.junit.jupiter.api.Test;
import io.micrometer.core.instrument.simple.SimpleMeterRegistry;
import pe.vichaval.gestionriego.auditoria.repository.EventoAuditoriaRepository;

class MantenimientoAuditoriaServiceTest {
    @Test void rechazaRetencionMenorATreintaDias() {
        assertThrows(IllegalArgumentException.class, () -> new MantenimientoAuditoriaService(
                mock(EventoAuditoriaRepository.class), new SimpleMeterRegistry(), 29));
    }
}
