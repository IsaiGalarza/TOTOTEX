package bo.buffalo.model;

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
@Entity
@XmlRootElement
@Table(name = "sucursal", schema = "public")
public class Sucursal  implements java.io.Serializable {

   	 private static final long serialVersionUID = -780684814128280508L;
   	 
   	 @Id  @GeneratedValue(strategy=GenerationType.IDENTITY)
   	 private Integer id;
     private String nombre;
     private String descripcion;
     
     @ManyToOne
 	 @JoinColumn(name = "id_ciudad")
     private Ciudad ciudad;
     private String direccion;
     private String telefono;
     private String nit;
     
     @ManyToOne
 	 @JoinColumn(name = "id_empresa")
     private Empresa empresa;
     
     private String estado;
     
     @Column(name="fecha_registro")
     private Date fechaRegistro;
     
     @ManyToOne
 	 @JoinColumn(name = "usuario_registro")
     private Usuario usuarioRegistro;
     
     private Integer numeroSucursal;
     
     

    public Sucursal() {
    	
    }



	public Integer getId() {
		return id;
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



	public String getDescripcion() {
		return descripcion;
	}



	public void setDescripcion(String descripcion) {
		this.descripcion = descripcion;
	}



	public Ciudad getCiudad() {
		return ciudad;
	}



	public void setCiudad(Ciudad ciudad) {
		this.ciudad = ciudad;
	}



	public String getDireccion() {
		return direccion;
	}



	public void setDireccion(String direccion) {
		this.direccion = direccion;
	}



	public String getTelefono() {
		return telefono;
	}



	public void setTelefono(String telefono) {
		this.telefono = telefono;
	}



	public String getNit() {
		return nit;
	}



	public void setNit(String nit) {
		this.nit = nit;
	}



	public Empresa getEmpresa() {
		return empresa;
	}



	public void setEmpresa(Empresa empresa) {
		this.empresa = empresa;
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



	public Usuario getUsuarioRegistro() {
		return usuarioRegistro;
	}



	public void setUsuarioRegistro(Usuario usuarioRegistro) {
		this.usuarioRegistro = usuarioRegistro;
	}



	public Integer getNumeroSucursal() {
		return numeroSucursal;
	}



	public void setNumeroSucursal(Integer numeroSucursal) {
		this.numeroSucursal = numeroSucursal;
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
		Sucursal other = (Sucursal) obj;
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
		return "Sucursal [id=" + id + ", nombre=" + nombre + ", descripcion="
				+ descripcion + ", direccion=" + direccion + ", telefono="
				+ telefono + ", nit=" + nit + ", numeroSucursal="
				+ numeroSucursal + "]";
	}
    


}