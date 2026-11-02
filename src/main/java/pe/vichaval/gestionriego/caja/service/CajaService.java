package pe.vichaval.gestionriego.caja.service;

import java.math.BigDecimal;import java.time.LocalDate;import java.util.List;
import org.springframework.stereotype.Service;import org.springframework.transaction.annotation.Transactional;
import pe.vichaval.gestionriego.caja.dto.*;import pe.vichaval.gestionriego.caja.entity.*;import pe.vichaval.gestionriego.caja.repository.*;import pe.vichaval.gestionriego.infraestructura.CorrelativoService;import pe.vichaval.gestionriego.tesoreria.repository.PagoRepository;import pe.vichaval.gestionriego.usuario.repository.UsuarioRepository;

@Service @Transactional
public class CajaService {
    private final EgresoRepository egresos;private final DevolucionRepository devoluciones;private final CierreCajaRepository cierres;private final PagoRepository pagos;private final UsuarioRepository usuarios;private final CorrelativoService correlativos;private final ControlCierreCajaService control;
    public CajaService(EgresoRepository e,DevolucionRepository d,CierreCajaRepository c,PagoRepository p,UsuarioRepository u,CorrelativoService r,ControlCierreCajaService control){egresos=e;devoluciones=d;cierres=c;pagos=p;usuarios=u;correlativos=r;this.control=control;}
    public List<Egreso> egresos(){return egresos.findAllByOrderByFechaDescIdDesc();} public List<Devolucion> devoluciones(){return devoluciones.findAllByOrderByFechaDescIdDesc();} public List<CierreCaja> cierres(){return cierres.findAllByOrderByFechaDesc();}
    public void crearEgreso(EgresoForm f,Long uid){control.validarFechaAbierta(f.getFecha());if(egresos.existsByComprobanteIgnoreCase(f.getComprobante()))throw new IllegalArgumentException("Comprobante duplicado");egresos.save(new Egreso(correlativos.siguiente("seq_egreso_codigo","EG"),f,usuarios.findById(uid).orElseThrow()));}
    public void anularEgreso(Long id,String motivo){Egreso e=egresos.findById(id).orElseThrow();control.validarFechaAbierta(e.getFecha());e.anular(motivo);}
    public void solicitar(DevolucionForm f,Long uid){var pago=pagos.findById(f.getPagoId()).orElseThrow(()->new IllegalArgumentException("Pago no encontrado"));if(!"CONFIRMADO".equals(pago.getEstado()))throw new IllegalStateException("Solo pagos confirmados");BigDecimal disponible=pago.getMonto().subtract(devoluciones.totalPorPago(pago.getId()));if(f.getMonto().compareTo(disponible)>0)throw new IllegalStateException("La devolución supera el monto disponible");devoluciones.save(new Devolucion(correlativos.siguiente("seq_devolucion_codigo","DV"),pago,f.getMonto(),f.getMotivo(),usuarios.findById(uid).orElseThrow()));}
    public void aprobar(Long id){devoluciones.findById(id).orElseThrow().aprobar();}
    public void ejecutar(Long id){Devolucion d=devoluciones.findById(id).orElseThrow();control.validarFechaAbierta(d.getFecha());d.ejecutar();if(d.getPago().estaVinculadoACuenta())d.getPago().getCuentaCobrar().revertirPago(d.getMonto());}
    public void rechazar(Long id){devoluciones.findById(id).orElseThrow().rechazar();}
    public void cerrar(CierreCajaForm f,Long uid){BigDecimal i=ingresosFecha(f.getFecha());BigDecimal e=egresos.totalFecha(f.getFecha());BigDecimal d=devoluciones.totalFecha(f.getFecha());var usuario=usuarios.findById(uid).orElseThrow();var existente=cierres.findByFecha(f.getFecha());if(existente.isPresent()){if("CERRADO".equals(existente.get().getEstado()))throw new IllegalStateException("Ya existe cierre para la fecha");existente.get().cerrar(f.getSaldoInicial(),i,e,d,f.getSaldoContado(),f.getObservaciones(),usuario);}else cierres.save(new CierreCaja(f.getFecha(),f.getSaldoInicial(),i,e,d,f.getSaldoContado(),f.getObservaciones(),usuario));}
    public void reabrir(Long id,String motivo,Long uid){cierres.findById(id).orElseThrow().reabrir(motivo,usuarios.findById(uid).orElseThrow());}
    private BigDecimal ingresosFecha(LocalDate f){return pagos.findAll().stream().filter(p->f.equals(p.getFecha())&&"CONFIRMADO".equals(p.getEstado())).map(p->p.getMonto()).reduce(BigDecimal.ZERO,BigDecimal::add);}
}
