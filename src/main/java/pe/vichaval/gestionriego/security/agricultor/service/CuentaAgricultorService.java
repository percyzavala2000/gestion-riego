package pe.vichaval.gestionriego.security.agricultor.service;

import java.time.Duration;
import java.time.OffsetDateTime;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pe.vichaval.gestionriego.agricultor.service.AgricultorService;
import pe.vichaval.gestionriego.security.agricultor.entity.CuentaAgricultor;
import pe.vichaval.gestionriego.security.agricultor.repository.CuentaAgricultorRepository;

@Service
@Transactional
public class CuentaAgricultorService {
    private final CuentaAgricultorRepository cuentas;
    private final AgricultorService agricultores;
    private final PasswordEncoder encoder;
    private final int maxIntentos;
    private final Duration bloqueo;

    public CuentaAgricultorService(CuentaAgricultorRepository cuentas, AgricultorService agricultores,
            PasswordEncoder encoder,
            @Value("${vichayal.auth.max-intentos:5}") int maxIntentos,
            @Value("${vichayal.auth.bloqueo-minutos:15}") long bloqueoMinutos) {
        this.cuentas = cuentas; this.agricultores = agricultores; this.encoder = encoder;
        this.maxIntentos = maxIntentos; this.bloqueo = Duration.ofMinutes(bloqueoMinutos);
    }

    public CuentaAgricultor crear(Long agricultorId, String usuario, String password) {
        String normalizado = normalizar(usuario);
        if (cuentas.existsByUsuarioIgnoreCase(normalizado)) throw new IllegalStateException("Usuario de agricultor ya registrado");
        if (cuentas.existsByAgricultorId(agricultorId)) throw new IllegalStateException("El agricultor ya tiene una cuenta de acceso");
        if (password == null || password.length() < 12) throw new IllegalArgumentException("La contraseña debe tener al menos 12 caracteres");
        return cuentas.save(new CuentaAgricultor(agricultores.get(agricultorId), normalizado, encoder.encode(password)));
    }

    public CuentaAgricultor autenticar(String usuario, String password) {
        CuentaAgricultor cuenta = cuentas.findByUsuarioIgnoreCase(normalizar(usuario))
                .orElseThrow(() -> new IllegalArgumentException("Usuario o contraseña incorrectos"));
        OffsetDateTime ahora = OffsetDateTime.now();
        if (!cuenta.isActivo()) throw new IllegalStateException("Cuenta inactiva");
        if (cuenta.estaBloqueada(ahora)) throw new IllegalStateException("Cuenta bloqueada temporalmente");
        if (!encoder.matches(password, cuenta.getPasswordHash())) {
            cuenta.registrarFallo(maxIntentos, ahora.plus(bloqueo));
            throw new IllegalArgumentException("Usuario o contraseña incorrectos");
        }
        cuenta.registrarAcceso();
        return cuenta;
    }

    @Transactional(readOnly = true)
    public CuentaAgricultor buscarPorUsuario(String usuario) {
        return cuentas.findByUsuarioIgnoreCase(normalizar(usuario))
                .orElseThrow(() -> new IllegalArgumentException("Cuenta de agricultor no encontrada"));
    }

    private String normalizar(String usuario) {
        if (usuario == null || usuario.isBlank()) throw new IllegalArgumentException("El usuario es obligatorio");
        return usuario.trim().toLowerCase();
    }
}
