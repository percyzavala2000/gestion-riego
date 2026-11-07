package pe.vichaval.gestionriego.finanzas.entity;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.math.BigDecimal;
import java.time.LocalDate;

import org.junit.jupiter.api.Test;
import pe.vichaval.gestionriego.agricultor.dto.AgricultorForm;
import pe.vichaval.gestionriego.agricultor.entity.Agricultor;
import pe.vichaval.gestionriego.finanzas.dto.ConceptoCobroForm;
import pe.vichaval.gestionriego.finanzas.dto.CuentaCobrarForm;

class CuentaCobrarReversionTest {
    @Test
    void unaDevolucionRestituyeSaldoYRecalculaEstado() {
        CuentaCobrar cuenta = cuenta("500.00");
        cuenta.aplicarPago(new BigDecimal("500.00"));
        assertEquals("PAGADA", cuenta.getEstado());
        cuenta.revertirPago(new BigDecimal("100.00"));
        assertMonto("400.00", cuenta.getMontoPagado());
        assertMonto("100.00", cuenta.getSaldoPendiente());
        assertEquals("PAGO_PARCIAL", cuenta.getEstado());
    }

    @Test
    void noPermiteRevertirMasDeLoPagado() {
        CuentaCobrar cuenta = cuenta("500.00");
        cuenta.aplicarPago(new BigDecimal("100.00"));
        assertThrows(IllegalStateException.class,
                () -> cuenta.revertirPago(new BigDecimal("101.00")));
        assertMonto("100.00", cuenta.getMontoPagado());
    }

    private CuentaCobrar cuenta(String monto) {
        AgricultorForm af = new AgricultorForm();
        af.setDni("12345678");
        af.setNombres("Prueba");
        af.setApellidos("Financiera");
        af.setTelefono("999999999");
        af.setSector("Norte");
        af.setEstado("ACTIVO");
        ConceptoCobroForm cf = new ConceptoCobroForm();
        cf.setCodigo("PRUEBA");
        cf.setNombre("Prueba");
        cf.setActivo(true);
        CuentaCobrarForm f = new CuentaCobrarForm();
        f.setAgricultorId(1L);
        f.setConceptoId(1L);
        f.setMontoTotal(new BigDecimal(monto));
        f.setFechaEmision(LocalDate.now());
        f.setFechaVencimiento(LocalDate.now().plusDays(30));
        return new CuentaCobrar("CC-TEST-REV", f, new Agricultor(af), new ConceptoCobro(cf));
    }

    private void assertMonto(String esperado, BigDecimal actual) {
        assertEquals(0, new BigDecimal(esperado).compareTo(actual));
    }
}
