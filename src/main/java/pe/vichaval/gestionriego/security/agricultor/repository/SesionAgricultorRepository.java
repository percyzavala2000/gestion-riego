package pe.vichaval.gestionriego.security.agricultor.repository;

import java.time.OffsetDateTime;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import pe.vichaval.gestionriego.security.agricultor.entity.SesionAgricultor;

public interface SesionAgricultorRepository extends JpaRepository<SesionAgricultor, Long> {
    Optional<SesionAgricultor> findByTokenHash(String tokenHash);
    long deleteByFechaExpiracionBefore(OffsetDateTime limite);
}
