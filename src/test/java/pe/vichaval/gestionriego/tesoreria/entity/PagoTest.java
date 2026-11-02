package pe.vichaval.gestionriego.tesoreria.entity;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import java.math.BigDecimal;
import java.time.LocalDate;
import org.junit.jupiter.api.Test;
import pe.vichaval.gestionriego.agricultor.dto.AgricultorForm;
import pe.vichaval.gestionriego.agricultor.entity.Agricultor;
import pe.vichaval.gestionriego.tesoreria.dto.PagoForm;

class PagoTest {
    @Test void efectivoNaceConfirmadoYDigitalPendiente() {
        assertEquals("CONFIRMADO", pago("EFECTIVO").getEstado());
        assertEquals("PENDIENTE", pago("YAPE").getEstado());
    }
    @Test void soloPermiteTransicionesValidasYMotivoObligatorio() {
        Pago digital=pago("YAPE");
        assertThrows(IllegalArgumentException.class,()->digital.rechazar(""));
        digital.confirmar(); assertEquals("CONFIRMADO",digital.getEstado());
        assertThrows(IllegalStateException.class,digital::confirmar);
        assertThrows(IllegalArgumentException.class,()->digital.anular(" "));
        digital.anular("Comprobante incorrecto"); assertEquals("ANULADO",digital.getEstado());
    }
    private Pago pago(String metodo){PagoForm f=new PagoForm();f.setAgricultorId(1L);f.setMonto(new BigDecimal("100"));f.setFecha(LocalDate.now());f.setConcepto("Prueba");f.setComprobante("COMP-"+metodo);f.setMetodo(metodo);AgricultorForm a=new AgricultorForm();a.setDni("12345678");a.setNombres("Prueba");a.setApellidos("Pago");a.setSector("Norte");a.setEstado("ACTIVO");return new Pago("PG-TEST",f,new Agricultor(a));}
}
