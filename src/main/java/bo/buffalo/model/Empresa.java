package bo.buffalo.model;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

@Entity
@Table(name="empresa"
    ,schema="public"
)
public class Empresa  implements java.io.Serializable {

	 private static final long serialVersionUID = -6424368364250163287L;
     private Integer id;
     private String nombre;
     private String nit;
     private String representanteLegar;
     private String carnetIdentidad;
     private String direccion;
     private String ciudad;
     private String telefono;
     private String estado;
     private Date fechaRegistro;
     private String usuarioRegistro;

    public Empresa() {
    	
    }

    @Id 
    @GeneratedValue(strategy=GenerationType.IDENTITY)
    @Column(name="id", unique=true, nullable=false)
    public Integer getId() {
        return this.id;
    }
    
    public void setId(Integer id) {
        this.id = id;
    }

    
    @Column(name="razon_social", nullable=false, length=200)
    public String getNombre() {
        return this.nombre;
    }
    
    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    @Column(name="ciudad", length=50)
    public String getCiudad() {
        return this.ciudad;
    }
    
    public void setCiudad(String ciudad) {
        this.ciudad = ciudad;
    }
    
    @Column(name="direccion", length=200)
	public String getDireccion() {
		return direccion;
	}


	public void setDireccion(String direccion) {
		this.direccion = direccion;
	}

	@Column(name="telefono", length=20)
	public String getTelefono() {
		return telefono;
	}

	public void setTelefono(String telefono) {
		this.telefono = telefono;
	}
    
    @Column(name="nit", nullable=false, length=20)
    public String getNit() {
        return this.nit;
    }
    
    public void setNit(String nit) {
        this.nit = nit;
    }
    
    @Column(name="representante_legal", nullable=false, length=200)
	public String getRepresentanteLegar() {
		return representanteLegar;
	}

	public void setRepresentanteLegar(String representanteLegar) {
		this.representanteLegar = representanteLegar;
	}
	
	@Column(name="carnet_identidad", nullable=false, length=20)
	public String getCarnetIdentidad() {
		return carnetIdentidad;
	}

	public void setCarnetIdentidad(String carnetIdentidad) {
		this.carnetIdentidad = carnetIdentidad;
	}
	
	@Column(name="estado", length=2)
    public String getEstado() {
        return this.estado;
    }
    
    public void setEstado(String estado) {
        this.estado = estado;
    }

    @Temporal(TemporalType.DATE)
    @Column(name="fecha_registro", length=29)
    public Date getFechaRegistro() {
        return this.fechaRegistro;
    }
    
    public void setFechaRegistro(Date fechaRegistro) {
        this.fechaRegistro = fechaRegistro;
    }

    
    @Column(name="usuario_registro", length=30)
    public String getUsuarioRegistro() {
        return this.usuarioRegistro;
    }
    
    public void setUsuarioRegistro(String usuarioRegistro) {
        this.usuarioRegistro = usuarioRegistro;
    }

}
