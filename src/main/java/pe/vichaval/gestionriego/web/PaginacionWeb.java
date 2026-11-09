package pe.vichaval.gestionriego.web;
public final class PaginacionWeb {
    public static final int TAMANIO_PREDETERMINADO = 20;
    private PaginacionWeb() {}
    public static int pagina(int page) { return Math.max(0, page); }
    public static int tamanio(int size) { return size == 10 || size == 20 || size == 50 ? size : TAMANIO_PREDETERMINADO; }
    public static String texto(String value) { return value == null ? "" : value.trim(); }
    public static String estado(String value) { return value == null ? "" : value.trim().toUpperCase(); }
}
