package pe.vichaval.gestionriego.security.agricultor.auth;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.Base64;
import org.springframework.stereotype.Service;
@Service
public class TokenSeguroService {
 private final SecureRandom random=new SecureRandom();
 public String nuevo(){ byte[] b=new byte[32]; random.nextBytes(b); return Base64.getUrlEncoder().withoutPadding().encodeToString(b); }
 public String hash(String token){
  if(token==null||token.isBlank()) throw new IllegalArgumentException("Token obligatorio");
  try{return java.util.HexFormat.of().formatHex(MessageDigest.getInstance("SHA-256").digest(token.getBytes(StandardCharsets.UTF_8)));}
  catch(NoSuchAlgorithmException e){throw new IllegalStateException("SHA-256 no disponible",e);}
 }
}
