package pe.vichaval.gestionriego.security.agricultor.auth;
import java.time.OffsetDateTime;
import java.util.UUID;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pe.vichaval.gestionriego.security.agricultor.entity.SesionAgricultor;
import pe.vichaval.gestionriego.security.agricultor.repository.SesionAgricultorRepository;
import pe.vichaval.gestionriego.security.agricultor.service.CuentaAgricultorService;
@Service @Transactional
public class AutenticacionAgricultorService {
 private final CuentaAgricultorService cuentas; private final SesionAgricultorRepository sesiones; private final TokenSeguroService tokens;
 private final long accessMinutos; private final long refreshDias;
 public AutenticacionAgricultorService(CuentaAgricultorService c,SesionAgricultorRepository s,TokenSeguroService t,
   @Value("${vichayal.auth.access-token-minutos:15}") long am,@Value("${vichayal.auth.refresh-token-dias:14}") long rd){cuentas=c;sesiones=s;tokens=t;accessMinutos=am;refreshDias=rd;}
 public Tokens emit(String usuario,String password,String dispositivo,String ip){ return emitir(cuentas.autenticar(usuario,password),dispositivo,ip); }
 public Tokens refresh(String refresh,String dispositivo,String ip){
  var actual=sesiones.findByTokenHash(tokens.hash(refresh)).orElseThrow(()->new CredencialesInvalidasException());
  if(!actual.refreshUtilizable(OffsetDateTime.now())) throw new CredencialesInvalidasException();
  String reemplazo=UUID.randomUUID().toString(); actual.revocar(reemplazo);
  return emitir(actual.getCuenta(),dispositivo,ip,reemplazo);
 }
 public void logout(String refresh,String access){
  if(refresh!=null&&!refresh.isBlank()) sesiones.findByTokenHash(tokens.hash(refresh)).ifPresent(s->s.revocar(null));
  else if(access!=null&&!access.isBlank()) sesiones.findByAccessTokenHash(tokens.hash(access)).ifPresent(s->s.revocar(null));
 }
 @Transactional(readOnly=true)
 public AgricultorPrincipal validarAccess(String access){
  var s=sesiones.findByAccessTokenHash(tokens.hash(access)).orElseThrow(CredencialesInvalidasException::new);
  if(!s.accessUtilizable(OffsetDateTime.now())||!s.getCuenta().isActivo()) throw new CredencialesInvalidasException();
  return new AgricultorPrincipal(s.getCuenta().getId(),s.getCuenta().getAgricultor().getId(),s.getCuenta().getUsuario());
 }
 private Tokens emitir(pe.vichaval.gestionriego.security.agricultor.entity.CuentaAgricultor c,String d,String ip){return emitir(c,d,ip,UUID.randomUUID().toString());}
 private Tokens emitir(pe.vichaval.gestionriego.security.agricultor.entity.CuentaAgricultor c,String d,String ip,String id){
  var now=OffsetDateTime.now(); String access=tokens.nuevo(),refresh=tokens.nuevo();
  var sesion=new SesionAgricultor(c,tokens.hash(refresh),tokens.hash(access),id,d,ip,now.plusDays(refreshDias),now.plusMinutes(accessMinutos));
  sesiones.save(sesion); return new Tokens(access,refresh,"Bearer",accessMinutos*60,refreshDias*86400,c.getAgricultor().getId(),c.getUsuario());
 }
 public record Tokens(String accessToken,String refreshToken,String tokenType,long expiresIn,long refreshExpiresIn,Long agricultorId,String usuario){}
 public static class CredencialesInvalidasException extends RuntimeException { public CredencialesInvalidasException(){super("Credenciales o token inválido");} }
}
