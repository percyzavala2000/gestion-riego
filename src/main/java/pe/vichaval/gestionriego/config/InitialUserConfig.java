package pe.vichaval.gestionriego.config;

import java.util.Arrays;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.security.crypto.password.PasswordEncoder;

import pe.vichaval.gestionriego.usuario.entity.Usuario;
import pe.vichaval.gestionriego.usuario.enums.CodigoRol;
import pe.vichaval.gestionriego.usuario.repository.RolRepository;
import pe.vichaval.gestionriego.usuario.repository.UsuarioRepository;

@Configuration
public class InitialUserConfig {

    @Bean
    CommandLineRunner init(
            UsuarioRepository usuarios,
            RolRepository roles,
            PasswordEncoder encoder,
            Environment environment,
            @Value("${APP_ADMIN_PASSWORD:}") String adminPassword,
            @Value("${APP_TESORERO_PASSWORD:}") String tesoreroPassword,
            @Value("${APP_OPERADOR_PASSWORD:}") String operadorPassword
    ) {
        return args -> {
            boolean produccion = Arrays.asList(
                    environment.getActiveProfiles()
            ).contains("prod");

            crearSiCorresponde(
                    usuarios,
                    roles,
                    encoder,
                    "admin",
                    adminPassword,
                    CodigoRol.ADMINISTRADOR,
                    produccion
            );

            crearSiCorresponde(
                    usuarios,
                    roles,
                    encoder,
                    "tesorero",
                    tesoreroPassword,
                    CodigoRol.TESORERIA,
                    produccion
            );

            crearSiCorresponde(
                    usuarios,
                    roles,
                    encoder,
                    "operador",
                    operadorPassword,
                    CodigoRol.OPERADOR,
                    produccion
            );
        };
    }

    private void crearSiCorresponde(
            UsuarioRepository usuarios,
            RolRepository roles,
            PasswordEncoder encoder,
            String username,
            String password,
            CodigoRol codigoRol,
            boolean produccion
    ) {
        if (usuarios.existsByUsernameIgnoreCase(username)) {
            return;
        }

        if (password == null || password.isBlank()) {
            if (produccion) {
                throw new IllegalStateException(
                        "Falta la contraseña inicial para el usuario "
                                + username
                                + " en producción"
                );
            }
            return;
        }

        if (password.length() < 12) {
            throw new IllegalStateException(
                    "La contraseña inicial de "
                            + username
                            + " debe tener al menos 12 caracteres"
            );
        }

        var rol = roles.findByCodigo(codigoRol)
                .orElseThrow(() -> new IllegalStateException(
                        "No existe el rol " + codigoRol
                ));

        usuarios.save(
                new Usuario(
                        username,
                        encoder.encode(password),
                        username,
                        "Vichayal",
                        rol
                )
        );
    }
}
