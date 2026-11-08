package pe.vichaval.gestionriego.api.v1.error;
import jakarta.servlet.http.HttpServletRequest; import java.net.URI; import org.springframework.http.*; import org.springframework.web.bind.annotation.*;
import pe.vichaval.gestionriego.auditoria.web.AuditoriaFilter; import pe.vichaval.gestionriego.security.agricultor.auth.AutenticacionAgricultorService;
@RestControllerAdvice(basePackages="pe.vichaval.gestionriego.api")
public class ApiSecurityExceptionHandler {
 @ExceptionHandler(AutenticacionAgricultorService.CredencialesInvalidasException.class)
 ResponseEntity<ProblemDetail> credenciales(RuntimeException ex,HttpServletRequest r){var p=ProblemDetail.forStatusAndDetail(HttpStatus.UNAUTHORIZED,"Credenciales o token inválido");p.setTitle("No autorizado");p.setType(URI.create("https://vichayal.pe/problems/401"));p.setInstance(URI.create(r.getRequestURI()));p.setProperty("correlationId",String.valueOf(r.getAttribute(AuditoriaFilter.CORRELACION_HEADER)));return ResponseEntity.status(401).body(p);}
}
