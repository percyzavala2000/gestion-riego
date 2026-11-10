package pe.vichaval.gestionriego.reporte;
import static org.junit.jupiter.api.Assertions.*;import java.util.List;import org.apache.poi.xssf.usermodel.XSSFWorkbook;import org.junit.jupiter.api.Test;import pe.vichaval.gestionriego.reporte.dto.ReporteFiltro;import pe.vichaval.gestionriego.reporte.service.*;
class ReporteExportacionTest {
 @Test void excelPagosIncluyeCriteriosYDatos(){byte[] b=new ReporteExcelService().pagos(List.of(),new ReporteFiltro(),"admin");assertTrue(b.length>0);assertDoesNotThrow(()->{try(var w=new XSSFWorkbook(new java.io.ByteArrayInputStream(b))){assertNotNull(w.getSheet("Criterios"));assertNotNull(w.getSheet("Pagos"));}});}
 @Test void pdfPagosEsValido(){byte[] b=new ReportePdfService().pagos(List.of(),new ReporteFiltro(),"admin");assertTrue(b.length>4);assertEquals('%',b[0]);assertEquals('P',b[1]);assertEquals('D',b[2]);assertEquals('F',b[3]);}
}
