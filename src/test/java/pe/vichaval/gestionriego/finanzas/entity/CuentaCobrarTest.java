package pe.vichaval.gestionriego.finanzas.entity;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.math.BigDecimal;
import java.time.LocalDate;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import pe.vichaval.gestionriego.agricultor.dto.AgricultorForm;
import pe.vichaval.gestionriego.agricultor.entity.Agricultor;
import pe.vichaval.gestionriego.finanzas.dto.ConceptoCobroForm;
import pe.vichaval.gestionriego.finanzas.dto.CuentaCobrarForm;

class CuentaCobrarTest {

    @Test
    @DisplayName("Una cuenta nueva conserva todo el saldo pendiente")
    void cuentaNuevaDebeConservarSaldoCompleto() {

        CuentaCobrar cuenta =
            crearCuenta(
                new BigDecimal("500.00")
            );

        assertMontoIgual(
            "500.00",
            cuenta.getMontoTotal()
        );

        assertMontoIgual(
            "0.00",
            cuenta.getMontoPagado()
        );

        assertMontoIgual(
            "500.00",
            cuenta.getSaldoPendiente()
        );

        assertEquals(
            "PENDIENTE",
            cuenta.getEstado()
        );
    }

    @Test
    @DisplayName("Un pago parcial reduce el saldo pendiente")
    void pagoParcialDebeReducirSaldo() {

        CuentaCobrar cuenta =
            crearCuenta(
                new BigDecimal("500.00")
            );

        cuenta.aplicarPago(
            new BigDecimal("200.00")
        );

        assertMontoIgual(
            "200.00",
            cuenta.getMontoPagado()
        );

        assertMontoIgual(
            "300.00",
            cuenta.getSaldoPendiente()
        );

        assertEquals(
            "PAGO_PARCIAL",
            cuenta.getEstado()
        );
    }

    @Test
    @DisplayName("Un pago superior al saldo debe ser rechazado")
    void pagoSuperiorAlSaldoDebeSerRechazado() {

        CuentaCobrar cuenta =
            crearCuenta(
                new BigDecimal("500.00")
            );

        IllegalStateException excepcion =
            assertThrows(
                IllegalStateException.class,
                () -> cuenta.aplicarPago(
                    new BigDecimal("600.00")
                )
            );

        assertEquals(
            "El pago supera el saldo pendiente",
            excepcion.getMessage()
        );

        /*
         * El intento fallido no debe modificar
         * la cuenta.
         */
        assertMontoIgual(
            "0.00",
            cuenta.getMontoPagado()
        );

        assertMontoIgual(
            "500.00",
            cuenta.getSaldoPendiente()
        );

        assertEquals(
            "PENDIENTE",
            cuenta.getEstado()
        );
    }

    private CuentaCobrar crearCuenta(
            BigDecimal monto
    ) {
        CuentaCobrarForm form =
            new CuentaCobrarForm();

        form.setAgricultorId(1L);
        form.setConceptoId(1L);
        form.setMontoTotal(monto);
        form.setFechaEmision(
            LocalDate.now()
        );
        form.setFechaVencimiento(
            LocalDate.now().plusDays(30)
        );
        form.setObservaciones(
            "Cuenta de prueba"
        );

        return new CuentaCobrar(
            "CC-TEST-001",
            form,
            crearAgricultor(),
            crearConcepto()
        );
    }

    private Agricultor crearAgricultor() {

        AgricultorForm form =
            new AgricultorForm();

        form.setDni("12345678");
        form.setNombres("Agricultor");
        form.setApellidos("De Prueba");
        form.setTelefono("999999999");
        form.setSector("Sector de prueba");
        form.setEstado("ACTIVO");

        return new Agricultor(form);
    }

    private ConceptoCobro crearConcepto() {

        ConceptoCobroForm form =
            new ConceptoCobroForm();

        form.setCodigo("PRUEBA");
        form.setNombre("Concepto de prueba");
        form.setDescripcion(
            "Concepto utilizado en la prueba"
        );
        form.setActivo(true);

        return new ConceptoCobro(form);
    }

    /*
     * BigDecimal.equals también compara la escala.
     * compareTo verifica el valor monetario.
     */
    private void assertMontoIgual(
            String esperado,
            BigDecimal actual
    ) {
        assertEquals(
            0,
            new BigDecimal(esperado)
                .compareTo(actual)
        );
    }
}
