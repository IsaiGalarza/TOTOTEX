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
@Table(name="detalle_factura"
    ,schema="public"
)
public class DetalleFactura  implements java.io.Serializable {

	private static final long serialVersionUID = -1533941158619049969L;
	private Integer id;
     private Factura factura;
     private String codigoReceta;
     private String codigoProducto;
     private String concepto;
     private double cantidad;
     private double precioUnitario;
     private double precioTotal;
     private double descuentos;
     private String estado;
     private Date fechaRegistro;
     private String usuarioRegistro;
     private String origen;
     
    public DetalleFactura() {
    }

	
    public DetalleFactura(Integer id, Factura factura, String concepto, double cantidad, double precioUnitario, double precioTotal, String origen) {
        this.id = id;
        this.factura = factura;
        this.concepto = concepto;
        this.cantidad = cantidad;
        this.precioUnitario = precioUnitario;
        this.precioTotal = precioTotal;
        this.origen = origen;
    }
    public DetalleFactura(Integer id, Factura factura, String codigoReceta, String codigoProducto, String concepto, double cantidad, double precioUnitario, double precioTotal, double descuentos, String estado, Date fechaRegistro, String usuarioRegistro, String origen) {
       this.id = id;
       this.factura = factura;
       this.codigoReceta = codigoReceta;
       this.codigoProducto = codigoProducto;
       this.concepto = concepto;
       this.cantidad = cantidad;
       this.precioUnitario = precioUnitario;
       this.precioTotal = precioTotal;
       this.descuentos = descuentos;
       this.estado = estado;
       this.fechaRegistro = fechaRegistro;
       this.usuarioRegistro = usuarioRegistro;
       this.origen = origen;
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

    //@ManyToOne(fetch=FetchType.LAZY)
    @ManyToOne(fetch=FetchType.EAGER, optional=false)
    @JoinColumn(name="id_factura", nullable=false)
    public Factura getFactura() {
        return this.factura;
    }
    
    public void setFactura(Factura factura) {
        this.factura = factura;
    }

    
    @Column(name="codigo_receta", length=20)
    public String getCodigoReceta() {
        return this.codigoReceta;
    }
    
    public void setCodigoReceta(String codigoReceta) {
        this.codigoReceta = codigoReceta;
    }

    
    @Column(name="codigo_producto", length=20)
    public String getCodigoProducto() {
        return this.codigoProducto;
    }
    
    public void setCodigoProducto(String codigoProducto) {
        this.codigoProducto = codigoProducto;
    }

    
    @Column(name="concepto", nullable=false, length=200)
    public String getConcepto() {
        return this.concepto;
    }
    
    public void setConcepto(String concepto) {
        this.concepto = concepto;
    }

    
    @Column(name="cantidad", nullable=false)
    public double getCantidad() {
        return this.cantidad;
    }
    
    public void setCantidad(double cantidad) {
        this.cantidad = cantidad;
    }

    
    @Column(name="precio_unitario", nullable=false, precision=8, scale=8)
    public double getPrecioUnitario() {
    	precioUnitario = round(precioUnitario, 2);
        return this.precioUnitario;
    }
    
    public void setPrecioUnitario(double precioUnitario) {
        this.precioUnitario = precioUnitario;
    }
    
    public double round(double value, int places) {
	    if (places < 0) throw new IllegalArgumentException();

	    long factor = (long) Math.pow(10, places);
	    value = value * factor;
	    long tmp = Math.round(value);
	    return (double) tmp / factor;
	}

    
    @Column(name="precio_total", nullable=false, precision=8, scale=8)
    public double getPrecioTotal() {
    	precioTotal = round(precioTotal, 2);
        return this.precioTotal;
    }
    
    public void setPrecioTotal(double precioTotal) {
        this.precioTotal = precioTotal;
    }

    
    @Column(name="descuentos", precision=8, scale=8)
    public double getDescuentos() {
        return this.descuentos;
    }
    
    public void setDescuentos(double descuentos) {
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

    @Column(name="origen", nullable=false, length=60)
	public String getOrigen() {
		return origen;
	}


	public void setOrigen(String origen) {
		this.origen = origen;
	}

}
