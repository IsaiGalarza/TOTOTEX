package bo.buffalo.controller;

import java.io.Serializable;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.enterprise.context.Conversation;
import javax.enterprise.context.ConversationScoped;
import javax.enterprise.event.Event;
import javax.faces.context.FacesContext;
import javax.inject.Inject;
import javax.inject.Named;

import org.richfaces.cdi.push.Push;

import bo.buffalo.data.UsuarioRepository;
import bo.buffalo.model.Usuario;
import bo.buffalo.model.UsuarioRol;
import bo.buffalo.util.SessionMain;

import com.ahosoft.utils.FacesUtil;

/**
 * 
 * @author mauriciobejaranorivera
 *
 */
@Named(value="fichaTecnicaIndexController")
@ConversationScoped
public class FichaTecnicaIndexController implements Serializable{

	private static final long serialVersionUID = -92891555476876011L;

	public static final String PUSH_CDI_TOPIC = "pushCdi";

	@Inject
	@Push(topic = PUSH_CDI_TOPIC)
	Event<String> pushEventSucursal;

	@Inject
	Conversation conversation;

	//REPOSITORY
	private @Inject UsuarioRepository usuarioRepository;

	//OBJECT
	
	//ESTADO
	private boolean nuevo; /*Control para renderizar la vista*/
	private boolean modificar; 
	private boolean administrador;
	private boolean detalle;
	
	//VAR
	private String titulo;
	private String urlVista;
	private String password;

	//SESSION
	private @Inject SessionMain sessionMain; //variable del login
	private Usuario usuarioSistema;
	private String usuario;

	@PostConstruct
	public void init(){
		usuarioSistema= usuarioRepository.findByLogin(FacesUtil.getUserSession());
		administrador=esAdministrador();
		newEntity();
	}

	private void newEntity(){
		nuevo=false;
		modificar=false;
		detalle=false;
		titulo="Registrar Ficha Tecnica";
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

	/* method useful */
	private boolean esAdministrador(){
		boolean resultado=false;
		try {
			List<UsuarioRol> ur=usuarioRepository.buscarUsuarioRol(usuarioSistema);
			if(!ur.isEmpty()){
				for (UsuarioRol usuarioRol : ur) {
					if(usuarioRol.getRoles().getName().equals("administracion")){
						resultado=true;
					}
				}
			}
		} catch (Exception e) {
			System.out.println("Error verifiacar si es administrador: "+e.getMessage());
		}
		return resultado;
	}
	
	public void irAFichaTecnica(String clasePrenda){
		System.out.println("irAFichaTecnica(clasePrenda: "+clasePrenda);
		//settear atributo
		sessionMain.setAttributeSession("pClasePrenda", clasePrenda);
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

	public String getTitulo() {
		return titulo;
	}

	public void setTitulo(String titulo) {
		this.titulo = titulo;
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

}
