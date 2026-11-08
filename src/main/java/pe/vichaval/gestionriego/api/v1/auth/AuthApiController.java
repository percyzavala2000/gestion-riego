package pe.vichaval.gestionriego.api.v1.auth;
import jakarta.servlet.http.HttpServletRequest; import jakarta.validation.Valid; import jakarta.validation.constraints.*;
import org.springframework.http.ResponseEntity; import org.springframework.security.core.Authentication; import org.springframework.web.bind.annotation.*;
import pe.vichaval.gestionriego.security.agricultor.auth.*;
@RestController @RequestMapping("/api/v1/auth")
public class AuthApiController {
 private final AutenticacionAgricultorService auth;
 public AuthApiController(AutenticacionAgricultorService a){auth=a;}
 @PostMapping("/login") public TokensRespuesta login(@Valid @RequestBody LoginSolicitud s,HttpServletRequest r){return TokensRespuesta.desde(auth.emit(s.usuario(),s.password(),r.getHeader("User-Agent"),ip(r)));}
 @PostMapping("/refresh") public TokensRespuesta refresh(@Valid @RequestBody RefreshSolicitud s,HttpServletRequest r){return TokensRespuesta.desde(auth.refresh(s.refreshToken(),r.getHeader("User-Agent"),ip(r)));}
 @PostMapping("/logout") public ResponseEntity<Void> logout(@RequestBody(required=false) LogoutSolicitud s,HttpServletRequest r){auth.logout(s==null?null:s.refreshToken(),bearer(r));return ResponseEntity.noContent().build();}
 @GetMapping("/me") public MeRespuesta me(Authentication a){if(a==null||!(a.getPrincipal() instanceof AgricultorPrincipal p))throw new AutenticacionAgricultorService.CredencialesInvalidasException();return new MeRespuesta(p.cuentaId(),p.agricultorId(),p.usuario());}
 private String bearer(HttpServletRequest r){String h=r.getHeader("Authorization");return h!=null&&h.startsWith("Bearer ")?h.substring(7):null;}
 private String ip(HttpServletRequest r){String x=r.getHeader("X-Forwarded-For");return x==null?r.getRemoteAddr():x.split(",")[0].trim();}
 public record LoginSolicitud(@NotBlank String usuario,@NotBlank @Size(min=12,max=100) String password){}
 public record RefreshSolicitud(@NotBlank String refreshToken){}
 public record LogoutSolicitud(String refreshToken){}
 public record MeRespuesta(Long cuentaId,Long agricultorId,String usuario){}
 public record TokensRespuesta(String accessToken,String refreshToken,String tokenType,long expiresIn,long refreshExpiresIn,Long agricultorId,String usuario){static TokensRespuesta desde(AutenticacionAgricultorService.Tokens t){return new TokensRespuesta(t.accessToken(),t.refreshToken(),t.tokenType(),t.expiresIn(),t.refreshExpiresIn(),t.agricultorId(),t.usuario());}}
}
