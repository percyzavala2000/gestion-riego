package pe.vichaval.gestionriego.reporte.controller;

import org.springframework.http.*;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import pe.vichaval.gestionriego.reporte.dto.ReporteFiltro;
import pe.vichaval.gestionriego.reporte.service.*;

@Controller
@RequestMapping("/reportes")
public class ReporteWebController {
    private final ReporteService reportes;
    private final ReporteExcelService excel;
    private final ReportePdfService pdf;

    public ReporteWebController(ReporteService r, ReporteExcelService e, ReportePdfService p) {
        reportes = r;
        excel = e;
        pdf = p;
    }

    @GetMapping
    public String index(Model m) {
        m.addAttribute("resumen", reportes.resumen());
        return "reportes/index";
    }

    @PreAuthorize("hasAnyRole('ADMINISTRADOR','OPERADOR')")
    @GetMapping("/operativos")
    public String operativos(Model m) {
        m.addAttribute("agricultores", reportes.agricultores());
        m.addAttribute("parcelas", reportes.parcelas());
        m.addAttribute("turnos", reportes.turnos(new ReporteFiltro()));
        return "reportes/operativos";
    }

    @PreAuthorize("hasAnyRole('ADMINISTRADOR','TESORERIA')")
    @GetMapping("/financieros")
    public String financieros(@ModelAttribute("filtro") ReporteFiltro f, Model m) {
        m.addAttribute("pagos", reportes.pagos(f));
        m.addAttribute("cuentas", reportes.cuentas(f));
        m.addAttribute("agricultores", reportes.agricultores());
        return "reportes/financieros";
    }

    @PreAuthorize("hasAnyRole('ADMINISTRADOR','TESORERIA')")
    @GetMapping("/financieros/pagos.xlsx")
    public ResponseEntity<byte[]> pagosExcel(@ModelAttribute ReporteFiltro f) {
        return archivo(excel.pagos(reportes.pagos(f)), "pagos.xlsx", MediaType.parseMediaType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"));
    }

    @PreAuthorize("hasAnyRole('ADMINISTRADOR','TESORERIA')")
    @GetMapping("/financieros/cuentas.xlsx")
    public ResponseEntity<byte[]> cuentasExcel(@ModelAttribute ReporteFiltro f) {
        return archivo(excel.cuentas(reportes.cuentas(f)), "cuentas-cobrar.xlsx", MediaType.parseMediaType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"));
    }

    @PreAuthorize("hasAnyRole('ADMINISTRADOR','TESORERIA')")
    @GetMapping("/financieros/pagos.pdf")
    public ResponseEntity<byte[]> pagosPdf(@ModelAttribute ReporteFiltro f) {
        return archivo(pdf.pagos(reportes.pagos(f)), "pagos.pdf", MediaType.APPLICATION_PDF);
    }

    @PreAuthorize("hasAnyRole('ADMINISTRADOR','TESORERIA')")
    @GetMapping("/financieros/cuentas.pdf")
    public ResponseEntity<byte[]> cuentasPdf(@ModelAttribute ReporteFiltro f) {
        return archivo(pdf.cuentas(reportes.cuentas(f)), "cuentas-cobrar.pdf", MediaType.APPLICATION_PDF);
    }

    private ResponseEntity<byte[]> archivo(byte[] data, String nombre, MediaType tipo) {
        return ResponseEntity.ok().contentType(tipo).header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + nombre + "\"").body(data);
    }
}
