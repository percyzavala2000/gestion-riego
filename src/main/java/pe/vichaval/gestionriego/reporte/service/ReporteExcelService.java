package pe.vichaval.gestionriego.reporte.service;

import java.io.ByteArrayOutputStream;
import java.util.List;

import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Service;
import pe.vichaval.gestionriego.finanzas.entity.CuentaCobrar;
import pe.vichaval.gestionriego.tesoreria.entity.Pago;

@Service
public class ReporteExcelService {
    public byte[] pagos(List<Pago> datos) {
        try (Workbook wb = new XSSFWorkbook(); ByteArrayOutputStream out = new ByteArrayOutputStream()) {
            Sheet sh = wb.createSheet("Pagos");
            encabezado(sh, "Código", "Fecha", "Agricultor", "Concepto", "Monto", "Método", "Estado");
            int i = 1;
            for (Pago p : datos) {
                Row r = sh.createRow(i++);
                r.createCell(0).setCellValue(p.getCodigo());
                r.createCell(1).setCellValue(p.getFecha().toString());
                r.createCell(2).setCellValue(p.getAgricultor().getNombres() + " " + p.getAgricultor().getApellidos());
                r.createCell(3).setCellValue(p.getConcepto());
                r.createCell(4).setCellValue(p.getMonto().doubleValue());
                r.createCell(5).setCellValue(p.getMetodo());
                r.createCell(6).setCellValue(p.getEstado());
            }
            ajustar(sh, 7);
            wb.write(out);
            return out.toByteArray();
        } catch (Exception e) {
            throw new IllegalStateException("No se pudo generar el Excel de pagos", e);
        }
    }

    public byte[] cuentas(List<CuentaCobrar> datos) {
        try (Workbook wb = new XSSFWorkbook(); ByteArrayOutputStream out = new ByteArrayOutputStream()) {
            Sheet sh = wb.createSheet("Cuentas por cobrar");
            encabezado(sh, "Código", "Agricultor", "Concepto", "Emisión", "Vencimiento", "Total", "Pagado", "Saldo", "Estado");
            int i = 1;
            for (CuentaCobrar c : datos) {
                Row r = sh.createRow(i++);
                r.createCell(0).setCellValue(c.getCodigo());
                r.createCell(1).setCellValue(c.getAgricultor().getNombres() + " " + c.getAgricultor().getApellidos());
                r.createCell(2).setCellValue(c.getConcepto().getNombre());
                r.createCell(3).setCellValue(c.getFechaEmision().toString());
                r.createCell(4).setCellValue(c.getFechaVencimiento().toString());
                r.createCell(5).setCellValue(c.getMontoTotal().doubleValue());
                r.createCell(6).setCellValue(c.getMontoPagado().doubleValue());
                r.createCell(7).setCellValue(c.getSaldoPendiente().doubleValue());
                r.createCell(8).setCellValue(c.getEstado());
            }
            ajustar(sh, 9);
            wb.write(out);
            return out.toByteArray();
        } catch (Exception e) {
            throw new IllegalStateException("No se pudo generar el Excel de cuentas", e);
        }
    }

    private void encabezado(Sheet sh, String... valores) {
        Row row = sh.createRow(0);
        CellStyle style = sh.getWorkbook().createCellStyle();
        Font f = sh.getWorkbook().createFont();
        f.setBold(true);
        style.setFont(f);
        for (int i = 0; i < valores.length; i++) {
            Cell c = row.createCell(i);
            c.setCellValue(valores[i]);
            c.setCellStyle(style);
        }
        sh.createFreezePane(0, 1);
        sh.setAutoFilter(new org.apache.poi.ss.util.CellRangeAddress(0, 0, 0, valores.length - 1));
    }

    private void ajustar(Sheet sh, int n) {
        for (int i = 0; i < n; i++) {
            sh.autoSizeColumn(i);
            sh.setColumnWidth(i, Math.min(sh.getColumnWidth(i) + 800, 15000));
        }
    }
}
