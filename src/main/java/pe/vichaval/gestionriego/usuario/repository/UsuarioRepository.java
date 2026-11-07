package pe.vichaval.gestionriego.usuario.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import pe.vichaval.gestionriego.usuario.entity.Usuario;
import pe.vichaval.gestionriego.usuario.enums.CodigoRol;

public interface UsuarioRepository extends JpaRepository<Usuario, Long> {
    Optional<Usuario> findByUsernameIgnoreCase(String username);

    boolean existsByUsernameIgnoreCase(String username);

    boolean existsByUsernameIgnoreCaseAndIdNot(String username, Long id);

    long countByRolCodigoAndActivoTrue(CodigoRol codigo);

    @Override
    @EntityGraph(attributePaths = "rol")
    List<Usuario> findAll();

    @Override
    @EntityGraph(attributePaths = "rol")
    Optional<Usuario> findById(Long id);
}
