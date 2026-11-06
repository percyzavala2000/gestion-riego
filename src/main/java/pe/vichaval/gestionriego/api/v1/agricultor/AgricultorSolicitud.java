package pe.vichaval.gestionriego.api.v1.agricultor;

import jakarta.validation.constraints.*;
import pe.vichaval.gestionriego.agricultor.dto.AgricultorForm;

public record AgricultorSolicitud(@NotBlank @Pattern(regexp = "[0-9]{8}") String dni,
                                  @NotBlank @Size(max = 100) String nombres,
                                  @NotBlank @Size(max = 120) String apellidos,
                                  @Pattern(regexp = "^$|^[0-9+ -]{7,20}$") String telefono,
                                  @NotBlank @Size(max = 30) String sector,
                                  @NotBlank @Pattern(regexp = "ACTIVO|INACTIVO") String estado) {
    public AgricultorForm aForm() {
        var f = new AgricultorForm();
        f.setDni(dni);
        f.setNombres(nombres);
        f.setApellidos(apellidos);
        f.setTelefono(telefono);
        f.setSector(sector);
        f.setEstado(estado);
        return f;
    }
}
