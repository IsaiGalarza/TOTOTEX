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

import bo.buffalo.data.UnidadMedidaRepository;
import bo.buffalo.data.UsuarioRepository;
import bo.buffalo.model.UnidadMedida;
import bo.buffalo.model.Usuario;
import bo.buffalo.service.UnidadMedidaRegistration;

@Named(value="unidadesMedidaController")
@ConversationScoped
public class UnidadesMedidaController implements Serializable{

	private static final long serialVersionUID = -5497426699770430529L;

	public static final String PUSH_CDI_TOPIC = "pushCdi";

    @Inject
    private FacesContext facesContext;
    
    @Inject
    private EntityManager em;
    
    @Inject 
	Conversation conversation;
    
    @Inject
    private UnidadMedidaRegistration unidadMedidaRegistration;
    
    @Inject
    private UnidadMedidaRepository unidadMedidaRepository;
    
    @Inject 
    UsuarioRepository usuarioRepository;
    private Usuario usuarioSession;
    
    @Inject
    @Push(topic = PUSH_CDI_TOPIC)
    Event<String> pushEventSucursal;
	
    private boolean modificar = false;
    private String tituloPanel = "Registrar Unidad Medida";
    private UnidadMedida selectedUnidadMedida;
	private UnidadMedida newUnidadMedida;
	

	private boolean nuevo=false;
	private List<UnidadMedida> listaUnidadMedida;

    // @Named provides access the return value via the EL variable name "servicios" in the UI (e.g.
    // Facelets or JSP view)
    @Produces
    @Named
    public List<UnidadMedida> getListaUnidadMedida() {
        return listaUnidadMedida;
    }
	
	@PostConstruct
    public void initNewUnidadMedida() {
		
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
		
		
		
		//traer todos los TipoProduct ordenados por ID
		listaUnidadMedida = unidadMedidaRepository.findAllOrderedByID();
		modificar = false;
		nuevo=false;
    }
	
	
	public void crearUnidad(){
		try {
			newUnidadMedida = new UnidadMedida();
			newUnidadMedida.setEstado("AC");
			newUnidadMedida.setFechaRegistro(new Date());
			newUnidadMedida.setUsuarioRegistro(usuarioSession.getLogin());
			
			//tituloPanel 
			tituloPanel = "Registrar Area";
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
	public void onRowSelectUnidadMedidaClick(SelectEvent event) {
		try {
			UnidadMedida unidadMedida = (UnidadMedida) event.getObject();
			System.out.println("onRowSelectUnidadMedidaClick  " + unidadMedida.getId());
			selectedUnidadMedida = unidadMedida;
			newUnidadMedida = em.find(UnidadMedida.class, unidadMedida.getId());
			newUnidadMedida.setFechaRegistro(new Date());
			newUnidadMedida.setUsuarioRegistro(usuarioSession.getLogin());

			tituloPanel = "Modificar Unidad Medida";
			modificar = true;
			setNuevo(false);

		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
			System.out.println("Error in onRowSelectUnidadMedidaClick: "
					+ e.getMessage());
		}
	}
	

	// SELECT TIPO PRODUCTO CLICK
	public void onRowSelectUnidadMedidaBDLClick(SelectEvent event) {
		try {
			UnidadMedida unidadMedida = (UnidadMedida) event.getObject();
			System.out.println("onRowSelectUnidadMedidaBDLClick  " + unidadMedida.getId());
			selectedUnidadMedida = unidadMedida;
			newUnidadMedida = em.find(UnidadMedida.class, unidadMedida.getId());
			newUnidadMedida.setFechaRegistro(new Date());
			newUnidadMedida.setUsuarioRegistro(usuarioSession.getLogin());

			tituloPanel = "Modificar Unidad Medida";
			modificar = true;
			setNuevo(true);

		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
			System.out.println("Error in onRowSelectUnidadMedidaBDLClick: "
					+ e.getMessage());
		}
	}
	
	public void registrarUnidadMedida(){
        try {
        	System.out.println("Ingreso a registrarUnidadMedida: "+newUnidadMedida.getId());
        	unidadMedidaRegistration.register(newUnidadMedida);
            FacesMessage m = new FacesMessage(FacesMessage.SEVERITY_INFO, "Registered!", "Registration successful");
            facesContext.addMessage(null, m);
            pushEventSucursal.fire(String.format("Nueva Unidad Medida Registrado: %s (id: %d)", newUnidadMedida.getDescripcion(), newUnidadMedida.getId()));
            initNewUnidadMedida();
        } catch (Exception e) {
            String errorMessage = getRootErrorMessage(e);
            FacesMessage m = new FacesMessage(FacesMessage.SEVERITY_ERROR, errorMessage, "Registro Incorrecto.");
            facesContext.addMessage(null, m);
        }
    }
	
	public void modificarUnidadMedida(){
        try {
        	System.out.println("Ingreso a modificarTipoProducto: "+newUnidadMedida.getId());
        	unidadMedidaRegistration.updated(newUnidadMedida);
            FacesMessage m = new FacesMessage(FacesMessage.SEVERITY_INFO, "Modificado!", "Modificado successful");
            facesContext.addMessage(null, m);
            pushEventSucursal.fire(String.format("Unidad Medida Modificada: %s (id: %d)", newUnidadMedida.getDescripcion(), newUnidadMedida.getId()));
            initNewUnidadMedida();
            
        } catch (Exception e) {
            String errorMessage = getRootErrorMessage(e);
            FacesMessage m = new FacesMessage(FacesMessage.SEVERITY_ERROR, errorMessage, "Modificado Incorrecto.");
            facesContext.addMessage(null, m);
        }
    }
	
	public void eliminarUnidadMedida(){
        try {
        	System.out.println("Ingreso a eliminarUnidadMedida: "+newUnidadMedida.getId());
        	unidadMedidaRegistration.remover(newUnidadMedida);
            FacesMessage m = new FacesMessage(FacesMessage.SEVERITY_INFO, "Borrado!", "Borrado successful");
            facesContext.addMessage(null, m);
            pushEventSucursal.fire(String.format("Unidad de Medida Borrada: %s (id: %d)", newUnidadMedida.getDescripcion(), newUnidadMedida.getId()));
            initNewUnidadMedida();
            
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

	public UnidadMedida getSelectedUnidadMedida() {
		return selectedUnidadMedida;
	}

	public void setSelectedUnidadMedida(UnidadMedida selectedUnidadMedida) {
		this.selectedUnidadMedida = selectedUnidadMedida;
	}

	public UnidadMedida getNewUnidadMedida() {
		return newUnidadMedida;
	}

	public void setNewUnidadMedida(UnidadMedida newUnidadMedida) {
		this.newUnidadMedida = newUnidadMedida;
	}

	public boolean isNuevo() {
		return nuevo;
	}

	public void setNuevo(boolean nuevo) {
		this.nuevo = nuevo;
	}
	
	
}
