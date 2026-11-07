package pe.vichaval.gestionriego.caja.service;

import java.time.LocalDate;

import org.springframework.stereotype.Service;
import pe.vichaval.gestionriego.caja.repository.CierreCajaRepository;

@Service
public class ControlCierreCajaService {
    private final CierreCajaRepository cierres;

    public ControlCierreCajaService(CierreCajaRepository cierres) {
        this.cierres = cierres;
    }

    public void validarFechaAbierta(LocalDate fecha) {
        if (fecha != null && cierres.existsByFechaAndEstado(fecha, "CERRADO")) {
            throw new IllegalStateException("La caja de la fecha " + fecha + " está cerrada");
        }
    }
}
