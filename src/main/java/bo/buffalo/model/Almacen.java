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
@Table(name="almacen" ,schema="public")
//@NamedQuery(name="Almacen.findAll", query="SELECT a FROM Almacen a")
public class Almacen implements Serializable {

	private static final long serialVersionUID = 347357319180148125L;

	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	private Integer id;

	private String direccion;

	private String estado;

	@Column(name="fecha_registro")
	private Date fechaRegistro;

	private String nombre;

	@Column(name="precio_total")
	private double precioTotal;

	private String telefono;

	private String tipoAlmacen;
	
	@Column(name="usuario_registro")
	private String usuarioRegistro;


	

	@ManyToOne
	@JoinColumn(name="id_encargado", nullable= true)
	private Usuario encargado;

	@ManyToOne
	@JoinColumn(name="id_usuario",nullable= true)
	private Usuario usuario;


	public Almacen() {
		this.id = 0;
		usuario=new Usuario();
		encargado= new Usuario();
	}

	public Integer getId() {
		return this.id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public String getDireccion() {
		return this.direccion;
	}

	public void setDireccion(String direccion) {
		this.direccion = direccion;
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

	public String getNombre() {
		return this.nombre;
	}

	public void setNombre(String nombre) {
		this.nombre = nombre;
	}

	public double getPrecioTotal() {
		return this.precioTotal;
	}

	public void setPrecioTotal(double precioTotal) {
		this.precioTotal = precioTotal;
	}

	public String getTelefono() {
		return this.telefono;
	}

	public void setTelefono(String telefono) {
		this.telefono = telefono;
	}

	public String getUsuarioRegistro() {
		return this.usuarioRegistro;
	}

	public void setUsuarioRegistro(String usuarioRegistro) {
		this.usuarioRegistro = usuarioRegistro;
	}



	public Usuario getUsuario() {
		return this.usuario;
	}

	public void setUsuario(Usuario usuario) {
		this.usuario = usuario;
	}

	
	@Column(name="tipo_almacen")
	public String getTipoAlmacen() {
		return tipoAlmacen;
	}

	public void setTipoAlmacen(String tipoAlmacen) {
		this.tipoAlmacen = tipoAlmacen;
	}

	public Usuario getEncargado() {
		return encargado;
	}

	public void setEncargado(Usuario encargado) {
		this.encargado = encargado;
	}

	@Override
	public String toString() {
		return "Almacen [id=" + id + ", direccion=" + direccion + ", estado="
				+ estado + ", fechaRegistro=" + fechaRegistro + ", nombre="
				+ nombre + ", precioTotal=" + precioTotal + ", telefono="
				+ telefono + ", tipoAlmacen=" + tipoAlmacen
				+ ", usuarioRegistro=" + usuarioRegistro + ", encargado="
				+ encargado + ", usuario=" + usuario + "]";
	}
	
	
	

}