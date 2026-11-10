package pe.vichaval.gestionriego.reporte.service;

import java.math.BigDecimal;
import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
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
import pe.vichaval.gestionriego.web.PaginacionWeb;

@Service
@Transactional(readOnly=true)
public class ReporteService {
 private final AgricultorRepository agricultores; private final ParcelaRepository parcelas; private final TurnoRiegoRepository turnos; private final PagoRepository pagos; private final CuentaCobrarRepository cuentas;
 public ReporteService(AgricultorRepository a,ParcelaRepository p,TurnoRiegoRepository t,PagoRepository pg,CuentaCobrarRepository c){agricultores=a;parcelas=p;turnos=t;pagos=pg;cuentas=c;}
 public ResumenReportes resumen(){List<CuentaCobrar> l=cuentas.findAll();l.forEach(CuentaCobrar::actualizarVencida);long p=l.stream().filter(c->"PENDIENTE".equals(c.getEstado())||"PAGO_PARCIAL".equals(c.getEstado())).count();long v=l.stream().filter(c->"VENCIDA".equals(c.getEstado())).count();BigDecimal i=pagos.total()==null?BigDecimal.ZERO:pagos.total();BigDecimal d=cuentas.totalDeuda()==null?BigDecimal.ZERO:cuentas.totalDeuda();return new ResumenReportes(agricultores.count(),parcelas.count(),turnos.count(),pagos.count(),i,d,p,v);}
 public List<Agricultor> agricultores(){return agricultores.findAll();}
 public List<Parcela> parcelas(){return parcelas.findAll();}
 public Page<Agricultor> agricultoresPagina(ReporteFiltro f,int page,int size){f.validarRango();return agricultores.buscarReporte(f.textoNormalizado(),f.estadoNormalizado(),f.sectorNormalizado(),PageRequest.of(PaginacionWeb.pagina(page),PaginacionWeb.tamanio(size),Sort.by("apellidos","nombres")));}
 public Page<Parcela> parcelasPagina(ReporteFiltro f,int page,int size){f.validarRango();return parcelas.buscarReporte(f.textoNormalizado(),f.estadoNormalizado(),f.sectorNormalizado(),f.cultivoNormalizado(),f.getAgricultorId(),PageRequest.of(PaginacionWeb.pagina(page),PaginacionWeb.tamanio(size),Sort.by("codigo")));}
 public Page<TurnoRiego> turnosPagina(ReporteFiltro f,int page,int size){f.validarRango();return turnos.buscarReporte(f.textoNormalizado(),f.estadoNormalizado(),f.sectorNormalizado(),f.getAgricultorId(),f.getParcelaId(),f.getDesde(),f.getHasta(),PageRequest.of(PaginacionWeb.pagina(page),PaginacionWeb.tamanio(size),Sort.by(Sort.Order.desc("fecha"),Sort.Order.desc("horaInicio"))));}
 public List<TurnoRiego> turnos(ReporteFiltro f){f.validarRango();return turnos.buscarReporte(f.textoNormalizado(),f.estadoNormalizado(),f.sectorNormalizado(),f.getAgricultorId(),f.getParcelaId(),f.getDesde(),f.getHasta(),PageRequest.of(0,Integer.MAX_VALUE,Sort.by(Sort.Order.desc("fecha"),Sort.Order.desc("horaInicio")))).getContent();}
 public List<Pago> pagos(ReporteFiltro f){f.validarRango();return pagos.findAll().stream().filter(p->f.getDesde()==null||!p.getFecha().isBefore(f.getDesde())).filter(p->f.getHasta()==null||!p.getFecha().isAfter(f.getHasta())).filter(p->f.estadoNormalizado().isBlank()||f.estadoNormalizado().equals(p.getEstado())).filter(p->f.metodoNormalizado().isBlank()||f.metodoNormalizado().equals(p.getMetodo())).filter(p->f.getAgricultorId()==null||f.getAgricultorId().equals(p.getAgricultor().getId())).toList();}
 public List<CuentaCobrar> cuentas(ReporteFiltro f){f.validarRango();List<CuentaCobrar> l=cuentas.findAll();l.forEach(CuentaCobrar::actualizarVencida);return l.stream().filter(c->f.getDesde()==null||!c.getFechaEmision().isBefore(f.getDesde())).filter(c->f.getHasta()==null||!c.getFechaEmision().isAfter(f.getHasta())).filter(c->f.estadoNormalizado().isBlank()||f.estadoNormalizado().equals(c.getEstado())).filter(c->f.getAgricultorId()==null||f.getAgricultorId().equals(c.getAgricultor().getId())).toList();}
}
