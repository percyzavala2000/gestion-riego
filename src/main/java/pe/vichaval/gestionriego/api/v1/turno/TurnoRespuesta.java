package pe.vichaval.gestionriego.api.v1.turno;
import java.time.*;
import pe.vichaval.gestionriego.riego.entity.TurnoRiego;
public record TurnoRespuesta(Long id,String codigo,LocalDate fecha,LocalTime horaInicio,LocalTime horaFin,String estado,String observaciones,String motivoCancelacion,Long parcelaId,String parcelaCodigo,Long agricultorId){
 public static TurnoRespuesta desde(TurnoRiego t){var p=t.getParcela();return new TurnoRespuesta(t.getId(),t.getCodigo(),t.getFecha(),t.getHoraInicio(),t.getHoraFin(),t.getEstado(),t.getObservaciones(),t.getMotivoCancelacion(),p.getId(),p.getCodigo(),p.getAgricultor().getId());}
}
