package bo.buffalo.controller;

import java.io.Serializable;
import java.text.SimpleDateFormat;
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

import bo.buffalo.data.ServicioRepository;
import bo.buffalo.data.UsuarioRepository;
import bo.buffalo.model.Servicio;
import bo.buffalo.model.Usuario;
import bo.buffalo.service.ServicioRegistration;

@Named(value="servicioController")
@ConversationScoped
public class ServicioController implements Serializable{

	private static final long serialVersionUID = 5841301875223608635L;
	
	public static final String PUSH_CDI_TOPIC = "pushCdi";

    @Inject
    private FacesContext facesContext;
    
    @Inject
    private EntityManager em;
    
    @Inject 
	Conversation conversation;
    
    @Inject
    private ServicioRegistration servicioRegistration;
    
    @Inject
    private ServicioRepository servicioRepository;
    
    @Inject 
    UsuarioRepository usuarioRepository;
    private Usuario usuarioSession;
    
    @Inject
    @Push(topic = PUSH_CDI_TOPIC)
    Event<String> pushEventSucursal;
	
    private boolean modificar = false;
    private String tituloPanel = "Registrar Servicio";
    private Servicio selectedServicio;
	private Servicio newServicio;
	
	private List<Servicio> servicios;

    // @Named provides access the return value via the EL variable name "servicios" in the UI (e.g.
    // Facelets or JSP view)
    @Produces
    @Named
    public List<Servicio> getServicios() {
        return servicios;
    }
	
	@PostConstruct
    public void initNewServicio() {
		
		// initConversation();
    	beginConversation();
    	
    	HttpServletRequest request = (HttpServletRequest) facesContext
				.getExternalContext().getRequest();
		System.out.println("init Servicio*********************************");
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
		
		newServicio = new Servicio();
		newServicio.setEstado("AC");
		newServicio.setFechaRegistro(new Date());
		newServicio.setUsuarioRegistro(usuarioSession.getLogin());
		
		//traer todos los servicios ordenados por nombre
		servicios = servicioRepository.findAllOrderedByID();

		modificar = false;
    }
	
	public String obtenerFecha(Date date){
    	try {
    		String DATE_FORMAT = "dd/MM/yyyy";
			SimpleDateFormat sdf = new SimpleDateFormat(DATE_FORMAT);
    		return sdf.format(date);
    		
		} catch (Exception e) {
			// TODO: handle exception
			System.out.println("Error en obtenerFecha: "+e.getMessage());
			return null;
		}
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

	// SELECT SERVICIO CLICK
	public void onRowSelectServicioClick(SelectEvent event) {
		try {
			Servicio servicio = (Servicio) event.getObject();
			System.out.println("onRowSelectServicioClick  " + servicio.getId());
			selectedServicio = servicio;
			newServicio = em.find(Servicio.class, servicio.getId());
			newServicio.setFechaRegistro(new Date());
			newServicio.setUsuarioRegistro(usuarioSession.getLogin());
	
			tituloPanel = "Modificar Servicio";
			modificar = true;

		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
			System.out.println("Error in onRowSelectUsuarioClick: "
					+ e.getMessage());
		}
	}
	
	public void registrarServicio(){
        try {
        	System.out.println("Ingreso a registrarServicio: "+newServicio.getId());
        	servicioRegistration.register(newServicio);
            FacesMessage m = new FacesMessage(FacesMessage.SEVERITY_INFO, "Servicio Registrado!", newServicio.getNombreServicio());
            facesContext.addMessage(null, m);
            
            initNewServicio();
        } catch (Exception e) {
            String errorMessage = getRootErrorMessage(e);
            FacesMessage m = new FacesMessage(FacesMessage.SEVERITY_ERROR, errorMessage, "Registro Incorrecto.");
            facesContext.addMessage(null, m);
        }
    }
	
	public void modificarServicio(){
        try {
        	System.out.println("Ingreso a modificarServicio: "+newServicio.getId());
        	servicioRegistration.updated(newServicio);
        	FacesMessage m = new FacesMessage(FacesMessage.SEVERITY_INFO, "Servicio Modificado!", newServicio.getNombreServicio());
            facesContext.addMessage(null, m);
            
            initNewServicio();
        } catch (Exception e) {
            String errorMessage = getRootErrorMessage(e);
            FacesMessage m = new FacesMessage(FacesMessage.SEVERITY_ERROR, errorMessage, "Modificado Incorrecto.");
            facesContext.addMessage(null, m);
        }
    }
	
	public void eliminarServicio(){
        try {
        	System.out.println("Ingreso a eliminarServicio: "+newServicio.getId());
        	servicioRegistration.remover(newServicio);
        	FacesMessage m = new FacesMessage(FacesMessage.SEVERITY_INFO, "Servicio Borrado!", newServicio.getNombreServicio());
            facesContext.addMessage(null, m);
            initNewServicio();
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
	public Servicio getNewServicio() {
		return newServicio;
	}

	public void setNewServicio(Servicio newServicio) {
		this.newServicio = newServicio;
	}

	public String getTituloPanel() {
		return tituloPanel;
	}

	public void setTituloPanel(String tituloPanel) {
		this.tituloPanel = tituloPanel;
	}

	public Servicio getSelectedServicio() {
		return selectedServicio;
	}

	public void setSelectedServicio(Servicio selectedServicio) {
		this.selectedServicio = selectedServicio;
	}

	public boolean isModificar() {
		return modificar;
	}

	public void setModificar(boolean modificar) {
		this.modificar = modificar;
	}

	
	
}
