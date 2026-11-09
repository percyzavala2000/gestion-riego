package pe.vichaval.gestionriego.api.v1.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.*;

@Configuration
public class OpenApiConfig {
    @Bean
    OpenAPI vichayalOpenApi() {
        return new OpenAPI().info(new Info().title("Vichayal Riego API").version("v1 estable para móvil")
                .description("Contrato backend v10.5 para React Native Android e iOS: autenticación Bearer, refresh rotatorio, datos privados, notificaciones y dispositivos push."));
    }
}
