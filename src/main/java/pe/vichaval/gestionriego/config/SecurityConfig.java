package pe.vichaval.gestionriego.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableMethodSecurity
public class SecurityConfig {
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http.authorizeHttpRequests(authorize -> authorize
                .requestMatchers("/login", "/error", "/error/**", "/css/**", "/js/**",
                        "/images/**", "/icons/**", "/favicon.ico", "/actuator/health")
                .permitAll()
                .requestMatchers("/", "/dashboard")
                .hasAnyRole("ADMINISTRADOR", "OPERADOR", "TESORERIA")
                .requestMatchers("/agricultores/**", "/parcelas/**", "/turnos/**")
                .hasAnyRole("ADMINISTRADOR", "OPERADOR")
                .requestMatchers("/tesoreria/**")
                .hasAnyRole("ADMINISTRADOR", "TESORERIA")
                .requestMatchers("/reportes/financieros/**")
                .hasAnyRole("ADMINISTRADOR", "TESORERIA")
                .requestMatchers("/reportes/**")
                .hasAnyRole("ADMINISTRADOR", "OPERADOR")
                .requestMatchers("/inteligencia/financiera/**")
                .hasAnyRole("ADMINISTRADOR", "TESORERIA")
                .requestMatchers("/inteligencia/**")
                .hasAnyRole("ADMINISTRADOR", "OPERADOR")
                .requestMatchers("/auditoria/**", "/usuarios/**", "/roles/**")
                .hasRole("ADMINISTRADOR")
                .anyRequest().authenticated());

        http.formLogin(form -> form
                .loginPage("/login")
                .loginProcessingUrl("/login")
                .defaultSuccessUrl("/dashboard", true)
                .failureUrl("/login?error")
                .permitAll());

        http.logout(logout -> logout
                .logoutUrl("/logout")
                .logoutSuccessUrl("/login?logout")
                .invalidateHttpSession(true)
                .clearAuthentication(true)
                .deleteCookies("VICHAYAL_SESSION")
                .permitAll());

        http.sessionManagement(session -> session
                .sessionFixation(fixation -> fixation.migrateSession())
                .maximumSessions(3));

        http.headers(headers -> headers
                .contentTypeOptions(options -> {})
                .frameOptions(frame -> frame.deny())
                .referrerPolicy(referrer -> referrer.policy(
                        org.springframework.security.web.header.writers.ReferrerPolicyHeaderWriter.ReferrerPolicy.SAME_ORIGIN))
                .permissionsPolicyHeader(policy -> policy.policy("camera=(), microphone=(), geolocation=()")));

        http.exceptionHandling(exception -> exception.accessDeniedPage("/error/403"));
        return http.build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return PasswordEncoderFactories.createDelegatingPasswordEncoder();
    }
}
