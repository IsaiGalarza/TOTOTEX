package bo.buffalo.controller;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.enterprise.context.Conversation;
import javax.enterprise.context.ConversationScoped;
import javax.enterprise.event.Event;
import javax.enterprise.inject.Produces;
import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
import javax.inject.Inject;
import javax.inject.Named;
import javax.persistence.EntityManager;
import javax.servlet.http.HttpServletRequest;

import org.primefaces.event.SelectEvent;
import org.richfaces.cdi.push.Push;

import bo.buffalo.data.CiudadRepository;
import bo.buffalo.data.UsuarioRepository;
import bo.buffalo.model.Ciudad;
import bo.buffalo.model.Usuario;
import bo.buffalo.service.CiudadRegistration;

@Named(value="ciudadController")
@ConversationScoped
public class CiudadController implements Serializable{

	private static final long serialVersionUID = 5920847122773806675L;

	public static final String PUSH_CDI_TOPIC = "pushCdi";

    @Inject
    private FacesContext facesContext;
    
    @Inject
    private EntityManager em;
    
    @Inject 
	Conversation conversation;
    
    @Inject
    private CiudadRegistration ciudadRegistration;
    
    @Inject
    private CiudadRepository ciudadRepository;
    
    @Inject 
    UsuarioRepository usuarioRepository;
    private Usuario usuarioSession;
    
    @Inject
    @Push(topic = PUSH_CDI_TOPIC)
    Event<String> pushEventSucursal;
	
    private boolean modificar = false;
    private String tituloPanel = "Registrar Ciudad";
    private Ciudad selectedCiudad;
	private Ciudad newCiudad;
	
	private List<Ciudad> listaCiudad;

	private boolean nuevo;

    // @Named provides access the return value via the EL variable name "servicios" in the UI (e.g.
    // Facelets or JSP view)
    @Produces
    @Named
    public List<Ciudad> getListaCiudad() {
        return listaCiudad;
    }
	
	@PostConstruct
    public void initNewCiudad() {
		
		// initConversation();
    	beginConversation();
    	
    	HttpServletRequest request = (HttpServletRequest) facesContext
				.getExternalContext().getRequest();
		System.out.println("init Tipo Producto*********************************");
		System.out.println("request.getClass().getName():"
				+ request.getClass().getName());
		System.out.println("isVentas:" + request.isUserInRole("ventas"));
		System.out.println("remoteUser:" + request.getRemoteUser());
		System.out.println("userPrincipalName:"
				+ (request.getUserPrincipal() == null ? "null" : request
						.getUserPrincipal().getName()));

		usuarioSession = usuarioRepository.findByLogin(request
				.getUserPrincipal().getName());
		System.out.println("Sucursal Usuario: "
				+ usuarioSession.getSucursal().getNombre());
		
		newCiudad = new Ciudad();
		newCiudad.setEstado("AC");
		newCiudad.setFechaRegistro(new Date());
		newCiudad.setUsuarioRegistro(usuarioSession.getLogin());
		
		//traer todos los TipoProduct ordenados por ID
		listaCiudad = ciudadRepository.findAllOrderedByID();
		modificar = false;
		setNuevo(false);
    }
	
	public void crearCiudad(){
		try {
			newCiudad = new Ciudad();
			newCiudad.setEstado("AC");
			newCiudad.setFechaRegistro(new Date());
			newCiudad.setUsuarioRegistro(usuarioSession.getLogin());
			
			//tituloPanel 
			tituloPanel = "Registrar Ciudad";
			setNuevo(true);
			modificar= false;
		} catch (Exception e) {
			// TODO: handle exception
		}
	}
	
	public void modificarEstado(){
		modificar=true;
		setNuevo(true);
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

	// SELECT TIPO PRODUCTO CLICK
	public void onRowSelectCiudadClick(SelectEvent event) {
		try {
			Ciudad ciudad = (Ciudad) event.getObject();
			System.out.println("onRowSelectCiudadClick  " + ciudad.getId());
			selectedCiudad = ciudad;
			newCiudad = em.find(Ciudad.class, ciudad.getId());
			newCiudad.setFechaRegistro(new Date());
			newCiudad.setUsuarioRegistro(usuarioSession.getLogin());

			tituloPanel = "Modificar Ciudad";
			modificar = true;
			setNuevo(false);

		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
			System.out.println("Error in onRowSelectCiudadClick: "
					+ e.getMessage());
		}
	}
	
	// SELECT TIPO PRODUCTO CLICK
	public void onRowSelectCiudadDBLClick(SelectEvent event) {
		try {
			Ciudad ciudad = (Ciudad) event.getObject();
			System.out.println("onRowSelectCiudadClick  " + ciudad.getId());
			selectedCiudad = ciudad;
			newCiudad = em.find(Ciudad.class, ciudad.getId());
			newCiudad.setFechaRegistro(new Date());
			newCiudad.setUsuarioRegistro(usuarioSession.getLogin());

			tituloPanel = "Modificar Ciudad";
			modificar = true;
			setNuevo(true);

		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
			System.out.println("Error in onRowSelectCiudadClick: "
					+ e.getMessage());
		}
	}
	
	public void registrarCiudad(){
        try {
        	System.out.println("Ingreso a registrarCiudad: "+newCiudad.getId());
        	ciudadRegistration.register(newCiudad);
            FacesMessage m = new FacesMessage(FacesMessage.SEVERITY_INFO, "Registered!", "Registration successful");
            facesContext.addMessage(null, m);
            pushEventSucursal.fire(String.format("Nuevo Tipo Producto Registrado: %s (id: %d)", newCiudad.getNombre(), newCiudad.getId()));
            initNewCiudad();
        } catch (Exception e) {
            String errorMessage = getRootErrorMessage(e);
            FacesMessage m = new FacesMessage(FacesMessage.SEVERITY_ERROR, errorMessage, "Registro Incorrecto.");
            facesContext.addMessage(null, m);
        }
    }
	
	public void modificarCiudad(){
        try {
        	System.out.println("Ingreso a modificarCiudad: "+newCiudad.getId());
        	ciudadRegistration.updated(newCiudad);
            FacesMessage m = new FacesMessage(FacesMessage.SEVERITY_INFO, "Modificado!", "Modificado successful");
            facesContext.addMessage(null, m);
            pushEventSucursal.fire(String.format("Tipo Producto Modificado: %s (id: %d)", newCiudad.getNombre(), newCiudad.getId()));
            initNewCiudad();
            
        } catch (Exception e) {
            String errorMessage = getRootErrorMessage(e);
            FacesMessage m = new FacesMessage(FacesMessage.SEVERITY_ERROR, errorMessage, "Modificado Incorrecto.");
            facesContext.addMessage(null, m);
        }
    }
	
	public void eliminarCiudad(){
        try {
        	System.out.println("Ingreso a eliminarCiudad: "+newCiudad.getId());
        	ciudadRegistration.remover(newCiudad);
            FacesMessage m = new FacesMessage(FacesMessage.SEVERITY_INFO, "Borrado!", "Borrado successful");
            facesContext.addMessage(null, m);
            pushEventSucursal.fire(String.format("Servicio Borrado: %s (id: %d)", newCiudad.getNombre(), newCiudad.getId()));
            initNewCiudad();
            
        } catch (Exception e) {
            String errorMessage = getRootErrorMessage(e);
            FacesMessage m = new FacesMessage(FacesMessage.SEVERITY_ERROR, errorMessage, "Borrado Incorrecto.");
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
	
	
	//get and set
	public String getTituloPanel() {
		return tituloPanel;
	}

	public void setTituloPanel(String tituloPanel) {
		this.tituloPanel = tituloPanel;
	}

	public boolean isModificar() {
		return modificar;
	}

	public void setModificar(boolean modificar) {
		this.modificar = modificar;
	}

	public Ciudad getNewCiudad() {
		return newCiudad;
	}

	public void setNewCiudad(Ciudad newCiudad) {
		this.newCiudad = newCiudad;
	}

	public Ciudad getSelectedCiudad() {
		return selectedCiudad;
	}

	public void setSelectedCiudad(Ciudad selectedCiudad) {
		this.selectedCiudad = selectedCiudad;
	}

	public boolean isNuevo() {
		return nuevo;
	}

	public void setNuevo(boolean nuevo) {
		this.nuevo = nuevo;
	}
	
	
}
