package pe.vichaval.gestionriego.reporte.service;

import java.math.BigDecimal;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pe.vichaval.gestionriego.agricultor.entity.Agricultor;
import pe.vichaval.gestionriego.agricultor.repository.AgricultorRepository;
import pe.vichaval.gestionriego.finanzas.entity.CuentaCobrar;
import pe.vichaval.gestionriego.finanzas.repository.CuentaCobrarRepository;
import pe.vichaval.gestionriego.parcela.entity.Parcela;
import pe.vichaval.gestionriego.parcela.repository.ParcelaRepository;
import pe.vichaval.gestionriego.reporte.dto.ReporteFiltro;
import pe.vichaval.gestionriego.reporte.dto.ResumenReportes;
import pe.vichaval.gestionriego.riego.entity.TurnoRiego;
import pe.vichaval.gestionriego.riego.repository.TurnoRiegoRepository;
import pe.vichaval.gestionriego.tesoreria.entity.Pago;
import pe.vichaval.gestionriego.tesoreria.repository.PagoRepository;

@Service
@Transactional(readOnly = true)
public class ReporteService {
    private final AgricultorRepository agricultores;
    private final ParcelaRepository parcelas;
    private final TurnoRiegoRepository turnos;
    private final PagoRepository pagos;
    private final CuentaCobrarRepository cuentas;

    public ReporteService(AgricultorRepository agricultores, ParcelaRepository parcelas,
                          TurnoRiegoRepository turnos, PagoRepository pagos, CuentaCobrarRepository cuentas) {
        this.agricultores = agricultores;
        this.parcelas = parcelas;
        this.turnos = turnos;
        this.pagos = pagos;
        this.cuentas = cuentas;
    }

    public ResumenReportes resumen() {
        List<CuentaCobrar> lista = cuentas.findAll();
        lista.forEach(CuentaCobrar::actualizarVencida);
        long pendientes = lista.stream().filter(c -> "PENDIENTE".equals(c.getEstado()) || "PAGO_PARCIAL".equals(c.getEstado())).count();
        long vencidas = lista.stream().filter(c -> "VENCIDA".equals(c.getEstado())).count();
        BigDecimal ingresos = pagos.total() == null ? BigDecimal.ZERO : pagos.total();
        BigDecimal deuda = cuentas.totalDeuda() == null ? BigDecimal.ZERO : cuentas.totalDeuda();
        return new ResumenReportes(agricultores.count(), parcelas.count(), turnos.count(), pagos.count(), ingresos, deuda, pendientes, vencidas);
    }

    public List<Agricultor> agricultores() {
        return agricultores.findAll();
    }

    public List<Parcela> parcelas() {
        return parcelas.findAll();
    }

    public List<TurnoRiego> turnos(ReporteFiltro f) {
        return turnos.findAll().stream()
                .filter(t -> f.getDesde() == null || !t.getFecha().isBefore(f.getDesde()))
                .filter(t -> f.getHasta() == null || !t.getFecha().isAfter(f.getHasta()))
                .filter(t -> vacio(f.getEstado()) || f.getEstado().equals(t.getEstado()))
                .filter(t -> f.getAgricultorId() == null || f.getAgricultorId().equals(t.getParcela().getAgricultor().getId()))
                .toList();
    }

    public List<Pago> pagos(ReporteFiltro f) {
        return pagos.findAll().stream()
                .filter(p -> f.getDesde() == null || !p.getFecha().isBefore(f.getDesde()))
                .filter(p -> f.getHasta() == null || !p.getFecha().isAfter(f.getHasta()))
                .filter(p -> vacio(f.getEstado()) || f.getEstado().equals(p.getEstado()))
                .filter(p -> f.getAgricultorId() == null || f.getAgricultorId().equals(p.getAgricultor().getId()))
                .toList();
    }

    public List<CuentaCobrar> cuentas(ReporteFiltro f) {
        List<CuentaCobrar> lista = cuentas.findAll();
        lista.forEach(CuentaCobrar::actualizarVencida);
        return lista.stream()
                .filter(c -> f.getDesde() == null || !c.getFechaEmision().isBefore(f.getDesde()))
                .filter(c -> f.getHasta() == null || !c.getFechaEmision().isAfter(f.getHasta()))
                .filter(c -> vacio(f.getEstado()) || f.getEstado().equals(c.getEstado()))
                .filter(c -> f.getAgricultorId() == null || f.getAgricultorId().equals(c.getAgricultor().getId()))
                .toList();
    }

    private boolean vacio(String s) {
        return s == null || s.isBlank();
    }
}
