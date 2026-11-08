package pe.vichaval.gestionriego.config;
import jakarta.servlet.http.HttpServletResponse; import java.io.IOException;
import org.springframework.context.annotation.*; import org.springframework.http.MediaType; import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity; import org.springframework.security.crypto.factory.PasswordEncoderFactories; import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain; import org.springframework.security.web.authentication.LoginUrlAuthenticationEntryPoint; import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter; import org.springframework.security.web.util.matcher.RequestMatcher;
import pe.vichaval.gestionriego.security.agricultor.auth.AccessTokenFilter;
@Configuration @EnableMethodSecurity
public class SecurityConfig {
 @Bean public SecurityFilterChain securityFilterChain(HttpSecurity http,AccessTokenFilter tokenFilter)throws Exception{
  http.authorizeHttpRequests(a->a
   .requestMatchers("/login","/error","/error/**","/css/**","/js/**","/images/**","/icons/**","/favicon.ico","/actuator/health","/actuator/prometheus","/api/v1/auth/login","/api/v1/auth/refresh","/api/v1/auth/logout").permitAll()
   .requestMatchers("/api/v1/auth/me","/api/v1/agricultor/**").hasRole("AGRICULTOR")
   .requestMatchers("/api/v1/cuentas-agricultor/**","/v3/api-docs/**","/swagger-ui/**","/swagger-ui.html").hasRole("ADMINISTRADOR")
   .requestMatchers("/api/v1/agricultores/**","/api/v1/parcelas/**","/api/v1/turnos/**").hasAnyRole("ADMINISTRADOR","OPERADOR")
   .requestMatchers("/","/dashboard").hasAnyRole("ADMINISTRADOR","OPERADOR","TESORERIA")
   .requestMatchers("/agricultores/**","/parcelas/**","/turnos/**").hasAnyRole("ADMINISTRADOR","OPERADOR")
   .requestMatchers("/tesoreria/**","/reportes/financieros/**","/inteligencia/financiera/**").hasAnyRole("ADMINISTRADOR","TESORERIA")
   .requestMatchers("/reportes/**","/inteligencia/**").hasAnyRole("ADMINISTRADOR","OPERADOR")
   .requestMatchers("/auditoria/**","/usuarios/**","/roles/**").hasRole("ADMINISTRADOR").anyRequest().authenticated());
  http.csrf(c->c.ignoringRequestMatchers("/api/v1/auth/**","/api/v1/agricultor/**"));
  http.addFilterBefore(tokenFilter,UsernamePasswordAuthenticationFilter.class);
  http.formLogin(f->f.loginPage("/login").loginProcessingUrl("/login").defaultSuccessUrl("/dashboard",true).failureUrl("/login?error").permitAll());
  http.logout(l->l.logoutUrl("/logout").logoutSuccessUrl("/login?logout").invalidateHttpSession(true).clearAuthentication(true).deleteCookies("VICHAYAL_SESSION").permitAll());
  http.sessionManagement(s->s.sessionFixation(x->x.migrateSession()).maximumSessions(3));
  http.headers(h->h.contentTypeOptions(x->{}).frameOptions(x->x.deny()).referrerPolicy(x->x.policy(org.springframework.security.web.header.writers.ReferrerPolicyHeaderWriter.ReferrerPolicy.SAME_ORIGIN)).permissionsPolicyHeader(x->x.policy("camera=(), microphone=(), geolocation=()")));
  RequestMatcher apiAgricultor = request -> {
      String uri = request.getRequestURI();
      return uri.startsWith("/api/v1/auth/")
              || uri.startsWith("/api/v1/agricultor/");
  };
  RequestMatcher webOSesion = request -> !apiAgricultor.matches(request);
  http.exceptionHandling(e -> e
          .defaultAuthenticationEntryPointFor(
                  (request, response, exception) -> json(response, 401, "No autorizado"),
                  apiAgricultor
          )
          .defaultAuthenticationEntryPointFor(
                  new LoginUrlAuthenticationEntryPoint("/login"),
                  webOSesion
          )
          .accessDeniedPage("/error/403")
  );
  return http.build();
 }
 private static void json(HttpServletResponse p,int status,String title)throws IOException{p.setStatus(status);p.setContentType(MediaType.APPLICATION_PROBLEM_JSON_VALUE);p.getWriter().write("{\"title\":\""+title+"\",\"status\":"+status+"}");}
 @Bean public PasswordEncoder passwordEncoder(){return PasswordEncoderFactories.createDelegatingPasswordEncoder();}
}
