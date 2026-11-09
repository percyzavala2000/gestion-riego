package pe.vichaval.gestionriego.notificacion.firebase;
import com.google.firebase.messaging.*; import java.util.Map;
import org.springframework.context.annotation.Conditional; import org.springframework.stereotype.Component;
@Component @Conditional(FirebaseEnabledCondition.class)
public class FirebasePushGateway implements PushGateway {
 private final FirebaseMessaging firebase;
 public FirebasePushGateway(FirebaseMessaging firebase){this.firebase=firebase;}
 public Resultado enviar(String token,String titulo,String cuerpo,Map<String,String> datos){
  try{
   Message m=Message.builder().setToken(token).setNotification(Notification.builder().setTitle(titulo).setBody(cuerpo).build()).putAllData(datos).build();
   return Resultado.ok(firebase.send(m));
  }catch(FirebaseMessagingException e){
   MessagingErrorCode mc = e.getMessagingErrorCode();
   String codigo = mc != null
           ? mc.name()
           : (e.getErrorCode() != null
                   ? e.getErrorCode().name()
                   : "FIREBASE_ERROR");
   boolean finalError = mc == MessagingErrorCode.UNREGISTERED
           || mc == MessagingErrorCode.INVALID_ARGUMENT;
   return finalError?Resultado.definitivo(codigo,e.getMessage()):Resultado.temporal(codigo,e.getMessage());
  }
 }
}
