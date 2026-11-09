package pe.vichaval.gestionriego.security.mobile;

import java.util.List;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

@Configuration
public class CorsMovilConfig {

    @Bean
    CorsConfigurationSource corsConfigurationSource(
            CorsMovilProperties properties
    ) {
        CorsConfiguration config = new CorsConfiguration();

        if (properties.isEnabled()) {
            if (properties.getAllowedOrigins().isEmpty()) {
                throw new IllegalStateException(
                        "CORS móvil está habilitado, pero no tiene orígenes permitidos"
                );
            }
            config.setAllowedOrigins(properties.getAllowedOrigins());
        } else {
            config.setAllowedOrigins(List.of());
        }

        config.setAllowedMethods(List.of(
                "GET", "POST", "PUT", "PATCH", "DELETE", "OPTIONS"
        ));
        config.setAllowedHeaders(List.of(
                "Authorization", "Content-Type", "X-Correlation-Id"
        ));
        config.setExposedHeaders(List.of(
                "X-Correlation-Id", "Location", "Retry-After"
        ));
        config.setAllowCredentials(false);
        config.setMaxAge(3600L);

        UrlBasedCorsConfigurationSource source =
                new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/api/v1/**", config);
        return source;
    }
}
