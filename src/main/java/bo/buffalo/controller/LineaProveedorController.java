package bo.buffalo.controller;

import java.io.Serializable;
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

import bo.buffalo.data.LineaProveedorRepository;
import bo.buffalo.data.ProveedorRepository;
import bo.buffalo.data.UsuarioRepository;
import bo.buffalo.model.LineasProveedor;
import bo.buffalo.model.Proveedor;
import bo.buffalo.model.Usuario;
import bo.buffalo.service.LineaProveedorRegistration;

@Named(value="lineaProveedorController")
@ConversationScoped
public class LineaProveedorController implements Serializable{

	private static final long serialVersionUID = -1992094769231496851L;

	public static final String PUSH_CDI_TOPIC = "pushCdi";

    @Inject
    private FacesContext facesContext;
    
    @Inject
    private EntityManager em;
    
    @Inject 
	Conversation conversation;
    
    @Inject 
    private ProveedorRepository proveedorRepository;
    
    @Inject
    private LineaProveedorRegistration lineasProveedorRegistration;
    
    @Inject
    private LineaProveedorRepository lineasProveedorRepository;
    
    @Inject 
    UsuarioRepository usuarioRepository;
    private Usuario usuarioSession;
    
    @Inject
    @Push(topic = PUSH_CDI_TOPIC)
    Event<String> pushEventSucursal;
	
    private boolean modificar = false;
    private String tituloPanel = "Registrar LineasProveedor";
    private Integer idProveedor = 0; 
    private LineasProveedor selectedLineasProveedor;
	private LineasProveedor newLineasProveedor;
	private List<Proveedor> listaProveedores = new ArrayList<Proveedor>();
	private List<LineasProveedor> listaLineasProveedores;

	private boolean nuevo;

    // @Named provides access the return value via the EL variable name "GrupoProducto" in the UI (e.g.
    // Facelets or JSP view)
    @Produces
    @Named
    public List<LineasProveedor> getListaLineasProveedores() {
        return listaLineasProveedores;
    }
	
	@PostConstruct
    public void initNewLineasProveedores() {
		
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
		
		
		idProveedor = 0; 
		
		//cargamos lista proveedores
		listaProveedores.clear();
		listaProveedores = proveedorRepository.traerProveedoresActivas();
		
		//traer todos las LineasProveedores ordenados por ID Desc
		listaLineasProveedores = lineasProveedorRepository.findAllOrderedByID();
		setNuevo(false);
		modificar= false;
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

	// SELECT LineasProveedor CLICK
	public void onRowSelectLineasProveedorClick(SelectEvent event) {
		try {
			LineasProveedor lineasProveedor = (LineasProveedor) event.getObject();
			System.out.println("onRowSelectLineasProveedorClick  " + lineasProveedor.getId());
			selectedLineasProveedor = lineasProveedor;
			newLineasProveedor = em.find(LineasProveedor.class, lineasProveedor.getId());
			newLineasProveedor.setFechaRegistro(new Date());
			newLineasProveedor.setUsuarioRegistro(usuarioSession.getLogin());

			idProveedor = lineasProveedor.getProveedor().getId();
			tituloPanel = "Modificar LineasProveedor";
			modificar = true;
			setNuevo(false);

		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
			System.out.println("Error in onRowSelectLineasProveedorClick: "
					+ e.getMessage());
		}
	}
	
	// SELECT LineasProveedor CLICK
	public void onRowSelectLineasProveedorDBLClick(SelectEvent event) {
		try {
			LineasProveedor lineasProveedor = (LineasProveedor) event.getObject();
			System.out.println("onRowSelectLineasProveedorClick  " + lineasProveedor.getId());
			selectedLineasProveedor = lineasProveedor;
			newLineasProveedor = em.find(LineasProveedor.class, lineasProveedor.getId());
			newLineasProveedor.setFechaRegistro(new Date());
			newLineasProveedor.setUsuarioRegistro(usuarioSession.getLogin());

			idProveedor = lineasProveedor.getProveedor().getId();
			tituloPanel = "Modificar LineasProveedor";
			modificar = true;
			setNuevo(true);

		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
			System.out.println("Error in onRowSelectLineasProveedorClick: "
					+ e.getMessage());
		}
	}
	
	public void modificarEstado(){
		setNuevo(true);
		modificar= true;
	}
	
	public void crearLineaProveedor(){
		try {
			System.out.println("Ingreso en crearLineaProveedor");
			newLineasProveedor = new LineasProveedor();
			newLineasProveedor.setEstado("AC");
			newLineasProveedor.setFechaRegistro(new Date());
			newLineasProveedor.setUsuarioRegistro(usuarioSession.getLogin());
			setNuevo(true);
			modificar= false;
			//tituloPanel 
			tituloPanel = "Registrar LineasProveedor";
		} catch (Exception e) {
			System.err.println("Error en crearLineaProveedor : "+e.getMessage());
		}
		
	}
	
	public void registrarLineasProveedor(){
        try {
        	System.out.println("Ingreso a registrarLineasProveedor: "+newLineasProveedor.getId());
        	
        	//seteamos el proveedor
        	Proveedor proveedor = em.find(Proveedor.class, this.getIdProveedor());
        	newLineasProveedor.setProveedor(proveedor); 
        	
        	lineasProveedorRegistration.register(newLineasProveedor);
            FacesMessage m = new FacesMessage(FacesMessage.SEVERITY_INFO, "Registered!", "Registration successful");
            facesContext.addMessage(null, m);
            pushEventSucursal.fire(String.format("Nuevo LineasProveedor Registrado: %s (id: %d)", newLineasProveedor.getNombre(), newLineasProveedor.getId()));
            initNewLineasProveedores();
            
        } catch (Exception e) {
            String errorMessage = getRootErrorMessage(e);
            FacesMessage m = new FacesMessage(FacesMessage.SEVERITY_ERROR, errorMessage, "Registro Incorrecto.");
            facesContext.addMessage(null, m);
        }
    }
	
	public void modificarLineasProveedor(){
        try {
        	System.out.println("Ingreso a modificarLineasProveedor: "+newLineasProveedor.getId());
        	lineasProveedorRegistration.updated(newLineasProveedor);
            FacesMessage m = new FacesMessage(FacesMessage.SEVERITY_INFO, "Modificado!", "Modificado successful");
            facesContext.addMessage(null, m);
            pushEventSucursal.fire(String.format("LineasProveedor Modificado: %s (id: %d)", newLineasProveedor.getNombre(), newLineasProveedor.getId()));
            initNewLineasProveedores();
            
        } catch (Exception e) {
            String errorMessage = getRootErrorMessage(e);
            FacesMessage m = new FacesMessage(FacesMessage.SEVERITY_ERROR, errorMessage, "Modificado Incorrecto.");
            facesContext.addMessage(null, m);
        }
    }
	
	public void eliminarLineasProveedor(){
        try {
        	System.out.println("Ingreso a eliminarLineasProveedor: "+newLineasProveedor.getId());
        	lineasProveedorRegistration.remover(newLineasProveedor);
            FacesMessage m = new FacesMessage(FacesMessage.SEVERITY_INFO, "Borrado!", "Borrado successful");
            facesContext.addMessage(null, m);
            pushEventSucursal.fire(String.format("LineasProveedor Borrado: %s (id: %d)", newLineasProveedor.getNombre(), newLineasProveedor.getId()));
            initNewLineasProveedores();
            
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

	public LineasProveedor getNewLineasProveedor() {
		return newLineasProveedor;
	}

	public void setNewLineasProveedor(LineasProveedor newLineasProveedor) {
		this.newLineasProveedor = newLineasProveedor;
	}

	public LineasProveedor getSelectedLineasProveedor() {
		return selectedLineasProveedor;
	}

	public void setSelectedLineasProveedor(LineasProveedor selectedLineasProveedor) {
		this.selectedLineasProveedor = selectedLineasProveedor;
	}

	public Integer getIdProveedor() {
		return idProveedor;
	}

	public void setIdProveedor(Integer idProveedor) {
		this.idProveedor = idProveedor;
	}

	public List<Proveedor> getListaProveedores() {
		return listaProveedores;
	}

	public void setListaProveedores(List<Proveedor> listaProveedores) {
		this.listaProveedores = listaProveedores;
	}

	public boolean isNuevo() {
		return nuevo;
	}

	public void setNuevo(boolean nuevo) {
		this.nuevo = nuevo;
	}
	
	
}
