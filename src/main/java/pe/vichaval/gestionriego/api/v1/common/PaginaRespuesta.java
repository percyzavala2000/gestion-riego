package pe.vichaval.gestionriego.api.v1.common;
import java.util.List;
import org.springframework.data.domain.Page;
public record PaginaRespuesta<T>(List<T> contenido,int pagina,int tamanio,long totalElementos,int totalPaginas,boolean primera,boolean ultima){
 public static <T> PaginaRespuesta<T> desde(Page<T> p){return new PaginaRespuesta<>(p.getContent(),p.getNumber(),p.getSize(),p.getTotalElements(),p.getTotalPages(),p.isFirst(),p.isLast());}
}
