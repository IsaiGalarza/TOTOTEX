package bo.buffalo.model;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQuery;
import javax.persistence.OneToMany;
import javax.persistence.Table;

@Entity
@Table(name = "pedido_mov", schema = "public")
//@NamedQuery(name = "PedidoMov.findAll", query = "SELECT p FROM PedidoMov p")
public class PedidoMov implements Serializable {

	private static final long serialVersionUID = 1301684961853851210L;
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Integer id;
	private String entrega;
	private String estado;

	private String preelaborado = "MATERIA PRIMA";
	private double cantidad = 0;

	@ManyToOne(fetch = FetchType.LAZY, optional = false)
	@JoinColumn(name = "id_producto_pre_elaborado", nullable=true)
	private Producto productoPreElaborado;

	private Integer correlativo;

	@Column(name = "tipo_ingreso")
	private String tipoIngreso = "CONTADO";

	@Column(name = "tipo_documento")
	private String tipoDocumento;

	@Column(name = "numero_documento")
	private String numeroDocumento;

	@ManyToOne(fetch = FetchType.LAZY, optional = false)
	@JoinColumn(name = "id_proveedor", nullable = true)
	private Proveedor proveedor;

	@Column(name = "fecha_registro")
	private Date fechaRegistro;

	private String observacion;

	@Column(name = "tipo_pedido")
	private String tipoPedido;

	@Column(name = "total_entregado")
	private double totalEntregado = 0;

	private double total;
	
	@Column(name = "tipo_cambio")
	private double  tipoCambio;
	
	@Column(name = "total_sus")
	private double totalSus;
	
	@Column(name = "descuento_uno")
	private double descuentoUno;
	
//	@Column(name = "descuento_dos")
//	private double descuentoDos;

	@Column(name = "usuario_registro")
	private String usuarioRegistro;

	@Column(name = "fecha_aprobacion")
	private Date fechaAprobacion;

	// bi-directional many-to-one association to DetallePedidoMov
	@OneToMany(mappedBy = "pedidoMov")
	private List<DetallePedidoMov> detallePedidoMovs;

	// bi-directional many-to-one association to MovimientoAlmacen
	@OneToMany(mappedBy = "pedidoMov")
	private List<MovimientoAlmacen> movimientoAlmacens;

	// bi-directional many-to-one association to Usuario
	@ManyToOne
	@JoinColumn(name = "id_usuario")
	private Usuario usuario;

	@ManyToOne
	@JoinColumn(name = "id_usuario_aprobacion")
	private Usuario usuarioAprobacion;

	@ManyToOne
	@JoinColumn(name = "id_usuario_out")
	private Usuario usuarioOut;

	@ManyToOne
	@JoinColumn(name = "id_alm_out")
	private Almacen AlmOut;

	@ManyToOne
	@JoinColumn(name = "id_alm_In")
	private Almacen AlmIn;

	public PedidoMov() {
		AlmIn = new Almacen();
		AlmOut = new Almacen();
		this.proveedor=new Proveedor();
		this.tipoCambio=7;
		descuentoUno=0;
		//descuentoDos=0;
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

	public String getObservacion() {
		return this.observacion;
	}

	public void setObservacion(String observacion) {
		this.observacion = observacion;
	}

	public String getTipoPedido() {
		return this.tipoPedido;
	}

	public void setTipoPedido(String tipoPedido) {
		this.tipoPedido = tipoPedido;
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

	public List<DetallePedidoMov> getDetallePedidoMovs() {
		return this.detallePedidoMovs;
	}

	public void setDetallePedidoMovs(List<DetallePedidoMov> detallePedidoMovs) {
		this.detallePedidoMovs = detallePedidoMovs;
	}

	public DetallePedidoMov addDetallePedidoMov(
			DetallePedidoMov detallePedidoMov) {
		getDetallePedidoMovs().add(detallePedidoMov);
		detallePedidoMov.setPedidoMov(this);

		return detallePedidoMov;
	}

	public DetallePedidoMov removeDetallePedidoMov(
			DetallePedidoMov detallePedidoMov) {
		getDetallePedidoMovs().remove(detallePedidoMov);
		detallePedidoMov.setPedidoMov(null);

		return detallePedidoMov;
	}

	public List<MovimientoAlmacen> getMovimientoAlmacens() {
		return this.movimientoAlmacens;
	}

	public void setMovimientoAlmacens(List<MovimientoAlmacen> movimientoAlmacens) {
		this.movimientoAlmacens = movimientoAlmacens;
	}

	public MovimientoAlmacen addMovimientoAlmacen(
			MovimientoAlmacen movimientoAlmacen) {
		getMovimientoAlmacens().add(movimientoAlmacen);
		movimientoAlmacen.setPedidoMov(this);

		return movimientoAlmacen;
	}

	public MovimientoAlmacen removeMovimientoAlmacen(
			MovimientoAlmacen movimientoAlmacen) {
		getMovimientoAlmacens().remove(movimientoAlmacen);
		movimientoAlmacen.setPedidoMov(null);

		return movimientoAlmacen;
	}

	public Usuario getUsuario() {
		return this.usuario;
	}

	public void setUsuario(Usuario usuario) {
		this.usuario = usuario;
	}

	public Almacen getAlmIn() {
		return AlmIn;
	}

	public void setAlmIn(Almacen almIn) {
		AlmIn = almIn;
	}

	public Almacen getAlmOut() {
		return AlmOut;
	}

	public void setAlmOut(Almacen almOut) {
		AlmOut = almOut;
	}

	public Usuario getUsuarioAprobacion() {
		return usuarioAprobacion;
	}

	public void setUsuarioAprobacion(Usuario usuarioAprobacion) {
		this.usuarioAprobacion = usuarioAprobacion;
	}

	public Date getFechaAprobacion() {
		return fechaAprobacion;
	}

	public void setFechaAprobacion(Date fechaAprobacion) {
		this.fechaAprobacion = fechaAprobacion;
	}

	public Usuario getUsuarioOut() {
		return usuarioOut;
	}

	public void setUsuarioOut(Usuario usuarioOut) {
		this.usuarioOut = usuarioOut;
	}

	public double getTotalEntregado() {
		return totalEntregado;
	}

	public void setTotalEntregado(double totalEntregado) {
		this.totalEntregado = totalEntregado;
	}

	public String getEntrega() {
		return entrega;
	}

	public void setEntrega(String entrega) {
		this.entrega = entrega;
	}

	public Proveedor getProveedor() {
		return proveedor;
	}

	public void setProveedor(Proveedor idProveedor) {
		this.proveedor = idProveedor;
	}

	public String getTipoDocumento() {
		return tipoDocumento;
	}

	public void setTipoDocumento(String tipoDocumento) {
		this.tipoDocumento = tipoDocumento;
	}

	public String getTipoIngreso() {
		return tipoIngreso;
	}

	public void setTipoIngreso(String tipoIngreso) {
		this.tipoIngreso = tipoIngreso;
	}

	public String getNumeroDocumento() {
		return numeroDocumento;
	}

	public void setNumeroDocumento(String numeroDocumento) {
		this.numeroDocumento = numeroDocumento;
	}

	public Integer getCorrelativo() {
		return correlativo;
	}

	public void setCorrelativo(Integer correlativo) {
		this.correlativo = correlativo;
	}

	public String getPreelaborado() {
		return preelaborado;
	}

	public void setPreelaborado(String preelaborado) {
		this.preelaborado = preelaborado;
	}

	public double getCantidad() {
		return cantidad;
	}

	public void setCantidad(double cantidad) {
		this.cantidad = cantidad;
	}

	public Producto getProductoPreElaborado() {
		return productoPreElaborado;
	}

	public void setProductoPreElaborado(Producto productoPreElaborado) {
		this.productoPreElaborado = productoPreElaborado;
	}

	public double getTotalSus() {
		return totalSus;
	}

	public void setTotalSus(double totalSus) {
		this.totalSus = totalSus;
	}

	public double getDescuentoUno() {
		return descuentoUno;
	}

	public void setDescuentoUno(double descuentoUno) {
		this.descuentoUno = descuentoUno;
	}

//	public double getDescuentoDos() {
//		return descuentoDos;
//	}
//
//	public void setDescuentoDos(double descuentoDos) {
//		this.descuentoDos = descuentoDos;
//	}

	public double getTipoCambio() {
		return tipoCambio;
	}

	public void setTipoCambio(double tipoCambio) {
		this.tipoCambio = tipoCambio;
	}
	
	

}
