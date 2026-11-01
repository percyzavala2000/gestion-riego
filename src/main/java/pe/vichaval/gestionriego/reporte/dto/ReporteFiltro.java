package pe.vichaval.gestionriego.reporte.dto;

import java.time.LocalDate;

import org.springframework.format.annotation.DateTimeFormat;

public class ReporteFiltro {
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
    private LocalDate desde;
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
    private LocalDate hasta;
    private String estado;
    private Long agricultorId;

    public LocalDate getDesde() {
        return desde;
    }

    public void setDesde(LocalDate desde) {
        this.desde = desde;
    }

    public LocalDate getHasta() {
        return hasta;
    }

    public void setHasta(LocalDate hasta) {
        this.hasta = hasta;
    }

    public String getEstado() {
        return estado;
    }

    public void setEstado(String estado) {
        this.estado = estado;
    }

    public Long getAgricultorId() {
        return agricultorId;
    }

    public void setAgricultorId(Long agricultorId) {
        this.agricultorId = agricultorId;
    }
}
