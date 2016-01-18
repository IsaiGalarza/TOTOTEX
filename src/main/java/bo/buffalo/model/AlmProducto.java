package bo.buffalo.model;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQuery;
import javax.persistence.Table;

@Entity
@Table(name="alm_producto" ,schema="public")
//@NamedQuery(name="AlmProducto.findAll", query="SELECT a FROM AlmProducto a")
public class AlmProducto implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	private Integer id;

	private String estado;

	@Column(name="fecha_registro")
	private Date fechaRegistro;

	private double stock;

	@Column(name="usuario_registro")
	private String usuarioRegistro;

	//bi-directional many-to-one association to Almacen

    @ManyToOne(fetch=FetchType.EAGER, optional=false)
	@JoinColumn(name="id_almacen", nullable=false)
	private Almacen almacen;

	//bi-directional many-to-one association to Producto
	@ManyToOne(fetch=FetchType.EAGER, optional=false)
	@JoinColumn(name="id_producto", nullable=false)
	private Producto producto;
	
	@ManyToOne(fetch=FetchType.EAGER, optional=false)
	@JoinColumn(name="id_proveedor", nullable=false)
	private Proveedor proveedor;
	
	@ManyToOne(fetch=FetchType.EAGER, optional=false)
	@JoinColumn(name="id_linea_proveedor", nullable=false)
	private LineasProveedor lineaProvedor;

	public AlmProducto() {
		producto = new Producto();
		lineaProvedor= new LineasProveedor();
		proveedor= new Proveedor();
		almacen= new Almacen();
	}

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

	public Date getFechaRegistro() {
		return this.fechaRegistro;
	}

	public void setFechaRegistro(Date fechaRegistro) {
		this.fechaRegistro = fechaRegistro;
	}

	

	public double getStock() {
		return this.stock;
	}

	public void setStock(double stock) {
		this.stock = stock;
	}

	public String getUsuarioRegistro() {
		return this.usuarioRegistro;
	}

	public void setUsuarioRegistro(String usuarioRegistro) {
		this.usuarioRegistro = usuarioRegistro;
	}

	public Almacen getAlmacen() {
		return this.almacen;
	}

	public void setAlmacen(Almacen almacen) {
		this.almacen = almacen;
	}


	public Producto getProducto() {
		return this.producto;
	}

	public void setProducto(Producto producto) {
		this.producto = producto;
	}

	
	public Proveedor getProveedor() {
		return proveedor;
	}

	public void setProveedor(Proveedor proveedor) {
		this.proveedor = proveedor;
	}

	
	
	public LineasProveedor getLineaProvedor() {
		return lineaProvedor;
	}

	public void setLineaProvedor(LineasProveedor lineaProvedor) {
		this.lineaProvedor = lineaProvedor;
	}

}