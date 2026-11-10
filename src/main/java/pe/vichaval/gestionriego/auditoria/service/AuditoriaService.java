package pe.vichaval.gestionriego.auditoria.service;

import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.time.temporal.ChronoUnit;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import pe.vichaval.gestionriego.auditoria.entity.EventoAuditoria;
import pe.vichaval.gestionriego.auditoria.repository.EventoAuditoriaRepository;

@Service
public class AuditoriaService {
    private static final ZoneId ZONA = ZoneId.of("America/Lima");
    private static final OffsetDateTime FECHA_MINIMA =
            OffsetDateTime.of(1970, 1, 1, 0, 0, 0, 0, ZoneOffset.UTC);
    private static final OffsetDateTime FECHA_MAXIMA =
            OffsetDateTime.of(9999, 12, 31, 23, 59, 59, 0, ZoneOffset.UTC);

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
        return buscar(q, "", "", "", "", "", null, null, pagina, 25);
    }

    @Transactional(readOnly = true)
    public Page<EventoAuditoria> buscar(
            String q,
            String usuario,
            String metodo,
            String ruta,
            String correlacionId,
            String familiaHttp,
            LocalDate fechaDesde,
            LocalDate fechaHasta,
            int page,
            int size
    ) {
        validarRango(fechaDesde, fechaHasta);

        int tamanio = size == 10 || size == 25 || size == 50 ? size : 25;
        RangoHttp rango = RangoHttp.desde(familiaHttp);

        boolean filtrarDesde = fechaDesde != null;
        boolean filtrarHasta = fechaHasta != null;
        OffsetDateTime desde = filtrarDesde
                ? fechaDesde.atStartOfDay(ZONA).toOffsetDateTime()
                : FECHA_MINIMA;
        OffsetDateTime hasta = filtrarHasta
                ? fechaHasta.plusDays(1).atStartOfDay(ZONA).toOffsetDateTime()
                : FECHA_MAXIMA;

        return repository.buscarAvanzado(
                texto(q),
                texto(usuario),
                mayusculas(metodo),
                texto(ruta),
                texto(correlacionId),
                rango.aplicar(),
                rango.desde(),
                rango.hasta(),
                filtrarDesde,
                desde,
                filtrarHasta,
                hasta,
                PageRequest.of(
                        Math.max(0, page),
                        tamanio,
                        Sort.by(
                                Sort.Order.desc("fechaHora"),
                                Sort.Order.desc("id")
                        )
                )
        );
    }

    private void validarRango(LocalDate desde, LocalDate hasta) {
        if (desde != null && hasta != null) {
            if (desde.isAfter(hasta)) {
                throw new IllegalArgumentException(
                        "La fecha desde no puede ser posterior a la fecha hasta."
                );
            }
            if (ChronoUnit.DAYS.between(desde, hasta) > 366) {
                throw new IllegalArgumentException(
                        "El rango de auditoría no puede superar 366 días."
                );
            }
        }
    }

    private String texto(String valor) {
        return valor == null ? "" : valor.trim();
    }

    private String mayusculas(String valor) {
        return texto(valor).toUpperCase();
    }

    private record RangoHttp(boolean aplicar, int desde, int hasta) {
        private static RangoHttp desde(String familia) {
            return switch (familia == null ? "" : familia.trim().toUpperCase()) {
                case "2XX" -> new RangoHttp(true, 200, 299);
                case "3XX" -> new RangoHttp(true, 300, 399);
                case "4XX" -> new RangoHttp(true, 400, 499);
                case "5XX" -> new RangoHttp(true, 500, 599);
                default -> new RangoHttp(false, 0, 999);
            };
        }
    }
}
