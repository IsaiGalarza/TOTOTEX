package bo.buffalo.model;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

@Entity
@Table(name="compra"
    ,schema="public"
)
public class Compra  implements java.io.Serializable {

	 /**
	 * 
	 */
	private static final long serialVersionUID = 4260981264042548162L;
	
	private Integer id;
	
	//campos obligatorios
	private int correlativo;
	private Date fechaFactura;
	private String nitProveedor;
	private String razonSocial;
	private String numeroFactura;
	private String numeroDUI;
	private String numeroAutorizacion;
	private double importeTotal;
	
	//ANTIGUA NORMA 
	private double importeICE = 0;
	private double importeExcentos = 0;
	
	//NUEVA NORMA
	private double importeNoSujetoCreditoFiscal;
	private double importeSubTotal;
	private double descuentosBonosRebajas;
	private double importeBaseCreditoFiscal;
	private double creditoFiscal;
	private String codigoControl;
	private String tipoCompra;
	
	//control interno
	private int idSucursal;
	private String mes;
	private String gestion;
	private String estado;
	private Date fechaRegistro;
	private String usuarioRegistro;

	public Compra() {

	}

   
    @Id 
    @GeneratedValue(strategy=GenerationType.IDENTITY)
    @Column(name="id", unique=true, nullable=false)
    public Integer getId() {
        return this.id;
    }
    
    public void setId(Integer id) {
        this.id = id;
    }
    
    @Column(name="correlativo", nullable=false)
    public int getCorrelativo() {
		return correlativo;
	}


	public void setCorrelativo(int correlativo) {
		this.correlativo = correlativo;
	}
	
	@Temporal(TemporalType.DATE)
    @Column(name="fecha_factura", nullable=false)
	public Date getFechaFactura() {
		return fechaFactura;
	}

	public void setFechaFactura(Date fechaFactura) {
		this.fechaFactura = fechaFactura;
	}
	
	@Column(name="nit_proveedor", nullable=false, length=40)
	public String getNitProveedor() {
		return nitProveedor;
	}

	public void setNitProveedor(String nitProveedor) {
		this.nitProveedor = nitProveedor;
	}
    
	@Column(name="razon_social", nullable=false, length=200)
    public String getRazonSocial() {
		return razonSocial;
	}

	public void setRazonSocial(String razonSocial) {
		this.razonSocial = razonSocial;
	}
	
	@Column(name="numero_factura", nullable=false, length=20)
	public String getNumeroFactura() {
		return numeroFactura;
	}

	public void setNumeroFactura(String numeroFactura) {
		this.numeroFactura = numeroFactura;
	}

	@Column(name="numero_dui", nullable=false, length=20)
	public String getNumeroDUI() {
		return numeroDUI;
	}

	public void setNumeroDUI(String numeroDUI) {
		this.numeroDUI = numeroDUI;
	}

	@Column(name="numero_autorizacion", nullable=false, length=40)
	public String getNumeroAutorizacion() {
		return numeroAutorizacion;
	}

	public void setNumeroAutorizacion(String numeroAutorizacion) {
		this.numeroAutorizacion = numeroAutorizacion;
	}

	@Column(name="importeTotal", nullable=false, precision=10, scale=2)
    public double getImporteTotal() {
		return importeTotal;
	}
	public void setImporteTotal(double importeTotal) {
		this.importeTotal = importeTotal;
	}
    
	@Column(name="importe_ice", nullable=false, precision=10, scale=2)
	public double getImporteICE() {
		return importeICE;
	}

	public void setImporteICE(double importeICE) {
		this.importeICE = importeICE;
	}
	
	@Column(name="importe_no_sujeto_credito_fiscal", nullable=false, precision=10, scale=2)
    public double getImporteNoSujetoCreditoFiscal() {
		return importeNoSujetoCreditoFiscal;
	}

	public void setImporteNoSujetoCreditoFiscal(double importeNoSujetoCreditoFiscal) {
		this.importeNoSujetoCreditoFiscal = importeNoSujetoCreditoFiscal;
	}

	@Column(name="importe_sub_total", nullable=false, precision=10, scale=2)
	public double getImporteSubTotal() {
		importeSubTotal = importeTotal - importeNoSujetoCreditoFiscal;
		return importeSubTotal;
	}

	public void setImporteSubTotal(double importeSubTotal) {
		this.importeSubTotal = importeSubTotal;
	}

	@Column(name="descuentos_bonos_rebajas", nullable=false, precision=10, scale=2)
	public double getDescuentosBonosRebajas() {
		return descuentosBonosRebajas;
	}

	public void setDescuentosBonosRebajas(double descuentosBonosRebajas) {
		this.descuentosBonosRebajas = descuentosBonosRebajas;
	}

	@Column(name="importe_base_credito_fiscal", nullable=false, precision=10, scale=2)
	public double getImporteBaseCreditoFiscal() {
//		importeBaseCreditoFiscal = importeSubTotal - descuentosBonosRebajas;
		return importeBaseCreditoFiscal;
	}

	public void setImporteBaseCreditoFiscal(double importeBaseCreditoFiscal) {
		this.importeBaseCreditoFiscal = importeBaseCreditoFiscal;
	}

	@Column(name="credito_fiscal", nullable=false, precision=10, scale=2)
	public double getCreditoFiscal() {
		creditoFiscal = importeBaseCreditoFiscal * 0.13;
		return creditoFiscal;
	}

	public void setCreditoFiscal(double creditoFiscal) {
		this.creditoFiscal = creditoFiscal;
	}

	@Column(name="codigo_control", nullable=false, length=40)
	public String getCodigoControl() {
		return codigoControl;
	}

	public void setCodigoControl(String codigoControl) {
		this.codigoControl = codigoControl;
	}

	@Column(name="tipo_compra", nullable=false, length=40)
	public String getTipoCompra() {
		return tipoCompra;
	}
	public void setTipoCompra(String tipoCompra) {
		this.tipoCompra = tipoCompra;
	}

	@Column(name="id_sucursal", nullable=false)
    public int getIdSucursal() {
        return this.idSucursal;
    }
    
    public void setIdSucursal(int idSucursal) {
        this.idSucursal = idSucursal;
    }

    @Column(name="mes", nullable=false, length=2)
    public String getMes() {
		return mes;
	}

	public void setMes(String mes) {
		this.mes = mes;
	}
	
	@Column(name="gestion", nullable=false, length=4)
	public String getGestion() {
		return gestion;
	}

	public void setGestion(String gestion) {
		this.gestion = gestion;
	}

	@Column(name="estado", nullable=false, length=2)
    public String getEstado() {
        return this.estado;
    }
    
    public void setEstado(String estado) {
        this.estado = estado;
    }

    @Temporal(TemporalType.DATE)
    @Column(name="fecha_registro", nullable=false)
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

    @Column(name="importe_excentos", nullable=false, precision=10, scale=2)
	public double getImporteExcentos() {
		return importeExcentos;
	}

	public void setImporteExcentos(double importeExcentos) {
		this.importeExcentos = importeExcentos;
	}

}
