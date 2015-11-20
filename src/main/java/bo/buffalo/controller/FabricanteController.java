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

import bo.buffalo.data.FabricanteRepository;
import bo.buffalo.data.UsuarioRepository;
import bo.buffalo.model.Fabricante;
import bo.buffalo.model.Usuario;
import bo.buffalo.service.FabricacionRegistration;

@Named(value="fabricanteController")
@ConversationScoped
public class FabricanteController implements Serializable{


	/**
	 * 
	 */
	private static final long serialVersionUID = -2508431274449879536L;

	public static final String PUSH_CDI_TOPIC = "pushCdi";

    @Inject
    private FacesContext facesContext;
    
    @Inject
    private EntityManager em;
    
    @Inject 
	Conversation conversation;
    
    @Inject
    private FabricacionRegistration FabricanteRegistration;
    
    @Inject
    private FabricanteRepository FabricanteRepository;
    
    @Inject 
    UsuarioRepository usuarioRepository;
    private Usuario usuarioSession;
    
    @Inject
    @Push(topic = PUSH_CDI_TOPIC)
    Event<String> pushEventFabricante;
	
    private boolean modificar = false;
    private boolean nuevo=false;
    private String tituloPanel = "Registrar Fabricante";
    private Fabricante selectedFabricante;
	private Fabricante newFabricante;
	
	private List<Fabricante> listaFabricante;

    // @Named provides access the return value via the EL variable name "Fabricante" in the UI (e.g.
    // Facelets or JSP view)
    @Produces
    @Named
    public List<Fabricante> getListaFabricante() {
        return listaFabricante;
    }
	
	@PostConstruct
    public void initNewFabricante() {
		
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

		newFabricante = new Fabricante();
		newFabricante.setEstado("AC");
		newFabricante.setFechaRegistro(new Date());
		newFabricante.setUsuarioRegistro(usuarioSession.getLogin());
		
		//tituloPanel 
		tituloPanel = "Registrar Fabricante";
		
		//traer todos las Grupos de Productos ordenados por ID Desc
		listaFabricante = FabricanteRepository.findAllOrderedByID();
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

	// SELECT Fabricante CLICK
	public void onRowSelectFabricanteClick(SelectEvent event) {
		try {
			Fabricante Fabricante = (Fabricante) event.getObject();
			System.out.println("onRowSelectFabricanteClick  " + Fabricante.getId());
			selectedFabricante = Fabricante;
			newFabricante = em.find(Fabricante.class, Fabricante.getId());
			newFabricante.setFechaRegistro(new Date());
			newFabricante.setUsuarioRegistro(usuarioSession.getLogin());

			tituloPanel = "Modificar Fabricante";
			modificar = true;
			nuevo=false;

		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
			System.out.println("Error in onRowSelectFabricanteClick: "
					+ e.getMessage());
		}
	}
	
	// SELECT Fabricante CLICK
		public void onRowSelectFabricanteDBLClick(SelectEvent event) {
			try {
				Fabricante Fabricante = (Fabricante) event.getObject();
				System.out.println("onRowSelectFabricanteClick  " + Fabricante.getId());
				selectedFabricante = Fabricante;
				newFabricante = em.find(Fabricante.class, Fabricante.getId());
				newFabricante.setFechaRegistro(new Date());
				newFabricante.setUsuarioRegistro(usuarioSession.getLogin());

				tituloPanel = "Modificar Fabricante";
				modificar = true;
				nuevo=true;

			} catch (Exception e) {
				// TODO: handle exception
				e.printStackTrace();
				System.out.println("Error in onRowSelectFabricanteClick: "
						+ e.getMessage());
			}
		}
	
	public void registrarFabricante(){
        try {
        	System.out.println("Ingreso a registrarFabricante: "+newFabricante.getId());
        	FabricanteRegistration.register(newFabricante);
            FacesMessage m = new FacesMessage(FacesMessage.SEVERITY_INFO, "Registered!", "Registration successful");
            facesContext.addMessage(null, m);
            pushEventFabricante.fire(String.format("Nuevo Grupo Registrado: %s (id: %d)", newFabricante.getDescripcion(), newFabricante.getId()));
            initNewFabricante();
            
        } catch (Exception e) {
            String errorMessage = getRootErrorMessage(e);
            FacesMessage m = new FacesMessage(FacesMessage.SEVERITY_ERROR, errorMessage, "Registro Incorrecto.");
            facesContext.addMessage(null, m);
        }
    }
	
	public void modificarFabricante(){
        try {
        	System.out.println("Ingreso a modificarPresentacion: "+newFabricante.getId());
        	FabricanteRegistration.updated(newFabricante);
            FacesMessage m = new FacesMessage(FacesMessage.SEVERITY_INFO, "Modificado!", "Modificado successful");
            facesContext.addMessage(null, m);
            pushEventFabricante.fire(String.format("Grupo Modificado: %s (id: %d)", newFabricante.getDescripcion(), newFabricante.getId()));
            initNewFabricante();
            
        } catch (Exception e) {
            String errorMessage = getRootErrorMessage(e);
            FacesMessage m = new FacesMessage(FacesMessage.SEVERITY_ERROR, errorMessage, "Modificado Incorrecto.");
            facesContext.addMessage(null, m);
        }
    }
	
	public void eliminarFabricante(){
        try {
        	System.out.println("Ingreso a eliminarFabricante: "+newFabricante.getId());
        	FabricanteRegistration.remover(newFabricante);
            FacesMessage m = new FacesMessage(FacesMessage.SEVERITY_INFO, "Borrado!", "Borrado successful");
            facesContext.addMessage(null, m);
            pushEventFabricante.fire(String.format("Grupo Borrado: %s (id: %d)", newFabricante.getDescripcion(), newFabricante.getId()));
            initNewFabricante();
            
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

	public Fabricante getSelectedFabricante() {
		return selectedFabricante;
	}

	public void setSelectedFabricante(Fabricante selectedFabricante) {
		this.selectedFabricante = selectedFabricante;
	}

	public Fabricante getNewFabricante() {
		return newFabricante;
	}

	public void setNewFabricante(Fabricante newFabricante) {
		this.newFabricante = newFabricante;
	}

	public boolean isNuevo() {
		return nuevo;
	}

	public void setNuevo(boolean nuevo) {
		this.nuevo = nuevo;
	}
	
	
}
