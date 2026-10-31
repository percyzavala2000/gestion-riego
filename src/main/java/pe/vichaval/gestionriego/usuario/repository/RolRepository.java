package pe.vichaval.gestionriego.usuario.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import pe.vichaval.gestionriego.usuario.entity.Rol;
import pe.vichaval.gestionriego.usuario.enums.CodigoRol;

public interface RolRepository extends JpaRepository<Rol, Long> {
    Optional<Rol> findByCodigo(CodigoRol c);
}
