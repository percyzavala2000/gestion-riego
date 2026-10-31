package pe.vichaval.gestionriego.tesoreria.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pe.vichaval.gestionriego.agricultor.repository.AgricultorRepository;
import pe.vichaval.gestionriego.tesoreria.dto.PagoForm;
import pe.vichaval.gestionriego.tesoreria.entity.Pago;
import pe.vichaval.gestionriego.tesoreria.repository.PagoRepository;

@Service
@Transactional
public class PagoService {
    private final PagoRepository p;
    private final AgricultorRepository a;

    public PagoService(PagoRepository p, AgricultorRepository a) {
        this.p = p;
        this.a = a;
    }

    public Pago get(Long id) {
        return p.findById(id).orElseThrow(() -> new IllegalArgumentException("Pago no encontrado"));
    }

    public void registrar(PagoForm f) {
        if (p.existsByComprobante(f.getComprobante())) throw new IllegalArgumentException("Comprobante duplicado");
        var ag = a.findById(f.getAgricultorId()).orElseThrow();
        p.save(new Pago("PG-%06d".formatted(p.count() + 1), f, ag));
    }

    public void confirmar(Long id) {
        get(id).confirmar();
    }

    public void rechazar(Long id, String m) {
        get(id).rechazar(m);
    }

    public void anular(Long id, String m) {
        get(id).anular(m);
    }
}
