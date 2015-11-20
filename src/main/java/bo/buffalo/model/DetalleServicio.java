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
@Table(name="detalle_servicio_v2"
    ,schema="public"
)
public class DetalleServicio implements Serializable {
	private static final long serialVersionUID = 1L;
	private Integer id;
	private double cantidad;
	private double descuento;
	private String estado;
	private Date fechaRegistro;
	private double precio;
	private double total;
	private String usuarioRegistro;
	private Servicio servicio;
	private Proforma proforma;
	private Integer correlativo;

	public DetalleServicio() {
	}


	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	public Integer getId() {
		return this.id;
	}

	public void setId(Integer id) {
		this.id = id;
	}


	public double getCantidad() {
		return this.cantidad;
	}

	public void setCantidad(double cantidad) {
		this.cantidad = cantidad;
	}


	public double getDescuento() {
		return this.descuento;
	}

	public void setDescuento(double descuento) {
		this.descuento = descuento;
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


	public double getPrecio() {
		return this.precio;
	}

	public void setPrecio(double precio) {
		this.precio = precio;
	}


	public double getTotal() {
		return this.total;
	}

	public void setTotal(double total) {
		this.total = total;
	}


	@Column(name="usuario_registro")
	public String getUsuarioRegistro() {
		return this.usuarioRegistro;
	}

	public void setUsuarioRegistro(String usuarioRegistro) {
		this.usuarioRegistro = usuarioRegistro;
	}


	//bi-directional many-to-one association to Productop
	@ManyToOne
	@JoinColumn(name="id_servicio")
	public Servicio getServicio() {
		return this.servicio;
	}

	public void setServicio(Servicio servicio) {
		this.servicio = servicio;
	}


	//bi-directional many-to-one association to Proforma
	@ManyToOne
	@JoinColumn(name="id_proforma")
	public Proforma getProforma() {
		return this.proforma;
	}

	public void setProforma(Proforma proforma) {
		this.proforma = proforma;
	}


	public Integer getCorrelativo() {
		return correlativo;
	}


	public void setCorrelativo(Integer correlativo) {
		this.correlativo = correlativo;
	}

}
