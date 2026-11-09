package pe.vichaval.gestionriego.notificacion.firebase;

import org.springframework.context.annotation.Condition;
import org.springframework.context.annotation.ConditionContext;
import org.springframework.core.type.AnnotatedTypeMetadata;

/** Activa el gateway local cuando Firebase está deshabilitado. */
public class FirebaseDisabledCondition implements Condition {

    @Override
    public boolean matches(
            ConditionContext context,
            AnnotatedTypeMetadata metadata
    ) {
        return !Boolean.parseBoolean(
                context.getEnvironment().getProperty(
                        "vichayal.firebase.enabled",
                        "false"
                )
        );
    }
}
