package bo.buffalo.model;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Table;

@Entity
@Table(name="presentacion"
    ,schema="public"
)
public class Presentacion implements Serializable {

	private static final long serialVersionUID = -1444503619112556786L;

	private Integer id;
	private String descripcion;
	private double cantidadCaja;
	private boolean showCantidad = false;
	private String estado;
	private Date fechaRegistro;
	private String usuarioRegistro;
	private List<CantidadUnidadPresentacion> listCantidadUnidadPresentacions;

	public Presentacion() {
	}


	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	public Integer getId() {
		return this.id;
	}

	public void setId(Integer id) {
		this.id = id;
	}


	public String getDescripcion() {
		return this.descripcion;
	}

	public void setDescripcion(String descripcion) {
		this.descripcion = descripcion;
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


	//bi-directional many-to-one association to CantidadUnidadPresentacion
	@OneToMany(mappedBy="presentacion", fetch=FetchType.EAGER)
	public List<CantidadUnidadPresentacion> getListCantidadUnidadPresentacions() {
		return this.listCantidadUnidadPresentacions;
	}

	public void setListCantidadUnidadPresentacions(List<CantidadUnidadPresentacion> listCantidadUnidadPresentacions) {
		this.listCantidadUnidadPresentacions = listCantidadUnidadPresentacions;
	}

	public CantidadUnidadPresentacion addListCantidadUnidadPresentacion(CantidadUnidadPresentacion listCantidadUnidadPresentacion) {
		getListCantidadUnidadPresentacions().add(listCantidadUnidadPresentacion);
		listCantidadUnidadPresentacion.setPresentacion(this);

		return listCantidadUnidadPresentacion;
	}

	public CantidadUnidadPresentacion removeListCantidadUnidadPresentacion(CantidadUnidadPresentacion listCantidadUnidadPresentacion) {
		getListCantidadUnidadPresentacions().remove(listCantidadUnidadPresentacion);
		listCantidadUnidadPresentacion.setPresentacion(null);

		return listCantidadUnidadPresentacion;
	}

	@Column(name="show_cantidad", nullable=true)
	public boolean isShowCantidad() {
		return showCantidad;
	}

	public void setShowCantidad(boolean showCantidad) {
		this.showCantidad = showCantidad;
	}

	@Column(name="cantidad_caja", nullable=true, precision=8, scale=8)
	public double getCantidadCaja() {
		return cantidadCaja;
	}

	public void setCantidadCaja(double cantidadCaja) {
		this.cantidadCaja = cantidadCaja;
	}

}
