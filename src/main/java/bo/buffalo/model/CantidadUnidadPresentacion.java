package bo.buffalo.model;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

@Entity
@Table(name="cantidad_unidad_presentacion"
    ,schema="public"
)
public class CantidadUnidadPresentacion implements Serializable {
	
	private static final long serialVersionUID = 3231663423265866224L;
	
	private Integer id;
	private Integer cantidad;
	private String estado;
	private Date fechaRegistro;
	private String usuarioRegistro;
	private Presentacion presentacion;
	private UnidadMedida unidadMedida;
	private String descripcion;

	public CantidadUnidadPresentacion() {
		presentacion= new Presentacion();
		unidadMedida= new UnidadMedida();
	}


	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	public Integer getId() {
		return this.id;
	}

	public void setId(Integer id) {
		this.id = id;
	}


	public Integer getCantidad() {
		return this.cantidad;
	}

	public void setCantidad(Integer cantidad) {
		this.cantidad = cantidad;
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


	//bi-directional many-to-one association to Presentacion
	@ManyToOne(cascade={CascadeType.ALL})
	@JoinColumn(name="id_presentacion")
	public Presentacion getPresentacion() {
		return this.presentacion;
	}

	public void setPresentacion(Presentacion presentacion) {
		this.presentacion = presentacion;
	}


	//bi-directional many-to-one association to UnidadMedida
	@ManyToOne(cascade={CascadeType.ALL})
	@JoinColumn(name="id_unidad_medida")
	public UnidadMedida getUnidadMedida() {
		return this.unidadMedida;
	}

	public void setUnidadMedida(UnidadMedida unidadMedida) {
		this.unidadMedida = unidadMedida;
	}

	 @Column(name="descripcion", length=200)
	public String getDescripcion() {
		return descripcion;
	}


	public void setDescripcion(String descripcion) {
		this.descripcion = descripcion;
	}
	
	
}
