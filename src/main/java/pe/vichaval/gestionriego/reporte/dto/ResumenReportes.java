package pe.vichaval.gestionriego.reporte.dto;

import java.math.BigDecimal;

public record ResumenReportes(
        long agricultores,
        long parcelas,
        long turnos,
        long pagos,
        BigDecimal ingresos,
        BigDecimal deudaPendiente,
        long cuentasPendientes,
        long cuentasVencidas) {}
