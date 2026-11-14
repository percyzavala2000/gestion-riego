package pe.vichaval.gestionriego.config.seed;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.Test;

class DemoSeedGuardTest {

    @Test
    void aceptaPerfilDemo() {
        assertDoesNotThrow(
                () -> DemoSeedGuard.validar(
                        new String[]{"demo"}
                )
        );
    }

    @Test
    void aceptaPerfilProdParaActivacionControlada() {
        assertDoesNotThrow(
                () -> DemoSeedGuard.validar(
                        new String[]{"prod"}
                )
        );
    }

    @Test
    void rechazaAusenciaDePerfil() {
        assertThrows(
                IllegalStateException.class,
                () -> DemoSeedGuard.validar(new String[]{})
        );
    }

    @Test
    void rechazaPerfilNoPermitido() {
        assertThrows(
                IllegalStateException.class,
                () -> DemoSeedGuard.validar(
                        new String[]{"test"}
                )
        );
    }

    @Test
    void rechazaDemoYProdSimultaneos() {
        assertThrows(
                IllegalStateException.class,
                () -> DemoSeedGuard.validar(
                        new String[]{"demo", "prod"}
                )
        );
    }
}
