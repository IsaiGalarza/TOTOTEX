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

import bo.buffalo.data.ConversionesRepository;
import bo.buffalo.data.UnidadMedidaRepository;
import bo.buffalo.data.UsuarioRepository;
import bo.buffalo.model.Conversiones;
import bo.buffalo.model.UnidadMedida;
import bo.buffalo.model.Usuario;
import bo.buffalo.service.ConversionesRegistration;

@Named(value = "conversionesController")
@ConversationScoped
public class ConversionesController implements Serializable {

	private static final long serialVersionUID = -5497426699770430529L;

	public static final String PUSH_CDI_TOPIC = "pushCdi";

	@Inject
	private FacesContext facesContext;

	@Inject
	private EntityManager em;

	@Inject
	Conversation conversation;

	@Inject
	private UnidadMedidaRepository unidadMedidaRepository;

	@Inject
	private ConversionesRegistration conversionesRegistration;

	@Inject
	private ConversionesRepository conversionesRepository;

	@Inject
	UsuarioRepository usuarioRepository;
	private Usuario usuarioSession;

	private UnidadMedida selectedUnidadMedida;
	@Inject
	@Push(topic = PUSH_CDI_TOPIC)
	Event<String> pushEventSucursal;

	private boolean modificar = false;
	private String tituloPanel = "Registrar Unidad Medida";
	private Conversiones selectedConversiones;
	private Conversiones newConversiones;

	private List<UnidadMedida> listUnidadesMedida;

	private List<Conversiones> listaConversiones;

	// LISTA PARA EL MODAL
	private List<UnidadMedida> listUnidadMedida;

	private boolean nuevo = false;

	// @Named provides access the return value via the EL variable name
	// "servicios" in the UI (e.g.
	// Facelets or JSP view)
	@Produces
	@Named
	public List<UnidadMedida> getListUnidadesMedida() {
		return listUnidadesMedida;
	}

	@PostConstruct
	public void initNewConversiones() {

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

		// traer todos los TipoProduct ordenados por ID
		listUnidadesMedida = unidadMedidaRepository.findAllOrderedByID();
		modificar = false;
		nuevo = false;
	}

	// SELECT TIPO PRODUCTO CLICK
	public void onRowSelectUnidadMedidaBDLClick(SelectEvent event) {
		try {
			UnidadMedida unidadMedida = (UnidadMedida) event.getObject();
			System.out.println("onRowSelectUnidadMedidaBDLClick  "
					+ unidadMedida.getId());
			setSelectedUnidadMedida(unidadMedida);

			listaConversiones = conversionesRepository
					.traerConversionesActivas(selectedUnidadMedida);
			listUnidadMedida = unidadMedidaRepository
					.traerUnidadMedidaActivas(selectedUnidadMedida);

			tituloPanel = "Modificar Unidad Medida";
			modificar = true;

		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
			System.out.println("Error in onRowSelectUnidadMedidaBDLClick: "
					+ e.getMessage());
		}
	}

	public void crearUnidad() {
		try {
			newConversiones = new Conversiones();
			newConversiones.setEstado("AC");
			newConversiones.setFechaRegistro(new Date());
			newConversiones.setUsuarioRegistro(usuarioSession.getLogin());

			// tituloPanel
			tituloPanel = "Registrar Area";
			setNuevo(true);
			modificar = false;
		} catch (Exception e) {
			// TODO: handle exception
		}
	}

	public void modificarEstado() {
		modificar = true;
		setNuevo(true);
	}
	
	
	public void eliminarItem(Conversiones conversiones){
		for (int i = 0; i < listaConversiones.size(); i++) {
			if (listaConversiones.get(i).getId().equals(conversiones.getId())) {
				conversionesRegistration.remover(listaConversiones.get(i));
				listaConversiones.remove(i);
				return;
			}
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

	// SELECT TIPO PRODUCTO CLICK
	public void onRowSelectConversionesClick(SelectEvent event) {
		try {
			Conversiones conversiones = (Conversiones) event.getObject();
			System.out.println("onRowSelectConversionesClick  "
					+ conversiones.getId());
			selectedConversiones = conversiones;
			newConversiones = em.find(Conversiones.class, conversiones.getId());
			newConversiones.setFechaRegistro(new Date());
			newConversiones.setUsuarioRegistro(usuarioSession.getLogin());

			tituloPanel = "Modificar Unidad Medida";
			modificar = true;
			setNuevo(false);

		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
			System.out.println("Error in onRowSelectConversionesClick: "
					+ e.getMessage());
		}
	}

	private UnidadMedida []selectListUnidadMedida;
	
	public void registrarConversiones() {
		try {
			System.out.println("Ingreso a registrarConversiones: "
					+ selectListUnidadMedida.length);
			for (int i = 0; i < selectListUnidadMedida.length; i++) {
				Conversiones conversiones= new Conversiones();
				conversiones.setEstado("AC");
				conversiones.setConversion(selectListUnidadMedida[i]);
				conversiones.setUnidadMedida(selectedUnidadMedida);
				conversiones.setEquivalente(1);
				conversiones.setUnidad(1);
				conversiones.setFechaRegistro(new Date());
				conversiones.setUsuarioRegistro(usuarioSession.getName());
				conversionesRegistration.register(conversiones);
			}
			
			listaConversiones = conversionesRepository
					.traerConversionesActivas(selectedUnidadMedida);
			selectListUnidadMedida= new UnidadMedida[0];
			listUnidadMedida.clear();
			
		} catch (Exception e) {
			String errorMessage = getRootErrorMessage(e);
			FacesMessage m = new FacesMessage(FacesMessage.SEVERITY_ERROR,
					errorMessage, "Registro Incorrecto.");
			facesContext.addMessage(null, m);
		}
	}
	
	public void updateConversion(Conversiones _um){
		try {
			System.out.println("Ingreso a updateConversion");
			_um.setFechaRegistro(new Date());
			_um.setUsuarioRegistro(usuarioSession.getName());
			conversionesRegistration.updated(_um);
		} catch (Exception e) {
			System.err.println("Error en updateConversion : "+e.getMessage());
		}
	}
	
	public void modificarConversiones() {
		try {
			System.out.println("Ingreso a modificarTipoProducto: "
					+ newConversiones.getId());
			conversionesRegistration.updated(newConversiones);
			FacesMessage m = new FacesMessage(FacesMessage.SEVERITY_INFO,
					"Modificado!", "Modificado successful");
			facesContext.addMessage(null, m);
			pushEventSucursal.fire(String.format(
					"Unidad Medida Modificada: %s (id: %d)", newConversiones
							.getUnidadMedida().getDescripcion(),
					newConversiones.getId()));
			initNewConversiones();

		} catch (Exception e) {
			String errorMessage = getRootErrorMessage(e);
			FacesMessage m = new FacesMessage(FacesMessage.SEVERITY_ERROR,
					errorMessage, "Modificado Incorrecto.");
			facesContext.addMessage(null, m);
		}
	}

	public void eliminarConversiones() {
		try {
			System.out.println("Ingreso a eliminarConversiones: "
					+ newConversiones.getId());
			conversionesRegistration.remover(newConversiones);
			FacesMessage m = new FacesMessage(FacesMessage.SEVERITY_INFO,
					"Borrado!", "Borrado successful");
			facesContext.addMessage(null, m);
			pushEventSucursal.fire(String.format(
					"Unidad de Medida Borrada: %s (id: %d)", newConversiones
							.getUnidadMedida().getDescripcion(),
					newConversiones.getId()));
			initNewConversiones();

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

	public Conversiones getSelectedConversiones() {
		return selectedConversiones;
	}

	public void setSelectedConversiones(Conversiones selectedConversiones) {
		this.selectedConversiones = selectedConversiones;
	}

	public Conversiones getNewConversiones() {
		return newConversiones;
	}

	public void setNewConversiones(Conversiones newConversiones) {
		this.newConversiones = newConversiones;
	}

	public boolean isNuevo() {
		return nuevo;
	}

	public void setNuevo(boolean nuevo) {
		this.nuevo = nuevo;
	}

	public UnidadMedida getSelectedUnidadMedida() {
		return selectedUnidadMedida;
	}

	public void setSelectedUnidadMedida(UnidadMedida selectedUnidadMedida) {
		this.selectedUnidadMedida = selectedUnidadMedida;
	}

	public List<Conversiones> getListaConversiones() {
		return listaConversiones;
	}

	public void setListaConversiones(List<Conversiones> listaConversiones) {
		this.listaConversiones = listaConversiones;
	}

	public List<UnidadMedida> getListUnidadMedida() {
		return listUnidadMedida;
	}

	public void setListUnidadMedida(List<UnidadMedida> listUnidadMedida) {
		this.listUnidadMedida = listUnidadMedida;
	}

	public UnidadMedida [] getSelectListUnidadMedida() {
		return selectListUnidadMedida;
	}

	public void setSelectListUnidadMedida(UnidadMedida [] selectListConversiones) {
		this.selectListUnidadMedida = selectListConversiones;
	}

}
