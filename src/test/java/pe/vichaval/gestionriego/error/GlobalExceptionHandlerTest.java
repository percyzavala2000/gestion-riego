package pe.vichaval.gestionriego.error;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;
import org.springframework.http.HttpMethod;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.ui.ConcurrentModel;
import org.springframework.web.servlet.resource.NoResourceFoundException;

import pe.vichaval.gestionriego.auditoria.web.AuditoriaFilter;

class GlobalExceptionHandlerTest {

    @Test
    void muestraCorrelacionSinExponerExcepcionInesperada() {

        var request = new MockHttpServletRequest();

        request.setAttribute(
                AuditoriaFilter.CORRELACION_HEADER,
                "corr-12345678"
        );

        var model = new ConcurrentModel();

        String vista = new GlobalExceptionHandler().inesperado(
                new RuntimeException("detalle interno"),
                request,
                model
        );

        assertEquals("error/operacion", vista);

        assertEquals(
                "corr-12345678",
                model.getAttribute("correlacion")
        );

        assertEquals(
                "Comunícate con soporte e indica el código de seguimiento.",
                model.getAttribute("mensaje")
        );
    }

    @Test
    void clasificaUnRecursoInexistenteSinTratarloComoErrorInterno() {

        var request = new MockHttpServletRequest();

        request.setAttribute(
                AuditoriaFilter.CORRELACION_HEADER,
                "corr-40400000"
        );

        var model = new ConcurrentModel();

        var excepcion = new NoResourceFoundException(
                HttpMethod.GET,
                "/ruta-inexistente",
                null
        );

        String vista = new GlobalExceptionHandler()
                .recursoNoEncontrado(
                        excepcion,
                        request,
                        model
                );

        assertEquals("error/operacion", vista);

        assertEquals(
                "Recurso no encontrado",
                model.getAttribute("titulo")
        );

        assertEquals(
                "La página o el recurso solicitado no existe.",
                model.getAttribute("mensaje")
        );

        assertEquals(
                "corr-40400000",
                model.getAttribute("correlacion")
        );
    }
}
