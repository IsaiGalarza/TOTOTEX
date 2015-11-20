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

import bo.buffalo.data.MargenUtilidadRepository;
import bo.buffalo.data.ProveedorRepository;
import bo.buffalo.data.UsuarioRepository;
import bo.buffalo.model.HistoriaMargenUtilidad;
import bo.buffalo.model.Proveedor;
import bo.buffalo.model.Usuario;
import bo.buffalo.service.MargenUtilidadRegistration;

@Named(value = "margenUtilidadController")
@ConversationScoped
public class MargenUtilidadController implements Serializable {

	private static final long serialVersionUID = 3204710129518351932L;

	public static final String PUSH_CDI_TOPIC = "pushCdi";

	@Inject
	private FacesContext facesContext;

	@Inject
	private EntityManager em;

	@Inject
	Conversation conversation;

	@Inject
	MargenUtilidadRegistration margenUtilidadRegistration;

	@Inject
	MargenUtilidadRepository margenUtilidadRepository;

	@Inject
	UsuarioRepository usuarioRepository;
	private Usuario usuarioSession;

	@Inject
	@Push(topic = PUSH_CDI_TOPIC)
	Event<String> pushEventSucursal;

	private boolean modificar = false;
	private String tituloPanel = "Registrar Margen Utilidad";
	private HistoriaMargenUtilidad selectedMedico;
	private HistoriaMargenUtilidad newHistoria;
	private List<Proveedor> listProveedor = new ArrayList<Proveedor>();
	@Inject
	private ProveedorRepository proveedorRepository;

	private List<HistoriaMargenUtilidad> margenUtilidad;

	private HistoriaMargenUtilidad historias=new HistoriaMargenUtilidad();

	// @Named provides access the return value via the EL variable name
	// "servicios" in the UI (e.g.
	// Facelets or JSP view)
	@Produces
	@Named
	public List<HistoriaMargenUtilidad> getMargenUtilidad() {
		return margenUtilidad;
	}

	@PostConstruct
	public void initNewHistoria() {

		// initConversation();
		beginConversation();

		HttpServletRequest request = (HttpServletRequest) facesContext
				.getExternalContext().getRequest();
		System.out.println("init NewHistoria*********************************");
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

		newHistoria = new HistoriaMargenUtilidad();
		newHistoria.setEstado("AC");
		newHistoria.setFechaRegistro(new Date());
		newHistoria.setUsuarioRegistro(usuarioSession.getLogin());
		listProveedor = proveedorRepository.findAllOrderedByID();
		// traer todos los servicios ordenados por nombre
		margenUtilidad = margenUtilidadRepository.findAllOrderedByID();
		modificar = false;
		historias= new HistoriaMargenUtilidad();
		tituloPanel = "Registrar Margen Utilidad";
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
	public void onRowSelectMedicoClick(SelectEvent event) {
		try {
			historias = (HistoriaMargenUtilidad) event
					.getObject();
			System.out.println("onRowSelectMedicoClick  " + historias.getId());
			selectedMedico = historias;
			
			newHistoria = em.find(HistoriaMargenUtilidad.class, historias.getId());
			newHistoria.setFechaRegistro(new Date());
			newHistoria.setUsuarioRegistro(usuarioSession.getLogin());

			tituloPanel = "Modificar Margen Utilidad";
			modificar = true;

		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
			System.out.println("Error in onRowSelectMedicoClick: "
					+ e.getMessage());
		}
	}

	public void registrarMedico() {
		try {
			System.out.println("Ingreso a registrarMedico: "
					+ newHistoria.getId());
			Proveedor pro = em.find(Proveedor.class, newHistoria.getProveedor()
					.getId());
			HistoriaMargenUtilidad his = margenUtilidadRepository
					.findAllOrderedByID(pro);
			System.out.println("Paso..." + his);
			if (his != null) {
				margenUtilidadRegistration.enable(his);
			}

			margenUtilidadRegistration.register(newHistoria);
			FacesMessage m = new FacesMessage(FacesMessage.SEVERITY_INFO,
					"Registered!", "Registration successful");
			facesContext.addMessage(null, m);
			pushEventSucursal.fire(String.format(
					"Nuevo Medico Registrado: %s (id: %d)", newHistoria.getId(),
					newHistoria.getId()));
			initNewHistoria();
		} catch (Exception e) {
			String errorMessage = getRootErrorMessage(e);
			FacesMessage m = new FacesMessage(FacesMessage.SEVERITY_ERROR,
					errorMessage, "Registro Incorrecto.");
			facesContext.addMessage(null, m);
		}
	}

	public void modificarMedico() {
		try {
			System.out.println("Ingreso a modificarMedico: "
					+ newHistoria.getId());

			margenUtilidadRegistration.enable(historias);
			newHistoria.setId(null);
			margenUtilidadRegistration.register(newHistoria);
			FacesMessage m = new FacesMessage(FacesMessage.SEVERITY_INFO,
					"Modificado!", "Modificado successful");
			facesContext.addMessage(null, m);
			pushEventSucursal.fire(String.format(
					"Producto Modificado: %s (id: %d)", newHistoria.getId(),
					newHistoria.getId()));
			initNewHistoria();
		} catch (Exception e) {
			String errorMessage = getRootErrorMessage(e);
			FacesMessage m = new FacesMessage(FacesMessage.SEVERITY_ERROR,
					errorMessage, "Modificado Incorrecto.");
			facesContext.addMessage(null, m);
		}
	}

	public void eliminarMedico() {
		try {
			System.out
					.println("Ingreso a eliminarMedico: " + newHistoria.getId());
			margenUtilidadRegistration.remover(newHistoria);
			FacesMessage m = new FacesMessage(FacesMessage.SEVERITY_INFO,
					"Borrado!", "Borrado successful");
			facesContext.addMessage(null, m);
			pushEventSucursal.fire(String.format("Medico Borrado: %s (id: %d)",
					newHistoria.getId(), newHistoria.getId()));
			initNewHistoria();
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
	public HistoriaMargenUtilidad getNewHistoria() {
		return newHistoria;
	}

	public void setNewHistoria(HistoriaMargenUtilidad newHistoria) {
		this.newHistoria = newHistoria;
	}

	public String getTituloPanel() {
		return tituloPanel;
	}

	public void setTituloPanel(String tituloPanel) {
		this.tituloPanel = tituloPanel;
	}

	public HistoriaMargenUtilidad getSelectedMedico() {
		return selectedMedico;
	}

	public void setSelectedMedico(HistoriaMargenUtilidad selectedMedico) {
		this.selectedMedico = selectedMedico;
	}

	public boolean isModificar() {
		return modificar;
	}

	public void setModificar(boolean modificar) {
		this.modificar = modificar;
	}

	public List<Proveedor> getListProveedor() {
		return listProveedor;
	}

	public void setListProveedor(List<Proveedor> listProveedor) {
		this.listProveedor = listProveedor;
	}

}
