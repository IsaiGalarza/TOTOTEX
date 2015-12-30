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
import javax.xml.bind.annotation.XmlRootElement;

/**
 * @author Arturo.Herrera
 *
 */
@SuppressWarnings("serial")
@Entity
@XmlRootElement
@Table(name = "ficha_tecnica", schema = "public")
public class FichaTecnica implements Serializable{
	
	@Id  @GeneratedValue(strategy=GenerationType.IDENTITY)
	private Integer id;
	
	@Column(name="codigo",nullable=true)
	private String codigo;
	
	@Column(name="correlativo",nullable=true)
	private Integer correlativo;
	
	private String confeccionista;
	
	@ManyToOne
	@JoinColumn(name="id_marca")
	private Atributo marca;
	
	@ManyToOne
	@JoinColumn(name="id_color_atraque")
	private Atributo colorAtraque;
	
	@ManyToOne
	@JoinColumn(name="id_color_hilo")
	private Atributo colorHilo;
	
	@Column(name="partida")
	private String partida;
	
	@Column(name="tipo_tela")
	private String tipoTela;
	
	@Column(name="fecha_salida")
	private Date fechaSalida;
	
	@Column(name="fecha_entrada")
	private Date fechaEntrada;
	
	private Integer total;
	
	private Boolean baja;
	
	private String estado;
	
	@Column(name="fecha_registro")
	private Date fechaRegistro;
	
	@Column(name="usuario_registro")
	private String usuarioRegistro;
	
	private byte[] molde;
	
	private String operario;
	
	@ManyToOne
	@JoinColumn(name="id_tela",nullable=true)
	private Producto tela;
	
	@Column(name="metro_tela")
	private Integer metroTela;
	
	@Column(name="fecha_corte")
	private Date fechaCorte;
	
	@Column(name="fecha_proceso")
	private Date fechaProceso;

	public FichaTecnica(){
		this.id = 0;
		this.codigo="";
		this.correlativo = 0;
		baja=false;
		molde=null;
		tela=new Producto();
	}
	
	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public String getConfeccionista() {
		return confeccionista;
	}

	public void setConfeccionista(String confeccionista) {
		this.confeccionista = confeccionista;
	}

	public Atributo getMarca() {
		return marca;
	}

	public void setMarca(Atributo marca) {
		this.marca = marca;
	}

	public Atributo getColorAtraque() {
		return colorAtraque;
	}

	public void setColorAtraque(Atributo colorAtraque) {
		this.colorAtraque = colorAtraque;
	}

	public Atributo getColorHilo() {
		return colorHilo;
	}

	public void setColorHilo(Atributo colorHilo) {
		this.colorHilo = colorHilo;
	}

	public String getPartida() {
		return partida;
	}

	public void setPartida(String partida) {
		this.partida = partida;
	}

	public String getTipoTela() {
		return tipoTela;
	}

	public void setTipoTela(String tipoTela) {
		this.tipoTela = tipoTela;
	}

	public Date getFechaSalida() {
		return fechaSalida;
	}

	public void setFechaSalida(Date fechaSalida) {
		this.fechaSalida = fechaSalida;
	}

	public Date getFechaEntrada() {
		return fechaEntrada;
	}

	public void setFechaEntrada(Date fechaEntrada) {
		this.fechaEntrada = fechaEntrada;
	}

	public Integer getTotal() {
		return total;
	}

	public void setTotal(Integer total) {
		this.total = total;
	}

	public Boolean getBaja() {
		return baja;
	}

	public void setBaja(Boolean baja) {
		this.baja = baja;
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

	public byte[] getMolde() {
		return molde;
	}

	public void setMolde(byte[] molde) {
		this.molde = molde;
	}
	

	public String getOperario() {
		return operario;
	}

	public void setOperario(String operario) {
		this.operario = operario;
	}

	public Producto getTela() {
		return tela;
	}

	public void setTela(Producto tela) {
		this.tela = tela;
	}

	public Integer getMetroTela() {
		return metroTela;
	}

	public void setMetroTela(Integer metroTela) {
		this.metroTela = metroTela;
	}

	public Date getFechaCorte() {
		return fechaCorte;
	}

	public void setFechaCorte(Date fechaCorte) {
		this.fechaCorte = fechaCorte;
	}

	public Date getFechaProceso() {
		return fechaProceso;
	}

	public void setFechaProceso(Date fechaProceso) {
		this.fechaProceso = fechaProceso;
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
		FichaTecnica other = (FichaTecnica) obj;
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
		return "FichaTecnica [id=" + id + ", marca=" + marca
				+ ", colorAtraque=" + colorAtraque + ", colorHilo=" + colorHilo
				+ ", tipoTela=" + tipoTela + ", fechaSalida=" + fechaSalida
				+ ", fechaEntrada=" + fechaEntrada + ", total=" + total
				+ ", baja=" + baja + ", fechaRegistro=" + fechaRegistro
				+ ", usuarioRegistro=" + usuarioRegistro + ", molde=" + molde
				+ "]";
	}

	public String getCodigo() {
		return codigo;
	}

	public void setCodigo(String codigo) {
		this.codigo = codigo;
	}

	public Integer getCorrelativo() {
		return correlativo;
	}

	public void setCorrelativo(Integer correlativo) {
		this.correlativo = correlativo;
	}
	
	
	
}
