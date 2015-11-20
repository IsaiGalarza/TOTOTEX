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
@Table(name="cliente"
    ,schema="public"
)
public class Cliente  implements java.io.Serializable {

	private static final long serialVersionUID = -3474766084882479477L;
	private Integer id;
    private String nombreCompleto;
    private String direccion;
    private String numeroDocumento;
    private String correoElectronico;
    private String nit;
    private String tipoCliente;
    private String celular;
    private String telefono;
    private String numeroCuenta;
    private Boolean credito;
    private double porcentajeCredito;
    private Integer descuentos;
    private String estado;
    private Date fechaRegistro;
    private String usuarioRegistro;
    private Set<NitCliente> nitClientes = new HashSet<NitCliente>(0);
     
	private Ciudad ciudad;

    public Cliente() {
    	ciudad= new Ciudad();
    	this.nit="0";
    	this.credito=false;
    	this.porcentajeCredito=0.0;
    }

	
    public Cliente(Integer id, String nombreCompleto) {
        this.id = id;
        this.nombreCompleto = nombreCompleto;
    }
    
    public Cliente(String numeroDocumento, String nombreCompleto) {
        this.numeroDocumento = numeroDocumento;
        this.nombreCompleto = nombreCompleto;
    }
    
    public Cliente(Integer id, String nombreCompleto, String direccion, String numeroDocumento, String celular, String telefono, String numeroCuenta, Integer descuentos, String estado, Date fechaRegistro, String usuarioRegistro, Set<NitCliente> nitClientes) {
       this.id = id;
       this.nombreCompleto = nombreCompleto;
       this.direccion = direccion;
       this.numeroDocumento = numeroDocumento;
       this.celular = celular;
       this.telefono = telefono;
       this.numeroCuenta = numeroCuenta;
       this.descuentos = descuentos;
       this.estado = estado;
       this.fechaRegistro = fechaRegistro;
       this.usuarioRegistro = usuarioRegistro;
       this.nitClientes = nitClientes;
    }
   
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name="id", unique=true, nullable=false, length=20)
    public Integer getId() {
        return this.id;
    }
    
    public void setId(Integer id) {
        this.id = id;
    }

    
    @Column(name="nombre_completo", nullable=false)
    public String getNombreCompleto() {
        return this.nombreCompleto;
    }
    
    public void setNombreCompleto(String nombreCompleto) {
        this.nombreCompleto = nombreCompleto;
    }

    
    @Column(name="direccion", length=2000)
    public String getDireccion() {
        return this.direccion;
    }
    
    public void setDireccion(String direccion) {
        this.direccion = direccion;
    }

    
    @Column(name="numero_documento", length=40)
    public String getNumeroDocumento() {
        return this.numeroDocumento;
    }
    
    public void setNumeroDocumento(String numeroDocumento) {
        this.numeroDocumento = numeroDocumento;
    }

    
    @Column(name="celular", length=20)
    public String getCelular() {
        return this.celular;
    }
    
    public void setCelular(String celular) {
        this.celular = celular;
    }

    
    @Column(name="telefono", length=20)
    public String getTelefono() {
        return this.telefono;
    }
    
    public void setTelefono(String telefono) {
        this.telefono = telefono;
    }

    
    @Column(name="numero_cuenta", length=20)
    public String getNumeroCuenta() {
        return this.numeroCuenta;
    }
    
    public void setNumeroCuenta(String numeroCuenta) {
        this.numeroCuenta = numeroCuenta;
    }
    
    @Column(name="permitir_credito")
    public Boolean getCredito() {
		return this.credito;
	}

	public void setCredito(Boolean credito) {
		this.credito = credito;
	}
    
    @Column(name="descuentos")
    public Integer getDescuentos() {
        return this.descuentos;
    }
    
    public void setDescuentos(Integer descuentos) {
        this.descuentos = descuentos;
    }

    
    @Column(name="estado", length=2)
    public String getEstado() {
        return this.estado;
    }
    
    public void setEstado(String estado) {
        this.estado = estado;
    }

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name="fecha_registro", length=29)
    public Date getFechaRegistro() {
        return this.fechaRegistro;
    }
    
    public void setFechaRegistro(Date fechaRegistro) {
        this.fechaRegistro = fechaRegistro;
    }

    
    @Column(name="usuario_registro", length=30)
    public String getUsuarioRegistro() {
        return this.usuarioRegistro;
    }
    
    public void setUsuarioRegistro(String usuarioRegistro) {
        this.usuarioRegistro = usuarioRegistro;
    }

    @OneToMany(fetch=FetchType.LAZY, mappedBy="cliente")
    public Set<NitCliente> getNitClientes() {
        return this.nitClientes;
    }
    
    public void setNitClientes(Set<NitCliente> nitClientes) {
        this.nitClientes = nitClientes;
    }

    @Column(name="correo_electronico", length=200)
	public String getCorreoElectronico() {
		return correoElectronico;
	}


	public void setCorreoElectronico(String correoElectronico) {
		this.correoElectronico = correoElectronico;
	}

	@Column(name="nit", length=20)
	public String getNit() {
		return nit;
	}


	public void setNit(String nit) {
		this.nit = nit;
	}

	@Column(name="tipo_cliente", length=60)
	public String getTipoCliente() {
		return tipoCliente;
	}


	public void setTipoCliente(String tipoCliente) {
		this.tipoCliente = tipoCliente;
	}

	// bi-directional many-to-one association to Usuario
		@ManyToOne
		@JoinColumn(name = "id_ciudad")
	public Ciudad getCiudad() {
		return ciudad;
	}


	public void setCiudad(Ciudad ciudad) {
		this.ciudad = ciudad;
	}

	@Column(name="porcentaje_credito", nullable=false, precision=8, scale=8)
	public double getPorcentajeCredito() {
		return porcentajeCredito;
	}


	public void setPorcentajeCredito(double porcentajeCredito) {
		this.porcentajeCredito = porcentajeCredito;
	}




}
