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
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * @author Arturo.Herrera
 *
 */
@Entity
@XmlRootElement
@Table(name = "dosificacion", schema = "public")
public class Dosificacion implements Serializable{

	private static final long serialVersionUID = -4177333692588548238L;

	@Id  @GeneratedValue(strategy=GenerationType.IDENTITY)
	 private Integer id;
	 
	 @NotNull(message="El numero de tramite no puede estar vacio.!")
	 @Size(min=1,max=20,message="El rago del numero de tramite es de 1 a 20 digitos.!")
	 @Column(name="numero_tramite", length=20)
	 private String numeroTramite;
	 
	 @NotNull(message="El numero de autorizacion no puede estar vacio.!")
	 @Size(min=1,max=20,message="El rago del numero de autorizacion es de 1 a 20 digitos.!")
	 @Column(name="numero_autoridzacion", length=20)
	 private String numeroAutorizacion;
	 
	 @NotNull(message="La cantidad de dosificacion no puede estar vacia.!")
	 @Column(name="cantidad_Dosificacion")
	 private Integer cantidadDosificacion; 
	 
	 @NotNull(message="El numero inicial no puede estar vacio.!")
	 @Column(name="numero_inicial")
	 private Integer numeroInicial;
	 
	 @NotNull(message="El numero de secuencia no puede estar vacio.!")
	 @Column(name="numero_secuencia")
	 private Integer numeroSecuencia;
	 
	 @Column(name="normaAplicada", length=30)
	 private String normaAplicada;
	 
	 @Temporal(TemporalType.DATE)
	 @Column(name="fecha_limite_emision")
	 private Date fechaLimiteEmision;
	 
	 @NotNull(message="La llave de control no puede estar vacia.!")
	 @Column(name="llave_control")
	 private String llaveControl;
	 
	 @Column(name="actividad_economica", length=400)
	 private String actividadEconomica;
	 
	 @Column(name="leyenda_inferior1", length=400)
	 private String leyendaInferior1;
	 
	 @Column(name="leyenda_inferior2", length=400)
     private String leyendaInferior2;
	 
	 @Column(name="tipo_dosificacion")
	 private String tipoDosificacion;
	 
     private Boolean activo;
     
     private Boolean baja;
     
     @ManyToOne
 	 @JoinColumn(name = "id_sucursal")
     private Sucursal sucursal;
     
     @Column(name="fecha_registro")
     private Date fechaRegistro;
     
     @ManyToOne
 	 @JoinColumn(name = "usuario_registro")
     private Usuario usuarioRegistro;
     
     public Dosificacion(){
    	 baja=false;
    	 activo=false;
    	 tipoDosificacion="CANTIDAD";
     }

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public String getNumeroTramite() {
		return numeroTramite;
	}

	public void setNumeroTramite(String numeroTramite) {
		this.numeroTramite = numeroTramite;
	}

	public String getNumeroAutorizacion() {
		return numeroAutorizacion;
	}

	public void setNumeroAutorizacion(String numeroAutorizacion) {
		this.numeroAutorizacion = numeroAutorizacion;
	}

	public Integer getCantidadDosificacion() {
		return cantidadDosificacion;
	}

	public void setCantidadDosificacion(Integer cantidadDosificacion) {
		this.cantidadDosificacion = cantidadDosificacion;
	}

	public Integer getNumeroInicial() {
		return numeroInicial;
	}

	public void setNumeroInicial(Integer numeroInicial) {
		this.numeroInicial = numeroInicial;
	}

	public Integer getNumeroSecuencia() {
		return numeroSecuencia;
	}

	public void setNumeroSecuencia(Integer numeroSecuencia) {
		this.numeroSecuencia = numeroSecuencia;
	}

	public String getNormaAplicada() {
		return normaAplicada;
	}

	public void setNormaAplicada(String normaAplicada) {
		this.normaAplicada = normaAplicada;
	}

	public Date getFechaLimiteEmision() {
		return fechaLimiteEmision;
	}

	public void setFechaLimiteEmision(Date fechaLimiteEmision) {
		this.fechaLimiteEmision = fechaLimiteEmision;
	}

	public String getLlaveControl() {
		return llaveControl;
	}

	public void setLlaveControl(String llaveControl) {
		this.llaveControl = llaveControl;
	}

	public String getActividadEconomica() {
		return actividadEconomica;
	}

	public void setActividadEconomica(String actividadEconomica) {
		this.actividadEconomica = actividadEconomica;
	}

	public String getLeyendaInferior1() {
		return leyendaInferior1;
	}

	public void setLeyendaInferior1(String leyendaInferior1) {
		this.leyendaInferior1 = leyendaInferior1;
	}

	public String getLeyendaInferior2() {
		return leyendaInferior2;
	}

	public void setLeyendaInferior2(String leyendaInferior2) {
		this.leyendaInferior2 = leyendaInferior2;
	}

	public Boolean getActivo() {
		return activo;
	}

	public void setActivo(Boolean activo) {
		this.activo = activo;
	}

	public Boolean getBaja() {
		return baja;
	}

	public void setBaja(Boolean baja) {
		this.baja = baja;
	}

	public Sucursal getSucursal() {
		return sucursal;
	}
	

	public String getTipoDosificacion() {
		return tipoDosificacion;
	}

	public void setTipoDosificacion(String tipoDosificacion) {
		this.tipoDosificacion = tipoDosificacion;
	}

	public void setSucursal(Sucursal sucursal) {
		this.sucursal = sucursal;
	}

	public Date getFechaRegistro() {
		return fechaRegistro;
	}

	public void setFechaRegistro(Date fechaRegistro) {
		this.fechaRegistro = fechaRegistro;
	}

	public Usuario getUsuarioRegistro() {
		return usuarioRegistro;
	}

	public void setUsuarioRegistro(Usuario usuarioRegistro) {
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
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Dosificacion other = (Dosificacion) obj;
		if (id == null) {
			if (other.id != null)
				return false;
		} else if (!id.equals(other.id))
			return false;
		return true;
	}

	@Override
	public String toString() {
		return "Dosificacion [id=" + id + ", numeroTramite=" + numeroTramite
				+ ", numeroAutorizacion=" + numeroAutorizacion
				+ ", cantidadDosificacion=" + cantidadDosificacion
				+ ", numeroInicial=" + numeroInicial + ", numeroSecuencia="
				+ numeroSecuencia + ", normaAplicada=" + normaAplicada
				+ ", fechaLimiteEmision=" + fechaLimiteEmision
				+ ", llaveControl=" + llaveControl + ", actividadEconomica="
				+ actividadEconomica + ", leyendaInferior1=" + leyendaInferior1
				+ ", leyendaInferior2=" + leyendaInferior2 + ", activo="
				+ activo + ", baja=" + baja + "]";
	}
     
     
}
