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

import bo.buffalo.data.GastosRepository;
import bo.buffalo.data.UsuarioRepository;
import bo.buffalo.model.Gastos;
import bo.buffalo.model.Usuario;
import bo.buffalo.service.GastosRegistration;

@Named(value="gastosController")
@ConversationScoped
public class GastosController implements Serializable{

	private static final long serialVersionUID = 5920847122773806675L;

	public static final String PUSH_CDI_TOPIC = "pushCdi";

    @Inject
    private FacesContext facesContext;
    
    @Inject
    private EntityManager em;
    
    @Inject 
	Conversation conversation;
    
    @Inject
    private GastosRegistration gastosRegistration;
    
    @Inject
    private GastosRepository gastosRepository;
    
    @Inject 
    UsuarioRepository usuarioRepository;
    private Usuario usuarioSession;
    
    @Inject
    @Push(topic = PUSH_CDI_TOPIC)
    Event<String> pushEventGastos;
	
    private boolean modificar = false;
    private String tituloPanel = "Registrar Gastos";
    private Gastos selectedGastos;
	private Gastos newGastos;
	
	private List<Gastos> listaGastos;

	private boolean nuevo;

    // @Named provides access the return value via the EL variable name "servicios" in the UI (e.g.
    // Facelets or JSP view)
    @Produces
    @Named
    public List<Gastos> getListaGastos() {
        return listaGastos;
    }
	
	@PostConstruct
    public void initNewGastos() {
		
		// initConversation();
    	beginConversation();
    	
    	HttpServletRequest request = (HttpServletRequest) facesContext
				.getExternalContext().getRequest();
		System.out.println("init Gastos*********************************");
		System.out.println("request.getClass().getName():"
				+ request.getClass().getName());
		System.out.println("isVentas:" + request.isUserInRole("ventas"));
		System.out.println("remoteUser:" + request.getRemoteUser());
		System.out.println("userPrincipalName:"
				+ (request.getUserPrincipal() == null ? "null" : request
						.getUserPrincipal().getName()));

		usuarioSession = usuarioRepository.findByLogin(request
				.getUserPrincipal().getName());
		
		newGastos = new Gastos();
		newGastos.setEstado("AC");
		newGastos.setFechaRegistro(new Date());
		newGastos.setUsuarioRegistro(usuarioSession.getLogin());
		
		//traer todos los TipoProduct ordenados por ID
		listaGastos = gastosRepository.findAllOrderedByID();
		modificar = false;
		setNuevo(false);
    }
	
	public void crearGastos(){
		try {
			newGastos = new Gastos();
			newGastos.setEstado("AC");
			newGastos.setFechaRegistro(new Date());
			newGastos.setUsuarioRegistro(usuarioSession.getLogin());
			
			//tituloPanel 
			tituloPanel = "Registrar Gastos";
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
	public void onRowSelectGastosClick(SelectEvent event) {
		try {
			Gastos gastos = (Gastos) event.getObject();
			System.out.println("onRowSelectGastosClick  " + gastos.getId());
			selectedGastos = gastos;
			newGastos = em.find(Gastos.class, gastos.getId());
			newGastos.setFechaRegistro(new Date());
			newGastos.setUsuarioRegistro(usuarioSession.getLogin());

			tituloPanel = "Modificar Gastos";
			modificar = true;
			setNuevo(false);

		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
			System.out.println("Error in onRowSelectGastosClick: "
					+ e.getMessage());
		}
	}
	
	// SELECT TIPO PRODUCTO CLICK
	public void onRowSelectGastosDBLClick(SelectEvent event) {
		try {
			Gastos gastos = (Gastos) event.getObject();
			System.out.println("onRowSelectGastosClick  " + gastos.getId());
			selectedGastos = gastos;
			newGastos = em.find(Gastos.class, gastos.getId());
			newGastos.setFechaRegistro(new Date());
			newGastos.setUsuarioRegistro(usuarioSession.getLogin());

			tituloPanel = "Modificar Gastos";
			modificar = true;
			setNuevo(true);

		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
			System.out.println("Error in onRowSelectGastosClick: "
					+ e.getMessage());
		}
	}
	
	public void registrarGastos(){
        try {
        	System.out.println("Ingreso a registrarGastos: "+newGastos.getId());
        	gastosRegistration.register(newGastos);
            FacesMessage m = new FacesMessage(FacesMessage.SEVERITY_INFO, "Registered!", "Registration successful");
            facesContext.addMessage(null, m);
            pushEventGastos.fire(String.format("Nuevo Gastos Registrado: %s (id: %d)", newGastos.getDescripcion(), newGastos.getId()));
            initNewGastos();
        } catch (Exception e) {
            String errorMessage = getRootErrorMessage(e);
            FacesMessage m = new FacesMessage(FacesMessage.SEVERITY_ERROR, errorMessage, "Registro Incorrecto.");
            facesContext.addMessage(null, m);
        }
    }
	
	public void modificarGastos(){
        try {
        	System.out.println("Ingreso a modificarGastos: "+newGastos.getId());
        	gastosRegistration.updated(newGastos);
            FacesMessage m = new FacesMessage(FacesMessage.SEVERITY_INFO, "Modificado!", "Modificado successful");
            facesContext.addMessage(null, m);
            pushEventGastos.fire(String.format("Gastos Modificado: %s (id: %d)", newGastos.getDescripcion(), newGastos.getId()));
            initNewGastos();
            
        } catch (Exception e) {
            String errorMessage = getRootErrorMessage(e);
            FacesMessage m = new FacesMessage(FacesMessage.SEVERITY_ERROR, errorMessage, "Modificado Incorrecto.");
            facesContext.addMessage(null, m);
        }
    }
	
	public void eliminarGastos(){
        try {
        	System.out.println("Ingreso a eliminarGastos: "+newGastos.getId());
        	gastosRegistration.remover(newGastos);
            FacesMessage m = new FacesMessage(FacesMessage.SEVERITY_INFO, "Borrado!", "Borrado successful");
            facesContext.addMessage(null, m);
            pushEventGastos.fire(String.format("Servicio Borrado: %s (id: %d)", newGastos.getDescripcion(), newGastos.getId()));
            initNewGastos();
            
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

	public Gastos getNewGastos() {
		return newGastos;
	}

	public void setNewGastos(Gastos newGastos) {
		this.newGastos = newGastos;
	}

	public Gastos getSelectedGastos() {
		return selectedGastos;
	}

	public void setSelectedGastos(Gastos selectedGastos) {
		this.selectedGastos = selectedGastos;
	}

	public boolean isNuevo() {
		return nuevo;
	}

	public void setNuevo(boolean nuevo) {
		this.nuevo = nuevo;
	}
	
	
}
