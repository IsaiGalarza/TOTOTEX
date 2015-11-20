package bo.buffalo.model;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

@Entity
@Table(name="nota_venta"
    ,schema="public"
)
public class NotaVenta  implements java.io.Serializable {

	private static final long serialVersionUID = 6855035430254364956L;
	 
	private Integer id;
    private Cliente cliente;
    private Factura factura;
    
    //validos
    private double tipoCambio;
    private Date fechaVenta;
    private double totalDescuento;
     
    private double totalVenta;
    private double totalPagado;
     
    private double totalSaldo;
    private double totalCambio;
     
    private double totalNotaVenta;
    private String totalLiteral;
     
    private String observacion;
    private String piePagina;
     
    private double porcentajeComision;
     
    private String estado;
    private Date fechaRegistro;
    private String usuarioRegistro;
     
    public NotaVenta() {
    	 tipoCambio = 0;
    	 estado = "AC";
    	 fechaVenta = new Date();
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
	
	@ManyToOne
	@JoinColumn(name="id_factura", nullable=true)
	public Factura getFactura() {
		return factura;
	}

	public void setFactura(Factura factura) {
		this.factura = factura;
	}
	
	@Temporal(TemporalType.DATE)
	@Column(name="fecha_venta", length=29)
	public Date getFechaVenta() {
		return fechaVenta;
	}

	public void setFechaVenta(Date fechaVenta) {
		this.fechaVenta = fechaVenta;
	}

	@Column(name="tipo_cambio", nullable=false, precision=8, scale=8)
	public double getTipoCambio() {
		return tipoCambio;
	}

	public void setTipoCambio(double tipoCambio) {
		this.tipoCambio = tipoCambio;
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

    public double getTotalDescuento() {
		return totalDescuento;
	}

	public void setTotalDescuento(double totalDescuento) {
		this.totalDescuento = totalDescuento;
	}

	public double getTotalVenta() {
		return totalVenta;
	}

	public void setTotalVenta(double totalVenta) {
		this.totalVenta = totalVenta;
	}

	public double getTotalPagado() {
		return totalPagado;
	}

	public void setTotalPagado(double totalPagado) {
		this.totalPagado = totalPagado;
	}

	public double getTotalSaldo() {
		return totalSaldo;
	}

	public void setTotalSaldo(double totalSaldo) {
		this.totalSaldo = totalSaldo;
	}

	public double getTotalCambio() {
		return totalCambio;
	}

	public void setTotalCambio(double totalCambio) {
		this.totalCambio = totalCambio;
	}

	public double getTotalNotaVenta() {
		return totalNotaVenta;
	}

	public void setTotalNotaVenta(double totalNotaVenta) {
		this.totalNotaVenta = totalNotaVenta;
	}

	public String getTotalLiteral() {
		return totalLiteral;
	}

	public void setTotalLiteral(String totalLiteral) {
		this.totalLiteral = totalLiteral;
	}

	public String getObservacion() {
		return observacion;
	}

	public void setObservacion(String observacion) {
		this.observacion = observacion;
	}

	public String getPiePagina() {
		return piePagina;
	}

	public void setPiePagina(String piePagina) {
		this.piePagina = piePagina;
	}

	public double getPorcentajeComision() {
		return porcentajeComision;
	}

	public void setPorcentajeComision(double porcentajeComision) {
		this.porcentajeComision = porcentajeComision;
	}

	@ManyToOne
	@JoinColumn(name="id_cliente", nullable=true)
	public Cliente getCliente() {
		return this.cliente;
	}

	public void setCliente(Cliente cliente) {
		this.cliente = cliente;
	}

}
