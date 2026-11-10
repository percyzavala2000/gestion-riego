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
    private String estadoPago;
    private String estadoCuenta;
    private String vencimiento;
    private Long agricultorId;
    private Long parcelaId;

    public void validarRango() {
        if (desde != null && hasta != null) {
            if (desde.isAfter(hasta)) throw new IllegalArgumentException("La fecha desde no puede ser posterior a la fecha hasta.");
            if (ChronoUnit.DAYS.between(desde, hasta) > 366) throw new IllegalArgumentException("El rango del reporte no puede superar 366 días.");
        }
    }
    public String textoNormalizado(){return q==null?"":q.trim();}
    public String estadoNormalizado(){return estado==null?"":estado.trim().toUpperCase();}
    public String sectorNormalizado(){return sector==null?"":sector.trim();}
    public String cultivoNormalizado(){return cultivo==null?"":cultivo.trim();}
    public String metodoNormalizado(){return metodo==null?"":metodo.trim().toUpperCase();}
    public String estadoPagoNormalizado(){return estadoPago==null?"":estadoPago.trim().toUpperCase();}
    public String estadoCuentaNormalizado(){return estadoCuenta==null?"":estadoCuenta.trim().toUpperCase();}
    public String vencimientoNormalizado(){return vencimiento==null?"":vencimiento.trim().toUpperCase();}
    public LocalDate getDesde(){return desde;} public void setDesde(LocalDate v){desde=v;}
    public LocalDate getHasta(){return hasta;} public void setHasta(LocalDate v){hasta=v;}
    public String getQ(){return q;} public void setQ(String v){q=v;}
    public String getEstado(){return estado;} public void setEstado(String v){estado=v;}
    public String getSector(){return sector;} public void setSector(String v){sector=v;}
    public String getCultivo(){return cultivo;} public void setCultivo(String v){cultivo=v;}
    public String getMetodo(){return metodo;} public void setMetodo(String v){metodo=v;}
    public String getEstadoPago(){return estadoPago;} public void setEstadoPago(String v){estadoPago=v;}
    public String getEstadoCuenta(){return estadoCuenta;} public void setEstadoCuenta(String v){estadoCuenta=v;}
    public String getVencimiento(){return vencimiento;} public void setVencimiento(String v){vencimiento=v;}
    public Long getAgricultorId(){return agricultorId;} public void setAgricultorId(Long v){agricultorId=v;}
    public Long getParcelaId(){return parcelaId;} public void setParcelaId(Long v){parcelaId=v;}
}
