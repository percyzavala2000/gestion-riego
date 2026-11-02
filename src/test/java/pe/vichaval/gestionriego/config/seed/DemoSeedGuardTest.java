package pe.vichaval.gestionriego.config.seed;
import static org.junit.jupiter.api.Assertions.*;import org.junit.jupiter.api.Test;
class DemoSeedGuardTest{
 @Test void aceptaDemoYRechazaProd(){assertDoesNotThrow(()->DemoSeedGuard.validar(new String[]{"demo"}));assertThrows(IllegalStateException.class,()->DemoSeedGuard.validar(new String[]{"prod"}));assertThrows(IllegalStateException.class,()->DemoSeedGuard.validar(new String[]{"demo","prod"}));}
}
