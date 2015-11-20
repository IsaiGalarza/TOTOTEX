package bo.buffalo.controller;

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
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
import bo.buffalo.data.ClienteRepository;
import bo.buffalo.data.UsuarioRepository;
import bo.buffalo.model.Ciudad;
import bo.buffalo.model.Cliente;
import bo.buffalo.model.Usuario;
import bo.buffalo.service.ClienteRegistration;

@Named(value="clienteController")
@ConversationScoped
public class ClienteController implements Serializable{

	private static final long serialVersionUID = -1992094769231496851L;

	public static final String PUSH_CDI_TOPIC = "pushCdi";

    @Inject
    private FacesContext facesContext;
    
    @Inject
    private EntityManager em;
    
    @Inject 
	Conversation conversation;
    
    private List<Ciudad> listaCiudad= new ArrayList<Ciudad>();
    @Inject
    private CiudadRepository ciudadRepository;
    
    @Inject
    private ClienteRegistration clienteRegistration;
    
    @Inject
    private ClienteRepository clienteRepository;
    
    @Inject 
    UsuarioRepository usuarioRepository;
    private Usuario usuarioSession;
    
    @Inject
    @Push(topic = PUSH_CDI_TOPIC)
    Event<String> pushEventSucursal;
	
    private boolean modificar = false;
    private String tituloPanel = "Registrar Cliente";
    private int ciudadID;
    private Cliente selectedCliente;
	private Cliente newCliente;
	
	private List<Cliente> listaClientes = new ArrayList<Cliente>();

    // @Named provides access the return value via the EL variable name "GrupoProducto" in the UI (e.g.
    // Facelets or JSP view)
    @Produces
    @Named
    public List<Cliente> getListaClientes() {
        return listaClientes;
    }
	
	@PostConstruct
    public void initNewClientes() {
		
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
		
		//inicializar variables
		ciudadID = 0;
		selectedCliente = null;
		
		newCliente = new Cliente();
		newCliente.setEstado("AC");
		newCliente.setFechaRegistro(new Date());
		newCliente.setUsuarioRegistro(usuarioSession.getLogin());
		
		//tituloPanel 
		tituloPanel = "Registrar Cliente";
		listaCiudad= ciudadRepository.traerCiudad();
		//traer todos las Clientees ordenados por ID Desc
		listaClientes.clear();
		listaClientes = clienteRepository.findAllOrderedByFechaReg();
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
	
	public String obtenerFecha(Date fecha) {
		try {
			String DATE_FORMAT = "dd/MM/yyyy";
			SimpleDateFormat sdf = new SimpleDateFormat(DATE_FORMAT);
			return sdf.format(fecha);

		} catch (Exception e) {
			// TODO: handle exception
			System.out.println("Error en obtenerFecha: " + e.getMessage());
			return "NULL";
		}
	}
	
	// SELECT PROVEEDOR CLICK
	public void onRowSelectClienteClick(SelectEvent event) {
		try {
			Cliente cliente = (Cliente) event.getObject();
			System.out.println("onRowSelectClienteClick  " + cliente.getId());
			selectedCliente = cliente;
			newCliente = em.find(Cliente.class, cliente.getId());
			newCliente.setFechaRegistro(new Date());
			newCliente.setUsuarioRegistro(usuarioSession.getLogin());
			try {
				ciudadID = newCliente.getCiudad().getId();
			} catch (Exception e) {
				ciudadID=-1;
			}
			
			tituloPanel = "Modificar Cliente";
			modificar = true;

		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
			System.out.println("Error in onRowSelectGrupoProductoClick: "
					+ e.getMessage());
		}
	}
	
	public void registrarCliente(){
        try {
        	System.out.println("Ingreso a registrarCliente: "+newCliente.getId());
        	newCliente.setCiudad(em.find(Ciudad.class, ciudadID));
        	clienteRegistration.register(newCliente);
           
        	FacesMessage m = new FacesMessage(FacesMessage.SEVERITY_INFO, "Cliente Registrado!", newCliente.getNombreCompleto());
            facesContext.addMessage(null, m);
            //pushEventSucursal.fire(String.format("Nuevo Cliente Registrado: %s (id: %d)", newCliente.getNombreCompleto(), newCliente.getId()));
            
            initNewClientes();
            
        } catch (Exception e) {
            String errorMessage = getRootErrorMessage(e);
            FacesMessage m = new FacesMessage(FacesMessage.SEVERITY_ERROR, errorMessage, "Registro Incorrecto.");
            facesContext.addMessage(null, m);
        }
    }
	
	public void modificarCliente(){
        try {
        	System.out.println("Ingreso a modificarCliente: "+newCliente.getId());
        	newCliente.setFechaRegistro(new Date());
        	newCliente.setUsuarioRegistro(usuarioSession.getLogin());
        	newCliente.setCiudad(em.find(Ciudad.class, ciudadID));
        	clienteRegistration.updated(newCliente);
        	FacesMessage m = new FacesMessage(FacesMessage.SEVERITY_INFO, "Cliente Modificado!", newCliente.getNombreCompleto());
            facesContext.addMessage(null, m);
            //pushEventSucursal.fire(String.format("Cliente Modificado: %s (id: %d)", newCliente.getNombreCompleto(), newCliente.getId()));
            
            initNewClientes();
            
        } catch (Exception e) {
            String errorMessage = getRootErrorMessage(e);
            FacesMessage m = new FacesMessage(FacesMessage.SEVERITY_ERROR, errorMessage, "Modificado Incorrecto.");
            facesContext.addMessage(null, m);
        }
    }
	
	public void eliminarCliente(){
        try {
        	System.out.println("Ingreso a eliminarCliente: "+newCliente.getId());
        	clienteRegistration.remover(newCliente);
        	FacesMessage m = new FacesMessage(FacesMessage.SEVERITY_INFO, "Cliente Borrado!", newCliente.getNombreCompleto());
            facesContext.addMessage(null, m);
            //pushEventSucursal.fire(String.format("Cliente Borrado: %s (id: %d)", newCliente.getNombreCompleto(), newCliente.getId()));
            
            initNewClientes();
            
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

	public Cliente getNewCliente() {
		return newCliente;
	}

	public void setNewCliente(Cliente newCliente) {
		this.newCliente = newCliente;
	}

	public Cliente getSelectedCliente() {
		return selectedCliente;
	}

	public void setSelectedCliente(Cliente selectedCliente) {
		this.selectedCliente = selectedCliente;
	}

	public List<Ciudad> getListaCiudad() {
		return listaCiudad;
	}

	public void setListaCiudad(List<Ciudad> listaCiudad) {
		this.listaCiudad = listaCiudad;
	}

	public int getCiudadID() {
		return ciudadID;
	}

	public void setCiudadID(int ciudadID) {
		this.ciudadID = ciudadID;
	}
	
	
}
