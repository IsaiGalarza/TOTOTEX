package bo.buffalo.util;

import javax.annotation.PostConstruct;
import javax.enterprise.context.SessionScoped;
import javax.faces.context.FacesContext;
import javax.inject.Inject;
import javax.inject.Named;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import bo.buffalo.data.UsuarioRepository;
import bo.buffalo.model.Usuario;
import bo.buffalo.model.UsuarioRol;

import com.ahosoft.utils.FacesUtil;

import java.io.Serializable;
import java.util.List;

/**
 * Class SessionMain, datos persistente durante la session del usuario
 * @author mauricio.bejarano.rivera
 *
 */
@Named
@SessionScoped
public class SessionMain implements Serializable {

	private static final long serialVersionUID = -645068727337928781L;
	private @Inject FacesContext facesContext;

	//Repository
	private @Inject UsuarioRepository usuarioRepository;

	//Object
	private Usuario usuarioSistema;
	private UsuarioRol usuarioRol;
	private boolean isAdministrador;

	//list

	@PostConstruct
	public void initSessionMain(){
		System.out.println("----- initSessionMain() --------");
		usuarioSistema = null;
		usuarioRol= null;
		isAdministrador = false;
	}

	public String getParameterRequest(String name){
		HttpServletRequest request = (HttpServletRequest) facesContext
				.getExternalContext().getRequest();
		return request.getParameter(name);
	}

	public void cargarPaginaConParametro(String ulPath, String key,String value){
		try {
			setAttributeSession(key,value);
			//lanzar pagina
			FacesUtil.redirect(ulPath);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void setAttributeSession(String key,String value){
		try{
			HttpSession session = (HttpSession) FacesContext.getCurrentInstance().getExternalContext().getSession(false);
			session.setAttribute(key, value);
		}catch(Exception e){
			System.out.println("setAttributeSession() ERROR: "+e.getMessage());
		}		
	}

	public String getAttributeSession(String key){
		try {
			HttpSession request = (HttpSession) FacesContext.getCurrentInstance().getExternalContext().getSession(false);
			return request.getAttribute(key)!=null ? (String) request.getAttribute(key):null;
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	public boolean removeAttributeSession(String key){
		try {
			HttpSession request = (HttpSession) FacesContext.getCurrentInstance().getExternalContext().getSession(false);
			request.removeAttribute(key);
			return true;
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
	}

	public Usuario getUsuarioSistema() {
		if(usuarioSistema==null){
			usuarioSistema= usuarioRepository.findByLogin(FacesUtil.getUserSession());
		}
		return usuarioSistema;
	}

	public void setUsuarioSistema(Usuario usuarioSistema) {
		this.usuarioSistema = usuarioSistema;
	}

	public UsuarioRol getUsuarioRol() {
		return usuarioRol;
	}

	public void setUsuarioRol(UsuarioRol usuarioRol) {
		this.usuarioRol = usuarioRol;
	}

	public boolean isAdministrador() {
		if(usuarioRol== null){
			try {
				List<UsuarioRol> ur = usuarioRepository.buscarUsuarioRol(getUsuarioSistema());
				if(!ur.isEmpty()){
					for (UsuarioRol usuarioRol : ur) {
						if(usuarioRol.getRoles().getName().equals("administracion")){
							isAdministrador=true;
						}
					}
				}
			} catch (Exception e) {
				System.out.println("Error verificar si es administrador: "+e.getMessage());
			}

		}
		return isAdministrador;
	}

	public void setAdministrador(boolean isAdministrador) {
		this.isAdministrador = isAdministrador;
	}

	//----------------------------------------


}
