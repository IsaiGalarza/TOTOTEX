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
 * SCHEMA = PRODUCCION
 */
@Entity
@Table(name = "falla_proceso", schema = "public")
public class FallaProceso implements Serializable{

	private static final long serialVersionUID = -3586357302235244181L;

	@Id  @GeneratedValue(strategy=GenerationType.IDENTITY)
	private Integer id;
	
	@Column(name="fecha_registro")
	private Date fechaRegistro;
	
	@Column(name="usuario_registro")
	private String usuarioRegistro;

	@Column(name = "estado")
	private String estado;
	
	private double falla;
	
	private String observacion;
	
	@ManyToOne
	@JoinColumn(name = "id_ficha_detalle_producto",nullable=true)
	private FichaDetalleProducto fichaDetalleProducto;
	
	@ManyToOne
	@JoinColumn(name = "id_proceso_corte",nullable=true)
	private ProcesoCorte procesoCorte;
	
	@ManyToOne
	@JoinColumn(name = "id_proceso_lavanderia",nullable=true)
	private ProcesoLavanderia procesoLavanderia;

	@ManyToOne
	@JoinColumn(name = "id_proceso_empaque_final",nullable=true)
	private ProcesoEmpaqueFinal procesoEmpaqueFinal;
	
	public FallaProceso(){
		this.id = 0;
		this.estado = "AC";
		this.falla = 0;
		this.observacion = "Ninguna";
	}

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
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

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((id == null) ? 0 : id.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj == null) {
			return false;
		}
		if (getClass() != obj.getClass()) {
			return false;
		}
		FallaProceso other = (FallaProceso) obj;
		if (id == null) {
			if (other.id != null) {
				return false;
			}
		} else if (!id.equals(other.id)) {
			return false;
		}
		return true;
	}

	@Override
	public String toString() {
		return "AlmacenPrenda [id=" + id + ", fechaRegistro=" + fechaRegistro
				+ ", usuarioRegistro=" + usuarioRegistro ;
	}

	public String getEstado() {
		return estado;
	}

	public void setEstado(String estado) {
		this.estado = estado;
	}

	public FichaDetalleProducto getFichaDetalleProducto() {
		return fichaDetalleProducto;
	}

	public void setFichaDetalleProducto(FichaDetalleProducto fichaDetalleProducto) {
		this.fichaDetalleProducto = fichaDetalleProducto;
	}

	public double getFalla() {
		return falla;
	}

	public void setFalla(double falla) {
		this.falla = falla;
	}

	public String getObservacion() {
		return observacion;
	}

	public void setObservacion(String observacion) {
		this.observacion = observacion;
	}

	public ProcesoCorte getProcesoCorte() {
		return procesoCorte;
	}

	public void setProcesoCorte(ProcesoCorte procesoCorte) {
		this.procesoCorte = procesoCorte;
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
