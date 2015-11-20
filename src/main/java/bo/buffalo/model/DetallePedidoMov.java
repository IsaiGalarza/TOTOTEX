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
@Table(name="detalle_pedido_mov",schema="public")
//@NamedQuery(name="DetallePedidoMov.findAll", query="SELECT d FROM DetallePedidoMov d")
public class DetallePedidoMov implements Serializable {
	private static final long serialVersionUID = 1L;
	
	private Integer correlativo;
	
	@Column(name="unidad_medida")
	private String unidadMedida = "Unidad";
	
	private double incidencia;

	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	private Integer id;

	private double cantidad;

	private String estado;

	@Column(name="fecha_registro")
	private Date fechaRegistro;

	@Column(name="precio_venta")
	private double precioVenta;

	private double total;
	
	@Column(name="total_dolares")
	private double totalDolares;
	
	private String moneda;
	
	@Column(name="cantidad_entregado")
	private double cantidadEntregado;

	@Column(name="cantidad_entrega")
	private Integer cantidadEntrega;
	
	@Column(name="total_entregado")
	private double totalEntregado=0;
	
	@Column(name="estado_entrega")
	private String estadoEntrega;

	@Column(name="usuario_registro")
	private String usuarioRegistro;
	
	@Column(name="precio_unitario")
	private double precioUnitario;
	
	@Column(name="descuento_uno")
	private double descuentoUno;
	
	@Column(name="descuento_dos")
	private double descuentoDos;
	
	@Column(name="bonificacion")
	private double bonificacion;
	
	@Column(name="importe")
	private double importe;

	//bi-directional many-to-one association to LineasProveedor
	 @ManyToOne(fetch=FetchType.EAGER, optional=false)
	@JoinColumn(name="id_linea_proveedor")
	private LineasProveedor lineasProveedor;

	//bi-directional many-to-one association to PedidoMov
	 @ManyToOne(fetch=FetchType.EAGER, optional=false)
	@JoinColumn(name="id_pedido_mov")
	private PedidoMov pedidoMov;

	//bi-directional many-to-one association to Producto
	@ManyToOne(fetch=FetchType.EAGER, optional=false)
	@JoinColumn(name="id_producto", nullable=true)
	private Producto producto;

	//bi-directional many-to-one association to Proveedor
	 @ManyToOne(fetch=FetchType.EAGER, optional=false)
	@JoinColumn(name="id_proveedor", nullable=true)
	private Proveedor proveedor;
	
	@Column(name = "marguen_utilidad")
	private double margenUtilidad; 
	
	@Column(name = "precio_proveedor")
	private double precioProveedor;
	 
	@Column(name = "aplicar_precio")
	private boolean aplicarPrecio;

	public DetallePedidoMov() {
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

	public double getPrecioVenta() {
		return this.precioVenta;
	}

	public void setPrecioVenta(double precioVenta) {
		this.precioVenta = precioVenta;
	}

	public double getTotal() {
		return this.total;
	}

	public void setTotal(double total) {
		this.total = total;
	}

	public String getUsuarioRegistro() {
		return this.usuarioRegistro;
	}

	public void setUsuarioRegistro(String usuarioRegistro) {
		this.usuarioRegistro = usuarioRegistro;
	}

	public LineasProveedor getLineasProveedor() {
		return this.lineasProveedor;
	}

	public void setLineasProveedor(LineasProveedor lineasProveedor) {
		this.lineasProveedor = lineasProveedor;
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

	public Proveedor getProveedor() {
		return this.proveedor;
	}

	public void setProveedor(Proveedor proveedor) {
		this.proveedor = proveedor;
	}

	public double getCantidadEntregado() {
		return cantidadEntregado;
	}

	public void setCantidadEntregado(double cantidadEntregado) {
		this.cantidadEntregado = cantidadEntregado;
	}

	public String getEstadoEntrega() {
		return estadoEntrega;
	}

	public void setEstadoEntrega(String estadoEntrega) {
		this.estadoEntrega = estadoEntrega;
	}

	public double getTotalEntregado() {
		return totalEntregado;
	}

	public void setTotalEntregado(double totalEntregado) {
		this.totalEntregado = totalEntregado;
	}

	public Integer getCantidadEntrega() {
		return cantidadEntrega;
	}

	public void setCantidadEntrega(Integer cantidadEntrega) {
		this.cantidadEntrega = cantidadEntrega;
	}

	public Integer getCorrelativo() {
		return correlativo;
	}

	public void setCorrelativo(Integer correlativo) {
		this.correlativo = correlativo;
	}

	public String getUnidadMedida() {
		return unidadMedida;
	}

	public void setUnidadMedida(String unidadMedida) {
		this.unidadMedida = unidadMedida;
	}

	public double getIncidencia() {
		return incidencia;
	}

	public void setIncidencia(double incidencia) {
		this.incidencia = incidencia;
	}

	public String getMoneda() {
		return moneda;
	}

	public void setMoneda(String moneda) {
		this.moneda = moneda;
	}

	public double getDescuentoUno() {
		return descuentoUno;
	}

	public void setDescuentoUno(double descuentoUno) {
		this.descuentoUno = descuentoUno;
	}

	public double getDescuentoDos() {
		return descuentoDos;
	}

	public void setDescuentoDos(double descuentoDos) {
		this.descuentoDos = descuentoDos;
	}


	public double getTotalDolares() {
		return totalDolares;
	}

	public void setTotalDolares(double totalDolares) {
		this.totalDolares = totalDolares;
	}

	public double getBonificacion() {
		return bonificacion;
	}

	public void setBonificacion(double bonificacion) {
		this.bonificacion = bonificacion;
	}

	public double getPrecioUnitario() {
		return precioUnitario;
	}

	public void setPrecioUnitario(double precioUnitario) {
		this.precioUnitario = precioUnitario;
	}

	public boolean isAplicarPrecio() {
		return aplicarPrecio;
	}

	public void setAplicarPrecio(boolean aplicarPrecio) {
		this.aplicarPrecio = aplicarPrecio;
	}

	public double getMargenUtilidad() {
		return margenUtilidad;
	}

	public void setMargenUtilidad(double margenUtilidad) {
		this.margenUtilidad = margenUtilidad;
	}

	public double getPrecioProveedor() {
		return precioProveedor;
	}

	public void setPrecioProveedor(double precioProveedor) {
		this.precioProveedor = precioProveedor;
	}

	public double getImporte() {
		return importe;
	}

	public void setImporte(double importe) {
		this.importe = importe;
	}
	

}
