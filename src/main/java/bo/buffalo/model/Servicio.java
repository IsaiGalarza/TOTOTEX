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
@Table(name="servicio"
    ,schema="public"
)
public class Servicio  implements java.io.Serializable {

	 private static final long serialVersionUID = -3260944324398135412L;
	 
	 private Integer id;
     private String nombreServicio;
     private double precioUnitarioVenta;
     private String estado;
     private Date fechaRegistro;
     private String usuarioRegistro;

    public Servicio() {
    }
    
    public Servicio(Integer id, String nombreServicio, float precioUnitarioVenta) {
        this.id = id;
        this.nombreServicio = nombreServicio;
        this.precioUnitarioVenta = precioUnitarioVenta;
    }
	
    public Servicio(Integer id, String nombreServicio, double precioUnitarioVenta, String estado, Date fechaRegistro, String usuarioRegistro) {
    	this.id = id;
        this.nombreServicio = nombreServicio;
        this.precioUnitarioVenta = precioUnitarioVenta;
        this.estado = estado;
        this.fechaRegistro = fechaRegistro;
        this.usuarioRegistro = usuarioRegistro;
    }
    
    @Id 
    @GeneratedValue(strategy=GenerationType.IDENTITY)
    @Column(name="id", unique=true, nullable=false)
	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}
	
	@Column(name="nombre_servicio", nullable=false, length=60)
	public String getNombreServicio() {
		return nombreServicio;
	}

	public void setNombreServicio(String nombreServicio) {
		this.nombreServicio = nombreServicio;
	}
	
	@Column(name="precio_unitario_venta", nullable=false)
	public double getPrecioUnitarioVenta() {
		return precioUnitarioVenta;
	}

	public void setPrecioUnitarioVenta(double precioUnitarioVenta) {
		this.precioUnitarioVenta = precioUnitarioVenta;
	}
	
	@Column(name="estado", length=2)
	public String getEstado() {
		return estado;
	}

	public void setEstado(String estado) {
		this.estado = estado;
	}

	@Temporal(TemporalType.TIMESTAMP)
    @Column(name="fecha_registro", length=29)
	public Date getFechaRegistro() {
		return fechaRegistro;
	}

	public void setFechaRegistro(Date fechaRegistro) {
		this.fechaRegistro = fechaRegistro;
	}

	@Column(name="usuario_registro", length=30)
	public String getUsuarioRegistro() {
		return usuarioRegistro;
	}

	public void setUsuarioRegistro(String usuarioRegistro) {
		this.usuarioRegistro = usuarioRegistro;
	}
    
}