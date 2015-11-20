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
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

@Entity
@Table(name="factura"
    ,schema="public"
)
public class Factura  implements java.io.Serializable {

	 private static final long serialVersionUID = 1696071147441082922L;
	 
	 private Integer id;
     private String numeroFactura;
     private String nitCi;
     private String nombreFactura;
     private String concepto;
     private Date fechaFactura;
     private String codigoControl;
     private String numeroAutorizacion;
     private String tipoPago;
     private String comprobanteDebito;
     private double totalPagoDebito;
     private double totalEfectivo;
     private double totalEfectivoDolares;
     private double tipoCambio;
     private double totalPagar;
     private double cambio;
     private double totalFacturado;
     
   //campos para libro ventas
     private double importeICE;
     private double importeExportaciones;
     private double importeVentasGrabadasTasaCero;
     private double importeSubTotal;
     private double importeDescuentosBonificaciones;
     private double importeBaseDebitoFiscal;
     private double debitoFiscal;
     private String mes;
 	 private String gestion;
     
     private String totalLiteral;
     private Date fechaLimiteEmision;
     private int idSucursal;
     private int idUsuario;
     private String estado;
     private Date fechaRegistro;
     private String usuarioRegistro;
     private String codigoRespuestaRapida;
     
     
     private Set<DetalleFactura> detalleFacturas = new HashSet<DetalleFactura>(0);

    public Factura() {
    	
    }

	
    public Factura(Integer id, String numeroFactura, String nitCi, String nombreFactura, String concepto, String codigoControl, 
    		String numeroAutorizacion, double totalEfectivo, double totalPagar, double cambio, 
    		double totalFacturado, String totalLiteral, int idSucursal, int idUsuario, String codigoRespuestaRapida) {
        this.id = id;
        this.numeroFactura = numeroFactura;
        this.nitCi = nitCi;
        this.nombreFactura = nombreFactura;
        this.concepto = concepto;
        this.codigoControl = codigoControl;
        this.numeroAutorizacion = numeroAutorizacion;
        this.totalEfectivo = totalEfectivo;
        this.totalPagar = totalPagar;
        this.cambio = cambio;
        this.totalFacturado = totalFacturado;
        this.totalLiteral = totalLiteral;
        this.idSucursal = idSucursal;
        this.idUsuario = idUsuario;
        this.codigoRespuestaRapida = codigoRespuestaRapida;
    }
    
    public Factura(Integer id, String numeroFactura, String nitCi, String nombreFactura, String concepto, Date fechaFactura, 
    		String codigoControl, String numeroAutorizacion, String tipoPago, String comprobanteDebito, 
    		double totalPagoDebito, double totalEfectivo, double totalPagar, 
    		double cambio, double totalFacturado, String totalLiteral, Date fechaLimiteEmision, int idSucursal, 
    		int idUsuario, String estado, Date fechaRegistro, String usuarioRegistro, String codigoRespuestaRapida, Set<DetalleFactura> detalleFacturas) {
       this.id = id;
       this.numeroFactura = numeroFactura;
       this.nitCi = nitCi;
       this.nombreFactura = nombreFactura;
       this.concepto = concepto;
       this.fechaFactura = fechaFactura;
       this.codigoControl = codigoControl;
       this.numeroAutorizacion = numeroAutorizacion;
       this.tipoPago = tipoPago;
       this.comprobanteDebito = comprobanteDebito;
       this.totalPagoDebito = totalPagoDebito;
       this.totalEfectivo = totalEfectivo;
       this.totalPagar = totalPagar;
       this.cambio = cambio;
       this.totalFacturado = totalFacturado;
       this.totalLiteral = totalLiteral;
       this.fechaLimiteEmision = fechaLimiteEmision;
       this.idSucursal = idSucursal;
       this.idUsuario = idUsuario;
       this.estado = estado;
       this.fechaRegistro = fechaRegistro;
       this.usuarioRegistro = usuarioRegistro;
       this.codigoRespuestaRapida = codigoRespuestaRapida;
       this.detalleFacturas = detalleFacturas;
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

    
    @Column(name="numero_factura", nullable=false, length=20)
    public String getNumeroFactura() {
        return this.numeroFactura;
    }
    
    public void setNumeroFactura(String numeroFactura) {
        this.numeroFactura = numeroFactura;
    }

    
    @Column(name="nit_ci", nullable=false, length=20)
    public String getNitCi() {
        return this.nitCi;
    }
    
    public void setNitCi(String nitCi) {
        this.nitCi = nitCi;
    }

    
    @Column(name="nombre_factura", nullable=false, length=255)
    public String getNombreFactura() {
        return this.nombreFactura;
    }
    
    public void setNombreFactura(String nombreFactura) {
        this.nombreFactura = nombreFactura;
    }

    
    @Column(name="concepto", length=2000)
    public String getConcepto() {
        return this.concepto;
    }
    
    public void setConcepto(String concepto) {
        this.concepto = concepto;
    }

    @Temporal(TemporalType.DATE)
    @Column(name="fecha_factura", length=29)
    public Date getFechaFactura() {
        return this.fechaFactura;
    }
    
    public void setFechaFactura(Date fechaFactura) {
        this.fechaFactura = fechaFactura;
    }

    
    @Column(name="codigo_control", nullable=false, length=20)
    public String getCodigoControl() {
        return this.codigoControl;
    }
    
    public void setCodigoControl(String codigoControl) {
        this.codigoControl = codigoControl;
    }

    
    @Column(name="numero_autorizacion", nullable=false, length=20)
    public String getNumeroAutorizacion() {
        return this.numeroAutorizacion;
    }
    
    public void setNumeroAutorizacion(String numeroAutorizacion) {
        this.numeroAutorizacion = numeroAutorizacion;
    }
    
    @Column(name="total_efectivo", nullable=false, precision=8, scale=8)
    public double getTotalEfectivo() {
        return this.totalEfectivo;
    }
    
    public void setTotalEfectivo(double totalEfectivo) {
        this.totalEfectivo = totalEfectivo;
    }

    
    @Column(name="total_pagar", nullable=false, precision=8, scale=8)
    public double getTotalPagar() {
        return this.totalPagar;
    }
    
    public void setTotalPagar(double totalPagar) {
        this.totalPagar = totalPagar;
    }

    
    @Column(name="cambio", nullable=false, precision=8, scale=8)
    public double getCambio() {
        return this.cambio;
    }
    
    public void setCambio(double cambio) {
        this.cambio = cambio;
    }

    
    @Column(name="total_facturado", nullable=false, precision=8, scale=8)
    public double getTotalFacturado() {
        return this.totalFacturado;
    }
    
    public void setTotalFacturado(double totalFacturado) {
        this.totalFacturado = totalFacturado;
    }

    
    @Column(name="total_literal", nullable=false, length=2000)
    public String getTotalLiteral() {
        return this.totalLiteral;
    }
    
    public void setTotalLiteral(String totalLiteral) {
        this.totalLiteral = totalLiteral;
    }

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name="fecha_limite_emision", length=29)
    public Date getFechaLimiteEmision() {
        return this.fechaLimiteEmision;
    }
    
    public void setFechaLimiteEmision(Date fechaLimiteEmision) {
        this.fechaLimiteEmision = fechaLimiteEmision;
    }

    
    @Column(name="id_sucursal", nullable=false)
    public int getIdSucursal() {
        return this.idSucursal;
    }
    
    public void setIdSucursal(int idSucursal) {
        this.idSucursal = idSucursal;
    }

    
    @Column(name="id_usuario", nullable=false)
    public int getIdUsuario() {
        return this.idUsuario;
    }
    
    public void setIdUsuario(int idUsuario) {
        this.idUsuario = idUsuario;
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

    @OneToMany(fetch=FetchType.LAZY, mappedBy="factura")
    public Set<DetalleFactura> getDetalleFacturas() {
        return this.detalleFacturas;
    }
    
    public void setDetalleFacturas(Set<DetalleFactura> detalleFacturas) {
        this.detalleFacturas = detalleFacturas;
    }

    @Column(name="tipo_pago", length=30)
	public String getTipoPago() {
		return tipoPago;
	}


	public void setTipoPago(String tipoPago) {
		this.tipoPago = tipoPago;
	}

	@Column(name="comprobante_debito", length=30)
	public String getComprobanteDebito() {
		return comprobanteDebito;
	}

	public void setComprobanteDebito(String comprobanteDebito) {
		this.comprobanteDebito = comprobanteDebito;
	}

	@Column(name="total_pago_debito", precision=8, scale=8)
	public double getTotalPagoDebito() {
		return totalPagoDebito;
	}

	public void setTotalPagoDebito(double totalPagoDebito) {
		this.totalPagoDebito = totalPagoDebito;
	}

	@Column(name="codigo_respuesta_rapida", nullable=false, length=4000)
	public String getCodigoRespuestaRapida() {
		return codigoRespuestaRapida;
	}


	public void setCodigoRespuestaRapida(String codigoRespuestaRapida) {
		this.codigoRespuestaRapida = codigoRespuestaRapida;
	}

	@Column(name="total_efectivo_dolares", nullable=false, precision=8, scale=8)
	public double getTotalEfectivoDolares() {
		return totalEfectivoDolares;
	}


	public void setTotalEfectivoDolares(double totalEfectivoDolares) {
		this.totalEfectivoDolares = totalEfectivoDolares;
	}

	@Column(name="tipo_cambio", nullable=false, precision=8, scale=8)
	public double getTipoCambio() {
		return tipoCambio;
	}


	public void setTipoCambio(double tipoCambio) {
		this.tipoCambio = tipoCambio;
	}
	
	@Column(name="importe_ice", precision=10, scale=2)
	public double getImporteICE() {
		return importeICE;
	}


	public void setImporteICE(double importeICE) {
		this.importeICE = importeICE;
	}

	@Column(name="importe_exportaciones", precision=10, scale=2)
	public double getImporteExportaciones() {
		return importeExportaciones;
	}


	public void setImporteExportaciones(double importeExportaciones) {
		this.importeExportaciones = importeExportaciones;
	}

	@Column(name="importe_ventas_grabadas_tasa_cero", precision=10, scale=2)
	public double getImporteVentasGrabadasTasaCero() {
		return importeVentasGrabadasTasaCero;
	}


	public void setImporteVentasGrabadasTasaCero(
			double importeVentasGrabadasTasaCero) {
		this.importeVentasGrabadasTasaCero = importeVentasGrabadasTasaCero;
	}

	@Column(name="importe_sub_total", precision=10, scale=2)
	public double getImporteSubTotal() {
		return importeSubTotal;
	}


	public void setImporteSubTotal(double importeSubTotal) {
		this.importeSubTotal = importeSubTotal;
	}

	@Column(name="importe_descuentos_bonificaciones", precision=10, scale=2)
	public double getImporteDescuentosBonificaciones() {
		return importeDescuentosBonificaciones;
	}


	public void setImporteDescuentosBonificaciones(
			double importeDescuentosBonificaciones) {
		this.importeDescuentosBonificaciones = importeDescuentosBonificaciones;
	}

	@Column(name="importe_base_debito_fiscal", precision=10, scale=2)
	public double getImporteBaseDebitoFiscal() {
		return importeBaseDebitoFiscal;
	}


	public void setImporteBaseDebitoFiscal(double importeBaseDebitoFiscal) {
		this.importeBaseDebitoFiscal = importeBaseDebitoFiscal;
	}

	@Column(name="debito_fiscal", precision=10, scale=2)
	public double getDebitoFiscal() {
		return debitoFiscal;
	}


	public void setDebitoFiscal(double debitoFiscal) {
		this.debitoFiscal = debitoFiscal;
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

}
