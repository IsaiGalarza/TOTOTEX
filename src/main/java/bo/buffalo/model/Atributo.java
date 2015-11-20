package bo.buffalo.model;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;
import javax.xml.bind.annotation.XmlRootElement;




/**
 * @author Arturo.Herrera
 *
 */
@SuppressWarnings("serial")
@Entity
@XmlRootElement
@Table(name = "atributo", schema = "public")
public class Atributo implements Serializable{
	
	@Id  @GeneratedValue(strategy=GenerationType.IDENTITY)
	private Integer id;
	@NotNull(message="Ingrese un nombre")
	private String nombre;
	private String descripcion;
	@ManyToOne
	@JoinColumn(name = "id_atributo_padre",nullable=true)
	private Atributo atributoPadre;
	@ManyToOne
	@JoinColumn(name = "id_usuarioRegistro")
	private Usuario usuarioRegistro;	
	@Column(name = "id_fechaRegistro")
	private Date fechaRegistro;
	
	private Boolean baja;
	
	public Atributo(){
		baja=false;
	}
	
	public Atributo(String nombre, String descripcion, Atributo atributoPadre,
			Usuario usuarioRegistro, Date fechaRegistro) {
		this.nombre = nombre;
		this.descripcion = descripcion;
		this.atributoPadre = atributoPadre;
		this.usuarioRegistro = usuarioRegistro;
		this.fechaRegistro = fechaRegistro;
	}

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public String getNombre() {
		return nombre;
	}

	public void setNombre(String nombre) {
		this.nombre = nombre;
	}

	public String getDescripcion() {
		return descripcion;
	}

	public void setDescripcion(String descripcion) {
		this.descripcion = descripcion;
	}

	public Atributo getAtributoPadre() {
		return atributoPadre;
	}

	public void setAtributoPadre(Atributo atributoPadre) {
		this.atributoPadre = atributoPadre;
	}

	public Usuario getUsuarioRegistro() {
		return usuarioRegistro;
	}

	public void setUsuarioRegistro(Usuario usuarioRegistro) {
		this.usuarioRegistro = usuarioRegistro;
	}

	public Date getFechaRegistro() {
		return fechaRegistro;
	}

	public void setFechaRegistro(Date fechaRegistro) {
		this.fechaRegistro = fechaRegistro;
	}

	public Boolean getBaja() {
		return baja;
	}

	public void setBaja(Boolean baja) {
		this.baja = baja;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((id == null) ? 0 : id.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj == null) {
			return false;
		}
		if (getClass() != obj.getClass()) {
			return false;
		}
		Atributo other = (Atributo) obj;
		if (id == null) {
			if (other.id != null) {
				return false;
			}
		} else if (!id.equals(other.id)) {
			return false;
		}
		return true;
	}

	@Override
	public String toString() {
		return "Atributo [id=" + id + ", nombre=" + nombre + ", descripcion="
				+ descripcion + ", fechaRegistro=" + fechaRegistro + "]";
	}
	
	
	
	
}
