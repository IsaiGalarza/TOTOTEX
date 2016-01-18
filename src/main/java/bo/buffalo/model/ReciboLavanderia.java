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

/**
 * 
 * @author mauriciobejaranorivera
 *
 */
@Entity
@Table(name = "recibo_lavanderia", schema = "public")
public class ReciboLavanderia implements Serializable {

	private static final long serialVersionUID = -749816136981683441L;
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Integer id;
	
	private String nombre;
	
	@Column(name="prenda")
	private String prenda;
	
	@Column(name="servicio")
	private String servicio;
	
	@Column(name="cantidad")
	private double cantidad;
	
	@Column(name="costo_unitario")
	private double costoUnitario;
	
	@Column(name="costo_total")
	private double costoTotal;
	
	private String recibi;
	
	private String entrege;
	
	private String observaciones;
	
	@ManyToOne
	@JoinColumn(name="id_lavanderia", nullable = true)
	private Lavanderia lavanderia;
	
	private String estado;
	
	@Column(name = "fecha_registro")
	private Date fechaRegistro;
	
	@Column(name = "usuario_registro")
	private String usuarioRegistro;

	public ReciboLavanderia() {
		super();
		this.id = 0;
		this.nombre = "";
		this.lavanderia = new Lavanderia();
	}

	@Override
	public String toString(){
		return nombre;
	}

	public Integer getId() {
		return this.id;
	}

	public void setId(Integer id) {
		this.id = id;
	}
	
	public String getNombre() {
		return nombre;
	}

	public void setNombre(String nombre) {
		this.nombre = nombre;
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
	
	public String getUsuarioRegistro() {
		return this.usuarioRegistro;
	}

	public void setUsuarioRegistro(String usuarioRegistro) {
		this.usuarioRegistro = usuarioRegistro;
	}

	public Lavanderia getLavanderia() {
		return lavanderia;
	}

	public void setLavanderia(Lavanderia lavanderia) {
		this.lavanderia = lavanderia;
	}

	public String getPrenda() {
		return prenda;
	}

	public void setPrenda(String prenda) {
		this.prenda = prenda;
	}

	public String getServicio() {
		return servicio;
	}

	public void setServicio(String servicio) {
		this.servicio = servicio;
	}

	public double getCantidad() {
		return cantidad;
	}

	public void setCantidad(double cantidad) {
		this.cantidad = cantidad;
	}

	public double getCostoUnitario() {
		return costoUnitario;
	}

	public void setCostoUnitario(double costoUnitario) {
		this.costoUnitario = costoUnitario;
	}

	public double getCostoTotal() {
		return costoTotal;
	}

	public void setCostoTotal(double costoTotal) {
		this.costoTotal = costoTotal;
	}

	public String getObservaciones() {
		return observaciones;
	}

	public void setObservaciones(String observaciones) {
		this.observaciones = observaciones;
	}

	public String getRecibi() {
		return recibi;
	}

	public void setRecibi(String recibi) {
		this.recibi = recibi;
	}

	public String getEntrege() {
		return entrege;
	}

	public void setEntrege(String entrege) {
		this.entrege = entrege;
	}

}
