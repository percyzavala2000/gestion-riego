package pe.vichaval.gestionriego.api.v1.error;

import java.net.URI;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.ConstraintViolationException;
import org.springframework.http.*;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.method.annotation.HandlerMethodValidationException;
import org.springframework.web.bind.annotation.*;
import pe.vichaval.gestionriego.auditoria.web.AuditoriaFilter;

@RestControllerAdvice(basePackages = "pe.vichaval.gestionriego.api")
public class ApiExceptionHandler {
    @ExceptionHandler(MethodArgumentNotValidException.class)
    ResponseEntity<ProblemDetail> validacion(MethodArgumentNotValidException ex, HttpServletRequest r) {
        var p = problema(HttpStatus.BAD_REQUEST, "Solicitud inválida", "Uno o más campos no cumplen las reglas de validación.", r);
        p.setProperty("errores", ex.getBindingResult().getFieldErrors().stream().collect(java.util.stream.Collectors.toMap(e -> e.getField(), e -> e.getDefaultMessage() == null ? "inválido" : e.getDefaultMessage(), (a, b) -> a)));
        return ResponseEntity.badRequest().body(p);
    }

    @ExceptionHandler(HandlerMethodValidationException.class)
    ResponseEntity<ProblemDetail> validacionParametros(
            HandlerMethodValidationException ex,
            HttpServletRequest r) {
        return ResponseEntity.badRequest().body(
                problema(
                        HttpStatus.BAD_REQUEST,
                        "Solicitud invalida",
                        "Uno o mas parametros no cumplen las reglas de validacion.",
                        r
                )
        );
    }

    @ExceptionHandler(ConstraintViolationException.class)
    ResponseEntity<ProblemDetail> violacionRestricciones(
            ConstraintViolationException ex,
            HttpServletRequest r) {
        var p = problema(
                HttpStatus.BAD_REQUEST,
                "Solicitud inválida",
                "Uno o más parámetros no cumplen las reglas de validación.",
                r
        );
        p.setProperty(
                "errores",
                ex.getConstraintViolations().stream()
                        .collect(java.util.stream.Collectors.toMap(
                                violation -> violation.getPropertyPath().toString(),
                                jakarta.validation.ConstraintViolation::getMessage,
                                (primero, segundo) -> primero
                        ))
        );
        return ResponseEntity.badRequest().body(p);
    }

    @ExceptionHandler(IllegalArgumentException.class)
    ResponseEntity<ProblemDetail> argumento(IllegalArgumentException ex, HttpServletRequest r) {
        var mensaje = ex.getMessage() == null ? "Solicitud inválida" : ex.getMessage();
        var status = mensaje.toLowerCase().contains("no encontr") ? HttpStatus.NOT_FOUND : HttpStatus.BAD_REQUEST;
        return ResponseEntity.status(status).body(problema(status, status == HttpStatus.NOT_FOUND ? "Recurso no encontrado" : "Solicitud inválida", mensaje, r));
    }

    @ExceptionHandler(IllegalStateException.class)
    ResponseEntity<ProblemDetail> conflicto(IllegalStateException ex, HttpServletRequest r) {
        return ResponseEntity.status(HttpStatus.CONFLICT).body(problema(HttpStatus.CONFLICT, "Conflicto de negocio", ex.getMessage(), r));
    }

    private ProblemDetail problema(HttpStatus s, String titulo, String detalle, HttpServletRequest r) {
        var p = ProblemDetail.forStatusAndDetail(s, detalle);
        p.setTitle(titulo);
        p.setType(URI.create("https://vichayal.pe/problems/" + s.value()));
        p.setInstance(URI.create(r.getRequestURI()));
        Object c = r.getAttribute(AuditoriaFilter.CORRELACION_HEADER);
        p.setProperty("correlationId", c == null ? "NO-DISPONIBLE" : c.toString());
        return p;
    }
}
