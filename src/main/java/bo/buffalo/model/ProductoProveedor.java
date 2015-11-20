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
import javax.persistence.Table;

@Entity
@Table(name="producto_proveedor")
public class ProductoProveedor implements Serializable {
	private static final long serialVersionUID = 1L;
	private Integer id;
	private String estado;
	private Date fechaRegistro;
	private Date fechaVencimiento;
	private double precioUnitarioCompra;
	private double precioUnitarioVenta;
	private String tipoCambio;
	private String usuarioRegistro;
	private double utilidadMax;
	private double utilidadMaxReCal;
	private double utilidadMin;
	private LineasProveedor lineasProveedor;
	private Producto producto;
	private Proveedor proveedor;

	public ProductoProveedor() {
		lineasProveedor = new LineasProveedor();
		proveedor = new Proveedor();
		producto = new Producto();
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


	@Column(name="fecha_vencimiento")
	public Date getFechaVencimiento() {
		return this.fechaVencimiento;
	}

	public void setFechaVencimiento(Date fechaVencimiento) {
		this.fechaVencimiento = fechaVencimiento;
	}


	@Column(name="precio_unitario_compra")
	public double getPrecioUnitarioCompra() {
		return this.precioUnitarioCompra;
	}

	public void setPrecioUnitarioCompra(double precioUnitarioCompra) {
		this.precioUnitarioCompra = precioUnitarioCompra;
	}


	@Column(name="precio_unitario_venta")
	public double getPrecioUnitarioVenta() {
		return this.precioUnitarioVenta;
	}

	public void setPrecioUnitarioVenta(double precioUnitarioVenta) {
		this.precioUnitarioVenta = precioUnitarioVenta;
	}


	@Column(name="tipo_cambio")
	public String getTipoCambio() {
		return this.tipoCambio;
	}

	public void setTipoCambio(String tipoCambio) {
		this.tipoCambio = tipoCambio;
	}


	@Column(name="usuario_registro")
	public String getUsuarioRegistro() {
		return this.usuarioRegistro;
	}

	public void setUsuarioRegistro(String usuarioRegistro) {
		this.usuarioRegistro = usuarioRegistro;
	}


	@Column(name="utilidad_max")
	public double getUtilidadMax() {
		return this.utilidadMax;
	}

	public void setUtilidadMax(double utilidadMax) {
		this.utilidadMax = utilidadMax;
	}


	@Column(name="utilidad_max_re_cal")
	public double getUtilidadMaxReCal() {
		return this.utilidadMaxReCal;
	}

	public void setUtilidadMaxReCal(double utilidadMaxReCal) {
		this.utilidadMaxReCal = utilidadMaxReCal;
	}


	@Column(name="utilidad_min")
	public double getUtilidadMin() {
		return this.utilidadMin;
	}

	public void setUtilidadMin(double utilidadMin) {
		this.utilidadMin = utilidadMin;
	}


	//bi-directional many-to-one association to LineasProveedor
	@ManyToOne(fetch=FetchType.EAGER, optional=false)
	@JoinColumn(name="id_linea_proveedor")
	public LineasProveedor getLineasProveedor() {
		return this.lineasProveedor;
	}

	public void setLineasProveedor(LineasProveedor lineasProveedor) {
		this.lineasProveedor = lineasProveedor;
	}


	//bi-directional many-to-one association to Producto
	@ManyToOne(fetch=FetchType.EAGER, optional=false)
	@JoinColumn(name="id_producto")
	public Producto getProducto() {
		return this.producto;
	}

	public void setProducto(Producto producto) {
		this.producto = producto;
	}


	//bi-directional many-to-one association to Proveedor
	@ManyToOne(fetch=FetchType.EAGER, optional=false)
	@JoinColumn(name="id_proveedor")
	public Proveedor getProveedor() {
		return this.proveedor;
	}

	public void setProveedor(Proveedor proveedor) {
		this.proveedor = proveedor;
	}

}