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
import javax.persistence.NamedQuery;
import javax.persistence.Table;

@Entity
@Table(name = "movimiento_almacen", schema = "public")
//@NamedQuery(name = "MovimientoAlmacen.findAll", query = "SELECT m FROM MovimientoAlmacen m")
public class MovimientoAlmacen implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Integer id;

	private double cantidad;
	
	@Column(name = "precio_venta")
	private double precioVenta;
	

	
	private String concepto;
	
	@Column(name = "precio_total")
	private double precioTotal;

	@Column(name = "numero_entrega")
	private Integer numeroEntrega;

	private String estado;

	@Column(name = "fecha_registro")
	private Date fechaRegistro;

	@Column(name = "tipo_movimiento")
	private String tipoMovimiento;

	@Column(name = "usuario_registro")
	private String usuarioRegistro;

	// bi-directional many-to-one association to Almacen
	@ManyToOne
	@JoinColumn(name = "id_almacen_in")
	private Almacen almacen1;

	// bi-directional many-to-one association to Almacen
	@ManyToOne
	@JoinColumn(name = "id_almacen_out")
	private Almacen almacen2;

	// bi-directional many-to-one association to PedidoMov
	@ManyToOne
	@JoinColumn(name = "id_pedido_mov")
	private PedidoMov pedidoMov;

	// bi-directional many-to-one association to Producto
	@ManyToOne
	@JoinColumn(name = "id_producto")
	private Producto producto;

	public MovimientoAlmacen() {
	}

	public Integer getId() {
		return this.id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public double getCantidad() {
		return this.cantidad;
	}

	public void setCantidad(double cantidad) {
		this.cantidad = cantidad;
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

	public String getTipoMovimiento() {
		return this.tipoMovimiento;
	}

	public void setTipoMovimiento(String tipoMovimiento) {
		this.tipoMovimiento = tipoMovimiento;
	}

	public String getUsuarioRegistro() {
		return this.usuarioRegistro;
	}

	public void setUsuarioRegistro(String usuarioRegistro) {
		this.usuarioRegistro = usuarioRegistro;
	}

	public Almacen getAlmacen1() {
		return this.almacen1;
	}

	public void setAlmacen1(Almacen almacen1) {
		this.almacen1 = almacen1;
	}

	public Almacen getAlmacen2() {
		return this.almacen2;
	}

	public void setAlmacen2(Almacen almacen2) {
		this.almacen2 = almacen2;
	}

	public PedidoMov getPedidoMov() {
		return this.pedidoMov;
	}

	public void setPedidoMov(PedidoMov pedidoMov) {
		this.pedidoMov = pedidoMov;
	}

	public Producto getProducto() {
		return this.producto;
	}

	public void setProducto(Producto producto) {
		this.producto = producto;
	}

	public Integer getNumeroEntrega() {
		return numeroEntrega;
	}

	public void setNumeroEntrega(Integer numeroEntrega) {
		this.numeroEntrega = numeroEntrega;
	}

	public double getPrecioVenta() {
		return precioVenta;
	}

	public void setPrecioVenta(double precioVenta) {
		this.precioVenta = precioVenta;
	}

	public double getPrecioTotal() {
		return precioTotal;
	}

	public void setPrecioTotal(double precioTotal) {
		this.precioTotal = precioTotal;
	}

	public String getConcepto() {
		return concepto;
	}

	public void setConcepto(String concepto) {
		this.concepto = concepto;
	}

}
