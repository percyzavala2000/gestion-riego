package pe.vichaval.gestionriego.api.v1.agricultor;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;

public record CambioEstadoAgricultorSolicitud(
        @NotBlank(message = "El estado es obligatorio")
        @Pattern(
                regexp = "ACTIVO|INACTIVO",
                message = "El estado debe ser ACTIVO o INACTIVO"
        )
        String estado
) {
}
