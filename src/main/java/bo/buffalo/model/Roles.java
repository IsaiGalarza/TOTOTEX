package bo.buffalo.model;

import java.util.HashSet;
import java.util.Set;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;

@Entity
@Table(name="roles"
,schema="public"
, uniqueConstraints = @UniqueConstraint(columnNames="name") 
		)
public class Roles  implements java.io.Serializable {

	private static final long serialVersionUID = -5965045359329213064L;
	private Integer id;
	private String name;
	private Set<UsuarioRol> usuarioRols = new HashSet<UsuarioRol>(0);

	public Roles() {
	}


	public Roles(Integer id, String name) {
		this.id = id;
		this.name = name;
	}
	public Roles(Integer id, String name, Set<UsuarioRol> usuarioRols) {
		this.id = id;
		this.name = name;
		this.usuarioRols = usuarioRols;
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


	@Column(name="name", unique=true, nullable=false, length=25)
	public String getName() {
		return this.name;
	}

	public void setName(String name) {
		this.name = name;
	}

	@OneToMany(fetch=FetchType.LAZY, mappedBy="roles")
	public Set<UsuarioRol> getUsuarioRols() {
		return this.usuarioRols;
	}

	public void setUsuarioRols(Set<UsuarioRol> usuarioRols) {
		this.usuarioRols = usuarioRols;
	}

}