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

import bo.buffalo.data.EmpresaRepository;
import bo.buffalo.data.UsuarioRepository;
import bo.buffalo.model.Empresa;
import bo.buffalo.model.Usuario;
import bo.buffalo.service.EmpresaRegistration;

@Named(value="empresaController")
@ConversationScoped
public class EmpresaController implements Serializable{

	private static final long serialVersionUID = 8274708378611908240L;

	public static final String PUSH_CDI_TOPIC = "pushCdi";

    @Inject
    private FacesContext facesContext;
    
    @Inject
    private EntityManager em;
    
    @Inject 
	Conversation conversation;
    
    @Inject
    EmpresaRegistration empresaRegistration;
    
    @Inject
    EmpresaRepository empresaRepository;
    
    @Inject 
    UsuarioRepository usuarioRepository;
    private Usuario usuarioSession;
    
    @Inject
    @Push(topic = PUSH_CDI_TOPIC)
    Event<String> pushEventSucursal;
	
    private boolean modificar = false;
    private String tituloPanel = "Registrar Empresa";
    private Empresa selectedEmpresa;
	private Empresa newEmpresa;
	private List<Empresa> listaEmpresas;

    // @Named provides access the return value via the EL variable name "servicios" in the UI (e.g.
    // Facelets or JSP view)
    @Produces
    @Named
    public List<Empresa> getListaEmpresas() {
        return listaEmpresas;
    }
	
	@PostConstruct
    public void initNewEmpresa() {
		
		// initConversation();
    	beginConversation();
    	
    	HttpServletRequest request = (HttpServletRequest) facesContext
				.getExternalContext().getRequest();
		System.out.println("init NewMedico*********************************");
		System.out.println("request.getClass().getName():"
				+ request.getClass().getName());
		System.out.println("isVentas:" + request.isUserInRole("ventas"));
		System.out.println("remoteUser:" + request.getRemoteUser());
		System.out.println("userPrincipalName:"
				+ (request.getUserPrincipal() == null ? "null" : request
						.getUserPrincipal().getName()));

		usuarioSession = usuarioRepository.findByLogin(request
				.getUserPrincipal().getName());
//		System.out.println("Sucursal Usuario: "
//				+ usuarioSession.getSucursal().getNombre());
		selectedEmpresa = null;
		
		newEmpresa = new Empresa();
		newEmpresa.setEstado("AC");
		newEmpresa.setFechaRegistro(new Date());
		newEmpresa.setUsuarioRegistro(usuarioSession.getLogin());
		
		//traer todas las empresas ordenadas por nombre
		listaEmpresas = empresaRepository.findAllOrderedByID();
		modificar = false;
		tituloPanel = "Registrar Empresa";
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

	// SELECT EMPRESA CLICK
	public void onRowSelectEmpresaClick(SelectEvent event) {
		try {
			Empresa empresa = (Empresa) event.getObject();
			System.out.println("onRowSelectEmpresaClick  " + empresa.getId());
			setSelectedEmpresa(empresa);
			newEmpresa = em.find(Empresa.class, empresa.getId());
			newEmpresa.setFechaRegistro(new Date());
			newEmpresa.setUsuarioRegistro(usuarioSession.getLogin());

			tituloPanel = "Modificar Empresa";
			modificar = true;

		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
			System.out.println("Error in onRowSelectEmpresaClick: "
					+ e.getMessage());
		}
	}
	
	public void registrarEmpresa(){
        try {
        	System.out.println("Ingreso a registrarEmpresa: "+newEmpresa.getId());
        	empresaRegistration.register(newEmpresa);
            FacesMessage m = new FacesMessage(FacesMessage.SEVERITY_INFO, "Empresa Registrada!", newEmpresa.getNombre());
            facesContext.addMessage(null, m);
            pushEventSucursal.fire(String.format("Empresa Registrada: %s (id: %d)", newEmpresa.getNombre(), newEmpresa.getId()));
            initNewEmpresa();
        } catch (Exception e) {
            String errorMessage = getRootErrorMessage(e);
            FacesMessage m = new FacesMessage(FacesMessage.SEVERITY_ERROR, errorMessage, "Registro Incorrecto.");
            facesContext.addMessage(null, m);
        }
    }
	
	public void modificarEmpresa(){
		try {
        	System.out.println("Ingreso a modificarEmpresa: "+newEmpresa.getId());
        	empresaRegistration.updated(newEmpresa);
            FacesMessage m = new FacesMessage(FacesMessage.SEVERITY_INFO, "Empresa Modificada!", newEmpresa.getNombre());
            facesContext.addMessage(null, m);
            pushEventSucursal.fire(String.format("Empresa Modificada: %s (id: %d)", newEmpresa.getNombre(), newEmpresa.getId()));
            initNewEmpresa();
        } catch (Exception e) {
            String errorMessage = getRootErrorMessage(e);
            FacesMessage m = new FacesMessage(FacesMessage.SEVERITY_ERROR, errorMessage, "Modificado Incorrecto.");
            facesContext.addMessage(null, m);
        }
    }
	
	public void eliminarEmpresa(){
		try {
        	System.out.println("Ingreso a eliminarEmpresa: "+newEmpresa.getId());
        	empresaRegistration.remover(newEmpresa);
            FacesMessage m = new FacesMessage(FacesMessage.SEVERITY_INFO, "Empresa Eliminada!", newEmpresa.getNombre());
            facesContext.addMessage(null, m);
            pushEventSucursal.fire(String.format("Empresa Eliminada: %s (id: %d)", newEmpresa.getNombre(), newEmpresa.getId()));
            initNewEmpresa();
        } catch (Exception e) {
            String errorMessage = getRootErrorMessage(e);
            FacesMessage m = new FacesMessage(FacesMessage.SEVERITY_ERROR, errorMessage, "Eliminado Incorrecto.");
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

	public Empresa getSelectedEmpresa() {
		return selectedEmpresa;
	}

	public void setSelectedEmpresa(Empresa selectedEmpresa) {
		this.selectedEmpresa = selectedEmpresa;
	}
	
	public Empresa getNewEmpresa() {
		return newEmpresa;
	}

	public void setNewEmpresa(Empresa newEmpresa) {
		this.newEmpresa = newEmpresa;
	}
	
}
