package pe.vichaval.gestionriego.inteligencia.service;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

class ClasificadorPrioridadTest {
    @Test
    void clasificaBajaMediaYAlta() {
        assertEquals("BAJA", ClasificadorPrioridad.porCantidad(0, 2, 5));
        assertEquals("MEDIA", ClasificadorPrioridad.porCantidad(2, 2, 5));
        assertEquals("ALTA", ClasificadorPrioridad.porCantidad(5, 2, 5));
    }
}
