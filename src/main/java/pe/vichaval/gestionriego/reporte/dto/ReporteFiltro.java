package pe.vichaval.gestionriego.reporte.dto;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;

import org.springframework.format.annotation.DateTimeFormat;

public class ReporteFiltro {

    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
    private LocalDate desde;

    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
    private LocalDate hasta;

    private String q;
    private String estado;
    private String sector;
    private String cultivo;
    private String metodo;
    private Long agricultorId;
    private Long parcelaId;

    public void validarRango() {
        if (desde != null && hasta != null) {
            if (desde.isAfter(hasta)) {
                throw new IllegalArgumentException(
                        "La fecha desde no puede ser posterior a la fecha hasta."
                );
            }
            if (ChronoUnit.DAYS.between(desde, hasta) > 366) {
                throw new IllegalArgumentException(
                        "El rango del reporte no puede superar 366 días."
                );
            }
        }
    }

    public String textoNormalizado() {
        return q == null ? "" : q.trim().toLowerCase();
    }

    public String estadoNormalizado() {
        return estado == null ? "" : estado.trim().toUpperCase();
    }

    public String sectorNormalizado() {
        return sector == null ? "" : sector.trim().toLowerCase();
    }

    public String cultivoNormalizado() {
        return cultivo == null ? "" : cultivo.trim().toLowerCase();
    }

    public String metodoNormalizado() {
        return metodo == null ? "" : metodo.trim().toUpperCase();
    }

    public LocalDate getDesde() { return desde; }
    public void setDesde(LocalDate desde) { this.desde = desde; }
    public LocalDate getHasta() { return hasta; }
    public void setHasta(LocalDate hasta) { this.hasta = hasta; }
    public String getQ() { return q; }
    public void setQ(String q) { this.q = q; }
    public String getEstado() { return estado; }
    public void setEstado(String estado) { this.estado = estado; }
    public String getSector() { return sector; }
    public void setSector(String sector) { this.sector = sector; }
    public String getCultivo() { return cultivo; }
    public void setCultivo(String cultivo) { this.cultivo = cultivo; }
    public String getMetodo() { return metodo; }
    public void setMetodo(String metodo) { this.metodo = metodo; }
    public Long getAgricultorId() { return agricultorId; }
    public void setAgricultorId(Long agricultorId) { this.agricultorId = agricultorId; }
    public Long getParcelaId() { return parcelaId; }
    public void setParcelaId(Long parcelaId) { this.parcelaId = parcelaId; }
}
