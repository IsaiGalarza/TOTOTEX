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
import javax.xml.bind.annotation.XmlRootElement;

/**
 * @author Arturo.Herrera
 *
 */
@SuppressWarnings("serial")
@Entity
@XmlRootElement
@Table(name = "ficha_insummo_corte", schema = "public")
public class FichaInsumoCorte implements Serializable{
	@Id  @GeneratedValue(strategy=GenerationType.IDENTITY)
	private Integer id;
	
	@ManyToOne
	@JoinColumn(name="id_producto")
	private Producto producto;
	
	private Integer cantidad;
	
	@Column(name="fecha_registro")
	private Date fechaRegistro;
	
	@Column(name="usuario_registro")
	private String usuarioRegistro;
	
	@ManyToOne
	@JoinColumn(name="ficha_tecnica")
	private FichaTecnica fichaTecnica;
	
	@Column(name = "estado",nullable=true)
	private String estado;
	
	@Column(name = "fecha_aprobacion",nullable=true)
	private Date fechaAprobacion;
	
	private Boolean baja;
	
	@ManyToOne
	@JoinColumn(name="id_ficha_corte",nullable=true)
	private FichaCorte fichaCorte;
	
	public FichaInsumoCorte(){
		this.id = 0;
		this.estado = "AC";
		baja=false;
		cantidad = 1;
		producto = new Producto();
	}

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public Producto getProducto() {
		return producto;
	}

	public void setProducto(Producto producto) {
		this.producto = producto;
	}

	public Integer getCantidad() {
		return cantidad;
	}

	public void setCantidad(Integer cantidad) {
		this.cantidad = cantidad;
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

	public FichaTecnica getFichaTecnica() {
		return fichaTecnica;
	}

	public void setFichaTecnica(FichaTecnica fichaTecnica) {
		this.fichaTecnica = fichaTecnica;
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
		FichaInsumoCorte other = (FichaInsumoCorte) obj;
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
		return "FichaDetalleInsumoCorte [id=" + id + ", producto=" + producto
				+ ", cantidad=" + cantidad + ", fechaRegistro=" + fechaRegistro
				+ ", usuarioRegistro=" + usuarioRegistro + ", fichaTecnica="
				+ fichaTecnica + "]";
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
	
	public FichaCorte getFichaCorte() {
		return fichaCorte;
	}

	public void setFichaCorte(FichaCorte fichaCorte) {
		this.fichaCorte = fichaCorte;
	}
	
	
}
