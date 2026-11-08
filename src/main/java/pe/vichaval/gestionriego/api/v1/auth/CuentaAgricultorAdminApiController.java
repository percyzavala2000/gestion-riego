package pe.vichaval.gestionriego.api.v1.auth;
import jakarta.validation.Valid; import jakarta.validation.constraints.*; import org.springframework.web.bind.annotation.*;
import pe.vichaval.gestionriego.security.agricultor.service.CuentaAgricultorService;
@RestController @RequestMapping("/api/v1/cuentas-agricultor")
public class CuentaAgricultorAdminApiController {
 private final CuentaAgricultorService service; public CuentaAgricultorAdminApiController(CuentaAgricultorService s){service=s;}
 @PostMapping public CuentaRespuesta crear(@Valid @RequestBody CuentaSolicitud s){var c=service.crear(s.agricultorId(),s.usuario(),s.password());return new CuentaRespuesta(c.getId(),c.getAgricultor().getId(),c.getUsuario(),c.isActivo());}
 public record CuentaSolicitud(@NotNull Long agricultorId,@NotBlank @Pattern(regexp="[A-Za-z0-9._-]{4,80}") String usuario,@NotBlank @Size(min=12,max=100) String password){}
 public record CuentaRespuesta(Long id,Long agricultorId,String usuario,boolean activo){}
}
