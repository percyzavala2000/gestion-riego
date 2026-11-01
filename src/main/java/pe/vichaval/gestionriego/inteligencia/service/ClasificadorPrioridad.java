package pe.vichaval.gestionriego.inteligencia.service;

public final class ClasificadorPrioridad {
    private ClasificadorPrioridad() {
    }

    public static String porCantidad(long cantidad, long umbralMedio, long umbralAlto) {
        if (cantidad >= umbralAlto) {
            return "ALTA";
        }
        if (cantidad >= umbralMedio) {
            return "MEDIA";
        }
        return "BAJA";
    }
}
