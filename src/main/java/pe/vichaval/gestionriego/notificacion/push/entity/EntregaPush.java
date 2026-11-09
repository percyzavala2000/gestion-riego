package pe.vichaval.gestionriego.notificacion.push.entity;
import jakarta.persistence.*;
import java.time.OffsetDateTime;
import pe.vichaval.gestionriego.notificacion.entity.Notificacion;
@Entity @Table(name="entregas_push")
public class EntregaPush {
 @Id @GeneratedValue(strategy=GenerationType.IDENTITY) private Long id;
 @ManyToOne(fetch=FetchType.LAZY,optional=false) @JoinColumn(name="notificacion_id",nullable=false) private Notificacion notificacion;
 @ManyToOne(fetch=FetchType.LAZY,optional=false) @JoinColumn(name="dispositivo_push_id",nullable=false) private DispositivoPush dispositivo;
 @Column(nullable=false,length=20) private String estado="PENDIENTE";
 @Column(nullable=false,length=20) private String proveedor="FIREBASE";
 @Column(name="mensaje_proveedor_id",length=255) private String mensajeProveedorId;
 @Column(name="codigo_error",length=100) private String codigoError;
 @Column(name="detalle_error",length=600) private String detalleError;
 @Column(nullable=false) private int intentos;
 @Column(name="proximo_intento") private OffsetDateTime proximoIntento;
 @Column(name="fecha_creacion",nullable=false) private OffsetDateTime fechaCreacion=OffsetDateTime.now();
 @Column(name="fecha_envio") private OffsetDateTime fechaEnvio;
 @Column(name="fecha_actualizacion",nullable=false) private OffsetDateTime fechaActualizacion=OffsetDateTime.now();
 protected EntregaPush(){}
 public EntregaPush(Notificacion n,DispositivoPush d){notificacion=n;dispositivo=d;}
 public void enviada(String id){estado="ENVIADA";mensajeProveedorId=id;intentos++;fechaEnvio=OffsetDateTime.now();fechaActualizacion=fechaEnvio;proximoIntento=null;}
 public void errorTemporal(String codigo,String detalle,OffsetDateTime proximo){estado="ERROR_TEMPORAL";codigoError=codigo;detalleError=limitar(detalle);proximoIntento=proximo;intentos++;fechaActualizacion=OffsetDateTime.now();}
 public void errorDefinitivo(String codigo,String detalle){estado="ERROR_DEFINITIVO";codigoError=codigo;detalleError=limitar(detalle);proximoIntento=null;intentos++;fechaActualizacion=OffsetDateTime.now();}
 private String limitar(String s){return s==null?null:s.substring(0,Math.min(600,s.length()));}
 public Long getId(){return id;} public Notificacion getNotificacion(){return notificacion;} public DispositivoPush getDispositivo(){return dispositivo;} public String getEstado(){return estado;} public int getIntentos(){return intentos;}
}
