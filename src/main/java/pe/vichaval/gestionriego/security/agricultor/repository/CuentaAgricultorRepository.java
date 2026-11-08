package pe.vichaval.gestionriego.security.agricultor.repository;

import java.util.Optional;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import pe.vichaval.gestionriego.security.agricultor.entity.CuentaAgricultor;

public interface CuentaAgricultorRepository extends JpaRepository<CuentaAgricultor, Long> {
    @EntityGraph(attributePaths = "agricultor")
    Optional<CuentaAgricultor> findByUsuarioIgnoreCase(String usuario);
    boolean existsByUsuarioIgnoreCase(String usuario);
    boolean existsByAgricultorId(Long agricultorId);
}
