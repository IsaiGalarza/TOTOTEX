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
@Table(name="detalle_orden_produccion",schema="public")
public class DetalleOrdenProduccion implements Serializable {

	private static final long serialVersionUID = -7374931160965878089L;

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Integer id;
	
	private String observacion;

	private String estado;
	
	@Column(name = "fecha_registro")
	private Date fechaRegistro;
	
	@Column(name = "usuario_registro")
	private String usuarioRegistro;

	@Column(name = "fecha_aprobacion")
	private Date fechaAprobacion;

	@ManyToOne
	@JoinColumn(name = "id_orden_produccion")
	private OrdenProduccion ordenProduccion;
	
	@ManyToOne
	@JoinColumn(name = "id_proceso_corte",nullable=true)
	private ProcesoCorte procesoCorte;
	
	@ManyToOne
	@JoinColumn(name = "id_proceso_lavanderia",nullable=true)
	private ProcesoLavanderia procesoLavanderia;
	
	@ManyToOne
	@JoinColumn(name = "id_proceso_maquilador",nullable=true)
	private ProcesoMaquilador procesoMaquilador;
	
	@ManyToOne
	@JoinColumn(name = "id_proceso_empaque_final",nullable=true)
	private ProcesoEmpaqueFinal procesoEmpaqueFinal;
	
	public DetalleOrdenProduccion() {
		super();
		this.id = 0;
		this.procesoCorte = new ProcesoCorte();
		this.procesoMaquilador = new ProcesoMaquilador();
		this.ordenProduccion = new OrdenProduccion();
		this.procesoLavanderia = new ProcesoLavanderia();
		this.procesoEmpaqueFinal = new ProcesoEmpaqueFinal();
	}

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public String getObservacion() {
		return observacion;
	}

	public void setObservacion(String observacion) {
		this.observacion = observacion;
	}

	public String getEstado() {
		return estado;
	}

	public void setEstado(String estado) {
		this.estado = estado;
	}

	public Date getFechaRegistro() {
		return fechaRegistro;
	}

	public void setFechaRegistro(Date fechaRegistro) {
		this.fechaRegistro = fechaRegistro;
	}

	public String getUsuarioRegistro() {
		return usuarioRegistro;
	}

	public void setUsuarioRegistro(String usuarioRegistro) {
		this.usuarioRegistro = usuarioRegistro;
	}

	public Date getFechaAprobacion() {
		return fechaAprobacion;
	}

	public void setFechaAprobacion(Date fechaAprobacion) {
		this.fechaAprobacion = fechaAprobacion;
	}
	
	public OrdenProduccion getOrdenProduccion() {
		return ordenProduccion;
	}

	public void setOrdenProduccion(OrdenProduccion ordenProduccion) {
		this.ordenProduccion = ordenProduccion;
	}

	public ProcesoCorte getProcesoCorte() {
		return procesoCorte;
	}

	public void setProcesoCorte(ProcesoCorte procesoCorte) {
		this.procesoCorte = procesoCorte;
	}

	public ProcesoMaquilador getProcesoMaquilador() {
		return procesoMaquilador;
	}

	public void setProcesoMaquilador(ProcesoMaquilador procesoMaquilador) {
		this.procesoMaquilador = procesoMaquilador;
	}

	public ProcesoLavanderia getProcesoLavanderia() {
		return procesoLavanderia;
	}

	public void setProcesoLavanderia(ProcesoLavanderia procesoLavanderia) {
		this.procesoLavanderia = procesoLavanderia;
	}

	public ProcesoEmpaqueFinal getProcesoEmpaqueFinal() {
		return procesoEmpaqueFinal;
	}

	public void setProcesoEmpaqueFinal(ProcesoEmpaqueFinal procesoEmpaqueFinal) {
		this.procesoEmpaqueFinal = procesoEmpaqueFinal;
	}

}
