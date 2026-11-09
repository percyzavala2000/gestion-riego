package pe.vichaval.gestionriego.observabilidad;

import io.micrometer.core.instrument.Gauge;
import io.micrometer.core.instrument.MeterRegistry;
import jakarta.annotation.PostConstruct;
import org.springframework.stereotype.Component;
import pe.vichaval.gestionriego.caja.repository.DevolucionRepository;
import pe.vichaval.gestionriego.finanzas.repository.CuentaCobrarRepository;
import pe.vichaval.gestionriego.tesoreria.repository.PagoRepository;
import pe.vichaval.gestionriego.notificacion.repository.NotificacionRepository;
import pe.vichaval.gestionriego.notificacion.push.repository.DispositivoPushRepository;
import pe.vichaval.gestionriego.notificacion.push.repository.EntregaPushRepository;

@Component
public class MetricasNegocio {
    private final MeterRegistry registry;
    private final PagoRepository pagos;
    private final CuentaCobrarRepository cuentas;
    private final DevolucionRepository devoluciones;
      private final NotificacionRepository notificaciones;
      private final DispositivoPushRepository dispositivos;
      private final EntregaPushRepository entregas;

    public MetricasNegocio(MeterRegistry registry, PagoRepository pagos,
                           CuentaCobrarRepository cuentas, DevolucionRepository devoluciones,
                         NotificacionRepository notificaciones,
                         DispositivoPushRepository dispositivos,
                         EntregaPushRepository entregas) {
        this.registry = registry;
        this.pagos = pagos;
        this.cuentas = cuentas;
        this.devoluciones = devoluciones;
          this.notificaciones = notificaciones;
          this.dispositivos = dispositivos;
          this.entregas = entregas;
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
          Gauge.builder("vichayal_dispositivos_push_activos", dispositivos, DispositivoPushRepository::countByActivoTrue)
                  .description("Dispositivos push activos").register(registry);
          Gauge.builder("vichayal_entregas_push_pendientes", entregas, e -> e.countByEstado("PENDIENTE") + e.countByEstado("ERROR_TEMPORAL"))
                  .description("Entregas push pendientes o en reintento").register(registry);
          Gauge.builder("vichayal_entregas_push_error_definitivo", entregas, e -> e.countByEstado("ERROR_DEFINITIVO"))
                  .description("Entregas push con error definitivo").register(registry);
    }
}
