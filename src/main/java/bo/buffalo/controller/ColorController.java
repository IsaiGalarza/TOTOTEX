package bo.buffalo.controller;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.enterprise.context.Conversation;
import javax.enterprise.context.ConversationScoped;
import javax.enterprise.event.Event;
import javax.faces.context.FacesContext;
import javax.inject.Inject;
import javax.inject.Named;

import org.richfaces.cdi.push.Push;

import bo.buffalo.data.AtributoRepository;
import bo.buffalo.data.UsuarioRepository;
import bo.buffalo.model.Atributo;
import bo.buffalo.model.Usuario;
import bo.buffalo.service.AtributoRegistration;

import com.ahosoft.utils.FacesUtil;

/**
 *
 * @author mauriciobejaranorivera
 *
 */
@Named(value="colorController")
@ConversationScoped
public class ColorController implements Serializable{

	private static final long serialVersionUID = -5765554986299229214L;

	public static final String PUSH_CDI_TOPIC = "pushCdi";
	
    @Inject
    @Push(topic = PUSH_CDI_TOPIC)
    Event<String> pushEventSucursal;
    
	@Inject
	Conversation conversation;
	
    //REPOSITORY
	private @Inject UsuarioRepository usuarioRepository;
	private @Inject AtributoRepository atributoRepository;
	
	//REGISTRATION
	private @Inject AtributoRegistration atributoRegistration;
	
	//ESTADO
	private boolean nuevo; /*Control para renderizar la vista*/
	private boolean modificar; 
	private boolean administrador;
	private boolean detalle;
	
	//OBJECT
	private Atributo newAtributo;
	private Atributo selectedAtributo;

	//LIST
	private List<Atributo> listaColor;
	private ArrayList<String> frms;

	//VAR
	private String urlVista;
	private String usuario;
	private String password;
	
	//SESION
	private Usuario usuarioSistema;

	@PostConstruct
	public void init(){
		usuarioSistema= usuarioRepository.findByLogin(FacesUtil.getUserSession());
		newEntity();
	}

	private void newEntity(){
		nuevo=false;
		modificar=false;
		detalle=false;
		
		newAtributo = new Atributo();
		newAtributo.setUsuarioRegistro(usuarioSistema);
		newAtributo.setFechaRegistro(new Date());
		selectedAtributo = new Atributo();
		listaColor = atributoRepository.findAllOrderedByParameter("id");
	}

	public void initConversation() {
		if (!FacesContext.getCurrentInstance().isPostback() && conversation.isTransient()) {
			conversation.begin();
			System.out.println(">>>>>>>>>> CONVERSACION INICIADA...");
		}
	}

	public String endConversation() {
		if (!conversation.isTransient()) {
			conversation.end();
			System.out.println(">>>>>>>>>> CONVERSACION TERMINADA...");
		}
		return "orden_ingreso.xhtml?faces-redirect=true";
	}

	/* Action Register*/
	public void register(){
		try {
			newEntity();
			FacesUtil.infoMessage("Registrado Correctamente.!");
			FacesUtil.updateComponets(frms);
		} catch (Exception e) {
			System.out.println("Error en registro Ficha Tecnica: "+e.getMessage());
			FacesUtil.errorMessage("Ocurrio un error en el registo.");
		}
	}

	public void update(){
		try {
			newEntity();
			FacesUtil.infoMessage("Modificado Correctamente.!");
			FacesUtil.updateComponets(frms);

		} catch (Exception e) {
			System.out.println("Error en update Ficha Tecnica: "+e.getMessage());
			FacesUtil.errorMessage("Ocurrio un error en el registo.");
		}
	}

	public void delete(){
		try {
			newEntity();
			FacesUtil.infoMessage("Eliminado Correctamente.!");
			FacesUtil.updateComponets(frms);

		} catch (Exception e) {
			System.out.println("Error en delete Ficha Tecnica: "+e.getMessage());
			FacesUtil.errorMessage("Ocurrio un error en el registo.");
		}
	}

	public void aprobar(){
		try {
			newEntity();
			FacesUtil.infoMessage("Modificado Correctamente.!");
			FacesUtil.updateComponets(frms);
		} catch (Exception e) {
			System.out.println("Error en update Ficha Tecnica: "+e.getMessage());
			FacesUtil.errorMessage("Ocurrio un error en el registo.");
		}
	}

	/* Action */
	public void nuevoEntity(){
		newEntity();
		nuevo=true;
		modificar=false;
		FacesUtil.updateComponets(frms);
	}

	public void cancelarEntity(){
		nuevo=false;
		modificar=false;
		FacesUtil.updateComponets(frms);
	}

	/* Get and Set */
	public boolean isNuevo() {
		return nuevo;
	}
	public void setNuevo(boolean nuevo) {
		this.nuevo = nuevo;
	}
	
	public Usuario getUsuarioSistema() {
		return usuarioSistema;
	}
	public void setUsuarioSistema(Usuario usuarioSistema) {
		this.usuarioSistema = usuarioSistema;
	}

	public boolean isModificar() {
		return modificar;
	}

	public void setModificar(boolean modificar) {
		this.modificar = modificar;
	}

	public boolean isAdministrador() {
		return administrador;
	}

	public void setAdministrador(boolean administrador) {
		this.administrador = administrador;
	}

	public boolean isDetalle() {
		return detalle;
	}

	public void setDetalle(boolean detalle) {
		this.detalle = detalle;
	}

	public String getUrlVista() {
		return urlVista;
	}

	public void setUrlVista(String urlVista) {
		this.urlVista = urlVista;
	}

	public String getUsuario() {
		return usuario;
	}

	public void setUsuario(String usuario) {
		this.usuario = usuario;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public Atributo getNewAtributo() {
		return newAtributo;
	}

	public void setNewAtributo(Atributo newAtributo) {
		this.newAtributo = newAtributo;
	}

	public Atributo getSelectedAtributo() {
		return selectedAtributo;
	}

	public void setSelectedAtributo(Atributo selectedAtributo) {
		this.selectedAtributo = selectedAtributo;
	}

	public List<Atributo> getListaColor() {
		return listaColor;
	}

	public void setListaColor(List<Atributo> listaColor) {
		this.listaColor = listaColor;
	}

}
