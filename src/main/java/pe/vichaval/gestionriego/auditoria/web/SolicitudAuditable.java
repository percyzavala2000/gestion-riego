package pe.vichaval.gestionriego.auditoria.web;

import java.util.Set;

public final class SolicitudAuditable {
    private static final Set<String> METODOS = Set.of("POST", "PUT", "PATCH", "DELETE");
    private SolicitudAuditable() {}

    public static boolean evaluar(String metodo, String ruta) {
        if (metodo == null || ruta == null || !METODOS.contains(metodo.toUpperCase())) return false;
        return !ruta.equals("/login")
                && !ruta.equals("/logout")
                && !ruta.startsWith("/css/")
                && !ruta.startsWith("/js/")
                && !ruta.startsWith("/images/")
                && !ruta.startsWith("/actuator/");
    }
}
