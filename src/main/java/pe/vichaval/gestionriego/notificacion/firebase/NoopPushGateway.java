package pe.vichaval.gestionriego.notificacion.firebase;

import java.util.Map;

import org.springframework.context.annotation.Conditional;
import org.springframework.stereotype.Component;

/**
 * Gateway seguro para demo, pruebas y producción sin credenciales Firebase.
 * Mantiene el contexto activo y registra el envío como error temporal.
 */
@Component
@Conditional(FirebaseDisabledCondition.class)
public class NoopPushGateway implements PushGateway {

    @Override
    public Resultado enviar(
            String token,
            String titulo,
            String cuerpo,
            Map<String, String> datos
    ) {
        return Resultado.temporal(
                "FIREBASE_DISABLED",
                "Firebase desactivado"
        );
    }
}
