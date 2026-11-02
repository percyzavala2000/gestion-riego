package pe.vichaval.gestionriego.auditoria.web;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.UUID;
import org.slf4j.MDC;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import pe.vichaval.gestionriego.auditoria.entity.EventoAuditoria;
import pe.vichaval.gestionriego.auditoria.service.AuditoriaService;

@Component
public class AuditoriaFilter extends OncePerRequestFilter {
    public static final String CORRELACION_HEADER = "X-Correlation-Id";
    private final AuditoriaService auditoria;

    public AuditoriaFilter(AuditoriaService auditoria) {
        this.auditoria = auditoria;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response,
            FilterChain chain) throws ServletException, IOException {
        String correlacion = normalizarCorrelacion(request.getHeader(CORRELACION_HEADER));
        response.setHeader(CORRELACION_HEADER, correlacion);
        request.setAttribute(CORRELACION_HEADER, correlacion);
        MDC.put("correlationId", correlacion);
        try {
            chain.doFilter(request, response);
        } finally {
            try {
                if (SolicitudAuditable.evaluar(request.getMethod(), request.getRequestURI())) {
                    registrarSinInterrumpir(request, response, correlacion);
                }
            } finally {
                MDC.remove("correlationId");
            }
        }
    }

    private void registrarSinInterrumpir(HttpServletRequest request, HttpServletResponse response, String correlacion) {
        try {
            Authentication auth = SecurityContextHolder.getContext().getAuthentication();
            String usuario = auth != null && auth.isAuthenticated() ? auth.getName() : "ANONIMO";
            String rol = auth == null ? null : auth.getAuthorities().stream().findFirst()
                    .map(a -> a.getAuthority().replace("ROLE_", "")).orElse(null);
            auditoria.registrar(new EventoAuditoria(
                    correlacion, usuario, rol, request.getMethod(), request.getRequestURI(),
                    response.getStatus(), clienteIp(request), request.getHeader("User-Agent")));
        } catch (RuntimeException ignored) {
            logger.warn("No se pudo persistir un evento de auditoria", ignored);
        }
    }

    static String normalizarCorrelacion(String valor) {
        if (valor != null && valor.matches("[A-Za-z0-9-]{8,36}")) return valor;
        return UUID.randomUUID().toString();
    }

    private String clienteIp(HttpServletRequest request) {
        String forwarded = request.getHeader("X-Forwarded-For");
        if (forwarded != null && !forwarded.isBlank()) return forwarded.split(",")[0].trim();
        return request.getRemoteAddr();
    }
}
