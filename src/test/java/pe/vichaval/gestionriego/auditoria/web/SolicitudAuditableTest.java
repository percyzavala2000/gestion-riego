package pe.vichaval.gestionriego.auditoria.web;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.Test;

class SolicitudAuditableTest {
    @Test
    void soloAuditaOperacionesQueModificanDatos() {
        assertTrue(SolicitudAuditable.evaluar("POST", "/tesoreria/pagos/guardar"));
        assertTrue(SolicitudAuditable.evaluar("DELETE", "/usuarios/9"));
        assertFalse(SolicitudAuditable.evaluar("GET", "/tesoreria"));
        assertFalse(SolicitudAuditable.evaluar("POST", "/login"));
        assertFalse(SolicitudAuditable.evaluar("POST", "/logout"));
    }
}
