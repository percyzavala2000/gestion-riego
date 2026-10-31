package pe.vichaval.gestionriego.dashboard.dto;

import java.math.BigDecimal;

public record DashboardResumen(

        long turnosHoy,

        long turnosProgramados,

        long agricultoresActivos,

        long parcelasRegistradas,

        BigDecimal totalIngresos,

        long cantidadPagos,

        BigDecimal totalDeuda,

        long agricultoresConDeuda

) {
}