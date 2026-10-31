package pe.vichaval.gestionriego.usuario.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import pe.vichaval.gestionriego.usuario.entity.Usuario;

public interface UsuarioRepository extends JpaRepository<Usuario, Long> {
    Optional<Usuario> findByUsernameIgnoreCase(String u);

    boolean existsByUsernameIgnoreCase(String u);
}
