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

import bo.buffalo.data.MarcaRepository;
import bo.buffalo.data.UsuarioRepository;
import bo.buffalo.model.Marca;
import bo.buffalo.model.Usuario;
import bo.buffalo.service.MarcaRegistration;

@Named(value="marcaController")
@ConversationScoped
public class MarcaController implements Serializable{


	/**
	 * 
	 */
	private static final long serialVersionUID = -8383121501816730053L;

	public static final String PUSH_CDI_TOPIC = "pushCdi";

    @Inject
    private FacesContext facesContext;
    
    @Inject
    private EntityManager em;
    
    @Inject 
	Conversation conversation;
    
    @Inject
    private MarcaRegistration MarcaRegistration;
    
    @Inject
    private MarcaRepository MarcaRepository;
    
    @Inject 
    UsuarioRepository usuarioRepository;
    private Usuario usuarioSession;
    
    @Inject
    @Push(topic = PUSH_CDI_TOPIC)
    Event<String> pushEventSucursal;
	
    private boolean modificar = false;
    private boolean nuevo=false;
    private String tituloPanel = "Registrar Marca";
    private Marca selectedMarca;
	private Marca newMarca;
	
	private List<Marca> listaMarca;

    // @Named provides access the return value via the EL variable name "Marca" in the UI (e.g.
    // Facelets or JSP view)
    @Produces
    @Named
    public List<Marca> getListaMarca() {
        return listaMarca;
    }
	
	@PostConstruct
    public void initNewMarca() {
		
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

		newMarca = new Marca();
		newMarca.setEstado("AC");
		newMarca.setFechaRegistro(new Date());
		newMarca.setUsuarioRegistro(usuarioSession.getLogin());
		
		//tituloPanel 
		tituloPanel = "Registrar Marca";
		
		//traer todos las Grupos de Productos ordenados por ID Desc
		listaMarca = MarcaRepository.findAllOrderedByID();
		modificar = false;
		nuevo=false;
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

	// SELECT Marca CLICK
	public void onRowSelectMarcaClick(SelectEvent event) {
		try {
			Marca Marca = (Marca) event.getObject();
			System.out.println("onRowSelectMarcaClick  " + Marca.getId());
			selectedMarca = Marca;
			newMarca = em.find(Marca.class, Marca.getId());
			newMarca.setFechaRegistro(new Date());
			newMarca.setUsuarioRegistro(usuarioSession.getLogin());

			tituloPanel = "Modificar Marca";
			modificar = true;
			nuevo=false;

		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
			System.out.println("Error in onRowSelectMarcaClick: "
					+ e.getMessage());
		}
	}
	
	// SELECT Marca CLICK
		public void onRowSelectMarcaDBLClick(SelectEvent event) {
			try {
				Marca Marca = (Marca) event.getObject();
				System.out.println("onRowSelectMarcaClick  " + Marca.getId());
				selectedMarca = Marca;
				newMarca = em.find(Marca.class, Marca.getId());
				newMarca.setFechaRegistro(new Date());
				newMarca.setUsuarioRegistro(usuarioSession.getLogin());

				tituloPanel = "Modificar Marca";
				modificar = true;
				nuevo=true;

			} catch (Exception e) {
				// TODO: handle exception
				e.printStackTrace();
				System.out.println("Error in onRowSelectMarcaClick: "
						+ e.getMessage());
			}
		}
	
	public void registrarMarca(){
        try {
        	System.out.println("Ingreso a registrarMarca: "+newMarca.getId());
        	MarcaRegistration.register(newMarca);
            FacesMessage m = new FacesMessage(FacesMessage.SEVERITY_INFO, "Registered!", "Registration successful");
            facesContext.addMessage(null, m);
            pushEventSucursal.fire(String.format("Nuevo Grupo Registrado: %s (id: %d)", newMarca.getDescripcion(), newMarca.getId()));
            initNewMarca();
            
        } catch (Exception e) {
            String errorMessage = getRootErrorMessage(e);
            FacesMessage m = new FacesMessage(FacesMessage.SEVERITY_ERROR, errorMessage, "Registro Incorrecto.");
            facesContext.addMessage(null, m);
        }
    }
	
	public void modificarMarca(){
        try {
        	System.out.println("Ingreso a modificarPresentacion: "+newMarca.getId());
        	MarcaRegistration.updated(newMarca);
            FacesMessage m = new FacesMessage(FacesMessage.SEVERITY_INFO, "Modificado!", "Modificado successful");
            facesContext.addMessage(null, m);
            pushEventSucursal.fire(String.format("Grupo Modificado: %s (id: %d)", newMarca.getDescripcion(), newMarca.getId()));
            initNewMarca();
            
        } catch (Exception e) {
            String errorMessage = getRootErrorMessage(e);
            FacesMessage m = new FacesMessage(FacesMessage.SEVERITY_ERROR, errorMessage, "Modificado Incorrecto.");
            facesContext.addMessage(null, m);
        }
    }
	
	public void eliminarMarca(){
        try {
        	System.out.println("Ingreso a eliminarMarca: "+newMarca.getId());
        	MarcaRegistration.remover(newMarca);
            FacesMessage m = new FacesMessage(FacesMessage.SEVERITY_INFO, "Borrado!", "Borrado successful");
            facesContext.addMessage(null, m);
            pushEventSucursal.fire(String.format("Grupo Borrado: %s (id: %d)", newMarca.getDescripcion(), newMarca.getId()));
            initNewMarca();
            
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

	public Marca getSelectedMarca() {
		return selectedMarca;
	}

	public void setSelectedMarca(Marca selectedMarca) {
		this.selectedMarca = selectedMarca;
	}

	public Marca getNewMarca() {
		return newMarca;
	}

	public void setNewMarca(Marca newMarca) {
		this.newMarca = newMarca;
	}

	public boolean isNuevo() {
		return nuevo;
	}

	public void setNuevo(boolean nuevo) {
		this.nuevo = nuevo;
	}
	
	
}
