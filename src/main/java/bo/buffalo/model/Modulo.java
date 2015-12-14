package bo.buffalo.model;

import java.io.Serializable;

import javax.persistence.*;

/**
 * Class Modulo
 * @author Mauricio.Bejarano.Rivera
 * @version v1.0
 * 
 */
@Entity
@Table(name = "modulo", schema = "public")
public class Modulo implements Serializable {

	private static final long serialVersionUID = 5422461008458515295L;
	private Integer id;
	private String nombre;
	
	private String estado;

	public Modulo() {
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
			if(!(obj instanceof Modulo)){
				return false;
			}else{
				if(((Modulo)obj).id==this.id){
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
}


