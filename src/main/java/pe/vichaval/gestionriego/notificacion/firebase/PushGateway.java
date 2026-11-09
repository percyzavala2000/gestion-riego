package pe.vichaval.gestionriego.notificacion.firebase;
import java.util.Map;
public interface PushGateway {
 Resultado enviar(String token,String titulo,String cuerpo,Map<String,String> datos);
 record Resultado(boolean enviado,boolean definitivo,String mensajeId,String codigo,String detalle){
  public static Resultado ok(String id){return new Resultado(true,false,id,null,null);}
  public static Resultado temporal(String codigo,String detalle){return new Resultado(false,false,null,codigo,detalle);}
  public static Resultado definitivo(String codigo,String detalle){return new Resultado(false,true,null,codigo,detalle);}
 }
}
