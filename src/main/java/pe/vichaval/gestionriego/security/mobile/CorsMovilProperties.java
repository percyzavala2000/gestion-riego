package pe.vichaval.gestionriego.security.mobile;

import java.util.Arrays;
import java.util.List;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class CorsMovilProperties {

    private final boolean enabled;
    private final List<String> allowedOrigins;

    public CorsMovilProperties(
            @Value("${vichayal.mobile.cors.enabled:false}") boolean enabled,
            @Value("${vichayal.mobile.cors.allowed-origins:}") String origins
    ) {
        this.enabled = enabled;
        this.allowedOrigins = Arrays.stream(origins.split(","))
                .map(String::trim)
                .filter(value -> !value.isBlank())
                .toList();
    }

    public boolean isEnabled() {
        return enabled;
    }

    public List<String> getAllowedOrigins() {
        return allowedOrigins;
    }
}
