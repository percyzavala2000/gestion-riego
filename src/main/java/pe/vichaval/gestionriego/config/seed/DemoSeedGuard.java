package pe.vichaval.gestionriego.config.seed;

import java.util.Arrays;

public final class DemoSeedGuard {
    private DemoSeedGuard() {
    }

    public static void validar(String[] perfiles) {
        boolean demo = Arrays.asList(perfiles).contains("demo");
        boolean prod = Arrays.asList(perfiles).contains("prod");
        if (!demo || prod) throw new IllegalStateException("El seed requiere solo el perfil demo y rechaza prod");
    }
}
