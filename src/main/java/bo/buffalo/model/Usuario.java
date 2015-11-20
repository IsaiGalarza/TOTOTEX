package bo.buffalo.model;

import java.util.Date;
import java.util.HashSet;
import java.util.Set;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

@Entity
@Table(name = "usuario", schema = "public")
public class Usuario implements java.io.Serializable {

	private static final long serialVersionUID = 5470398739437366915L;

	private Integer id;
	private String name; //NOMBRE COMPLETO
	private String direccion;
	private String email;
	private String barrrioUbicacion;
	private String numeroDocumento;
	private String telefono;
	private String celular;
	private String telefonoReferencia;
	private Sucursal sucursal;
	private Almacen almacen;
	private Cargo cargo= new Cargo();
	private boolean impresionSilencionsa;
	private boolean ingresoSistema;
	private String login;
	private String password;
	private byte[] fotoPerfil;
	
	private String estado;
	private Date fechaRegistro;
	private String usuarioRegistro;
	

	private Set<UsuarioRol> usuarioRols = new HashSet<UsuarioRol>(0);

	public Usuario() {
		cargo= new Cargo();
		sucursal= new Sucursal();	
	}

	public Usuario(Integer id, Sucursal sucursal, String email, String login,
			String name, String password, boolean impresionSilencionsa, boolean ingresoSistema) {
		this.id = id;
		this.sucursal = sucursal;
		this.email = email;
		this.login = login;
		this.name = name;
		this.password = password;
		this.impresionSilencionsa = impresionSilencionsa;
		this.ingresoSistema = ingresoSistema;
	}

	public Usuario(Integer id, Sucursal sucursal, String email, String login,
			String name, String password, boolean impresionSilencionsa, boolean ingresoSistema,
			String estado, Date fechaRegistro, String usuarioRegistro,
			Set<UsuarioRol> usuarioRols) {
		this.id = id;
		this.sucursal = sucursal;
		this.email = email;
		this.login = login;
		this.name = name;
		this.password = password;
		this.impresionSilencionsa = impresionSilencionsa;
		this.ingresoSistema = ingresoSistema;
		this.estado = estado;
		this.fechaRegistro = fechaRegistro;
		this.usuarioRegistro = usuarioRegistro;
		this.usuarioRols = usuarioRols;
	}

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "id", unique = true, nullable = false)
	public Integer getId() {
		return this.id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	// @ManyToOne(fetch=FetchType.LAZY)
	@ManyToOne(fetch = FetchType.EAGER, optional = false)
	@JoinColumn(name = "id_sucursal", nullable = true)
	public Sucursal getSucursal() {
		return this.sucursal;
	}

	public void setSucursal(Sucursal sucursal) {
		this.sucursal = sucursal;
	}
	
	@ManyToOne(fetch = FetchType.EAGER, optional = false)
	@JoinColumn(name = "id_almacen", nullable = true)
	public Almacen getAlmacen() {
		return almacen;
	}

	public void setAlmacen(Almacen almacen) {
		this.almacen = almacen;
	}

	@Column(name = "foto_perfil", nullable = true)
	public byte[] getFotoPerfil(){
		return this.fotoPerfil;
	}
	
	public void setFotoPerfil(byte[] fotoPerfil){
		this.fotoPerfil = fotoPerfil;
	}

	@Column(name = "email", unique = true, nullable = false)
	public String getEmail() {
		return this.email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	@Column(name = "login", nullable = true, length = 25)
	public String getLogin() {
		return this.login;
	}

	public void setLogin(String login) {
		this.login = login;
	}

	@Column(name = "name", nullable = false, length = 50)
	public String getName() {
		return this.name;
	}

	public void setName(String name) {
		this.name = name;
	}

	@Column(name = "password", nullable = true, length = 14)
	public String getPassword() {
		return this.password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	@Column(name = "impresion_silenciosa", nullable = false)
	public boolean isImpresionSilencionsa() {
		return impresionSilencionsa;
	}

	public void setImpresionSilencionsa(boolean impresionSilencionsa) {
		this.impresionSilencionsa = impresionSilencionsa;
	}

	@Column(name = "estado", length = 2)
	public String getEstado() {
		return estado;
	}

	public void setEstado(String estado) {
		this.estado = estado;
	}

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "fecha_registro", length = 29)
	public Date getFechaRegistro() {
		return fechaRegistro;
	}

	public void setFechaRegistro(Date fechaRegistro) {
		this.fechaRegistro = fechaRegistro;
	}

	@Column(name = "usuario_registro", length = 30)
	public String getUsuarioRegistro() {
		return usuarioRegistro;
	}

	public void setUsuarioRegistro(String usuarioRegistro) {
		this.usuarioRegistro = usuarioRegistro;
	}

	@OneToMany(fetch = FetchType.LAZY, mappedBy = "usuario")
	public Set<UsuarioRol> getUsuarioRols() {
		return this.usuarioRols;
	}

	public void setUsuarioRols(Set<UsuarioRol> usuarioRols) {
		this.usuarioRols = usuarioRols;
	}
	
	//@ManyToOne(fetch = FetchType.EAGER, optional = false)
	@ManyToOne(fetch=FetchType.EAGER, optional = false)
	@JoinColumn(name = "id_cargo", nullable = true)
	public Cargo getCargo() {
		return cargo;
	}

	public void setCargo(Cargo cargo) {
		this.cargo = cargo;
	}
	
	@Column(name = "ingreso_sistema", nullable = false)
	public boolean isIngresoSistema() {
		return ingresoSistema;
	}

	public void setIngresoSistema(boolean ingresoSistema) {
		this.ingresoSistema = ingresoSistema;
	}
	
	@Column(name = "direccion", length = 100)
	public String getDireccion() {
		return direccion;
	}

	public void setDireccion(String direccion) {
		this.direccion = direccion;
	}
	
	@Column(name = "barrio_ubicacion", length = 100)
	public String getBarrrioUbicacion() {
		return barrrioUbicacion;
	}

	public void setBarrrioUbicacion(String barrrioUbicacion) {
		this.barrrioUbicacion = barrrioUbicacion;
	}
	
	@Column(name = "telefono", length = 20)
	public String getTelefono() {
		return telefono;
	}

	public void setTelefono(String telefono) {
		this.telefono = telefono;
	}
	
	@Column(name = "celular", length = 20)
	public String getCelular() {
		return celular;
	}

	public void setCelular(String celular) {
		this.celular = celular;
	}
	
	@Column(name = "telefono_referencia", length = 20)
	public String getTelefonoReferencia() {
		return telefonoReferencia;
	}

	public void setTelefonoReferencia(String telefonoReferencia) {
		this.telefonoReferencia = telefonoReferencia;
	}
	
	@Column(name = "numero_documento", length = 20)
	public String getNumeroDocumento() {
		return numeroDocumento;
	}

	public void setNumeroDocumento(String numeroDocumento) {
		this.numeroDocumento = numeroDocumento;
	}

}