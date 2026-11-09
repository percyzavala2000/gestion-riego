package pe.vichaval.gestionriego.agricultor.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pe.vichaval.gestionriego.agricultor.dto.AgricultorForm;
import pe.vichaval.gestionriego.agricultor.entity.Agricultor;
import pe.vichaval.gestionriego.agricultor.repository.AgricultorRepository;
import pe.vichaval.gestionriego.parcela.repository.ParcelaRepository;
import pe.vichaval.gestionriego.tesoreria.repository.PagoRepository;
import pe.vichaval.gestionriego.web.PaginacionWeb;

@Service
@Transactional
public class AgricultorService {
    private final AgricultorRepository agricultores;
    private final ParcelaRepository parcelas;
    private final PagoRepository pagos;

    public AgricultorService(
            AgricultorRepository agricultores,
            ParcelaRepository parcelas,
            PagoRepository pagos) {
        this.agricultores = agricultores;
        this.parcelas = parcelas;
        this.pagos = pagos;
    }

    @Transactional(readOnly = true)
    public Page<Agricultor> listar(String q, int page) {
        return listar(q, "", page, PaginacionWeb.TAMANIO_PREDETERMINADO);
    }

    @Transactional(readOnly = true)
    public Page<Agricultor> listar(
            String q,
            String estado,
            int page,
            int size
    ) {
        return agricultores.buscar(
                PaginacionWeb.texto(q),
                PaginacionWeb.estado(estado),
                PageRequest.of(
                        PaginacionWeb.pagina(page),
                        PaginacionWeb.tamanio(size),
                        Sort.by("apellidos", "nombres").ascending()
                )
        );
    }

    @Transactional(readOnly = true)
    public Agricultor get(Long id) {
        return agricultores.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Agricultor no encontrado"));
    }

    public Agricultor save(AgricultorForm form, Long id) {
        String dni = form.getDni().trim();
        boolean duplicado = id == null
                ? agricultores.existsByDni(dni)
                : agricultores.existsByDniAndIdNot(dni, id);

        if (duplicado) {
            throw new IllegalStateException("DNI ya registrado");
        }

        Agricultor agricultor = id == null
                ? new Agricultor(form)
                : get(id);

        if (id != null) {
            agricultor.actualizar(form);
        }

        return agricultores.save(agricultor);
    }

    /** Mantiene el comportamiento de la interfaz web existente. */
    public Agricultor cambiarEstado(Long id) {
        Agricultor agricultor = get(id);
        if ("ACTIVO".equals(agricultor.getEstado())) {
            agricultor.desactivar();
        } else {
            agricultor.activar();
        }
        return agricultores.save(agricultor);
    }

    /** Operacion explicita e idempotente para la API REST. */
    public Agricultor cambiarEstado(Long id, String estado) {
        Agricultor agricultor = get(id);
        if ("ACTIVO".equals(estado)) {
            agricultor.activar();
        } else if ("INACTIVO".equals(estado)) {
            agricultor.desactivar();
        } else {
            throw new IllegalArgumentException("El estado debe ser ACTIVO o INACTIVO");
        }
        return agricultores.save(agricultor);
    }

    public void eliminar(Long id) {
        if (parcelas.countByAgricultorId(id) > 0) {
            throw new IllegalStateException("Tiene parcelas asociadas; desactivalo");
        }
        if (pagos.countByAgricultorId(id) > 0) {
            throw new IllegalStateException("Tiene pagos asociados; desactivalo");
        }
        agricultores.delete(get(id));
    }
}
