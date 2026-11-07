package pe.vichaval.gestionriego.infraestructura;

import jakarta.persistence.EntityManager;
import org.springframework.stereotype.Service;

@Service
public class CorrelativoService {
    private final EntityManager entityManager;

    public CorrelativoService(EntityManager entityManager) {
        this.entityManager = entityManager;
    }

    public String siguiente(String secuencia, String prefijo) {
        if (!secuencia.matches("seq_(pago|cuenta|egreso|devolucion)_codigo")) {
            throw new IllegalArgumentException("Secuencia no permitida");
        }
        Number valor = (Number) entityManager.createNativeQuery("select nextval('" + secuencia + "')").getSingleResult();
        return "%s-%06d".formatted(prefijo, valor.longValue());
    }
}
