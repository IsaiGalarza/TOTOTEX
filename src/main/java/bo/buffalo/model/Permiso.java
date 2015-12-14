package bo.buffalo.model;

import java.io.Serializable;

import javax.persistence.*;

/**
 * Class Permiso
 * @author Mauricio.Bejarano.Rivera
 * @version v1.0
 * 
 */
@Entity
@Table(name = "permiso", schema = "public")
public class Permiso implements Serializable {

	private static final long serialVersionUID = 1322449909690295931L;
	private Integer id;
	private String nombre;
	
	//referencia Modulo
	private Modulo modulo;
	
	private String estado;

	public Permiso() {
		super();
	}

	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	public Integer getId() {
		return this.id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	@Override
	public int hashCode() {
		int hash = 0;
		hash += (id != null ? id.hashCode() : 0);
		return hash;
	}
	
	@Override
	public String toString() {
		return nombre ;
	}
	
	@Override
	public boolean equals(Object obj) {
		if(obj==null){
			return false;
		}else{
			if(!(obj instanceof Permiso)){
				return false;
			}else{
				if(((Permiso)obj).id==this.id){
					return true;
				}else{
					return false;
				}
			}
		}
	}

	public String getNombre() {
		return nombre;
	}

	public void setNombre(String nombre) {
		this.nombre = nombre;
	}

	public String getEstado() {
		return estado;
	}

	public void setEstado(String estado) {
		this.estado = estado;
	}

	@ManyToOne(fetch = FetchType.EAGER, optional = false)
	@JoinColumn(name="id_modulo")
	public Modulo getModulo() {
		return modulo;
	}

	public void setModulo(Modulo modulo) {
		this.modulo = modulo;
	}
}


