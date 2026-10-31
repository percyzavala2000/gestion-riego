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
    public SecurityFilterChain securityFilterChain(
            HttpSecurity http
    ) throws Exception {

        http.authorizeHttpRequests(authorize -> authorize

                // Recursos públicos
                .requestMatchers(
                        "/login",
                        "/error",
                        "/error/**",
                        "/css/**",
                        "/js/**",
                        "/images/**",
                        "/icons/**",
                        "/favicon.ico",
                        "/actuator/health"
                ).permitAll()

                // Acceso compartido al dashboard
                .requestMatchers(
                        "/",
                        "/dashboard"
                ).hasAnyRole(
                        "ADMINISTRADOR",
                        "OPERADOR",
                        "TESORERIA"
                )

                // Gestión operativa
                .requestMatchers(
                        "/agricultores/**",
                        "/parcelas/**",
                        "/turnos/**"
                ).hasAnyRole(
                        "ADMINISTRADOR",
                        "OPERADOR"
                )

                // Gestión económica
                .requestMatchers(
                        "/tesoreria/**"
                ).hasAnyRole(
                        "ADMINISTRADOR",
                        "TESORERIA"
                )

                // Reportes financieros
                .requestMatchers(
                        "/reportes/financieros/**"
                ).hasAnyRole(
                        "ADMINISTRADOR",
                        "TESORERIA"
                )

                // Reportes operativos e inteligencia
                .requestMatchers(
                        "/reportes/**",
                        "/inteligencia/**"
                ).hasAnyRole(
                        "ADMINISTRADOR",
                        "OPERADOR"
                )

                // Administración de cuentas y roles
                .requestMatchers(
                        "/usuarios/**",
                        "/roles/**"
                ).hasRole(
                        "ADMINISTRADOR"
                )

                // Cualquier otra ruta exige autenticación
                .anyRequest()
                .authenticated()
        );

        http.formLogin(form -> form
                .loginPage("/login")
                .loginProcessingUrl("/login")
                .defaultSuccessUrl("/dashboard", true)
                .failureUrl("/login?error")
                .permitAll()
        );

        http.logout(logout -> logout
                .logoutUrl("/logout")
                .logoutSuccessUrl("/login?logout")
                .invalidateHttpSession(true)
                .clearAuthentication(true)
                .deleteCookies("VICHAYAL_SESSION")
                .permitAll()
        );

        http.exceptionHandling(exception -> exception
                .accessDeniedPage("/error/403")
        );

        return http.build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return PasswordEncoderFactories
                .createDelegatingPasswordEncoder();
    }
}