package pe.vichaval.gestionriego.api.v1.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.*;

@Configuration
public class OpenApiConfig {
    @Bean
    OpenAPI vichayalOpenApi() {
        return new OpenAPI().info(new Info().title("Vichayal Riego API").version("v1").description("API REST v10.0 para operación de riego. La autenticación móvil con tokens se incorporará en v10.1."));
    }
}
