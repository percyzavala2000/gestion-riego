package pe.vichaval.gestionriego.error;

import jakarta.servlet.http.HttpServletRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.servlet.resource.NoResourceFoundException;

import pe.vichaval.gestionriego.auditoria.web.AuditoriaFilter;

@ControllerAdvice
public class GlobalExceptionHandler {

    private static final Logger log =
            LoggerFactory.getLogger(GlobalExceptionHandler.class);

    @ExceptionHandler({
            IllegalArgumentException.class,
            IllegalStateException.class
    })
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public String operacionInvalida(
            RuntimeException ex,
            HttpServletRequest request,
            Model model) {

        completar(
                model,
                request,
                "No fue posible completar la operación",
                ex.getMessage()
        );

        return "error/operacion";
    }

    @ExceptionHandler(NoResourceFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public String recursoNoEncontrado(
            NoResourceFoundException ex,
            HttpServletRequest request,
            Model model) {

        completar(
                model,
                request,
                "Recurso no encontrado",
                "La página o el recurso solicitado no existe."
        );

        return "error/operacion";
    }

    @ExceptionHandler(Exception.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public String inesperado(
            Exception ex,
            HttpServletRequest request,
            Model model) {

        String correlacion = correlacion(request);

        log.error(
                "Error inesperado. correlacion={}",
                correlacion,
                ex
        );

        completar(
                model,
                request,
                "Ocurrió un error inesperado",
                "Comunícate con soporte e indica el código de seguimiento."
        );

        return "error/operacion";
    }

    private void completar(
            Model model,
            HttpServletRequest request,
            String titulo,
            String mensaje) {

        model.addAttribute("titulo", titulo);

        model.addAttribute(
                "mensaje",
                mensaje == null || mensaje.isBlank()
                        ? "La solicitud no pudo procesarse."
                        : mensaje
        );

        model.addAttribute(
                "correlacion",
                correlacion(request)
        );
    }

    private String correlacion(HttpServletRequest request) {

        Object valor = request.getAttribute(
                AuditoriaFilter.CORRELACION_HEADER
        );

        return valor == null
                ? "NO-DISPONIBLE"
                : valor.toString();
    }
}
