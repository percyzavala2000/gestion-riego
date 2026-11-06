package pe.vichaval.gestionriego.api.v1.parcela;

import java.math.BigDecimal;

import pe.vichaval.gestionriego.parcela.entity.Parcela;

public record ParcelaRespuesta(Long id, String codigo, String nombre, BigDecimal hectareas, String cultivo,
                               String estado, Long agricultorId, String agricultor) {
    public static ParcelaRespuesta desde(Parcela p) {
        var a = p.getAgricultor();
        return new ParcelaRespuesta(p.getId(), p.getCodigo(), p.getNombre(), p.getHectareas(), p.getCultivo(), p.getEstado(), a.getId(), a.getNombres() + " " + a.getApellidos());
    }
}
