package pe.vichaval.gestionriego.finanzas.service;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import pe.vichaval.gestionriego.agricultor.repository.AgricultorRepository;
import pe.vichaval.gestionriego.finanzas.dto.ConceptoCobroForm;
import pe.vichaval.gestionriego.finanzas.dto.CuentaCobrarForm;
import pe.vichaval.gestionriego.finanzas.dto.PagoCuentaForm;
import pe.vichaval.gestionriego.finanzas.entity.ConceptoCobro;
import pe.vichaval.gestionriego.finanzas.entity.CuentaCobrar;
import pe.vichaval.gestionriego.finanzas.repository.ConceptoCobroRepository;
import pe.vichaval.gestionriego.finanzas.repository.CuentaCobrarRepository;
import pe.vichaval.gestionriego.tesoreria.dto.PagoForm;
import pe.vichaval.gestionriego.tesoreria.service.PagoService;

@Service
@Transactional
public class FinanzasService {

    private final CuentaCobrarRepository cuentas;
    private final ConceptoCobroRepository conceptos;
    private final AgricultorRepository agricultores;
    private final PagoService pagos;

    public FinanzasService(
            CuentaCobrarRepository cuentas,
            ConceptoCobroRepository conceptos,
            AgricultorRepository agricultores,
            PagoService pagos
    ) {
        this.cuentas = cuentas;
        this.conceptos = conceptos;
        this.agricultores = agricultores;
        this.pagos = pagos;
    }

    public List<CuentaCobrar> listar() {

        var lista = cuentas.findAll();

        lista.forEach(
            CuentaCobrar::actualizarVencida
        );

        return lista;
    }

    @Transactional(readOnly = true)
    public CuentaCobrar get(Long id) {

        return cuentas
            .findById(id)
            .orElseThrow(() ->
                new IllegalArgumentException(
                    "Cuenta no encontrada."
                )
            );
    }

    public void crearCuenta(
            CuentaCobrarForm form
    ) {
        if (
            form.getFechaEmision() == null
            || form.getFechaVencimiento() == null
        ) {
            throw new IllegalArgumentException(
                "Las fechas de emisión y vencimiento son obligatorias."
            );
        }

        if (
            form.getFechaVencimiento()
                .isBefore(form.getFechaEmision())
        ) {
            throw new IllegalArgumentException(
                "El vencimiento no puede ser anterior a la emisión."
            );
        }

        var agricultor = agricultores
            .findById(form.getAgricultorId())
            .orElseThrow(() ->
                new IllegalArgumentException(
                    "El agricultor seleccionado no existe."
                )
            );

        if (!"ACTIVO".equals(agricultor.getEstado())) {
            throw new IllegalStateException(
                "No se puede emitir la cuenta a un agricultor inactivo."
            );
        }

        var concepto = conceptos
            .findById(form.getConceptoId())
            .orElseThrow(() ->
                new IllegalArgumentException(
                    "El concepto seleccionado no existe."
                )
            );

        if (!concepto.isActivo()) {
            throw new IllegalStateException(
                "El concepto seleccionado está inactivo."
            );
        }

        String codigo = "CC-%06d".formatted(
            cuentas.count() + 1
        );

        CuentaCobrar cuenta = new CuentaCobrar(
            codigo,
            form,
            agricultor,
            concepto
        );

        cuentas.save(cuenta);
    }

    public void registrarPago(
            Long cuentaId,
            PagoCuentaForm form
    ) {
        CuentaCobrar cuenta = get(cuentaId);

        if (
            "ANULADA".equals(cuenta.getEstado())
            || "PAGADA".equals(cuenta.getEstado())
        ) {
            throw new IllegalStateException(
                "La cuenta no admite pagos."
            );
        }

        if (
            form.getMonto() == null
            || form.getMonto().signum() <= 0
        ) {
            throw new IllegalArgumentException(
                "El monto debe ser mayor que cero."
            );
        }

        if (
            form.getMonto()
                .compareTo(cuenta.getSaldoPendiente()) > 0
        ) {
            throw new IllegalStateException(
                "El pago supera el saldo pendiente."
            );
        }

        /*
         * Primero se aplica el pago a la cuenta.
         * Si PagoService falla, la transacción completa
         * se revierte automáticamente.
         */
        cuenta.aplicarPago(
            form.getMonto()
        );

        PagoForm pagoForm = new PagoForm();

        pagoForm.setAgricultorId(
            cuenta.getAgricultor().getId()
        );

        pagoForm.setMonto(
            form.getMonto()
        );

        pagoForm.setFecha(
            form.getFecha()
        );

        pagoForm.setConcepto(
            cuenta.getConcepto().getNombre()
            + " - "
            + cuenta.getCodigo()
        );

        pagoForm.setComprobante(
            form.getComprobante()
        );

        pagoForm.setMetodo(
            form.getMetodo()
        );

        pagos.registrar(
            pagoForm
        );

        cuentas.save(cuenta);
    }

    public void anular(
            Long id,
            String motivo
    ) {
        CuentaCobrar cuenta = get(id);

        cuenta.anular(motivo);

        cuentas.save(cuenta);
    }

    @Transactional(readOnly = true)
    public List<ConceptoCobro> listarConceptos() {

        return conceptos
            .findAllByOrderByNombre();
    }

    @Transactional(readOnly = true)
    public List<ConceptoCobro> conceptosActivos() {

        return conceptos
            .findByActivoTrueOrderByNombre();
    }

    public void crearConcepto(
            ConceptoCobroForm form
    ) {
        if (form == null) {
            throw new IllegalArgumentException(
                "Los datos del concepto son obligatorios."
            );
        }

        /*
         * Convierte, por ejemplo:
         *
         * derecho  -> DERECHO
         * Derecho  -> DERECHO
         */
        String codigo = form.getCodigo() == null
                ? ""
                : form.getCodigo()
                    .trim()
                    .toUpperCase();

        String nombre = form.getNombre() == null
                ? ""
                : form.getNombre().trim();

        String descripcion =
            form.getDescripcion() == null
                ? null
                : form.getDescripcion().trim();

        if (codigo.isBlank()) {
            throw new IllegalArgumentException(
                "El código es obligatorio."
            );
        }

        if (nombre.isBlank()) {
            throw new IllegalArgumentException(
                "El nombre es obligatorio."
            );
        }

        if (
            conceptos.existsByCodigoIgnoreCase(
                codigo
            )
        ) {
            throw new IllegalArgumentException(
                "El código "
                + codigo
                + " ya está registrado."
            );
        }

        /*
         * Se guardan los valores normalizados en el formulario.
         */
        form.setCodigo(codigo);
        form.setNombre(nombre);

        if (
            descripcion == null
            || descripcion.isBlank()
        ) {
            form.setDescripcion(null);
        } else {
            form.setDescripcion(descripcion);
        }

        ConceptoCobro concepto =
            new ConceptoCobro(form);

        conceptos.save(concepto);
    }
}
