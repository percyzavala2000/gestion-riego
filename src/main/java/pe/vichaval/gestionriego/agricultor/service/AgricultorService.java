package pe.vichaval.gestionriego.agricultor.service;

import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pe.vichaval.gestionriego.agricultor.dto.AgricultorForm;
import pe.vichaval.gestionriego.agricultor.entity.Agricultor;
import pe.vichaval.gestionriego.agricultor.repository.AgricultorRepository;
import pe.vichaval.gestionriego.parcela.repository.ParcelaRepository;
import pe.vichaval.gestionriego.tesoreria.repository.PagoRepository;

@Service
@Transactional
public class AgricultorService {
    private final AgricultorRepository a;
    private final ParcelaRepository p;
    private final PagoRepository pagos;

    public AgricultorService(AgricultorRepository a, ParcelaRepository p, PagoRepository pagos) {
        this.a = a;
        this.p = p;
        this.pagos = pagos;
    }

    @Transactional(readOnly = true)
    public Page<Agricultor> listar(String q, int page) {
        return a.buscar(q == null ? "" : q.trim(), PageRequest.of(Math.max(0, page), 6, Sort.by("apellidos", "nombres")));
    }

    @Transactional(readOnly = true)
    public Agricultor get(Long id) {
        return a.findById(id).orElseThrow(() -> new IllegalArgumentException("Agricultor no encontrado"));
    }

    public void save(AgricultorForm f, Long id) {
        if (id == null && a.existsByDni(f.getDni())) throw new IllegalArgumentException("DNI ya registrado");
        if (id != null && a.existsByDniAndIdNot(f.getDni(), id))
            throw new IllegalArgumentException("DNI ya registrado");
        Agricultor x = id == null ? new Agricultor(f) : get(id);
        if (id != null) x.actualizar(f);
        a.save(x);
    }

    public void cambiarEstado(Long id) {
        Agricultor x = get(id);
        if ("ACTIVO".equals(x.getEstado())) x.desactivar();
        else x.activar();
    }

    public void eliminar(Long id) {
        if (p.countByAgricultorId(id) > 0) throw new IllegalStateException("Tiene parcelas asociadas; desactivalo");
        if (pagos.countByAgricultorId(id) > 0) throw new IllegalStateException("Tiene pagos asociados; desactivalo");
        a.delete(get(id));
    }
}
