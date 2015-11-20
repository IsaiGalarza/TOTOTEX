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

import bo.buffalo.data.CantUnitPresRepository;
import bo.buffalo.data.PresentacionRepository;
import bo.buffalo.data.UnidadMedidaRepository;
import bo.buffalo.data.UsuarioRepository;
import bo.buffalo.model.CantidadUnidadPresentacion;
import bo.buffalo.model.Presentacion;
import bo.buffalo.model.UnidadMedida;
import bo.buffalo.model.Usuario;
import bo.buffalo.service.CantUnitPresRegistration;

@Named(value="cantUnitController")
@ConversationScoped
public class CantUnitPresController implements Serializable{

	private static final long serialVersionUID = -1992094769231496851L;

	public static final String PUSH_CDI_TOPIC = "pushCdi";

    @Inject
    private FacesContext facesContext;
    
    @Inject
    private EntityManager em;
    
    @Inject 
	Conversation conversation;
    
    private List<UnidadMedida> listaUnidadMedida;
    private Integer idUnidadMeddida;
    @Inject
    private UnidadMedidaRepository unidadMedidaRegistration;
    private List<Presentacion> listaPresentacion;
    private Integer idPresentacion;
    @Inject
    private PresentacionRepository presentacionRegistration;
    
    @Inject
    private CantUnitPresRegistration cantUnitPresRegistration;
    
    @Inject
    private CantUnitPresRepository cantUnitPresRepository;
    
    @Inject 
    UsuarioRepository usuarioRepository;
    private Usuario usuarioSession;
    
    @Inject
    @Push(topic = PUSH_CDI_TOPIC)
    Event<String> pushEventCantidadUnidadPresentacion;
	
    private boolean modificar = false;
    private String tituloPanel = "Registrar CantidadUnidadPresentacion";
    private CantidadUnidadPresentacion selectedCantidadUnidadPresentacion;
	private CantidadUnidadPresentacion newCantidadUnidadPresentacion;
	
	private List<CantidadUnidadPresentacion> listaCantidadUnidadPresentacions;

    // @Named provides access the return value via the EL variable name "GrupoProducto" in the UI (e.g.
    // Facelets or JSP view)
    @Produces
    @Named
    public List<CantidadUnidadPresentacion> getListaCantidadUnidadPresentacions() {
        return listaCantidadUnidadPresentacions;
    }
	
	@PostConstruct
    public void initNewCantidadUnidadPresentacions() {
		
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
		
		newCantidadUnidadPresentacion = new CantidadUnidadPresentacion();
		newCantidadUnidadPresentacion.setEstado("AC");
		newCantidadUnidadPresentacion.setFechaRegistro(new Date());
		newCantidadUnidadPresentacion.setUsuarioRegistro(usuarioSession.getLogin());
		
		//tituloPanel 
		tituloPanel = "Registrar CantidadUnidadPresentacion";
		listaPresentacion= presentacionRegistration.traerPresentacionActivas();
		listaUnidadMedida= unidadMedidaRegistration.traerUnidadMedidaActivas();
		
		//traer todos las CantidadUnidadPresentaciones ordenados por ID Desc
		listaCantidadUnidadPresentacions = cantUnitPresRepository.findAllOrderedByID();
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

	// SELECT PROVEEDOR CLICK
	public void onRowSelectCantidadUnidadPresentacionClick(SelectEvent event) {
		try {
			CantidadUnidadPresentacion cliente = (CantidadUnidadPresentacion) event.getObject();
			System.out.println("onRowSelectCantidadUnidadPresentacionClick  " + cliente.getId());
			selectedCantidadUnidadPresentacion = cliente;
			newCantidadUnidadPresentacion = em.find(CantidadUnidadPresentacion.class, cliente.getId());
			newCantidadUnidadPresentacion.setFechaRegistro(new Date());
			newCantidadUnidadPresentacion.setUsuarioRegistro(usuarioSession.getLogin());

			tituloPanel = "Modificar CantidadUnidadPresentacion";
			modificar = true;

		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
			System.out.println("Error in onRowSelectGrupoProductoClick: "
					+ e.getMessage());
		}
	}
	
	public void registrarCantidadUnidadPresentacion(){
        try {
        	System.out.println("Ingreso a registrarCantidadUnidadPresentacion: "+idPresentacion+", "+idUnidadMeddida);
        	Presentacion p= em.find(Presentacion.class, this.idPresentacion);
        	UnidadMedida u = em.find(UnidadMedida.class, this.idUnidadMeddida);
        	System.out.println("Presentacion:"+p.getDescripcion());
        	System.out.println("Unidad Medida: "+u.getDescripcion());
        	newCantidadUnidadPresentacion.setPresentacion(p);
        	newCantidadUnidadPresentacion.setUnidadMedida(u);
        	
        	//#{_cantidadUnidad.presentacion.descripcion.toUpperCase()} #{_cantidadUnidad.cantidad} #{_cantidadUnidad.unidadMedida.descripcion.toUpperCase()
        	newCantidadUnidadPresentacion.setDescripcion(p.getDescripcion().toUpperCase()+" "+String.valueOf(newCantidadUnidadPresentacion.getCantidad()).toUpperCase()+" "+u.getDescripcion().toUpperCase());
        	cantUnitPresRegistration.register(newCantidadUnidadPresentacion);
        	
           
        	FacesMessage m = new FacesMessage(FacesMessage.SEVERITY_INFO, "Registered!", "Registration successful");
            facesContext.addMessage(null, m);
            pushEventCantidadUnidadPresentacion.fire(String.format("Nuevo CantidadUnidadPresentacion Registrado: %s (id: %d)", newCantidadUnidadPresentacion.getCantidad(), newCantidadUnidadPresentacion.getId()));
            
            initNewCantidadUnidadPresentacions();
            
        } catch (Exception e) {
            String errorMessage = getRootErrorMessage(e);
            FacesMessage m = new FacesMessage(FacesMessage.SEVERITY_ERROR, errorMessage, "Registro Incorrecto.");
            facesContext.addMessage(null, m);
        }
    }
	
	public void modificarCantidadUnidadPresentacion(){
        try {
        	System.out.println("Ingreso a modificarCantidadUnidadPresentacion: "+newCantidadUnidadPresentacion.getId());
        	cantUnitPresRegistration.updated(newCantidadUnidadPresentacion);
            FacesMessage m = new FacesMessage(FacesMessage.SEVERITY_INFO, "Modificado!", "Modificado successful");
            facesContext.addMessage(null, m);
            pushEventCantidadUnidadPresentacion.fire(String.format("CantidadUnidadPresentacion Modificado: %s (id: %d)", newCantidadUnidadPresentacion.getCantidad(), newCantidadUnidadPresentacion.getId()));
            initNewCantidadUnidadPresentacions();
            
        } catch (Exception e) {
            String errorMessage = getRootErrorMessage(e);
            FacesMessage m = new FacesMessage(FacesMessage.SEVERITY_ERROR, errorMessage, "Modificado Incorrecto.");
            facesContext.addMessage(null, m);
        }
    }
	
	public void eliminarCantidadUnidadPresentacion(){
        try {
        	System.out.println("Ingreso a eliminarCantidadUnidadPresentacion: "+newCantidadUnidadPresentacion.getId());
        	cantUnitPresRegistration.remover(newCantidadUnidadPresentacion);
            FacesMessage m = new FacesMessage(FacesMessage.SEVERITY_INFO, "Borrado!", "Borrado successful");
            facesContext.addMessage(null, m);
            pushEventCantidadUnidadPresentacion.fire(String.format("CantidadUnidadPresentacion Borrado: %s (id: %d)", newCantidadUnidadPresentacion.getCantidad(), newCantidadUnidadPresentacion.getId()));
            initNewCantidadUnidadPresentacions();
            
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

	public CantidadUnidadPresentacion getNewCantidadUnidadPresentacion() {
		return newCantidadUnidadPresentacion;
	}

	public void setNewCantidadUnidadPresentacion(CantidadUnidadPresentacion newCantidadUnidadPresentacion) {
		this.newCantidadUnidadPresentacion = newCantidadUnidadPresentacion;
	}

	public CantidadUnidadPresentacion getSelectedCantidadUnidadPresentacion() {
		return selectedCantidadUnidadPresentacion;
	}

	public void setSelectedCantidadUnidadPresentacion(CantidadUnidadPresentacion selectedCantidadUnidadPresentacion) {
		this.selectedCantidadUnidadPresentacion = selectedCantidadUnidadPresentacion;
	}

	public List<UnidadMedida> getListaUnidadMedida() {
		return listaUnidadMedida;
	}

	public void setListaUnidadMedida(List<UnidadMedida> listaUnidadMedida) {
		this.listaUnidadMedida = listaUnidadMedida;
	}

	public List<Presentacion> getListaPresentacion() {
		return listaPresentacion;
	}

	public void setListaPresentacion(List<Presentacion> listaPresentacion) {
		this.listaPresentacion = listaPresentacion;
	}

	public Integer getIdUnidadMeddida() {
		return idUnidadMeddida;
	}

	public void setIdUnidadMeddida(Integer idUnidadMeddida) {
		this.idUnidadMeddida = idUnidadMeddida;
	}

	public Integer getIdPresentacion() {
		return idPresentacion;
	}

	public void setIdPresentacion(Integer idPresentacion) {
		this.idPresentacion = idPresentacion;
	}
	
	
}
