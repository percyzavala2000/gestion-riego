package pe.vichaval.gestionriego.caja.entity;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.math.BigDecimal;
import java.time.LocalDate;

import org.junit.jupiter.api.Test;
import pe.vichaval.gestionriego.agricultor.dto.AgricultorForm;
import pe.vichaval.gestionriego.agricultor.entity.Agricultor;
import pe.vichaval.gestionriego.tesoreria.dto.PagoForm;
import pe.vichaval.gestionriego.tesoreria.entity.Pago;

class DevolucionTest {
    @Test
    void flujoSolicitadaAprobadaEjecutada() {
        Devolucion d = new Devolucion("DV-TEST", pago(), new BigDecimal("20"), "Prueba", null);
        assertEquals("SOLICITADA", d.getEstado());
        d.aprobar();
        assertEquals("APROBADA", d.getEstado());
        d.ejecutar();
        assertEquals("EJECUTADA", d.getEstado());
        assertThrows(IllegalStateException.class, d::ejecutar);
    }

    @Test
    void unaSolicitudPuedeRechazarseUnaSolaVez() {
        Devolucion d = new Devolucion("DV-TEST", pago(), new BigDecimal("20"), "Prueba", null);
        d.rechazar();
        assertEquals("RECHAZADA", d.getEstado());
        assertThrows(IllegalStateException.class, d::rechazar);
    }

    private Pago pago() {
        PagoForm f = new PagoForm();
        f.setAgricultorId(1L);
        f.setMonto(new BigDecimal("100"));
        f.setFecha(LocalDate.now());
        f.setConcepto("Prueba");
        f.setComprobante("DEV-COMP");
        f.setMetodo("EFECTIVO");
        AgricultorForm a = new AgricultorForm();
        a.setDni("12345678");
        a.setNombres("Prueba");
        a.setApellidos("Devolucion");
        a.setSector("Sur");
        a.setEstado("ACTIVO");
        return new Pago("PG-DEV", f, new Agricultor(a));
    }
}
