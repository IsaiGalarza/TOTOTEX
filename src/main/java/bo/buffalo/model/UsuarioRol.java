package bo.buffalo.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

@Entity
@Table(name="usuario_rol"
    ,schema="public"
)
public class UsuarioRol  implements java.io.Serializable {

	private static final long serialVersionUID = 470152485576211784L;
	private Integer id;
     private Roles roles;
     private Usuario usuario;

    public UsuarioRol() {
    }

    public UsuarioRol(Integer id, Roles roles, Usuario usuario) {
       this.id = id;
       this.roles = roles;
       this.usuario = usuario;
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

    //@ManyToOne(fetch=FetchType.LAZY)
    @ManyToOne(fetch=FetchType.EAGER, optional=false)
    @JoinColumn(name="id_roles", nullable=false)
    public Roles getRoles() {
        return this.roles;
    }
    
    public void setRoles(Roles roles) {
        this.roles = roles;
    }

  //@ManyToOne(fetch=FetchType.LAZY)
    @ManyToOne(fetch=FetchType.EAGER, optional=false)
    @JoinColumn(name="id_usuario", nullable=false)
    public Usuario getUsuario() {
        return this.usuario;
    }
    
    public void setUsuario(Usuario usuario) {
        this.usuario = usuario;
    }
}
