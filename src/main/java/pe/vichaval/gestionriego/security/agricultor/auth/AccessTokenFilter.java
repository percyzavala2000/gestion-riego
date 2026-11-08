package pe.vichaval.gestionriego.security.agricultor.auth;
import jakarta.servlet.*; import jakarta.servlet.http.*; import java.io.IOException; import java.util.List;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component; import org.springframework.web.filter.OncePerRequestFilter;
@Component
public class AccessTokenFilter extends OncePerRequestFilter {
 private final AutenticacionAgricultorService auth;
 public AccessTokenFilter(AutenticacionAgricultorService auth){this.auth=auth;}
 @Override protected boolean shouldNotFilter(HttpServletRequest r){return !r.getRequestURI().startsWith("/api/v1/agricultor/");}
 @Override protected void doFilterInternal(HttpServletRequest r,HttpServletResponse p,FilterChain c)throws ServletException,IOException{
  String h=r.getHeader("Authorization");
  if(h!=null&&h.startsWith("Bearer ")) try{
   var principal=auth.validarAccess(h.substring(7));
   var a=new UsernamePasswordAuthenticationToken(principal,null,List.of(new SimpleGrantedAuthority("ROLE_AGRICULTOR")));
   SecurityContextHolder.getContext().setAuthentication(a);
  }catch(RuntimeException ignored){SecurityContextHolder.clearContext();}
  c.doFilter(r,p);
 }
}
