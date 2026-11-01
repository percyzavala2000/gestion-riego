package pe.vichaval.gestionriego.reporte.service;

import java.io.ByteArrayOutputStream;
import java.time.LocalDate;
import java.util.List;

import com.lowagie.text.*;
import com.lowagie.text.pdf.*;
import org.springframework.stereotype.Service;
import pe.vichaval.gestionriego.finanzas.entity.CuentaCobrar;
import pe.vichaval.gestionriego.tesoreria.entity.Pago;

@Service
public class ReportePdfService {
    public byte[] pagos(List<Pago> datos) {
        return crear("Reporte de pagos", new String[]{"Código", "Fecha", "Agricultor", "Monto", "Estado"}, datos.stream().map(p -> new String[]{p.getCodigo(), p.getFecha().toString(), p.getAgricultor().getNombres() + " " + p.getAgricultor().getApellidos(), "S/ " + p.getMonto(), p.getEstado()}).toList());
    }

    public byte[] cuentas(List<CuentaCobrar> datos) {
        return crear("Reporte de cuentas por cobrar", new String[]{"Código", "Agricultor", "Total", "Saldo", "Estado"}, datos.stream().map(c -> new String[]{c.getCodigo(), c.getAgricultor().getNombres() + " " + c.getAgricultor().getApellidos(), "S/ " + c.getMontoTotal(), "S/ " + c.getSaldoPendiente(), c.getEstado()}).toList());
    }

    private byte[] crear(String titulo, String[] columnas, List<String[]> filas) {
        try (ByteArrayOutputStream out = new ByteArrayOutputStream()) {
            Document doc = new Document(PageSize.A4.rotate(), 28, 28, 28, 28);
            PdfWriter.getInstance(doc, out);
            doc.open();
            Font title = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 16);
            doc.add(new Paragraph(titulo, title));
            doc.add(new Paragraph("Vichayal Riego Web · Generado: " + LocalDate.now()));
            doc.add(Chunk.NEWLINE);
            PdfPTable table = new PdfPTable(columnas.length);
            table.setWidthPercentage(100);
            for (String c : columnas) {
                PdfPCell cell = new PdfPCell(new Phrase(c, FontFactory.getFont(FontFactory.HELVETICA_BOLD, 9)));
                cell.setBackgroundColor(new java.awt.Color(225, 239, 233));
                table.addCell(cell);
            }
            for (String[] fila : filas)
                for (String valor : fila)
                    table.addCell(new Phrase(valor == null ? "" : valor, FontFactory.getFont(FontFactory.HELVETICA, 8)));
            doc.add(table);
            doc.close();
            return out.toByteArray();
        } catch (Exception e) {
            throw new IllegalStateException("No se pudo generar el PDF", e);
        }
    }
}
