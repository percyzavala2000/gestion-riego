package pe.vichaval.gestionriego.notificacion.service;
import java.time.*; import org.springframework.scheduling.annotation.Scheduled; import org.springframework.stereotype.Service; import org.springframework.transaction.annotation.Transactional;
import pe.vichaval.gestionriego.finanzas.repository.CuentaCobrarRepository; import pe.vichaval.gestionriego.riego.repository.TurnoRiegoRepository;
@Service public class GeneradorNotificacionesService {
 private final TurnoRiegoRepository turnos; private final CuentaCobrarRepository cuentas; private final NotificacionService notificaciones;
 public GeneradorNotificacionesService(TurnoRiegoRepository t,CuentaCobrarRepository c,NotificacionService n){turnos=t;cuentas=c;notificaciones=n;}
 @Scheduled(cron="${vichayal.notifications.generation-cron:0 15 6 * * *}",zone="America/Lima") @Transactional
 public int generar(){int[] n={0};LocalDate hoy=LocalDate.now();
  turnos.findAll().stream().filter(t->"PROGRAMADO".equals(t.getEstado())&&(!t.getFecha().isBefore(hoy))&&(!t.getFecha().isAfter(hoy.plusDays(1)))).forEach(t->{String clave="TURNO_PROXIMO:"+t.getId()+":"+t.getFecha();if(notificaciones.crearAgricultor(t.getParcela().getAgricultor().getId(),"TURNO_PROXIMO","Turno de riego próximo","Turno "+t.getCodigo()+" programado para "+t.getFecha()+" de "+t.getHoraInicio()+" a "+t.getHoraFin()+".","ALTA","TURNO",t.getId(),"/turnos",clave,OffsetDateTime.now().plusDays(7))!=null)n[0]++;});
  cuentas.findAll().stream().filter(c->!"PAGADA".equals(c.getEstado())&&!"ANULADA".equals(c.getEstado())&&!c.getFechaVencimiento().isAfter(hoy.plusDays(3))).forEach(c->{boolean vencida=c.getFechaVencimiento().isBefore(hoy);String tipo=vencida?"CUENTA_VENCIDA":"CUENTA_PROXIMA_VENCER";String clave=tipo+":"+c.getId()+":"+hoy;if(notificaciones.crearAgricultor(c.getAgricultor().getId(),tipo,vencida?"Cuenta vencida":"Cuenta próxima a vencer","La cuenta "+c.getCodigo()+" mantiene un saldo de S/ "+c.getSaldoPendiente()+" y vence el "+c.getFechaVencimiento()+".",vencida?"CRITICA":"ALTA","CUENTA",c.getId(),"/tesoreria/cuentas/"+c.getId(),clave,OffsetDateTime.now().plusDays(30))!=null)n[0]++;});return n[0];}
}
