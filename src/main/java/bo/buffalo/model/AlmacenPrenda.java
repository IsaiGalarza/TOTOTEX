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

/**
 * 
 * @author mauriciobejaranorivera
 *
 */
@Entity
@Table(name = "almacen_prenda", schema = "public")
public class AlmacenPrenda implements Serializable{

	private static final long serialVersionUID = -3586357302235244181L;

	@Id  @GeneratedValue(strategy=GenerationType.IDENTITY)
	private Integer id;
	
	@Column(name="fecha_registro")
	private Date fechaRegistro;
	
	@Column(name="usuario_registro")
	private String usuarioRegistro;

	@Column(name = "estado",nullable=true)
	private String estado;
	
	@Column(name = "fecha_aprobacion",nullable=true)
	private Date fechaAprobacion;
	
	@ManyToOne
	@JoinColumn(name = "id_almacen")
	private Almacen almacen;
	
	public AlmacenPrenda(){
		this.id = 0;
		this.estado = "AC";
	}

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public Date getFechaRegistro() {
		return fechaRegistro;
	}

	public void setFechaRegistro(Date fechaRegistro) {
		this.fechaRegistro = fechaRegistro;
	}

	public String getUsuarioRegistro() {
		return usuarioRegistro;
	}

	public void setUsuarioRegistro(String usuarioRegistro) {
		this.usuarioRegistro = usuarioRegistro;
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
		AlmacenPrenda other = (AlmacenPrenda) obj;
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
		return "AlmacenPrenda [id=" + id + ", fechaRegistro=" + fechaRegistro
				+ ", usuarioRegistro=" + usuarioRegistro ;
	}

	public String getEstado() {
		return estado;
	}

	public void setEstado(String estado) {
		this.estado = estado;
	}

	public Date getFechaAprobacion() {
		return fechaAprobacion;
	}

	public void setFechaAprobacion(Date fechaAprobacion) {
		this.fechaAprobacion = fechaAprobacion;
	}

	public Almacen getAlmacen() {
		return almacen;
	}

	public void setAlmacen(Almacen almacen) {
		this.almacen = almacen;
	}
	
	
}
