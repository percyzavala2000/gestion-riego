package pe.vichaval.gestionriego.config.seed;

import java.util.Arrays;
import java.util.Set;
import java.util.stream.Collectors;

public final class DemoSeedGuard {

    private static final Set<String> PERFILES_PERMITIDOS =
            Set.of("demo", "prod");

    private DemoSeedGuard() {
    }

    public static void validar(String[] perfiles) {
        Set<String> activos = Arrays.stream(perfiles)
                .map(String::trim)
                .filter(perfil -> !perfil.isBlank())
                .collect(Collectors.toUnmodifiableSet());

        if (activos.isEmpty()) {
            throw new IllegalStateException(
                    "El seed requiere un perfil explícito: demo o prod"
            );
        }

        boolean demo = activos.contains("demo");
        boolean prod = activos.contains("prod");

        if (demo && prod) {
            throw new IllegalStateException(
                    "Los perfiles demo y prod no pueden activarse "
                            + "simultáneamente para ejecutar el seed"
            );
        }

        boolean contienePerfilPermitido = activos.stream()
                .anyMatch(PERFILES_PERMITIDOS::contains);

        if (!contienePerfilPermitido) {
            throw new IllegalStateException(
                    "El seed solo puede ejecutarse con el perfil "
                            + "demo o prod"
            );
        }
    }
}
