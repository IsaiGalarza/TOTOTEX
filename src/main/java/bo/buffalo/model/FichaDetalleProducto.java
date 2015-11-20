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
@Table(name = "ficha_detalle_producto", schema = "public")
public class FichaDetalleProducto implements Serializable{
	@Id  @GeneratedValue(strategy=GenerationType.IDENTITY)
	private Integer id;
	
	private Integer talla;
	
	private Integer cantidad;
	
	@Column(name="codigo_barra")
	private String codigoBarra;
	
	@Column(name="fecha_regisro")
	private Date FechaRegistro;
	
	@Column(name="usuario_registro")
	private String UsuarioRegistro;
	
	@ManyToOne
	@JoinColumn(name="id_ficha_tecnica")
	private FichaTecnica fichaTecnica;
	
	private Boolean baja;
	
	public FichaDetalleProducto(){
		baja=false;
		codigoBarra="000000";
	}

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public Integer getTalla() {
		return talla;
	}

	public void setTalla(Integer talla) {
		this.talla = talla;
	}

	public Integer getCantidad() {
		return cantidad;
	}

	public void setCantidad(Integer cantidad) {
		this.cantidad = cantidad;
	}

	public String getCodigoBarra() {
		return codigoBarra;
	}

	public void setCodigoBarra(String codigoBarra) {
		this.codigoBarra = codigoBarra;
	}

	public Date getFechaRegistro() {
		return FechaRegistro;
	}

	public void setFechaRegistro(Date fechaRegistro) {
		FechaRegistro = fechaRegistro;
	}

	public String getUsuarioRegistro() {
		return UsuarioRegistro;
	}

	public void setUsuarioRegistro(String usuarioRegistro) {
		UsuarioRegistro = usuarioRegistro;
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
		FichaDetalleProducto other = (FichaDetalleProducto) obj;
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
		return "FichaDetalleProducto [id=" + id + ", talla=" + talla
				+ ", cantidad=" + cantidad + ", CodigoBarra=" + codigoBarra
				+ ", FechaRegistro=" + FechaRegistro + ", UsuarioRegistro="
				+ UsuarioRegistro + ", fichaTecnica=" + fichaTecnica + "]";
	}
	
	
}
