package pe.vichaval.gestionriego.inteligencia.dto;

public record AlertaDto(
        String prioridad,
        String categoria,
        String titulo,
        String evidencia,
        String recomendacion,
        String referencia) {
}
