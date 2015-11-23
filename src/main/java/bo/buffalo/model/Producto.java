package bo.buffalo.model;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;

@Entity
@Table(name = "producto", schema = "public", uniqueConstraints = @UniqueConstraint(columnNames = "codigo_producto"))
public class Producto implements Serializable {
	private static final long serialVersionUID = 1L;
	@Id @GeneratedValue(strategy = GenerationType.IDENTITY)
	private Integer id;
	@Column(name = "codigo_barra")
	private String codigoBarra;
	@Column(name = "codigo_linea")
	private String codigoLinea;
	@Column(name = "codigo_producto")
	private String codigoProducto;
	private Integer correlativo;
	private String estado;
	@Column(name = "fecha_registro")
	private Date fechaRegistro;
	@Column(name = "fecha_vencimiento")
	private Date fechaVencimiento;
	@Column(name = "nombre_generico")
	private String nombreGenerico;
	@Column(name = "nombre_producto")
	private String nombreProducto;
	private String observacion;
	private double precio;
	@Column(name = "usuario_registro")
	private String usuarioRegistro;
	
	@ManyToOne
	@JoinColumn(name="id_tipo_producto",nullable= true)
	private TipoProducto tipoProducto;

	@OneToMany(mappedBy = "producto")
	private List<ProductoProveedor> listProductoProveedors;
	
	@ManyToOne
	@JoinColumn(name="id_fabricante", nullable = true)
	private Fabricante fabricante;
	
	@ManyToOne
	@JoinColumn(name="id_grupo_producto", nullable = true)
	private GrupoProducto grupoProducto;
	
	public Producto() {
	}

	
	public Integer getId() {
		return this.id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	
	public String getCodigoBarra() {
		return this.codigoBarra;
	}

	public void setCodigoBarra(String codigoBarra) {
		this.codigoBarra = codigoBarra;
	}

	
	public String getCodigoLinea() {
		return this.codigoLinea;
	}

	public void setCodigoLinea(String codigoLinea) {
		this.codigoLinea = codigoLinea;
	}

	
	public String getCodigoProducto() {
		return this.codigoProducto;
	}

	public void setCodigoProducto(String codigoProducto) {
		this.codigoProducto = codigoProducto;
	}

	public Integer getCorrelativo() {
		return this.correlativo;
	}

	public void setCorrelativo(Integer correlativo) {
		this.correlativo = correlativo;
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

	
	public Date getFechaVencimiento() {
		return this.fechaVencimiento;
	}

	public void setFechaVencimiento(Date fechaVencimiento) {
		this.fechaVencimiento = fechaVencimiento;
	}

	
	public String getNombreGenerico() {
		return this.nombreGenerico;
	}

	public void setNombreGenerico(String nombreGenerico) {
		this.nombreGenerico = nombreGenerico;
	}

	
	public String getNombreProducto() {
		return this.nombreProducto;
	}

	public void setNombreProducto(String nombreProducto) {
		this.nombreProducto = nombreProducto;
	}

	public String getObservacion() {
		return this.observacion;
	}

	public void setObservacion(String observacion) {
		this.observacion = observacion;
	}

	public double getPrecio() {
		return this.precio;
	}

	public void setPrecio(double precio) {
		this.precio = precio;
	}

	
	public String getUsuarioRegistro() {
		return this.usuarioRegistro;
	}

	public void setUsuarioRegistro(String usuarioRegistro) {
		this.usuarioRegistro = usuarioRegistro;
	}



	public TipoProducto getTipoProducto() {
		return tipoProducto;
	}

	public void setTipoProducto(TipoProducto tipoProducto) {
		this.tipoProducto = tipoProducto;
	}

	public List<ProductoProveedor> getListProductoProveedors() {
		return this.listProductoProveedors;
	}

	public void setListProductoProveedors(
			List<ProductoProveedor> listProductoProveedors) {
		this.listProductoProveedors = listProductoProveedors;
	}

	public ProductoProveedor addListProductoProveedor(
			ProductoProveedor listProductoProveedor) {
		getListProductoProveedors().add(listProductoProveedor);
		listProductoProveedor.setProducto(this);

		return listProductoProveedor;
	}

	public ProductoProveedor removeListProductoProveedor(
			ProductoProveedor listProductoProveedor) {
		getListProductoProveedors().remove(listProductoProveedor);
		listProductoProveedor.setProducto(null);

		return listProductoProveedor;
	}


	@Override
	public String toString() {
		return "Producto [id=" + id + ", codigoBarra=" + codigoBarra
				+ ", codigoLinea=" + codigoLinea + ", codigoProducto="
				+ codigoProducto + ", correlativo=" + correlativo + ", estado="
				+ estado + ", fechaRegistro=" + fechaRegistro
				+ ", fechaVencimiento=" + fechaVencimiento
				+ ", nombreGenerico=" + nombreGenerico + ", nombreProducto="
				+ nombreProducto + ", observacion=" + observacion + ", precio="
				+ precio + ", usuarioRegistro=" + usuarioRegistro
				+ ", tipoProducto=" + tipoProducto + "]";
	}

	
	public Fabricante getFabricante() {
		return fabricante;
	}


	public void setFabricante(Fabricante fabricante) {
		this.fabricante = fabricante;
	}


	public GrupoProducto getGrupoProducto() {
		return grupoProducto;
	}


	public void setGrupoProducto(GrupoProducto grupoProducto) {
		this.grupoProducto = grupoProducto;
	}

	
}