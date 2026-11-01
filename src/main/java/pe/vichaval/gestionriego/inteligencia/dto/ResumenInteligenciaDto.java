package pe.vichaval.gestionriego.inteligencia.dto;

import java.util.List;

public record ResumenInteligenciaDto(
        long total,
        long altas,
        long medias,
        long bajas,
        List<AlertaDto> alertas) {
}
