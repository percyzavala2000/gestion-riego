package pe.vichaval.gestionriego.notificacion.push.entity;
import jakarta.persistence.*;
import java.time.OffsetDateTime;
import pe.vichaval.gestionriego.security.agricultor.entity.CuentaAgricultor;
@Entity @Table(name="dispositivos_push")
public class DispositivoPush {
 @Id @GeneratedValue(strategy=GenerationType.IDENTITY) private Long id;
 @ManyToOne(fetch=FetchType.LAZY,optional=false) @JoinColumn(name="cuenta_agricultor_id",nullable=false) private CuentaAgricultor cuenta;
 @Column(name="firebase_installation_id",nullable=false,length=180) private String installationId;
 @Column(name="token_registro",nullable=false,length=4096) private String tokenRegistro;
 @Column(nullable=false,length=15) private String plataforma;
 @Column(name="nombre_dispositivo",length=120) private String nombreDispositivo;
 @Column(name="version_aplicacion",length=30) private String versionAplicacion;
 @Column(name="version_sistema",length=40) private String versionSistema;
 @Column(name="modelo_dispositivo",length=100) private String modeloDispositivo;
 @Column(length=20) private String idioma;
 @Column(name="zona_horaria",length=60) private String zonaHoraria;
 @Column(nullable=false) private boolean activo=true;
 @Column(name="fecha_registro",nullable=false) private OffsetDateTime fechaRegistro=OffsetDateTime.now();
 @Column(name="fecha_actualizacion_token",nullable=false) private OffsetDateTime fechaActualizacionToken=OffsetDateTime.now();
 @Column(name="fecha_ultimo_uso",nullable=false) private OffsetDateTime fechaUltimoUso=OffsetDateTime.now();
 @Column(name="fecha_desactivacion") private OffsetDateTime fechaDesactivacion;
 protected DispositivoPush(){}
 public DispositivoPush(CuentaAgricultor cuenta,String installationId){this.cuenta=cuenta;this.installationId=installationId;}
 public void actualizar(String token,String plataforma,String nombre,String app,String sistema,String modelo,String idioma,String zona){this.tokenRegistro=token;this.plataforma=plataforma;this.nombreDispositivo=nombre;this.versionAplicacion=app;this.versionSistema=sistema;this.modeloDispositivo=modelo;this.idioma=idioma;this.zonaHoraria=zona;this.activo=true;this.fechaActualizacionToken=OffsetDateTime.now();this.fechaUltimoUso=this.fechaActualizacionToken;this.fechaDesactivacion=null;}
 public void desactivar(){activo=false;fechaDesactivacion=OffsetDateTime.now();}
 public Long getId(){return id;} public CuentaAgricultor getCuenta(){return cuenta;} public String getInstallationId(){return installationId;} public String getTokenRegistro(){return tokenRegistro;} public String getPlataforma(){return plataforma;} public String getNombreDispositivo(){return nombreDispositivo;} public String getVersionAplicacion(){return versionAplicacion;} public boolean isActivo(){return activo;} public OffsetDateTime getFechaUltimoUso(){return fechaUltimoUso;}
}
