package pe.vichaval.gestionriego.security.mobile;

import java.io.IOException;
import java.time.Duration;
import java.time.Instant;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

@Component
public class RateLimitLoginFilter extends OncePerRequestFilter {

    private final Map<String, Ventana> ventanas = new ConcurrentHashMap<>();
    private final int maxSolicitudes;
    private final Duration duracion;

    public RateLimitLoginFilter(
            @Value("${vichayal.auth.rate-limit.max-requests:10}")
            int maxSolicitudes,
            @Value("${vichayal.auth.rate-limit.window-seconds:60}")
            long segundos
    ) {
        if (maxSolicitudes < 1 || segundos < 1) {
            throw new IllegalArgumentException(
                    "La configuración del rate limit debe ser positiva"
            );
        }
        this.maxSolicitudes = maxSolicitudes;
        this.duracion = Duration.ofSeconds(segundos);
    }

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) {
        return !"POST".equalsIgnoreCase(request.getMethod())
                || !"/api/v1/auth/login".equals(request.getRequestURI());
    }

    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain
    ) throws ServletException, IOException {
        Instant ahora = Instant.now();
        String clave = clienteIp(request);

        Ventana ventana = ventanas.compute(clave, (key, actual) -> {
            if (actual == null || actual.expirada(ahora, duracion)) {
                return new Ventana(ahora, 1);
            }
            return new Ventana(actual.inicio(), actual.cantidad() + 1);
        });

        if (ventana.cantidad() > maxSolicitudes) {
            long restante = Math.max(
                    1,
                    duracion.minus(
                            Duration.between(ventana.inicio(), ahora)
                    ).toSeconds()
            );

            response.setStatus(429);
            response.setContentType(MediaType.APPLICATION_PROBLEM_JSON_VALUE);
            response.setHeader("Retry-After", String.valueOf(restante));
            response.getWriter().write(
                    "{\"type\":\"https://vichayal.pe/problems/429\","
                            + "\"title\":\"Demasiadas solicitudes\","
                            + "\"status\":429,"
                            + "\"detail\":\"Espera antes de intentar iniciar sesión nuevamente.\"}"
            );
            return;
        }

        if (ventanas.size() > 10000) {
            ventanas.entrySet().removeIf(
                    entry -> entry.getValue().expirada(ahora, duracion)
            );
        }

        filterChain.doFilter(request, response);
    }

    private String clienteIp(HttpServletRequest request) {
        String forwarded = request.getHeader("X-Forwarded-For");
        if (forwarded != null && !forwarded.isBlank()) {
            return forwarded.split(",")[0].trim();
        }
        return request.getRemoteAddr();
    }

    record Ventana(Instant inicio, int cantidad) {
        boolean expirada(Instant ahora, Duration duracion) {
            return inicio.plus(duracion).isBefore(ahora);
        }
    }
}
