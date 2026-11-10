package pe.vichaval.gestionriego.usuario.repository;

import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import pe.vichaval.gestionriego.usuario.entity.Usuario;
import pe.vichaval.gestionriego.usuario.enums.CodigoRol;

public interface UsuarioRepository extends JpaRepository<Usuario, Long> {
    Optional<Usuario> findByUsernameIgnoreCase(String username);
    boolean existsByUsernameIgnoreCase(String username);
    boolean existsByUsernameIgnoreCaseAndIdNot(String username, Long id);
    long countByRolCodigoAndActivoTrue(CodigoRol codigo);

    @EntityGraph(attributePaths = "rol")
    @Query("""
            select u from Usuario u
            where (:q = ''
                or lower(u.username) like lower(concat('%', :q, '%'))
                or lower(u.nombres) like lower(concat('%', :q, '%'))
                or lower(u.apellidos) like lower(concat('%', :q, '%'))
                or lower(concat(u.nombres, ' ', u.apellidos)) like lower(concat('%', :q, '%')))
              and (:rol is null or u.rol.codigo = :rol)
              and (:estado = ''
                   or (:estado = 'ACTIVO' and u.activo = true)
                   or (:estado = 'INACTIVO' and u.activo = false))
            """)
    Page<Usuario> buscar(
            @Param("q") String q,
            @Param("rol") CodigoRol rol,
            @Param("estado") String estado,
            Pageable pageable
    );

    @Override
    @EntityGraph(attributePaths = "rol")
    List<Usuario> findAll();

    @Override
    @EntityGraph(attributePaths = "rol")
    Optional<Usuario> findById(Long id);
}
