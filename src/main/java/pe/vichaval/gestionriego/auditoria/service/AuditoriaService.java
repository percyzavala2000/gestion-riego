package pe.vichaval.gestionriego.auditoria.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import pe.vichaval.gestionriego.auditoria.entity.EventoAuditoria;
import pe.vichaval.gestionriego.auditoria.repository.EventoAuditoriaRepository;

@Service
public class AuditoriaService {
    private final EventoAuditoriaRepository repository;

    public AuditoriaService(EventoAuditoriaRepository repository) {
        this.repository = repository;
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void registrar(EventoAuditoria evento) {
        repository.save(evento);
    }

    @Transactional(readOnly = true)
    public Page<EventoAuditoria> buscar(String q, int pagina) {
        String consulta = q == null ? "" : q.trim();
        return repository.buscar(consulta, PageRequest.of(Math.max(0, pagina), 25));
    }
}
