package bo.buffalo.util;

import javax.annotation.PostConstruct;
import javax.enterprise.context.SessionScoped;
import javax.faces.context.FacesContext;
import javax.inject.Inject;
import javax.inject.Named;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.apache.log4j.Logger;

import com.ahosoft.utils.FacesUtil;

import java.io.IOException;
import java.io.Serializable;

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
	private Logger log = Logger.getLogger(this.getClass());

	//Repository

	//Object

	//list

	@PostConstruct
	public void initSessionMain(){
		log.info("----- initSessionMain() --------");
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
			log.error("setAttributeSession() ERROR: "+e.getMessage());
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

	//----------------------------------------


}
