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
@Table(name="conversiones"
    ,schema="public"
)
public class Conversiones implements Serializable {
	
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 8362606359512860285L;
	private Integer id;
	private UnidadMedida unidadMedida;
	private double unidad=1;
	private double equivalente;
	private UnidadMedida conversion;
	private String estado;
	private Date fechaRegistro;
	private String usuarioRegistro;

	public Conversiones() {
	}


	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	public Integer getId() {
		return this.id;
	}

	public void setId(Integer id) {
		this.id = id;
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


	@Column(name="usuario_registro")
	public String getUsuarioRegistro() {
		return this.usuarioRegistro;
	}

	public void setUsuarioRegistro(String usuarioRegistro) {
		this.usuarioRegistro = usuarioRegistro;
	}


	@ManyToOne
	@JoinColumn(name = "id_unidad_medida", nullable=false)
	public UnidadMedida getUnidadMedida() {
		return unidadMedida;
	}


	public void setUnidadMedida(UnidadMedida unidadMedida) {
		this.unidadMedida = unidadMedida;
	}


	public double getUnidad() {
		return unidad;
	}


	public void setUnidad(double unidad) {
		this.unidad = unidad;
	}


	public double getEquivalente() {
		return equivalente;
	}


	public void setEquivalente(double equivalente) {
		this.equivalente = equivalente;
	}

	@ManyToOne
	@JoinColumn(name = "id_unidad_conversion", nullable=false)
	public UnidadMedida getConversion() {
		return conversion;
	}


	public void setConversion(UnidadMedida conversion) {
		this.conversion = conversion;
	}


	
}
