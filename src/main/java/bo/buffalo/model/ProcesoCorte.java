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
 *SCHEMA = PRODUCCION
 */
@Entity
@Table(name = "proceso_corte", schema = "public")
public class ProcesoCorte implements Serializable {	

	private static final long serialVersionUID = -8595923895202956950L;

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Integer id;
	
	private Integer correlativo;

	private String estado;
	
	@Column(name = "fecha_registro")
	private Date fechaRegistro;
	
	@Column(name = "usuario_registro")
	private String usuarioRegistro;

	@Column(name = "fecha_aprobacion")
	private Date fechaAprobacion;
	
	@ManyToOne
	@JoinColumn(name = "id_ficha_tecnica",nullable=true)
	private FichaTecnica fichaTecnica;
	
	@ManyToOne
	@JoinColumn(name = "id_ficha_corte",nullable=true)
	private FichaCorte fichaCorte;

	@ManyToOne
	@JoinColumn(name = "id_usuario_aprobacion",nullable=true)
	private Usuario usuarioAprobacion;	

	public ProcesoCorte() {
		super();
		this.id = 0;
		this.fichaTecnica = new FichaTecnica();
		this.usuarioAprobacion = new Usuario();
	}

	public Integer getId() {
		return this.id;
	}

	public void setId(Integer id) {
		this.id = id;
	}
	
	public Integer getCorrelativo() {
		return correlativo;
	}

	public void setCorrelativo(Integer correlativo) {
		this.correlativo = correlativo;
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

	public Usuario getUsuarioAprobacion() {
		return usuarioAprobacion;
	}

	public void setUsuarioAprobacion(Usuario usuarioAprobacion) {
		this.usuarioAprobacion = usuarioAprobacion;
	}

	public Date getFechaAprobacion() {
		return fechaAprobacion;
	}

	public void setFechaAprobacion(Date fechaAprobacion) {
		this.fechaAprobacion = fechaAprobacion;
	}

	public FichaTecnica getFichaTecnica() {
		return fichaTecnica;
	}

	public void setFichaTecnica(FichaTecnica fichaTecnica) {
		this.fichaTecnica = fichaTecnica;
	}

	public FichaCorte getFichaCorte() {
		return fichaCorte;
	}

	public void setFichaCorte(FichaCorte fichaCorte) {
		this.fichaCorte = fichaCorte;
	}

}
