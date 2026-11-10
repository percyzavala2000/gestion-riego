package pe.vichaval.gestionriego.reporte.controller;

import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;

import pe.vichaval.gestionriego.reporte.dto.ReporteFiltro;
import pe.vichaval.gestionriego.reporte.service.ReporteExcelService;
import pe.vichaval.gestionriego.reporte.service.ReportePdfService;
import pe.vichaval.gestionriego.reporte.service.ReporteService;

@Controller
@RequestMapping("/reportes")
public class ReporteWebController {

    private final ReporteService reportes;
    private final ReporteExcelService excel;
    private final ReportePdfService pdf;

    public ReporteWebController(
            ReporteService reportes,
            ReporteExcelService excel,
            ReportePdfService pdf
    ) {
        this.reportes = reportes;
        this.excel = excel;
        this.pdf = pdf;
    }

    @GetMapping
    public String index(Model model) {
        model.addAttribute("resumen", reportes.resumen());
        return "reportes/index";
    }

    @PreAuthorize("hasAnyRole('ADMINISTRADOR','OPERADOR')")
    @GetMapping("/operativos")
    public String operativos(
            @ModelAttribute("filtro") ReporteFiltro filtro,
            Model model
    ) {
        try {
            model.addAttribute("agricultores", reportes.agricultores(filtro));
            model.addAttribute("parcelas", reportes.parcelas(filtro));
            model.addAttribute("turnos", reportes.turnos(filtro));
        } catch (IllegalArgumentException exception) {
            model.addAttribute("error", exception.getMessage());
            model.addAttribute("agricultores", java.util.List.of());
            model.addAttribute("parcelas", java.util.List.of());
            model.addAttribute("turnos", java.util.List.of());
        }
        model.addAttribute("agricultoresSelector", reportes.agricultores());
        model.addAttribute("parcelasSelector", reportes.parcelas());
        return "reportes/operativos";
    }

    @PreAuthorize("hasAnyRole('ADMINISTRADOR','TESORERIA')")
    @GetMapping("/financieros")
    public String financieros(
            @ModelAttribute("filtro") ReporteFiltro filtro,
            Model model
    ) {
        model.addAttribute("pagos", reportes.pagos(filtro));
        model.addAttribute("cuentas", reportes.cuentas(filtro));
        model.addAttribute("agricultores", reportes.agricultores());
        return "reportes/financieros";
    }

    @PreAuthorize("hasAnyRole('ADMINISTRADOR','TESORERIA')")
    @GetMapping("/financieros/pagos.xlsx")
    public ResponseEntity<byte[]> pagosExcel(@ModelAttribute ReporteFiltro filtro) {
        return archivo(
                excel.pagos(reportes.pagos(filtro)),
                "pagos.xlsx",
                MediaType.parseMediaType(
                        "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"
                )
        );
    }

    @PreAuthorize("hasAnyRole('ADMINISTRADOR','TESORERIA')")
    @GetMapping("/financieros/cuentas.xlsx")
    public ResponseEntity<byte[]> cuentasExcel(@ModelAttribute ReporteFiltro filtro) {
        return archivo(
                excel.cuentas(reportes.cuentas(filtro)),
                "cuentas-cobrar.xlsx",
                MediaType.parseMediaType(
                        "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"
                )
        );
    }

    @PreAuthorize("hasAnyRole('ADMINISTRADOR','TESORERIA')")
    @GetMapping("/financieros/pagos.pdf")
    public ResponseEntity<byte[]> pagosPdf(@ModelAttribute ReporteFiltro filtro) {
        return archivo(
                pdf.pagos(reportes.pagos(filtro)),
                "pagos.pdf",
                MediaType.APPLICATION_PDF
        );
    }

    @PreAuthorize("hasAnyRole('ADMINISTRADOR','TESORERIA')")
    @GetMapping("/financieros/cuentas.pdf")
    public ResponseEntity<byte[]> cuentasPdf(@ModelAttribute ReporteFiltro filtro) {
        return archivo(
                pdf.cuentas(reportes.cuentas(filtro)),
                "cuentas-cobrar.pdf",
                MediaType.APPLICATION_PDF
        );
    }

    private ResponseEntity<byte[]> archivo(
            byte[] data,
            String nombre,
            MediaType tipo
    ) {
        return ResponseEntity.ok()
                .contentType(tipo)
                .header(
                        HttpHeaders.CONTENT_DISPOSITION,
                        "attachment; filename=\"" + nombre + "\""
                )
                .body(data);
    }
}
