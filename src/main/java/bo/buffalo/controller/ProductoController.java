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

import bo.buffalo.data.ProductoRepository;
import bo.buffalo.data.UsuarioRepository;
import bo.buffalo.model.Producto;
import bo.buffalo.model.Usuario;
import bo.buffalo.service.ProductoRegistration;

@Named(value="productoController")
@ConversationScoped
public class ProductoController implements Serializable{

	private static final long serialVersionUID = -2745329186387328746L;

	public static final String PUSH_CDI_TOPIC = "pushCdi";

    @Inject
    private FacesContext facesContext;
    
    @Inject
    private EntityManager em;
    
    @Inject 
	Conversation conversation;
    
    @Inject
    private ProductoRegistration productoRegistration;
    
    @Inject
    private ProductoRepository productoRepository;
    
    @Inject 
    UsuarioRepository usuarioRepository;
    private Usuario usuarioSession;
    
    @Inject
    @Push(topic = PUSH_CDI_TOPIC)
    Event<String> pushEventSucursal;
	
    private boolean modificar = false;
    private String tituloPanel = "Registrar Producto";
    private Producto selectedProducto;
	private Producto newProducto;
	
	private List<Producto> productos;

    // @Named provides access the return value via the EL variable name "servicios" in the UI (e.g.
    // Facelets or JSP view)
    @Produces
    @Named
    public List<Producto> getProductos() {
        return productos;
    }
	
	@PostConstruct
    public void initNewProducto() {
		
		// initConversation();
    	beginConversation();
    	
    	HttpServletRequest request = (HttpServletRequest) facesContext
				.getExternalContext().getRequest();
		System.out.println("init Producto*********************************");
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
		
		newProducto = new Producto();
		newProducto.setEstado("AC");
		newProducto.setFechaRegistro(new Date());
		newProducto.setUsuarioRegistro(usuarioSession.getLogin());
		
		//traer todos los servicios ordenados por nombre
		productos = productoRepository.findAllOrderedByID();
		modificar = false;
		tituloPanel = "Registrar Producto";
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
	public void onRowSelectProductoClick(SelectEvent event) {
		try {
			Producto producto = (Producto) event.getObject();
			System.out.println("onRowSelectProductoClick  " + producto.getId());
			selectedProducto = producto;
			newProducto = em.find(Producto.class, producto.getId());
			newProducto.setFechaRegistro(new Date());
			newProducto.setUsuarioRegistro(usuarioSession.getLogin());

			tituloPanel = "Modificar Producto";
			modificar = true;

		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
			System.out.println("Error in onRowSelectProductoClick: "
					+ e.getMessage());
		}
	}
	
	public void registrarProducto(){
        try {
        	System.out.println("Ingreso a registrarProducto: "+newProducto.getId());
        	productoRegistration.register(newProducto);
            FacesMessage m = new FacesMessage(FacesMessage.SEVERITY_INFO, "Registered!", "Registration successful");
            facesContext.addMessage(null, m);
            pushEventSucursal.fire(String.format("Nuevo Producto Registrado: %s (id: %d)", newProducto.getNombreProducto(), newProducto.getId()));
            initNewProducto();
        } catch (Exception e) {
            String errorMessage = getRootErrorMessage(e);
            FacesMessage m = new FacesMessage(FacesMessage.SEVERITY_ERROR, errorMessage, "Registro Incorrecto.");
            facesContext.addMessage(null, m);
        }
    }
	
	public void modificarProducto(){
        try {
        	System.out.println("Ingreso a modificarProducto: "+newProducto.getId());
        	productoRegistration.updated(newProducto);
            FacesMessage m = new FacesMessage(FacesMessage.SEVERITY_INFO, "Modificado!", "Modificado successful");
            facesContext.addMessage(null, m);
            pushEventSucursal.fire(String.format("Producto Modificado: %s (id: %d)", newProducto.getNombreProducto(), newProducto.getId()));
            initNewProducto();
        } catch (Exception e) {
            String errorMessage = getRootErrorMessage(e);
            FacesMessage m = new FacesMessage(FacesMessage.SEVERITY_ERROR, errorMessage, "Modificado Incorrecto.");
            facesContext.addMessage(null, m);
        }
    }
	
	public void eliminarProducto(){
        try {
        	System.out.println("Ingreso a eliminarProducto: "+newProducto.getId());
        	productoRegistration.remover(newProducto);
            FacesMessage m = new FacesMessage(FacesMessage.SEVERITY_INFO, "Borrado!", "Borrado successful");
            facesContext.addMessage(null, m);
            pushEventSucursal.fire(String.format("Producto Borrado: %s (id: %d)", newProducto.getNombreProducto(), newProducto.getId()));
            initNewProducto();
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
	public Producto getNewProducto() {
		return newProducto;
	}

	public void setNewProducto(Producto newProducto) {
		this.newProducto = newProducto;
	}

	public String getTituloPanel() {
		return tituloPanel;
	}

	public void setTituloPanel(String tituloPanel) {
		this.tituloPanel = tituloPanel;
	}

	public Producto getSelectedProducto() {
		return selectedProducto;
	}

	public void setSelectedProducto(Producto selectedProducto) {
		this.selectedProducto = selectedProducto;
	}

	public boolean isModificar() {
		return modificar;
	}

	public void setModificar(boolean modificar) {
		this.modificar = modificar;
	}
	
	
}
