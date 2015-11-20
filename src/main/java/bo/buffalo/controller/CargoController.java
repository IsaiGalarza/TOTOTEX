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

import bo.buffalo.data.CargoRepository;
import bo.buffalo.data.UsuarioRepository;
import bo.buffalo.model.Cargo;
import bo.buffalo.model.Usuario;
import bo.buffalo.service.CargoRegistration;

@Named(value="cargoController")
@ConversationScoped
public class CargoController implements Serializable{

	private static final long serialVersionUID = 5920847122773806675L;

	public static final String PUSH_CDI_TOPIC = "pushCdi";

    @Inject
    private FacesContext facesContext;
    
    @Inject
    private EntityManager em;
    
    @Inject 
	Conversation conversation;
    
    @Inject
    private CargoRegistration cargoRegistration;
    
    @Inject
    private CargoRepository cargoRepository;
    
    @Inject 
    UsuarioRepository usuarioRepository;
    private Usuario usuarioSession;
    
    @Inject
    @Push(topic = PUSH_CDI_TOPIC)
    Event<String> pushEventGastos;
	
    private boolean modificar = false;
    private String tituloPanel = "Registrar Cargo";
    private Cargo selectedCargo;
	private Cargo newCargo;
	
	private List<Cargo> listaCargo;

	private boolean nuevo;

    // @Named provides access the return value via the EL variable name "servicios" in the UI (e.g.
    // Facelets or JSP view)
    @Produces
    @Named
    public List<Cargo> getListaCargo() {
        return listaCargo;
    }
	
	@PostConstruct
    public void initNewCargos() {
		
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
		
		newCargo = new Cargo();
		newCargo.setEstado("AC");
		newCargo.setFechaRegistro(new Date());
		newCargo.setUsuarioRegistro(usuarioSession.getLogin());
		
		//traer todos los TipoProduct ordenados por ID
		listaCargo = cargoRepository.findActivosOrderedByFechaRegistro();
		modificar = false;
		setNuevo(false);
    }
	
	public void crearGastos(){
		try {
			newCargo = new Cargo();
			newCargo.setEstado("AC");
			newCargo.setFechaRegistro(new Date());
			newCargo.setUsuarioRegistro(usuarioSession.getLogin());
			
			//tituloPanel 
			tituloPanel = "Registrar Cargo";
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

	// SELECT CARGO CLICK
	public void onRowSelectCargoClick(SelectEvent event) {
		try {
			Cargo cargo = (Cargo) event.getObject();
			System.out.println("onRowSelectCargoClick  " + cargo.getId());
			selectedCargo = cargo;
			newCargo = em.find(Cargo.class, cargo.getId());
			newCargo.setFechaRegistro(new Date());
			newCargo.setUsuarioRegistro(usuarioSession.getLogin());

			tituloPanel = "Modificar Cargo";
			modificar = true;
			setNuevo(false);

		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
			System.out.println("Error in onRowSelectCargoClick: "
					+ e.getMessage());
		}
	}
	
	public void registrar(){
        try {
        	System.out.println("Ingreso a registrar: "+newCargo.getId());
        	cargoRegistration.register(newCargo);
            FacesMessage m = new FacesMessage(FacesMessage.SEVERITY_INFO, "Cargo Registrado!", newCargo.getNombre());
            facesContext.addMessage(null, m);
            initNewCargos();
            
        } catch (Exception e) {
            String errorMessage = getRootErrorMessage(e);
            FacesMessage m = new FacesMessage(FacesMessage.SEVERITY_ERROR, errorMessage, "Registro Incorrecto.");
            facesContext.addMessage(null, m);
        }
    }
	
	public void modificar(){
        try {
        	System.out.println("Ingreso a modificar: "+newCargo.getId());
        	cargoRegistration.updated(newCargo);
            FacesMessage m = new FacesMessage(FacesMessage.SEVERITY_INFO, "Cargo Modificado!", newCargo.getNombre());
            facesContext.addMessage(null, m);
            initNewCargos();
            
        } catch (Exception e) {
            String errorMessage = getRootErrorMessage(e);
            FacesMessage m = new FacesMessage(FacesMessage.SEVERITY_ERROR, errorMessage, "Modificado Incorrecto.");
            facesContext.addMessage(null, m);
        }
    }
	
	public void eliminar(){
        try {
        	System.out.println("Ingreso a eliminar: "+newCargo.getId());
        	cargoRegistration.remover(newCargo);
            FacesMessage m = new FacesMessage(FacesMessage.SEVERITY_INFO, "Cargo Borrado!", newCargo.getNombre());
            facesContext.addMessage(null, m);
            initNewCargos();
            
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

	public boolean isNuevo() {
		return nuevo;
	}

	public void setNuevo(boolean nuevo) {
		this.nuevo = nuevo;
	}

	public Cargo getNewCargo() {
		return newCargo;
	}

	public void setNewCargo(Cargo newCargo) {
		this.newCargo = newCargo;
	}

	public Cargo getSelectedCargo() {
		return selectedCargo;
	}

	public void setSelectedCargo(Cargo selectedCargo) {
		this.selectedCargo = selectedCargo;
	}
	
	
}
