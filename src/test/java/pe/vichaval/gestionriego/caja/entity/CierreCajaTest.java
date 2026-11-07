package pe.vichaval.gestionriego.caja.entity;

import static org.junit.jupiter.api.Assertions.*;

import java.math.BigDecimal;
import java.time.LocalDate;

import org.junit.jupiter.api.Test;

class CierreCajaTest {
    @Test
    void calculaDiferenciaYExigeMotivoParaReabrir() {
        CierreCaja c = new CierreCaja(LocalDate.now(), new BigDecimal("100"), new BigDecimal("300"), new BigDecimal("50"), new BigDecimal("20"), new BigDecimal("325"), "Prueba", null);
        assertEquals(0, new BigDecimal("330").compareTo(c.getSaldoTeorico()));
        assertEquals(0, new BigDecimal("-5").compareTo(c.getDiferencia()));
        assertThrows(IllegalArgumentException.class, () -> c.reabrir("", null));
        c.reabrir("Corrección", null);
        assertEquals("REABIERTO", c.getEstado());
    }
}
