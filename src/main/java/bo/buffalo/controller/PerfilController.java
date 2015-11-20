package bo.buffalo.controller;

import java.io.Serializable;

import javax.annotation.PostConstruct;
import javax.enterprise.context.Conversation;
import javax.enterprise.context.ConversationScoped;
import javax.enterprise.event.Event;
import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
import javax.inject.Inject;
import javax.inject.Named;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.primefaces.model.UploadedFile;
import org.richfaces.cdi.push.Push;

import bo.buffalo.data.UsuarioRepository;
import bo.buffalo.model.Usuario;
import bo.buffalo.service.UsuarioRegistration;

@Named(value="perfilController")
@ConversationScoped
public class PerfilController implements Serializable{

	private static final long serialVersionUID = 5920847122773806675L;

	public static final String PUSH_CDI_TOPIC = "pushCdi";

	@Inject
	private FacesContext facesContext;


	@Inject 
	Conversation conversation;

	@Inject
	private UsuarioRegistration usuarioRegistration;

	@Inject 
	UsuarioRepository usuarioRepository;
	private Usuario usuarioSession;

	@Inject
	@Push(topic = PUSH_CDI_TOPIC)
	Event<String> pushEventSucursal;

	private String tituloPanel = "Perfil de Usuario";

	private Usuario newUsuario;

	private UploadedFile file;

	private boolean modificar;

	@PostConstruct
	public void initNewPerfil() {

		// initConversation();
		beginConversation();

		HttpServletRequest request = (HttpServletRequest) facesContext
				.getExternalContext().getRequest();
		System.out.println("init initNewPerfil*********************************");
		System.out.println("request.getClass().getName():"
				+ request.getClass().getName());
		System.out.println("remoteUser:" + request.getRemoteUser());
		System.out.println("userPrincipalName:"
				+ (request.getUserPrincipal() == null ? "null" : request
						.getUserPrincipal().getName()));

		usuarioSession = usuarioRepository.findByLogin(request
				.getUserPrincipal().getName());
		System.out.println("Sucursal Usuario: "
				+ usuarioSession.getSucursal().getNombre());
		modificar = true;

	}

	public void beginConversation() {

		if (conversation.isTransient()) {
			System.out.println("beginning conversation : " + this.conversation);
			conversation.begin();
			System.out.println("---> Init Conversation");
		}
	}

	public void endConversation() {
		if (!conversation.isTransient()) {
			conversation.end();
		}
	}

	public void modificarUsuario(){
		try {
			System.out.println("Ingreso a modificarUsuario: "+newUsuario.getId());
			//modificar

			FacesMessage m = new FacesMessage(FacesMessage.SEVERITY_INFO, "Modificado!", "Modificado successful");
			facesContext.addMessage(null, m);
			pushEventSucursal.fire(String.format("Usuario Modificado: %s (id: %d)", newUsuario.getName(), newUsuario.getId()));
			initNewPerfil();

		} catch (Exception e) {
			String errorMessage = getRootErrorMessage(e);
			FacesMessage m = new FacesMessage(FacesMessage.SEVERITY_ERROR, errorMessage, "Modificado Incorrecto.");
			facesContext.addMessage(null, m);
		}
	}

	private String getRootErrorMessage(Exception e) {
		// Default to general error message that registration failed.
		String errorMessage = "Registration failed. See server log for more information";
		if (e == null) {
			// This shouldn't happen, but return the default messages
			return errorMessage;
		}

		// Start with the exception and recurse to find the root cause
		Throwable t = e;
		while (t != null) {
			// Get the message from the Throwable class instance
			errorMessage = t.getLocalizedMessage();
			t = t.getCause();
		}
		// This is the root cause message
		return errorMessage;
	}

	public String getURLServletImage(){
		HttpServletRequest request = (HttpServletRequest) facesContext.getExternalContext().getRequest();  
		String urlPath = request.getRequestURL().toString();
		urlPath = urlPath.substring(0, urlPath.length() - request.getRequestURI().length()) + request.getContextPath() + "/";
		System.out.println("urlPath >> "+urlPath);
		return urlPath;
	}

	public void upload() {
		setModificar(true);
		System.out.println("upload()  file:"+file);
		if(file != null) {
			usuarioSession.setFotoPerfil(file.getContents());
			usuarioRegistration.updateUsuario(usuarioSession);
			setImageUserSession();
			FacesMessage m = new FacesMessage(FacesMessage.SEVERITY_INFO, "Modificado!", "Modificado successful");
			facesContext.addMessage(null, m);
//			pushEventSucursal.fire(String.format("Usuario Modificado: %s (id: %d)", newUsuario.getName(), newUsuario.getId()));
		}
	}

	public void setImageUserSession() {
		//cargar foto del usuario
		try{
			HttpSession session = (HttpSession) FacesContext.getCurrentInstance().getExternalContext().getSession(false);
			byte[] image = getUsuarioSession().getFotoPerfil();
			session.setAttribute("imageUser", image);

		}catch(Exception e){
			System.out.println("setImageUserSession() - Error: "+e.getMessage());
		}
	}
	
	//get and set
	public String getTituloPanel() {
		return tituloPanel;
	}

	public void setTituloPanel(String tituloPanel) {
		this.tituloPanel = tituloPanel;
	}

	public Usuario getNewUsuario() {
		return newUsuario;
	}

	public void setNewUsuario(Usuario newUsuario) {
		this.newUsuario = newUsuario;
	}

	public Usuario getUsuarioSession() {
		return usuarioSession;
	}

	public void setUsuarioSession(Usuario usuarioSession) {
		this.usuarioSession = usuarioSession;
	}

	public UploadedFile getFile() {
		return file;
	}

	public void setFile(UploadedFile file) {
		this.file = file;
	}

	public boolean isModificar() {
		return modificar;
	}

	public void setModificar(boolean modificar) {
		this.modificar = modificar;
	}

	public void cambiarModificar(){
		System.out.println("cambiarModificar()");
		setModificar(false);
	}

}
