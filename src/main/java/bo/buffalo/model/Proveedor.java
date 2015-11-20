package bo.buffalo.model;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Table;

@Entity
@Table(name="proveedor"
,schema="public"
)
public class Proveedor implements Serializable {
	private static final long serialVersionUID = 1L;
	private Integer id;
	private String cargo;
	private String celular;
	private String ciudad;
	private String codigo;
	private String direccion;
	private String email;
	private String estado;
	private String fax;
	private Date fechaRegistro;
	private String nacionalidad;
	private String nit;
	private String nombre;
	private String nombreContacto;
	private String nota;
	private String numeroAutorizacion;
	private String observacion;
	private String pais;
	private String postal;
	private String telefono;
	private String tipoCambio;
	private String usuarioRegistro;
	private Integer utilidadMax;
	private Integer utilidadMin;
	private List<LineasProveedor> listLineasProveedors;
	private List<ProductoProveedor> listProductoProveedors;

	public Proveedor() {
	}


	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	public Integer getId() {
		return this.id;
	}

	public void setId(Integer id) {
		this.id = id;
	}


	public String getCargo() {
		return this.cargo;
	}

	public void setCargo(String cargo) {
		this.cargo = cargo;
	}


	public String getCelular() {
		return this.celular;
	}

	public void setCelular(String celular) {
		this.celular = celular;
	}


	public String getCiudad() {
		return this.ciudad;
	}

	public void setCiudad(String ciudad) {
		this.ciudad = ciudad;
	}


	public String getCodigo() {
		return this.codigo;
	}

	public void setCodigo(String codigo) {
		this.codigo = codigo;
	}


	public String getDireccion() {
		return this.direccion;
	}

	public void setDireccion(String direccion) {
		this.direccion = direccion;
	}


	public String getEmail() {
		return this.email;
	}

	public void setEmail(String email) {
		this.email = email;
	}


	public String getEstado() {
		return this.estado;
	}

	public void setEstado(String estado) {
		this.estado = estado;
	}


	public String getFax() {
		return this.fax;
	}

	public void setFax(String fax) {
		this.fax = fax;
	}


	@Column(name="fecha_registro")
	public Date getFechaRegistro() {
		return this.fechaRegistro;
	}

	public void setFechaRegistro(Date fechaRegistro) {
		this.fechaRegistro = fechaRegistro;
	}


	public String getNacionalidad() {
		return this.nacionalidad;
	}

	public void setNacionalidad(String nacionalidad) {
		this.nacionalidad = nacionalidad;
	}


	public String getNit() {
		return this.nit;
	}

	public void setNit(String nit) {
		this.nit = nit;
	}


	public String getNombre() {
		return this.nombre;
	}

	public void setNombre(String nombre) {
		this.nombre = nombre;
	}


	@Column(name="nombre_contacto")
	public String getNombreContacto() {
		return this.nombreContacto;
	}

	public void setNombreContacto(String nombreContacto) {
		this.nombreContacto = nombreContacto;
	}


	public String getNota() {
		return this.nota;
	}

	public void setNota(String nota) {
		this.nota = nota;
	}


	@Column(name="numero_autorizacion")
	public String getNumeroAutorizacion() {
		return this.numeroAutorizacion;
	}

	public void setNumeroAutorizacion(String numeroAutorizacion) {
		this.numeroAutorizacion = numeroAutorizacion;
	}


	public String getObservacion() {
		return this.observacion;
	}

	public void setObservacion(String observacion) {
		this.observacion = observacion;
	}


	public String getPais() {
		return this.pais;
	}

	public void setPais(String pais) {
		this.pais = pais;
	}


	public String getPostal() {
		return this.postal;
	}

	public void setPostal(String postal) {
		this.postal = postal;
	}


	public String getTelefono() {
		return this.telefono;
	}

	public void setTelefono(String telefono) {
		this.telefono = telefono;
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
	public Integer getUtilidadMax() {
		return this.utilidadMax;
	}

	public void setUtilidadMax(Integer utilidadMax) {
		this.utilidadMax = utilidadMax;
	}


	@Column(name="utilidad_min")
	public Integer getUtilidadMin() {
		return this.utilidadMin;
	}

	public void setUtilidadMin(Integer utilidadMin) {
		this.utilidadMin = utilidadMin;
	}


	//bi-directional many-to-one association to LineasProveedor
	@OneToMany(mappedBy="proveedor")
	public List<LineasProveedor> getListLineasProveedors() {
		return this.listLineasProveedors;
	}

	public void setListLineasProveedors(List<LineasProveedor> listLineasProveedors) {
		this.listLineasProveedors = listLineasProveedors;
	}

	public LineasProveedor addListLineasProveedor(LineasProveedor listLineasProveedor) {
		getListLineasProveedors().add(listLineasProveedor);
		listLineasProveedor.setProveedor(this);

		return listLineasProveedor;
	}

	public LineasProveedor removeListLineasProveedor(LineasProveedor listLineasProveedor) {
		getListLineasProveedors().remove(listLineasProveedor);
		listLineasProveedor.setProveedor(null);

		return listLineasProveedor;
	}


	//bi-directional many-to-one association to ProductoProveedor
	@OneToMany(mappedBy="proveedor")
	public List<ProductoProveedor> getListProductoProveedors() {
		return this.listProductoProveedors;
	}

	public void setListProductoProveedors(List<ProductoProveedor> listProductoProveedors) {
		this.listProductoProveedors = listProductoProveedors;
	}

	public ProductoProveedor addListProductoProveedor(ProductoProveedor listProductoProveedor) {
		getListProductoProveedors().add(listProductoProveedor);
		listProductoProveedor.setProveedor(this);

		return listProductoProveedor;
	}

	public ProductoProveedor removeListProductoProveedor(ProductoProveedor listProductoProveedor) {
		getListProductoProveedors().remove(listProductoProveedor);
		listProductoProveedor.setProveedor(null);

		return listProductoProveedor;
	}

}