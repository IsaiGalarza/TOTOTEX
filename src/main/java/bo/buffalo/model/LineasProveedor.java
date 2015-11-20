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

@Entity
@Table(name="lineas_proveedor")
public class LineasProveedor implements Serializable {
	private static final long serialVersionUID = 1L;
	private Integer id;
	private String estado;
	private Date fechaRegistro;
	private String nombre;
	private String usuarioRegistro;
	private Proveedor proveedor;
	private List<ProductoProveedor> listProductoProveedors;

	public LineasProveedor() {
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


	public String getNombre() {
		return this.nombre;
	}

	public void setNombre(String nombre) {
		this.nombre = nombre;
	}


	@Column(name="usuario_registro")
	public String getUsuarioRegistro() {
		return this.usuarioRegistro;
	}

	public void setUsuarioRegistro(String usuarioRegistro) {
		this.usuarioRegistro = usuarioRegistro;
	}


	//bi-directional many-to-one association to Proveedor
	@ManyToOne
	@JoinColumn(name="id_proveedor")
	public Proveedor getProveedor() {
		return this.proveedor;
	}

	public void setProveedor(Proveedor proveedor) {
		this.proveedor = proveedor;
	}


	//bi-directional many-to-one association to ProductoProveedor
	@OneToMany(mappedBy="lineasProveedor")
	public List<ProductoProveedor> getListProductoProveedors() {
		return this.listProductoProveedors;
	}

	public void setListProductoProveedors(List<ProductoProveedor> listProductoProveedors) {
		this.listProductoProveedors = listProductoProveedors;
	}

	public ProductoProveedor addListProductoProveedor(ProductoProveedor listProductoProveedor) {
		getListProductoProveedors().add(listProductoProveedor);
		listProductoProveedor.setLineasProveedor(this);

		return listProductoProveedor;
	}

	public ProductoProveedor removeListProductoProveedor(ProductoProveedor listProductoProveedor) {
		getListProductoProveedors().remove(listProductoProveedor);
		listProductoProveedor.setLineasProveedor(null);

		return listProductoProveedor;
	}

}
