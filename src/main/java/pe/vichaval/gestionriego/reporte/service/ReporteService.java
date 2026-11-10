package pe.vichaval.gestionriego.reporte.service;

import java.math.BigDecimal;
import java.util.Comparator;
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

    public ReporteService(
            AgricultorRepository agricultores,
            ParcelaRepository parcelas,
            TurnoRiegoRepository turnos,
            PagoRepository pagos,
            CuentaCobrarRepository cuentas
    ) {
        this.agricultores = agricultores;
        this.parcelas = parcelas;
        this.turnos = turnos;
        this.pagos = pagos;
        this.cuentas = cuentas;
    }

    public ResumenReportes resumen() {
        List<CuentaCobrar> lista = cuentas.findAll();
        lista.forEach(CuentaCobrar::actualizarVencida);
        long pendientes = lista.stream()
                .filter(c -> "PENDIENTE".equals(c.getEstado())
                        || "PAGO_PARCIAL".equals(c.getEstado()))
                .count();
        long vencidas = lista.stream()
                .filter(c -> "VENCIDA".equals(c.getEstado()))
                .count();
        BigDecimal ingresos = pagos.total() == null
                ? BigDecimal.ZERO : pagos.total();
        BigDecimal deuda = cuentas.totalDeuda() == null
                ? BigDecimal.ZERO : cuentas.totalDeuda();
        return new ResumenReportes(
                agricultores.count(), parcelas.count(), turnos.count(),
                pagos.count(), ingresos, deuda, pendientes, vencidas
        );
    }

    public List<Agricultor> agricultores() {
        return agricultores.findAll();
    }

    public List<Agricultor> agricultores(ReporteFiltro filtro) {
        filtro.validarRango();
        String q = filtro.textoNormalizado();
        String estado = filtro.estadoNormalizado();
        String sector = filtro.sectorNormalizado();

        return agricultores.findAll().stream()
                .filter(a -> q.isBlank()
                        || contiene(a.getDni(), q)
                        || contiene(a.getNombres(), q)
                        || contiene(a.getApellidos(), q))
                .filter(a -> estado.isBlank() || estado.equals(a.getEstado()))
                .filter(a -> sector.isBlank()
                        || normalizar(a.getSector()).equals(sector))
                .sorted(Comparator.comparing(Agricultor::getApellidos,
                                String.CASE_INSENSITIVE_ORDER)
                        .thenComparing(Agricultor::getNombres,
                                String.CASE_INSENSITIVE_ORDER))
                .toList();
    }

    public List<Parcela> parcelas() {
        return parcelas.findAll();
    }

    public List<Parcela> parcelas(ReporteFiltro filtro) {
        filtro.validarRango();
        String q = filtro.textoNormalizado();
        String estado = filtro.estadoNormalizado();
        String sector = filtro.sectorNormalizado();
        String cultivo = filtro.cultivoNormalizado();

        return parcelas.findAll().stream()
                .filter(p -> q.isBlank()
                        || contiene(p.getCodigo(), q)
                        || contiene(p.getNombre(), q)
                        || contiene(p.getCultivo(), q)
                        || contiene(p.getAgricultor().getNombres(), q)
                        || contiene(p.getAgricultor().getApellidos(), q))
                .filter(p -> estado.isBlank() || estado.equals(p.getEstado()))
                .filter(p -> sector.isBlank()
                        || normalizar(p.getAgricultor().getSector()).equals(sector))
                .filter(p -> cultivo.isBlank()
                        || contiene(p.getCultivo(), cultivo))
                .filter(p -> filtro.getAgricultorId() == null
                        || filtro.getAgricultorId().equals(
                                p.getAgricultor().getId()))
                .sorted(Comparator.comparing(Parcela::getCodigo,
                        String.CASE_INSENSITIVE_ORDER))
                .toList();
    }

    public List<TurnoRiego> turnos(ReporteFiltro filtro) {
        filtro.validarRango();
        String q = filtro.textoNormalizado();
        String estado = filtro.estadoNormalizado();
        String sector = filtro.sectorNormalizado();

        return turnos.findAll().stream()
                .filter(t -> filtro.getDesde() == null
                        || !t.getFecha().isBefore(filtro.getDesde()))
                .filter(t -> filtro.getHasta() == null
                        || !t.getFecha().isAfter(filtro.getHasta()))
                .filter(t -> estado.isBlank() || estado.equals(t.getEstado()))
                .filter(t -> filtro.getAgricultorId() == null
                        || filtro.getAgricultorId().equals(
                                t.getParcela().getAgricultor().getId()))
                .filter(t -> filtro.getParcelaId() == null
                        || filtro.getParcelaId().equals(t.getParcela().getId()))
                .filter(t -> sector.isBlank()
                        || normalizar(t.getParcela().getAgricultor().getSector())
                                .equals(sector))
                .filter(t -> q.isBlank()
                        || contiene(t.getCodigo(), q)
                        || contiene(t.getParcela().getNombre(), q)
                        || contiene(t.getParcela().getAgricultor().getNombres(), q)
                        || contiene(t.getParcela().getAgricultor().getApellidos(), q))
                .sorted(Comparator.comparing(TurnoRiego::getFecha).reversed()
                        .thenComparing(TurnoRiego::getHoraInicio).reversed())
                .toList();
    }

    public List<Pago> pagos(ReporteFiltro filtro) {
        filtro.validarRango();
        return pagos.findAll().stream()
                .filter(p -> filtro.getDesde() == null
                        || !p.getFecha().isBefore(filtro.getDesde()))
                .filter(p -> filtro.getHasta() == null
                        || !p.getFecha().isAfter(filtro.getHasta()))
                .filter(p -> filtro.estadoNormalizado().isBlank()
                        || filtro.estadoNormalizado().equals(p.getEstado()))
                .filter(p -> filtro.metodoNormalizado().isBlank()
                        || filtro.metodoNormalizado().equals(p.getMetodo()))
                .filter(p -> filtro.getAgricultorId() == null
                        || filtro.getAgricultorId().equals(
                                p.getAgricultor().getId()))
                .toList();
    }

    public List<CuentaCobrar> cuentas(ReporteFiltro filtro) {
        filtro.validarRango();
        List<CuentaCobrar> lista = cuentas.findAll();
        lista.forEach(CuentaCobrar::actualizarVencida);
        return lista.stream()
                .filter(c -> filtro.getDesde() == null
                        || !c.getFechaEmision().isBefore(filtro.getDesde()))
                .filter(c -> filtro.getHasta() == null
                        || !c.getFechaEmision().isAfter(filtro.getHasta()))
                .filter(c -> filtro.estadoNormalizado().isBlank()
                        || filtro.estadoNormalizado().equals(c.getEstado()))
                .filter(c -> filtro.getAgricultorId() == null
                        || filtro.getAgricultorId().equals(
                                c.getAgricultor().getId()))
                .toList();
    }

    private boolean contiene(String valor, String buscado) {
        return normalizar(valor).contains(buscado);
    }

    private String normalizar(String valor) {
        return valor == null ? "" : valor.trim().toLowerCase();
    }
}
