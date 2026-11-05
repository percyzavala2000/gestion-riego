package pe.vichaval.gestionriego.api.v1.agricultor;
import pe.vichaval.gestionriego.agricultor.entity.Agricultor;
public record AgricultorRespuesta(Long id,String dni,String nombres,String apellidos,String telefono,String sector,String estado){
 public static AgricultorRespuesta desde(Agricultor a){return new AgricultorRespuesta(a.getId(),a.getDni(),a.getNombres(),a.getApellidos(),a.getTelefono(),a.getSector(),a.getEstado());}
}
