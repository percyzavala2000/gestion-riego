package pe.vichaval.gestionriego.inteligencia.service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import pe.vichaval.gestionriego.caja.repository.CierreCajaRepository;
import pe.vichaval.gestionriego.caja.repository.DevolucionRepository;
import pe.vichaval.gestionriego.finanzas.entity.CuentaCobrar;
import pe.vichaval.gestionriego.finanzas.repository.CuentaCobrarRepository;
import pe.vichaval.gestionriego.inteligencia.dto.AlertaDto;
import pe.vichaval.gestionriego.inteligencia.dto.ResumenInteligenciaDto;
import pe.vichaval.gestionriego.riego.entity.TurnoRiego;
import pe.vichaval.gestionriego.riego.repository.TurnoRiegoRepository;
import pe.vichaval.gestionriego.tesoreria.repository.PagoRepository;

@Service
@Transactional(readOnly = true)
public class InteligenciaService {
    private static final Map<String, Integer> ORDEN = Map.of("ALTA", 0, "MEDIA", 1, "BAJA", 2);

    private final TurnoRiegoRepository turnos;
    private final CuentaCobrarRepository cuentas;
    private final PagoRepository pagos;
    private final CierreCajaRepository cierres;
    private final DevolucionRepository devoluciones;

    public InteligenciaService(
            TurnoRiegoRepository turnos,
            CuentaCobrarRepository cuentas,
            PagoRepository pagos,
            CierreCajaRepository cierres,
            DevolucionRepository devoluciones) {
        this.turnos = turnos;
        this.cuentas = cuentas;
        this.pagos = pagos;
        this.cierres = cierres;
        this.devoluciones = devoluciones;
    }

    public ResumenInteligenciaDto operativa() {
        List<AlertaDto> alertas = new ArrayList<>();
        LocalDate hoy = LocalDate.now();
        LocalDate limite = hoy.plusDays(7);
        List<TurnoRiego> lista = turnos.findAll();

        long vencidos = lista.stream().filter(t -> "VENCIDO".equals(t.getEstado())).count();
        if (vencidos > 0) {
            alertas.add(new AlertaDto(
                    ClasificadorPrioridad.porCantidad(vencidos, 1, 3),
                    "RIEGO",
                    "Turnos vencidos sin reprogramar",
                    vencidos + " turno(s) permanecen en estado VENCIDO.",
                    "Revisar cada turno y reprogramar los que aún sean necesarios.",
                    "/turnos"));
        }

        Map<Long, List<TurnoRiego>> proximosPorParcela = lista.stream()
                .filter(t -> !t.getFecha().isBefore(hoy) && !t.getFecha().isAfter(limite))
                .filter(t -> "PROGRAMADO".equals(t.getEstado()))
                .collect(Collectors.groupingBy(t -> t.getParcela().getId()));

        proximosPorParcela.values().stream()
                .filter(grupo -> grupo.size() >= 3)
                .forEach(grupo -> {
                    TurnoRiego ejemplo = grupo.getFirst();
                    alertas.add(new AlertaDto(
                            ClasificadorPrioridad.porCantidad(grupo.size(), 3, 5),
                            "DEMANDA",
                            "Concentración de turnos en una parcela",
                            ejemplo.getParcela().getCodigo() + " tiene " + grupo.size() + " turnos programados en los próximos 7 días.",
                            "Verificar disponibilidad hídrica y distribuir los horarios si fuera necesario.",
                            "/turnos"));
                });

        long programadosHoy = lista.stream()
                .filter(t -> hoy.equals(t.getFecha()))
                .filter(t -> "PROGRAMADO".equals(t.getEstado()))
                .count();
        if (programadosHoy >= 4) {
            alertas.add(new AlertaDto(
                    ClasificadorPrioridad.porCantidad(programadosHoy, 4, 7),
                    "CAPACIDAD",
                    "Carga operativa elevada para hoy",
                    programadosHoy + " turnos están programados para la fecha actual.",
                    "Confirmar disponibilidad del canal y capacidad de supervisión.",
                    "/turnos"));
        }

        return resumen(alertas);
    }

    public ResumenInteligenciaDto financiera() {
        List<AlertaDto> alertas = new ArrayList<>();
        List<CuentaCobrar> lista = cuentas.findAll();
        lista.forEach(CuentaCobrar::actualizarVencida);

        List<CuentaCobrar> vencidas = lista.stream()
                .filter(c -> "VENCIDA".equals(c.getEstado()))
                .toList();
        BigDecimal deudaVencida = vencidas.stream()
                .map(CuentaCobrar::getSaldoPendiente)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        if (!vencidas.isEmpty()) {
            alertas.add(new AlertaDto(
                    ClasificadorPrioridad.porCantidad(vencidas.size(), 1, 4),
                    "COBRANZA",
                    "Cuentas vencidas con saldo",
                    vencidas.size() + " cuenta(s) vencida(s) acumulan S/ " + deudaVencida + ".",
                    "Priorizar contacto con los agricultores y registrar compromisos de pago.",
                    "/tesoreria/cuentas"));
        }

        long pendientes = pagos.countByEstado("PENDIENTE");
        if (pendientes > 0) {
            alertas.add(new AlertaDto(
                    ClasificadorPrioridad.porCantidad(pendientes, 1, 5),
                    "VERIFICACION",
                    "Pagos pendientes de verificación",
                    pendientes + " pago(s) todavía requieren confirmación o rechazo.",
                    "Contrastar los comprobantes antes del cierre diario.",
                    "/tesoreria"));
        }

        cierres.findAll().stream()
                .filter(c -> c.getDiferencia() != null && c.getDiferencia().abs().compareTo(BigDecimal.ZERO) > 0)
                .max(Comparator.comparing(c -> c.getDiferencia().abs()))
                .ifPresent(c -> alertas.add(new AlertaDto(
                        c.getDiferencia().abs().compareTo(new BigDecimal("50.00")) >= 0 ? "ALTA" : "MEDIA",
                        "CAJA",
                        "Diferencia detectada en cierre",
                        "El cierre del " + c.getFecha() + " presenta una diferencia de S/ " + c.getDiferencia() + ".",
                        "Revisar pagos, egresos, devoluciones y efectivo contado de esa fecha.",
                        "/tesoreria/caja")));

        long solicitudes = devoluciones.findAll().stream()
                .filter(d -> "SOLICITADA".equals(d.getEstado()))
                .count();
        if (solicitudes > 0) {
            alertas.add(new AlertaDto(
                    ClasificadorPrioridad.porCantidad(solicitudes, 1, 4),
                    "DEVOLUCIONES",
                    "Solicitudes de devolución pendientes",
                    solicitudes + " solicitud(es) esperan evaluación.",
                    "Revisar el pago original, el motivo y el importe disponible.",
                    "/tesoreria/caja"));
        }

        return resumen(alertas);
    }

    private ResumenInteligenciaDto resumen(List<AlertaDto> alertas) {
        List<AlertaDto> ordenadas = alertas.stream()
                .sorted(Comparator.comparingInt(a -> ORDEN.getOrDefault(a.prioridad(), 9)))
                .toList();
        return new ResumenInteligenciaDto(
                ordenadas.size(),
                ordenadas.stream().filter(a -> "ALTA".equals(a.prioridad())).count(),
                ordenadas.stream().filter(a -> "MEDIA".equals(a.prioridad())).count(),
                ordenadas.stream().filter(a -> "BAJA".equals(a.prioridad())).count(),
                ordenadas);
    }
}
