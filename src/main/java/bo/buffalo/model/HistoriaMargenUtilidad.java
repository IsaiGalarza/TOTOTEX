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

@Entity
@Table(name = "historia_margen_utilidad", schema = "public")
public class HistoriaMargenUtilidad implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 169643086129222878L;

	private Integer id;
	
	
	private Integer utilidadMin;
	
	
	private Integer utilidadMax;
	
	
	private Integer utilidadActual;
	
	private String estado;
	
	
	private Date fechaRegistro;
	
	
	private String usuarioRegistro;

	
	private Proveedor proveedor;

	public HistoriaMargenUtilidad() {
		super();
		proveedor= new Proveedor();
	}

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}
	@Column(name = "utilidad_min")
	public Integer getUtilidadMin() {
		return utilidadMin;
	}

	public void setUtilidadMin(Integer utilidadMin) {
		this.utilidadMin = utilidadMin;
	}

	@Column(name = "utilidad_max")
	public Integer getUtilidadMax() {
		return utilidadMax;
	}

	public void setUtilidadMax(Integer utilidadMax) {
		this.utilidadMax = utilidadMax;
	}

	@Column(name = "utilidad_actual")	
	public Integer getUtilidadActual() {
		return utilidadActual;
	}

	public void setUtilidadActual(Integer utilidadActual) {
		this.utilidadActual = utilidadActual;
	}

	public String getEstado() {
		return estado;
	}

	public void setEstado(String estado) {
		this.estado = estado;
	}

	@Column(name = "fecha_registro")
	public Date getFechaRegistro() {
		return fechaRegistro;
	}

	public void setFechaRegistro(Date fechaRegistro) {
		this.fechaRegistro = fechaRegistro;
	}

	@Column(name = "usuario_registro")
	public String getUsuarioRegistro() {
		return usuarioRegistro;
	}

	public void setUsuarioRegistro(String usuarioRegistro) {
		this.usuarioRegistro = usuarioRegistro;
	}

	@ManyToOne
	@JoinColumn(name = "id_proveedor")
	public Proveedor getProveedor() {
		return proveedor;
	}

	public void setProveedor(Proveedor proveedor) {
		this.proveedor = proveedor;
	}
}
