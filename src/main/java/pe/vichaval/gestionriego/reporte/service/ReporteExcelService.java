package pe.vichaval.gestionriego.reporte.service;

import java.io.ByteArrayOutputStream;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Service;
import pe.vichaval.gestionriego.finanzas.entity.CuentaCobrar;
import pe.vichaval.gestionriego.reporte.dto.ReporteFiltro;
import pe.vichaval.gestionriego.tesoreria.entity.Pago;

@Service
public class ReporteExcelService {
    public byte[] pagos(List<Pago> datos) { return pagos(datos, new ReporteFiltro(), "sistema"); }
    public byte[] cuentas(List<CuentaCobrar> datos) { return cuentas(datos, new ReporteFiltro(), "sistema"); }

    public byte[] pagos(List<Pago> datos, ReporteFiltro filtro, String usuario) {
        BigDecimal total = datos.stream().map(Pago::getMonto).reduce(BigDecimal.ZERO, BigDecimal::add);
        try (Workbook wb = new XSSFWorkbook(); ByteArrayOutputStream out = new ByteArrayOutputStream()) {
            criterios(wb, "Reporte de pagos", filtro, usuario, datos.size(), "Total", total);
            Sheet sh = wb.createSheet("Pagos");
            encabezado(sh, "Código", "Fecha", "Agricultor", "Concepto", "Monto", "Método", "Estado");
            CellStyle moneda = moneda(wb);
            int i = 1;
            for (Pago p : datos) {
                Row r = sh.createRow(i++);
                r.createCell(0).setCellValue(p.getCodigo()); r.createCell(1).setCellValue(p.getFecha().toString());
                r.createCell(2).setCellValue(p.getAgricultor().getNombres()+" "+p.getAgricultor().getApellidos());
                r.createCell(3).setCellValue(p.getConcepto()); Cell c=r.createCell(4);c.setCellValue(p.getMonto().doubleValue());c.setCellStyle(moneda);
                r.createCell(5).setCellValue(p.getMetodo()); r.createCell(6).setCellValue(p.getEstado());
            }
            ajustar(sh,7); wb.write(out); return out.toByteArray();
        } catch(Exception e){throw new IllegalStateException("No se pudo generar el Excel de pagos",e);}
    }

    public byte[] cuentas(List<CuentaCobrar> datos, ReporteFiltro filtro, String usuario) {
        BigDecimal saldo = datos.stream().map(CuentaCobrar::getSaldoPendiente).reduce(BigDecimal.ZERO, BigDecimal::add);
        try (Workbook wb = new XSSFWorkbook(); ByteArrayOutputStream out = new ByteArrayOutputStream()) {
            criterios(wb, "Reporte de cuentas por cobrar", filtro, usuario, datos.size(), "Saldo pendiente", saldo);
            Sheet sh=wb.createSheet("Cuentas"); encabezado(sh,"Código","Agricultor","Concepto","Emisión","Vencimiento","Total","Pagado","Saldo","Estado");
            CellStyle moneda=moneda(wb);int i=1;
            for(CuentaCobrar c:datos){Row r=sh.createRow(i++);r.createCell(0).setCellValue(c.getCodigo());r.createCell(1).setCellValue(c.getAgricultor().getNombres()+" "+c.getAgricultor().getApellidos());r.createCell(2).setCellValue(c.getConcepto().getNombre());r.createCell(3).setCellValue(c.getFechaEmision().toString());r.createCell(4).setCellValue(c.getFechaVencimiento().toString());for(int n=0;n<3;n++){Cell x=r.createCell(5+n);x.setCellValue((n==0?c.getMontoTotal():n==1?c.getMontoPagado():c.getSaldoPendiente()).doubleValue());x.setCellStyle(moneda);}r.createCell(8).setCellValue(c.getEstado());}
            ajustar(sh,9);wb.write(out);return out.toByteArray();
        }catch(Exception e){throw new IllegalStateException("No se pudo generar el Excel de cuentas",e);}
    }

    private void criterios(Workbook wb,String titulo,ReporteFiltro f,String usuario,int cantidad,String etiqueta,BigDecimal total){Sheet sh=wb.createSheet("Criterios");Map<String,String> m=Map.ofEntries(Map.entry("Reporte",titulo),Map.entry("Generado",LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm"))),Map.entry("Usuario",usuario),Map.entry("Desde",f.getDesde()==null?"Todos":f.getDesde().toString()),Map.entry("Hasta",f.getHasta()==null?"Todos":f.getHasta().toString()),Map.entry("Texto",v(f.getQ())),Map.entry("Agricultor ID",f.getAgricultorId()==null?"Todos":f.getAgricultorId().toString()),Map.entry("Estado pago",v(f.getEstadoPago())),Map.entry("Método",v(f.getMetodo())),Map.entry("Estado cuenta",v(f.getEstadoCuenta())),Map.entry("Vencimiento",v(f.getVencimiento())),Map.entry("Registros",String.valueOf(cantidad)),Map.entry(etiqueta,"S/ "+total));int r=0;for(var e:m.entrySet()){Row row=sh.createRow(r++);row.createCell(0).setCellValue(e.getKey());row.createCell(1).setCellValue(e.getValue());}sh.autoSizeColumn(0);sh.autoSizeColumn(1);}
    private String v(String s){return s==null||s.isBlank()?"Todos":s;}
    private CellStyle moneda(Workbook wb){CellStyle s=wb.createCellStyle();s.setDataFormat(wb.createDataFormat().getFormat("\"S/ \"#,##0.00"));return s;}
    private void encabezado(Sheet sh,String... v){Row row=sh.createRow(0);CellStyle s=sh.getWorkbook().createCellStyle();Font f=sh.getWorkbook().createFont();f.setBold(true);s.setFont(f);for(int i=0;i<v.length;i++){Cell c=row.createCell(i);c.setCellValue(v[i]);c.setCellStyle(s);}sh.createFreezePane(0,1);sh.setAutoFilter(new org.apache.poi.ss.util.CellRangeAddress(0,0,0,v.length-1));}
    private void ajustar(Sheet sh,int n){for(int i=0;i<n;i++){sh.autoSizeColumn(i);sh.setColumnWidth(i,Math.min(sh.getColumnWidth(i)+800,15000));}}
}
