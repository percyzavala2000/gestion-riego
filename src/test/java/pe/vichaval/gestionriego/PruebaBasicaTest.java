package pe.vichaval.gestionriego;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class PruebaBasicaTest {

    @Test
    @DisplayName("JUnit debe ejecutar una prueba elemental")
    void junitDebeEjecutarPruebaElemental() {

        int resultado = 2 + 2;

        assertEquals(
                4,
                resultado
        );
    }
}
