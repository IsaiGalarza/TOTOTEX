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
@Table(name = "ficha_corte", schema = "public")
public class FichaCorte implements Serializable {

	private static final long serialVersionUID = -749816136981683441L;
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Integer id;

	private String estado;
	
	@Column(name = "fecha_registro")
	private Date fechaRegistro;
	
	@Column(name = "usuario_registro")
	private String usuarioRegistro;
	
	@ManyToOne
	@JoinColumn(name="id_operario",nullable=true)
	private Operario operario;
	
	@ManyToOne
	@JoinColumn(name="id_tela",nullable=true)
	private Producto tela;
	
	@Column(name="metro_tela")
	private Integer metroTela;

	@Column(name="fecha_corte")
	private Date fechaCorte;
	
	@Column(name="fecha_proceso")
	private Date fechaProceso;
	
	public FichaCorte() {
		super();
		this.id = 0;
		this.tela = new Producto();
	}

	@Override
	public String toString(){
		return String.valueOf(id);
	}

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

	public Date getFechaProceso() {
		return fechaProceso;
	}

	public void setFechaProceso(Date fechaProceso) {
		this.fechaProceso = fechaProceso;
	}

	public Date getFechaCorte() {
		return fechaCorte;
	}

	public void setFechaCorte(Date fechaCorte) {
		this.fechaCorte = fechaCorte;
	}

	public Integer getMetroTela() {
		return metroTela;
	}

	public void setMetroTela(Integer metroTela) {
		this.metroTela = metroTela;
	}

	public Producto getTela() {
		return tela;
	}

	public void setTela(Producto tela) {
		this.tela = tela;
	}

	public Operario getOperario() {
		return operario;
	}

	public void setOperario(Operario operario) {
		this.operario = operario;
	}

}
