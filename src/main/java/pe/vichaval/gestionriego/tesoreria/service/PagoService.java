package pe.vichaval.gestionriego.tesoreria.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pe.vichaval.gestionriego.agricultor.repository.AgricultorRepository;
import pe.vichaval.gestionriego.caja.service.ControlCierreCajaService;
import pe.vichaval.gestionriego.finanzas.entity.CuentaCobrar;
import pe.vichaval.gestionriego.infraestructura.CorrelativoService;
import pe.vichaval.gestionriego.tesoreria.dto.PagoForm;
import pe.vichaval.gestionriego.tesoreria.entity.Pago;
import pe.vichaval.gestionriego.tesoreria.repository.PagoRepository;

@Service @Transactional
public class PagoService {
    private final PagoRepository pagos; private final AgricultorRepository agricultores; private final CorrelativoService correlativos; private final ControlCierreCajaService cierres;
    public PagoService(PagoRepository pagos,AgricultorRepository agricultores,CorrelativoService correlativos,ControlCierreCajaService cierres){this.pagos=pagos;this.agricultores=agricultores;this.correlativos=correlativos;this.cierres=cierres;}
    public Pago get(Long id){return pagos.findById(id).orElseThrow(()->new IllegalArgumentException("Pago no encontrado"));}
    public Pago registrar(PagoForm form){return registrar(form,null);}
    public Pago registrar(PagoForm form,CuentaCobrar cuenta){cierres.validarFechaAbierta(form.getFecha());if(pagos.existsByComprobante(form.getComprobante()))throw new IllegalArgumentException("Comprobante duplicado");var agricultor=agricultores.findById(form.getAgricultorId()).orElseThrow();Pago pago=new Pago(correlativos.siguiente("seq_pago_codigo","PG"),form,agricultor,cuenta);pagos.save(pago);if(cuenta!=null&&"CONFIRMADO".equals(pago.getEstado()))cuenta.aplicarPago(pago.getMonto());return pago;}
    public void confirmar(Long id){Pago pago=get(id);cierres.validarFechaAbierta(pago.getFecha());pago.confirmar();if(pago.estaVinculadoACuenta())pago.getCuentaCobrar().aplicarPago(pago.getMonto());}
    public void rechazar(Long id,String motivo){Pago pago=get(id);cierres.validarFechaAbierta(pago.getFecha());pago.rechazar(motivo);}
    public void anular(Long id,String motivo){Pago pago=get(id);cierres.validarFechaAbierta(pago.getFecha());pago.anular(motivo);if(pago.estaVinculadoACuenta())pago.getCuentaCobrar().revertirPago(pago.getMonto());}
}
