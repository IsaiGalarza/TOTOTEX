package bo.buffalo.controller;

import java.io.Serializable;

import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import javax.inject.Inject;

import bo.buffalo.data.UsuarioRepository;
import bo.buffalo.model.Usuario;

import com.ahosoft.utils.FacesUtil;

@ManagedBean
@SessionScoped
public class UsuarioV2Controller implements Serializable{
	
	private static final long serialVersionUID = -1438182901991994907L;

	private @Inject UsuarioRepository usuarioRepository;

	private Usuario usuarioSistema;
	private boolean nuevo; /*Control para renderizar la vista*/
	private boolean modificar; 
	private String titulo;

	
	@PostConstruct
	public void init(){
		setUsuarioSistema(usuarioRepository.findByLogin(FacesUtil.getUserSession()));

	}

	/* Get and Set */

	public Usuario getUsuarioSistema() {
		return usuarioSistema;
	}


	public void setUsuarioSistema(Usuario usuarioSistema) {
		this.usuarioSistema = usuarioSistema;
	}

	public boolean isNuevo() {
		return nuevo;
	}

	public void setNuevo(boolean nuevo) {
		this.nuevo = nuevo;
	}

	public boolean isModificar() {
		return modificar;
	}

	public void setModificar(boolean modificar) {
		this.modificar = modificar;
	}

	public String getTitulo() {
		return titulo;
	}

	public void setTitulo(String titulo) {
		this.titulo = titulo;
	}
	
	

	
	
	
	
}
