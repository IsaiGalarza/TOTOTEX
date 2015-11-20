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

import bo.buffalo.data.PresentacionRepository;
import bo.buffalo.data.UsuarioRepository;
import bo.buffalo.model.Presentacion;
import bo.buffalo.model.Usuario;
import bo.buffalo.service.PresentacionRegistration;

@Named(value="presentacionController")
@ConversationScoped
public class PresentacionController implements Serializable{

	private static final long serialVersionUID = 7725116215964890210L;

	public static final String PUSH_CDI_TOPIC = "pushCdi";

    @Inject
    private FacesContext facesContext;
    
    @Inject
    private EntityManager em;
    
    @Inject 
	Conversation conversation;
    
    @Inject
    private PresentacionRegistration presentacionRegistration;
    
    @Inject
    private PresentacionRepository presentacionRepository;
    
    @Inject 
    UsuarioRepository usuarioRepository;
    private Usuario usuarioSession;
    
    @Inject
    @Push(topic = PUSH_CDI_TOPIC)
    Event<String> pushEventSucursal;
	
    private boolean modificar = false;
    private String tituloPanel = "Registrar Presentacion";
    private Presentacion selectedPresentacion;
	private Presentacion newPresentacion;
	
	private List<Presentacion> listaPresentacion;

    // @Named provides access the return value via the EL variable name "servicios" in the UI (e.g.
    // Facelets or JSP view)
    @Produces
    @Named
    public List<Presentacion> getListaPresentacion() {
        return listaPresentacion;
    }
	
	@PostConstruct
    public void initNewPresentacion() {
		
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
		
		newPresentacion = new Presentacion();
		newPresentacion.setEstado("AC");
		newPresentacion.setFechaRegistro(new Date());
		newPresentacion.setUsuarioRegistro(usuarioSession.getLogin());
		
		//tituloPanel 
		tituloPanel = "Registrar Presentacion";
		
		//traer todos las presentaciones ordenados por ID Desc
		listaPresentacion = presentacionRepository.findAllOrderedByID();
		modificar = false;
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
	
	public void updatedCantidad(){
		if(!newPresentacion.isShowCantidad()){
			newPresentacion.setCantidadCaja(0);
		}
	}

	// SELECT PRESENTACION CLICK
	public void onRowSelectPresentacionClick(SelectEvent event) {
		try {
			Presentacion presentacion = (Presentacion) event.getObject();
			System.out.println("onRowSelectPresentacionClick  " + presentacion.getId());
			selectedPresentacion = presentacion;
			newPresentacion = em.find(Presentacion.class, presentacion.getId());
			newPresentacion.setFechaRegistro(new Date());
			newPresentacion.setUsuarioRegistro(usuarioSession.getLogin());

			tituloPanel = "Modificar Presentacion";
			modificar = true;

		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
			System.out.println("Error in onRowSelectPresentacionClick: "
					+ e.getMessage());
		}
	}
	
	public void registrarPresentacion(){
        try {
        	System.out.println("Ingreso a registrarPresentacion: "+newPresentacion.getId());
        	presentacionRegistration.register(newPresentacion);
            FacesMessage m = new FacesMessage(FacesMessage.SEVERITY_INFO, "Registered!", "Registration successful");
            facesContext.addMessage(null, m);
            pushEventSucursal.fire(String.format("Nueva Presentacion Registrada: %s (id: %d)", newPresentacion.getDescripcion(), newPresentacion.getId()));
            initNewPresentacion();
        } catch (Exception e) {
            String errorMessage = getRootErrorMessage(e);
            FacesMessage m = new FacesMessage(FacesMessage.SEVERITY_ERROR, errorMessage, "Registro Incorrecto.");
            facesContext.addMessage(null, m);
        }
    }
	
	public void modificarPresentacion(){
        try {
        	System.out.println("Ingreso a modificarPresentacion: "+newPresentacion.getId());
        	presentacionRegistration.updated(newPresentacion);
            FacesMessage m = new FacesMessage(FacesMessage.SEVERITY_INFO, "Modificado!", "Modificado successful");
            facesContext.addMessage(null, m);
            pushEventSucursal.fire(String.format("Presentacion Modificada: %s (id: %d)", newPresentacion.getDescripcion(), newPresentacion.getId()));
            initNewPresentacion();
            
        } catch (Exception e) {
            String errorMessage = getRootErrorMessage(e);
            FacesMessage m = new FacesMessage(FacesMessage.SEVERITY_ERROR, errorMessage, "Modificado Incorrecto.");
            facesContext.addMessage(null, m);
        }
    }
	
	public void eliminarPresentacion(){
        try {
        	System.out.println("Ingreso a eliminarPresentacion: "+newPresentacion.getId());
        	presentacionRegistration.remover(newPresentacion);
            FacesMessage m = new FacesMessage(FacesMessage.SEVERITY_INFO, "Borrado!", "Borrado successful");
            facesContext.addMessage(null, m);
            pushEventSucursal.fire(String.format("Presentacion Borrada: %s (id: %d)", newPresentacion.getDescripcion(), newPresentacion.getId()));
            initNewPresentacion();
            
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

	public Presentacion getSelectedPresentacion() {
		return selectedPresentacion;
	}

	public void setSelectedPresentacion(Presentacion selectedPresentacion) {
		this.selectedPresentacion = selectedPresentacion;
	}

	public Presentacion getNewPresentacion() {
		return newPresentacion;
	}

	public void setNewPresentacion(Presentacion newPresentacion) {
		this.newPresentacion = newPresentacion;
	}
	
	
}
