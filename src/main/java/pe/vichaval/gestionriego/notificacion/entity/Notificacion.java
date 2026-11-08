package pe.vichaval.gestionriego.notificacion.entity;
import jakarta.persistence.*; import java.time.OffsetDateTime;
import pe.vichaval.gestionriego.agricultor.entity.Agricultor; import pe.vichaval.gestionriego.usuario.entity.Usuario;
@Entity @Table(name="notificaciones")
public class Notificacion {
 @Id @GeneratedValue(strategy=GenerationType.IDENTITY) private Long id;
 @ManyToOne(fetch=FetchType.LAZY) @JoinColumn(name="agricultor_id") private Agricultor agricultor;
 @ManyToOne(fetch=FetchType.LAZY) @JoinColumn(name="usuario_id") private Usuario usuario;
 @Column(nullable=false,length=40) private String tipo; @Column(nullable=false,length=160) private String titulo;
 @Column(nullable=false,length=1000) private String mensaje; @Column(nullable=false,length=15) private String prioridad;
 @Column(nullable=false,length=15) private String estado="NO_LEIDA";
 @Column(name="referencia_tipo",length=40) private String referenciaTipo; @Column(name="referencia_id") private Long referenciaId;
 @Column(name="referencia_url",length=255) private String referenciaUrl;
 @Column(name="clave_idempotencia",nullable=false,unique=true,length=180) private String claveIdempotencia;
 @Column(name="fecha_creacion",nullable=false) private OffsetDateTime fechaCreacion=OffsetDateTime.now();
 @Column(name="fecha_lectura") private OffsetDateTime fechaLectura; @Column(name="fecha_archivado") private OffsetDateTime fechaArchivado;
 @Column(name="fecha_expiracion") private OffsetDateTime fechaExpiracion;
 protected Notificacion(){}
 private Notificacion(String tipo,String titulo,String mensaje,String prioridad,String refTipo,Long refId,String url,String clave,OffsetDateTime exp){this.tipo=tipo;this.titulo=titulo;this.mensaje=mensaje;this.prioridad=prioridad;this.referenciaTipo=refTipo;this.referenciaId=refId;this.referenciaUrl=url;this.claveIdempotencia=clave;this.fechaExpiracion=exp;}
 public static Notificacion paraAgricultor(Agricultor a,String tipo,String titulo,String mensaje,String prioridad,String refTipo,Long refId,String url,String clave,OffsetDateTime exp){var n=new Notificacion(tipo,titulo,mensaje,prioridad,refTipo,refId,url,clave,exp);n.agricultor=a;return n;}
 public static Notificacion paraUsuario(Usuario u,String tipo,String titulo,String mensaje,String prioridad,String refTipo,Long refId,String url,String clave,OffsetDateTime exp){var n=new Notificacion(tipo,titulo,mensaje,prioridad,refTipo,refId,url,clave,exp);n.usuario=u;return n;}
 public void leer(){if(!"ARCHIVADA".equals(estado)){estado="LEIDA";if(fechaLectura==null)fechaLectura=OffsetDateTime.now();}}
 public void archivar(){estado="ARCHIVADA";fechaArchivado=OffsetDateTime.now();if(fechaLectura==null)fechaLectura=fechaArchivado;}
 public Long getId(){return id;} public Agricultor getAgricultor(){return agricultor;} public Usuario getUsuario(){return usuario;} public String getTipo(){return tipo;} public String getTitulo(){return titulo;} public String getMensaje(){return mensaje;} public String getPrioridad(){return prioridad;} public String getEstado(){return estado;} public String getReferenciaTipo(){return referenciaTipo;} public Long getReferenciaId(){return referenciaId;} public String getReferenciaUrl(){return referenciaUrl;} public String getClaveIdempotencia(){return claveIdempotencia;} public OffsetDateTime getFechaCreacion(){return fechaCreacion;} public OffsetDateTime getFechaLectura(){return fechaLectura;}
}
