package pe.vichaval.gestionriego.security.agricultor.repository;
import java.time.OffsetDateTime;
import java.util.Optional;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.data.jpa.repository.JpaRepository;
import pe.vichaval.gestionriego.security.agricultor.entity.SesionAgricultor;
public interface SesionAgricultorRepository extends JpaRepository<SesionAgricultor,Long>{
 @EntityGraph(attributePaths={"cuenta","cuenta.agricultor"}) Optional<SesionAgricultor> findByTokenHash(String tokenHash);
 @EntityGraph(attributePaths={"cuenta","cuenta.agricultor"}) Optional<SesionAgricultor> findByAccessTokenHash(String accessTokenHash);
 long deleteByFechaExpiracionBefore(OffsetDateTime limite);
 @Modifying
 @Query("delete from SesionAgricultor s where "
       + "s.fechaExpiracion < :limite "
       + "or (s.revocado = true and s.fechaRevocacion < :limite)")
 long deleteExpiradasORevocadasAntes(@Param("limite") OffsetDateTime limite);
}
