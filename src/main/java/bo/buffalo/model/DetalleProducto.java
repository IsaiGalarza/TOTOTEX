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
@Table(name="detalle_producto"
    ,schema="public"
)
public class DetalleProducto implements Serializable {

	private static final long serialVersionUID = 3732889148427367112L;
	
	private Integer id;
	private String estado;
	private Date fechaRegistro;
	private double cantidad=0;
	private double concentracion=0;
	private double incidencia;
	private String usuarioRegistro;
	private Producto producto;
	private Integer correlativo;
	private String unidadIncidencia;
	private Producto productoCompuesto; 

	public DetalleProducto() {
	}


	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	public Integer getId() {
		return this.id;
	}

	public void setId(Integer id) {
		this.id = id;
	}


	public String getEstado() {
		return this.estado;
	}

	public void setEstado(String estado) {
		this.estado = estado;
	}


	@Column(name="fecha_registro")
	public Date getFechaRegistro() {
		return this.fechaRegistro;
	}

	public void setFechaRegistro(Date fechaRegistro) {
		this.fechaRegistro = fechaRegistro;
	}


	public double getIncidencia() {
		return this.incidencia;
	}

	public void setIncidencia(double incidencia) {
		this.incidencia = incidencia;
	}


	@Column(name="usuario_registro")
	public String getUsuarioRegistro() {
		return this.usuarioRegistro;
	}

	public void setUsuarioRegistro(String usuarioRegistro) {
		this.usuarioRegistro = usuarioRegistro;
	}


	//bi-directional many-to-one association to Producto
//	@ManyToOne(cascade={CascadeType.ALL})
//	@ManyToOne(fetch=FetchType.EAGER, optional=false)
//	@JoinColumn(name="id_producto")
	
	//bi-directional many-to-one association to Producto
	@ManyToOne
	@JoinColumn(name="id_producto", nullable=false)
	public Producto getProducto() {
		return this.producto;
	}

	public void setProducto(Producto producto) {
		this.producto = producto;
	}

	
	
	// bi-directional many-to-one association to Producto
	@ManyToOne
	@JoinColumn(name = "id_producto_compuesto", nullable=false)
	public Producto getProductoCompuesto() {
		return productoCompuesto;
	}

	public void setProductoCompuesto(Producto productoCompuesto) {
		this.productoCompuesto = productoCompuesto;
	}


	@Column(name="unidad_incidencia",length=50)
	public String getUnidadIncidencia() {
		return unidadIncidencia;
	}


	public void setUnidadIncidencia(String unidadIncidencia) {
		this.unidadIncidencia = unidadIncidencia;
	}


	public double getCantidad() {
		return cantidad;
	}


	public void setCantidad(double cantidad) {
		this.cantidad = cantidad;
	}


	public Integer getCorrelativo() {
		return correlativo;
	}


	public void setCorrelativo(Integer correlativo) {
		this.correlativo = correlativo;
	}


	public double getConcentracion() {
		return concentracion;
	}


	public void setConcentracion(double concentracion) {
		this.concentracion = concentracion;
	}


	

}
