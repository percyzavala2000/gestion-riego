package pe.vichaval.gestionriego.agricultor.entity;
import jakarta.persistence.*; import pe.vichaval.gestionriego.agricultor.dto.AgricultorForm;
@Entity @Table(name="agricultores") public class Agricultor {
 @Id @GeneratedValue(strategy=GenerationType.IDENTITY) private Long id;
 @Column(nullable=false,unique=true,length=8) private String dni; @Column(nullable=false,length=100) private String nombres; @Column(nullable=false,length=120) private String apellidos; @Column(length=20) private String telefono; @Column(nullable=false,length=30) private String sector; @Column(nullable=false,length=20) private String estado="ACTIVO";
 protected Agricultor(){} public Agricultor(AgricultorForm f){actualizar(f);} public void actualizar(AgricultorForm f){dni=f.getDni().trim();nombres=f.getNombres().trim();apellidos=f.getApellidos().trim();telefono=f.getTelefono()==null?null:f.getTelefono().trim();sector=f.getSector();estado=f.getEstado();} public void activar(){estado="ACTIVO";} public void desactivar(){estado="INACTIVO";}
 public Long getId(){return id;} public String getDni(){return dni;} public String getNombres(){return nombres;} public String getApellidos(){return apellidos;} public String getTelefono(){return telefono;} public String getSector(){return sector;} public String getEstado(){return estado;}
}