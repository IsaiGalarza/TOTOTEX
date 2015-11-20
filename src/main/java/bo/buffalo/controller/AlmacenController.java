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

import com.ahosoft.utils.FacesUtil;

import bo.buffalo.data.AlmacenRepository;
import bo.buffalo.data.AlmacenSucursalRepository;
import bo.buffalo.data.SucursalRepository;
import bo.buffalo.data.UsuarioRepository;
import bo.buffalo.model.Almacen;
import bo.buffalo.model.AlmacenSucursal;
import bo.buffalo.model.Sucursal;
import bo.buffalo.model.Usuario;
import bo.buffalo.service.AlmacenRegistration;
import bo.buffalo.service.AlmacenSucursalRegistration;

@Named(value = "almacenController")
@ConversationScoped
public class AlmacenController implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -7819149623543804669L;

	public static final String PUSH_CDI_TOPIC = "pushCdi";

	@Inject
	private FacesContext facesContext;

	@Inject
	private EntityManager em;

	@Inject
	Conversation conversation;

	@Inject
	private AlmacenRegistration almacenRegistration;

	@Inject
	private AlmacenRepository almacenRepository;

	@Inject
	UsuarioRepository usuarioRepository;
	private Usuario usuarioSession;

	@Inject
	@Push(topic = PUSH_CDI_TOPIC)
	Event<String> pushEventSucursal;

	private boolean modificar = false;
	private boolean nuevo;
	private String tituloPanel = "Registrar Almacen";
	private Almacen selectedAlmacen;
	private Almacen newAlmacen;
	private List<Usuario> listUsuario = new ArrayList<Usuario>();

	private Integer idSucursal;
	private List<Sucursal> listSucursales = new ArrayList<Sucursal>();

	@Inject
	private SucursalRepository sucursalRepository;

	@Inject
	private AlmacenSucursalRegistration almacenSucursalRegistration;

	@Inject
	private AlmacenSucursalRepository almacenSucursalRepository;

	private List<Almacen> listaAlmacen;

	private AlmacenSucursal almacenSucursal;

	// @Named provides access the return value via the EL variable name
	// "servicios" in the UI (e.g.
	// Facelets or JSP view)
	@Produces
	@Named
	public List<Almacen> getListaAlmacen() {
		return listaAlmacen;
	}

	@PostConstruct
	public void initNewAlmacen() {

		// initConversation();
		beginConversation();

		HttpServletRequest request = (HttpServletRequest) facesContext
				.getExternalContext().getRequest();
		System.out
				.println("init Tipo Producto*********************************");
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

		newAlmacen = new Almacen();
		newAlmacen.setEstado("AC");
		newAlmacen.setFechaRegistro(new Date());
		newAlmacen.setUsuarioRegistro(usuarioSession.getLogin());
		newAlmacen.setUsuario(usuarioSession); 

		almacenSucursal = new AlmacenSucursal();
		almacenSucursal.setEstado("AC");
		almacenSucursal.setFechaRegistro(new Date());
		almacenSucursal.setUsuarioRegistro(usuarioSession.getLogin());

		// tituloPanel
		selectedAlmacen = null;
		tituloPanel = "Registrar Almacen";

		// traer todos las almacenes ordenados por ID Desc
		listaAlmacen = almacenRepository.findAllOrderedByID();
		
		setListSucursales(sucursalRepository.traerSucursalesActivas());
		modificar = false;
	}

	public void cancelar(){
		newAlmacen = new Almacen();
		newAlmacen.setEstado("AC");
		newAlmacen.setFechaRegistro(new Date());
		newAlmacen.setUsuarioRegistro(usuarioSession.getLogin());
		newAlmacen.setUsuario(usuarioSession); 

		almacenSucursal = new AlmacenSucursal();
		almacenSucursal.setEstado("AC");
		almacenSucursal.setFechaRegistro(new Date());
		almacenSucursal.setUsuarioRegistro(usuarioSession.getLogin());
		selectedAlmacen = null;
		tituloPanel = "Registrar Almacen";
		FacesUtil.resetComponent("formRegistroTipoProducto:data");
	}
	public void obtenerUsuarios(){
		try {
			System.out.println("Ingreso a obtenerUsuarios");
			listUsuario = usuarioRepository.traerUsuariosPorSucursal(almacenSucursal.getSucursal());
		} catch (Exception e) {
			System.err.println("Error en obtenerUsuarios : "+e.getMessage());
			e.getStackTrace();
		}
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

	public void updatedCantidad() {
		/*
		 * if(!newAlmacen.isShowCantidad()){ newAlmacen.setCantidadCaja(0); }
		 */
	}

	// SELECT PRESENTACION CLICK
	public void onRowSelectAlmacenClick(SelectEvent event) {
		try {
			Almacen almacen = (Almacen) event.getObject();
			System.out.println("onRowSelectAlmacenClick  " + almacen.getId());
			selectedAlmacen = almacen;
			newAlmacen = em.find(Almacen.class, almacen.getId());
			newAlmacen.setFechaRegistro(new Date());
			newAlmacen.setUsuarioRegistro(usuarioSession.getLogin());

			tituloPanel = "Modificar Almacen";
			
			modificar = true;
			setNuevo(false);
			
			almacenSucursal = almacenSucursalRepository.findForAlmacen(newAlmacen);
			Sucursal sucursal=em.find(Sucursal.class, almacenSucursal.getSucursal().getId());
			almacenSucursal.setSucursal(sucursal);
			almacenSucursal.setEstado("AC");
			almacenSucursal.setFechaRegistro(new Date());
			almacenSucursal.setUsuarioRegistro(usuarioSession.getLogin());
			obtenerUsuarios();

		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
			System.out.println("Error in onRowSelectAlmacenClick: "
					+ e.getMessage());
		}
	}

	public void registrarAlmacen() {
		try {
			System.out.println("Ingreso a registrarAlmacen: ");
			almacenRegistration.register(newAlmacen);

			almacenSucursal.setAlmacen(newAlmacen);
			Sucursal suc = em.find(Sucursal.class, this.almacenSucursal.getSucursal().getId());
			almacenSucursal.setSucursal(suc);
			almacenSucursalRegistration.register(almacenSucursal);
			FacesMessage m = new FacesMessage(FacesMessage.SEVERITY_INFO,
					"Almacen Registrado!", newAlmacen.getNombre()+"!");
			facesContext.addMessage(null, m);
			
			initNewAlmacen();
		} catch (Exception e) {
			String errorMessage = getRootErrorMessage(e);
			FacesMessage m = new FacesMessage(FacesMessage.SEVERITY_ERROR,
					errorMessage, "Registro Incorrecto.");
			facesContext.addMessage(null, m);
		}
	}
	
	public void updateAlmacen(){
		try {
			System.out.println("Ingreso a Update Almacen....");
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}
	}
	
	public void modificarAlmacen() {
		try {
			System.out.println("Ingreso a modificarAlmacen: "
					+ newAlmacen.getId());
			
			newAlmacen.setEncargado(em.find(Usuario.class, newAlmacen.getEncargado().getId()));
			almacenRegistration.updated(newAlmacen);
			selectedAlmacen = null;
			almacenSucursal.setAlmacen(newAlmacen);
			Sucursal suc = em.find(Sucursal.class, this.almacenSucursal.getSucursal().getId());
			almacenSucursal.setSucursal(suc);
			almacenSucursalRegistration.updated(almacenSucursal);
			FacesMessage m = new FacesMessage(FacesMessage.SEVERITY_INFO,
					"Almacen Modificado!", newAlmacen.getNombre()+"!");
			facesContext.addMessage(null, m);
			initNewAlmacen();

		} catch (Exception e) {
			e.printStackTrace();
			String errorMessage = getRootErrorMessage(e);
			FacesMessage m = new FacesMessage(FacesMessage.SEVERITY_ERROR,
					errorMessage, "Modificado Incorrecto.");
			facesContext.addMessage(null, m);
		}
	}

	public void eliminarAlmacen() {
		try {
			System.out.println("Ingreso a eliminarAlmacen: "
					+ newAlmacen.getId());
			almacenRegistration.remover(newAlmacen);
			FacesMessage m = new FacesMessage(FacesMessage.SEVERITY_INFO,
					"Almacen Borrado!", newAlmacen.getNombre()+"!");
			facesContext.addMessage(null, m);
			initNewAlmacen();

		} catch (Exception e) {
			String errorMessage = getRootErrorMessage(e);
			FacesMessage m = new FacesMessage(FacesMessage.SEVERITY_ERROR,
					errorMessage, "Borrado Incorrecto.");
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

	// get and set
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

	public Almacen getSelectedAlmacen() {
		return selectedAlmacen;
	}

	public void setSelectedAlmacen(Almacen selectedAlmacen) {
		this.selectedAlmacen = selectedAlmacen;
	}

	public Almacen getNewAlmacen() {
		return newAlmacen;
	}

	public void setNewAlmacen(Almacen newAlmacen) {
		this.newAlmacen = newAlmacen;
	}

	public List<Usuario> getListUsuario() {
		return listUsuario;
	}

	public void setListUsuario(List<Usuario> listUsuario) {
		this.listUsuario = listUsuario;
	}

	public AlmacenSucursal getAlmacenSucursal() {
		return almacenSucursal;
	}

	public void setAlmacenSucursal(AlmacenSucursal almacenSucursal) {
		this.almacenSucursal = almacenSucursal;
	}

	public List<Sucursal> getListSucursales() {
		return listSucursales;
	}

	public void setListSucursales(List<Sucursal> listSucursales) {
		this.listSucursales = listSucursales;
	}

	public Integer getIdSucursal() {
		return idSucursal;
	}

	public void setIdSucursal(Integer idSucursal) {
		this.idSucursal = idSucursal;
	}

	public boolean isNuevo() {
		return nuevo;
	}

	public void setNuevo(boolean nuevo) {
		this.nuevo = nuevo;
	}

}
