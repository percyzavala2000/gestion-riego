package pe.vichaval.gestionriego.security.agricultor.repository;
import java.time.OffsetDateTime;
import java.util.Optional;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import pe.vichaval.gestionriego.security.agricultor.entity.SesionAgricultor;
public interface SesionAgricultorRepository extends JpaRepository<SesionAgricultor,Long>{
 @EntityGraph(attributePaths={"cuenta","cuenta.agricultor"}) Optional<SesionAgricultor> findByTokenHash(String tokenHash);
 @EntityGraph(attributePaths={"cuenta","cuenta.agricultor"}) Optional<SesionAgricultor> findByAccessTokenHash(String accessTokenHash);
 long deleteByFechaExpiracionBefore(OffsetDateTime limite);
}
