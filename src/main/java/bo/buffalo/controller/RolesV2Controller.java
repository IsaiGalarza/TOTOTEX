package bo.buffalo.controller;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.enterprise.context.Conversation;
import javax.enterprise.context.ConversationScoped;
import javax.enterprise.event.Event;
import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
import javax.inject.Inject;
import javax.inject.Named;

import org.primefaces.event.SelectEvent;
import org.richfaces.cdi.push.Push;

import bo.buffalo.data.RolesRepository;
import bo.buffalo.data.UsuarioRepository;
import bo.buffalo.model.Roles;
import bo.buffalo.model.Usuario;
import bo.buffalo.service.RolesRegistration;

import com.ahosoft.utils.FacesUtil;

@Named(value="rolesV2Controller")
@ConversationScoped
public class RolesV2Controller implements Serializable{

	private static final long serialVersionUID = -1438182901991994907L;	

	public static final String PUSH_CDI_TOPIC = "pushCdi";

	@Inject
	@Push(topic = PUSH_CDI_TOPIC)
	Event<String> pushEventSucursal;
	
	@Inject Conversation conversation;

	private @Inject FacesContext facesContext;

	private @Inject UsuarioRepository usuarioRepository;
	private @Inject RolesRepository rolesRepository;

	private @Inject RolesRegistration rolesRegistration;

	private Usuario usuarioSistema;
	private boolean nuevo; /*Control para renderizar la vista*/
	private boolean modificar; 

	//OBJECT
	private Roles newRoles;
	private Roles selectedRoles;

	//
	private String tituloPanel;

	//LIST
	private List<Roles> listaRoles = new ArrayList<Roles>();


	@PostConstruct
	public void init(){
		beginConversation();
		setUsuarioSistema(usuarioRepository.findByLogin(FacesUtil.getUserSession()));
		listaRoles = rolesRepository.findAllOrderedByName();
		this.nuevo=false;
	}

	public void accionNuevoRol(){
		this.newRoles= new Roles();
		this.tituloPanel = "Registrar Roles";
		this.selectedRoles=new Roles();
		this.modificar=false;
		this.nuevo=true;
	}

	public void accionesCancelarRol(){
		this.newRoles= new Roles();
		this.selectedRoles=new Roles();
		this.nuevo=false;
		this.modificar = false;
	}

	public void accionModificarRol(){
		if(this.newRoles.getId()!=null){
			this.nuevo=true;
			this.modificar=true;
		}else{
			FacesUtil.warmMessage("Seleccione un Rol");
		}
	}

	public void accionesEliminarRol(){
		if(this.newRoles.getId()!=null){
			FacesUtil.showDialog("id_confirm_delete");
		}else{
			FacesUtil.warmMessage("Seleccione un Rol");
		}
	}


	public void registrarRoles(){
		try {
			System.out.println("Ingreso a register...newRoles.");
			rolesRegistration.register(newRoles);

			FacesMessage m = new FacesMessage(FacesMessage.SEVERITY_INFO, "Empresa Registrada!", newRoles.getName());
			facesContext.addMessage(null, m);
			init();
		} catch (Exception e) {
			e.printStackTrace();

		}
	}

	public void modificarRoles(){
		try {
			System.out.println("Ingreso a modificar Roles....");
			rolesRegistration.updated(newRoles);

			FacesMessage m = new FacesMessage(FacesMessage.SEVERITY_INFO, "Empresa Modificada!", newRoles.getName());
			facesContext.addMessage(null, m);
			init();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void limpiarCampos(){
		newRoles = new Roles();
		tituloPanel = "Registrar Roles";
		modificar = false;
		FacesUtil.resetComponent("formRegistroRoles");
	}

	//SELECT ROLES CLICK
	public void onRowSelectRolesClick(SelectEvent event) {
		try {
			Roles roles = (Roles) event.getObject();
			System.out.println("onRowSelectSucursalClick  "+ roles.getId());
			selectedRoles = roles;
			tituloPanel = "Modificar Roles";
			modificar = true;

		} catch (Exception e) {
			System.out.println("Error in onRowSelectSucursalClick: "+e.getMessage());
		}
	}

	public void beginConversation()
	{
		if (conversation.isTransient())
		{
			System.out.println("beginning conversation : " + this.conversation);
			conversation.begin();
			System.out.println("---> Init Conversation");
		}
	}

	public void endConversation()
	{
		if (!conversation.isTransient())
		{
			conversation.end();
		}
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

	public Roles getNewRoles() {
		return newRoles;
	}

	public void setNewRoles(Roles newRoles) {
		this.newRoles = newRoles;
	}

	public String getTituloPanel() {
		return tituloPanel;
	}

	public void setTituloPanel(String tituloPanel) {
		this.tituloPanel = tituloPanel;
	}

	public Roles getSelectedRoles() {
		return selectedRoles;
	}

	public void setSelectedRoles(Roles selectedRoles) {
		this.selectedRoles = selectedRoles;
	}

	public List<Roles> getListaRoles() {
		return listaRoles;
	}

	public void setListaRoles(List<Roles> listaRoles) {
		this.listaRoles = listaRoles;
	}


}
