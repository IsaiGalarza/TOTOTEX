package bo.buffalo.model;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

@Entity
@Table(name="nit_cliente"
,schema="public" 
)
public class NitCliente  implements java.io.Serializable {

	private static final long serialVersionUID = -5064876659020751409L;
	private Integer id;
     private Cliente cliente;
     private String nombreFactura;
     private String nitCi;
     private String estado;
     private Date fechaRegistro;
     private String usuarioRegistro;

    public NitCliente() {
    }

	
    public NitCliente(Integer id, Cliente cliente) {
        this.id = id;
        this.cliente = cliente;
    }
    
    public NitCliente(Integer id, Cliente cliente, String nombreFactura, String nitCi, String estado, Date fechaRegistro, String usuarioRegistro) {
       this.id = id;
       this.cliente = cliente;
       this.nombreFactura = nombreFactura;
       this.nitCi = nitCi;
       this.estado = estado;
       this.fechaRegistro = fechaRegistro;
       this.usuarioRegistro = usuarioRegistro;
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

//    @ManyToOne(fetch=FetchType.LAZY)
    @ManyToOne(fetch=FetchType.EAGER, optional=false)
    @JoinColumn(name="id_cliente", nullable=false)
    public Cliente getCliente() {
        return this.cliente;
    }
    
    public void setCliente(Cliente cliente) {
        this.cliente = cliente;
    }

    
    @Column(name="nombre_factura", length=200)
    public String getNombreFactura() {
        return this.nombreFactura;
    }
    
    public void setNombreFactura(String nombreFactura) {
        this.nombreFactura = nombreFactura;
    }

    
    @Column(name="nit_ci", length=40)
    public String getNitCi() {
        return this.nitCi;
    }
    
    public void setNitCi(String nitCi) {
        this.nitCi = nitCi;
    }

    
    @Column(name="estado", length=2)
    public String getEstado() {
        return this.estado;
    }
    
    public void setEstado(String estado) {
        this.estado = estado;
    }

    @Temporal(TemporalType.TIMESTAMP)
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