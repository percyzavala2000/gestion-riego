package pe.vichaval.gestionriego.observabilidad;

import io.micrometer.core.instrument.Gauge;
import io.micrometer.core.instrument.MeterRegistry;
import jakarta.annotation.PostConstruct;
import org.springframework.stereotype.Component;
import pe.vichaval.gestionriego.caja.repository.DevolucionRepository;
import pe.vichaval.gestionriego.finanzas.repository.CuentaCobrarRepository;
import pe.vichaval.gestionriego.tesoreria.repository.PagoRepository;
import pe.vichaval.gestionriego.notificacion.repository.NotificacionRepository;

@Component
public class MetricasNegocio {
    private final MeterRegistry registry;
    private final PagoRepository pagos;
    private final CuentaCobrarRepository cuentas;
    private final DevolucionRepository devoluciones;
      private final NotificacionRepository notificaciones;

    public MetricasNegocio(MeterRegistry registry, PagoRepository pagos,
                           CuentaCobrarRepository cuentas, DevolucionRepository devoluciones, NotificacionRepository notificaciones) {
        this.registry = registry;
        this.pagos = pagos;
        this.cuentas = cuentas;
        this.devoluciones = devoluciones;
          this.notificaciones = notificaciones;
    }

    @PostConstruct
    void registrar() {
        Gauge.builder("vichayal_pagos_pendientes", pagos, p -> p.countByEstado("PENDIENTE"))
                .description("Pagos pendientes de verificación").register(registry);
        Gauge.builder("vichayal_deuda_total_soles", cuentas, c -> c.totalDeuda().doubleValue())
                .description("Saldo total pendiente, expresado en soles").register(registry);
        Gauge.builder("vichayal_devoluciones_solicitadas", devoluciones,
                        d -> d.countByEstado("SOLICITADA"))
                .description("Solicitudes de devolución pendientes").register(registry);
          Gauge.builder("vichayal_notificaciones_no_leidas", notificaciones, n -> n.countByEstado("NO_LEIDA"))
                  .description("Notificaciones persistentes no leídas").register(registry);
    }
}
