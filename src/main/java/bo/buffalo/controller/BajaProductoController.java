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

import bo.buffalo.data.AlmacenProductoRepository;
import bo.buffalo.data.AlmacenRepository;
import bo.buffalo.data.BajaProductoRepository;
import bo.buffalo.data.ProductoProveedorRepository;
import bo.buffalo.data.UsuarioRepository;
import bo.buffalo.model.AlmProducto;
import bo.buffalo.model.Almacen;
import bo.buffalo.model.BajaProductos;
import bo.buffalo.model.CardexProducto;
import bo.buffalo.model.LineasProveedor;
import bo.buffalo.model.Producto;
import bo.buffalo.model.ProductoProveedor;
import bo.buffalo.model.Proveedor;
import bo.buffalo.model.Usuario;
import bo.buffalo.service.AlmacenProductoRegistration;
import bo.buffalo.service.BajaProductoRegistration;
import bo.buffalo.service.CardexProductoRegistration;

@Named(value = "bajaProductoController")
@ConversationScoped
public class BajaProductoController implements Serializable {

	private static final long serialVersionUID = 3204710129518351932L;

	public static final String PUSH_CDI_TOPIC = "pushCdi";

	@Inject
	private FacesContext facesContext;

	@Inject
	private EntityManager em;

	private String selectedOption;

	@Inject
	Conversation conversation;

	@Inject
	BajaProductoRegistration bajaProductoRegistration;

	@Inject
	BajaProductoRepository bajaProductoRepository;

	@Inject
	UsuarioRepository usuarioRepository;
	private Usuario usuarioSession;

	@Inject
	@Push(topic = PUSH_CDI_TOPIC)
	Event<String> pushEventSucursal;

	private boolean modificar = false;
	private String tituloPanel = "Registrar Bajas";
	private BajaProductos selectedBajaProducto;
	private BajaProductos newBajaProducto;

	private List<Proveedor> listProveedor = new ArrayList<Proveedor>();

	private List<LineasProveedor> listLineasProveedor = new ArrayList<LineasProveedor>();
	
	@Inject
	private AlmacenRepository almacenRepository;

	private List<Producto> listProducto = new ArrayList<Producto>();

	@Inject
	private AlmacenProductoRepository almacenProductoRepository;

	private List<BajaProductos> listaBajaProducto;

	private BajaProductos historias = new BajaProductos();

	private Almacen almacen = new Almacen();

	private List<AlmProducto> listAlmProducto = new ArrayList<AlmProducto>();

	private Proveedor proveedor = new Proveedor();

	private Producto producto = new Producto();

	private LineasProveedor lineaProveedor = new LineasProveedor();

	private AlmProducto almacenProducto = new AlmProducto();

	@Inject
	private ProductoProveedorRepository productoProveedorRepository;

	@Inject
	private CardexProductoRegistration cardexProductoRegistration;
	
	@Inject
	private AlmacenProductoRegistration almacenProductoRegistration;

	// @Named provides access the return value via the EL variable name
	// "servicios" in the UI (e.g.
	// Facelets or JSP view)
	@Produces
	@Named
	public List<BajaProductos> getlistaBajaProducto() {
		return listaBajaProducto;
	}

	@PostConstruct
	public void initNewBajaProducto() {

		// initConversation();
		beginConversation();

		HttpServletRequest request = (HttpServletRequest) facesContext
				.getExternalContext().getRequest();
		System.out
				.println("init NewBajaProducto*********************************");
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
		almacen = new Almacen();
		producto = new Producto();
		proveedor = new Proveedor();
		lineaProveedor = new LineasProveedor();
		almacenProducto = new AlmProducto();
		newBajaProducto = new BajaProductos();
		newBajaProducto.setEstado("AC");
		newBajaProducto.setObservacion("BAJA DE PRODUCTO");
		newBajaProducto.setFechaRegistro(new Date());
		newBajaProducto.setUsuarioRegistro(usuarioSession.getLogin());
		newBajaProducto.setUsuario(usuarioSession);
		// traer todos los servicios ordenados por nombre
		listaBajaProducto = bajaProductoRepository.findAllOrderedByID();
		modificar = false;
		historias = new BajaProductos();
		tituloPanel = "Registrar Bajas de Producto";
		almacen = almacenRepository.findAlmacenForUser(usuarioSession);
		listAlmProducto = new ArrayList<AlmProducto>();
		obtenerAlmacenProductos();
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

	public void obtenerAlmacenProductos() {
		try {
			System.out.println("Ingresa as selectAlmacen");
			listAlmProducto = almacenProductoRepository.findForAlmacen(almacen);
			System.out.println("Size: " + listAlmProducto.size());

		} catch (Exception e) {
			System.out.println("Error as selectAlmacen : " + e.getMessage());
		}
	}

	public void selectProducto() {
		try {
			almacenProducto = em.find(AlmProducto.class,
					almacenProducto.getId());
			ProductoProveedor pp = productoProveedorRepository
					.findProductoProveedor(almacenProducto.getProducto(),
							almacenProducto.getProveedor(),
							almacenProducto.getLineaProvedor());
			newBajaProducto.setPrecioCompra(pp.getPrecioUnitarioCompra());
			newBajaProducto.setAlmProductos(almacenProducto);
		} catch (Exception e) {
			System.out.println("Error as selectAlmacen : " + e.getMessage());
		}
	}

	public void selectProveedor() {
		try {
			proveedor = em.find(Proveedor.class, this.proveedor.getId());
			System.out.println("Ingresa as selectAlmacen");
			listLineasProveedor = almacenProductoRepository
					.findLineaProveedorProductoForAlmacen(almacen, producto,
							proveedor);
		} catch (Exception e) {
			System.out.println("Error as selectAlmacen : " + e.getMessage());
		}
	}

	public void selectLinea() {
		try {
			System.out.println("Ingresa as selectAlmacen");
			lineaProveedor = em.find(LineasProveedor.class,
					this.lineaProveedor.getId());
			listLineasProveedor = almacenProductoRepository
					.findAllLineaProveedorProductoForAlmacen(almacen, producto,
							proveedor, lineaProveedor);

			;
		} catch (Exception e) {
			System.out.println("Error as selectAlmacen : " + e.getMessage());
		}
	}

	// SELECT SERVICIO CLICK
	public void onRowSelectBajaProductoClick(SelectEvent event) {
		try {
			historias = (BajaProductos) event.getObject();
			System.out.println("onRowSelectBajaProductoClick  "
					+ historias.getId());
			selectedBajaProducto = historias;

			newBajaProducto = em.find(BajaProductos.class, historias.getId());
			newBajaProducto.setFechaRegistro(new Date());
			newBajaProducto.setUsuarioRegistro(usuarioSession.getLogin());
			almacen = newBajaProducto.getAlmProductos().getAlmacen();
			obtenerAlmacenProductos();
			almacenProducto = newBajaProducto.getAlmProductos();
			System.out.println(almacenProducto.getId());
			tituloPanel = "Modificar Personal BajaProducto";
			modificar = true;

		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
			System.out.println("Error in onRowSelectBajaProductoClick: "
					+ e.getMessage());
		}
	}

	private void egresoAlmacen() {
		try {

			System.out.println("Ingreso a ingresoAlmacen..");
			AlmProducto almProd;

			System.out.println("existe:" + newBajaProducto.getCantidad() + " , " + newBajaProducto.getPrecioCompra());
			almProd = newBajaProducto.getAlmProductos();
			almProd.setAlmacen(newBajaProducto.getAlmProductos().getAlmacen());
			almProd.setEstado("AC");
			almProd.setStock(almProd.getStock() - newBajaProducto.getCantidad());
			almacenProductoRegistration.updated(almProd);
			registrarCardex();
			System.out.println("Actualizo AlmacenProducto Ingreso");

			
			System.out.println("Ingreso Registrado.. cardex");
		} catch (Exception e) {
			System.err.println("Error en ingresoAlmacen :" + e.getMessage());
		}
	}

	public void registrarBajaProducto() {
		try {
			System.out.println("Ingreso a registrarBajaProducto: "
					+ newBajaProducto.getId());

			bajaProductoRegistration.register(newBajaProducto);
			egresoAlmacen();
			FacesMessage m = new FacesMessage(FacesMessage.SEVERITY_INFO,
					"Registered!", "Registration successful");
			facesContext.addMessage(null, m);
			pushEventSucursal.fire(String.format(
					"Nuevo BajaProducto Registrado: %s (id: %d)",
					newBajaProducto.getId(), newBajaProducto.getId()));
			initNewBajaProducto();
		} catch (Exception e) {
			String errorMessage = getRootErrorMessage(e);
			FacesMessage m = new FacesMessage(FacesMessage.SEVERITY_ERROR,
					errorMessage, "Registro Incorrecto.");
			facesContext.addMessage(null, m);
		}
	}

	private void registrarCardex() {
		try {
			System.out.println("Ingreso a registrarCardex");
			CardexProducto cardex = new CardexProducto();
			cardex.setTransaccion("TRANSACCION : "+newBajaProducto.getObservacion()+" BJ-"+newBajaProducto.getId());
			cardex.setAlmacen(newBajaProducto.getAlmProductos().getAlmacen());
			cardex.setEstado("AC");
			cardex.setPrecioCompra(newBajaProducto.getPrecioCompra());
			cardex.setFechaRegistro(new Date());
			cardex.setUsuarioRegistro(usuarioSession.getName());
			cardex.setProducto(newBajaProducto.getAlmProductos().getProducto());
			cardex.setPrecioVenta(0);
			cardex.setCantidad(newBajaProducto.getCantidad());
			cardex.setStockAnterior(newBajaProducto.getAlmProductos()
					.getStock() );
			cardex.setStockActual(newBajaProducto.getAlmProductos().getStock()- newBajaProducto.getCantidad());
			cardex.setNumeroTransaccion(newBajaProducto.getId());
			cardex.setProveedor(newBajaProducto.getAlmProductos()
					.getProveedor());
			cardex.setLineaProveedor(newBajaProducto.getAlmProductos()
					.getLineaProvedor());
			cardex.setTipoMovimiento("SALIDA");
			cardex.setMovimiento("OS"); //ORDEN DE SALIDA
			// CARDEX

			cardexProductoRegistration.register(cardex);
		} catch (Exception e) {
			System.err.println("Error en registrarCardex : " + e.getMessage());
			e.printStackTrace();
		}
	}
	
	public void verificarCantidad(){
		try {
			System.out.println("Ingreso a verificarCantidad");
			if (newBajaProducto.getCantidad().doubleValue()>newBajaProducto.getAlmProductos().getStock()) {
				
				FacesMessage m = new FacesMessage(FacesMessage.SEVERITY_ERROR,
						"Revise la cantidad!", newBajaProducto.getCantidad()+" es mayor que "+newBajaProducto.getAlmProductos().getStock());
						facesContext.addMessage(null, m);
					newBajaProducto.setCantidad(0);
			}
		} catch (Exception e) {
			System.err.println("Error en verificarCantidad "+ e.getMessage());
			e.getStackTrace();
		}
	}

	public void modificarBajaProducto() {
		try {
			System.out.println("Ingreso a modificarBajaProducto: "
					+ newBajaProducto.getId());
			bajaProductoRegistration.updated(newBajaProducto);
			FacesMessage m = new FacesMessage(FacesMessage.SEVERITY_INFO,
					"Modificado!", "Modificado successful");
			facesContext.addMessage(null, m);
			pushEventSucursal.fire(String.format(
					"Producto Modificado: %s (id: %d)",
					newBajaProducto.getId(), newBajaProducto.getId()));
			initNewBajaProducto();
		} catch (Exception e) {
			String errorMessage = getRootErrorMessage(e);
			FacesMessage m = new FacesMessage(FacesMessage.SEVERITY_ERROR,
					errorMessage, "Modificado Incorrecto.");
			facesContext.addMessage(null, m);
		}
	}

	public void eliminarBajaProducto() {
		try {
			System.out.println("Ingreso a eliminarBajaProducto: "
					+ newBajaProducto.getId());
			bajaProductoRegistration.remover(newBajaProducto);
			FacesMessage m = new FacesMessage(FacesMessage.SEVERITY_INFO,
					"Borrado!", "Borrado successful");
			facesContext.addMessage(null, m);
			pushEventSucursal.fire(String.format(
					"BajaProducto Borrado: %s (id: %d)",
					newBajaProducto.getId(), newBajaProducto.getId()));
			initNewBajaProducto();
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
	public BajaProductos getNewBajaProducto() {
		return newBajaProducto;
	}

	public void setNewBajaProducto(BajaProductos newBajaProducto) {
		this.newBajaProducto = newBajaProducto;
	}

	public String getTituloPanel() {
		return tituloPanel;
	}

	public void setTituloPanel(String tituloPanel) {
		this.tituloPanel = tituloPanel;
	}

	public BajaProductos getSelectedBajaProducto() {
		return selectedBajaProducto;
	}

	public void setSelectedBajaProducto(BajaProductos selectedBajaProducto) {
		this.selectedBajaProducto = selectedBajaProducto;
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

	public String getSelectedOption() {
		return selectedOption;
	}

	public void setSelectedOption(String selectedOption) {
		this.selectedOption = selectedOption;
	}

	public List<Producto> getListProducto() {
		return listProducto;
	}

	public void setListProducto(List<Producto> listProducto) {
		this.listProducto = listProducto;
	}

	public List<LineasProveedor> getListLineasProveedor() {
		return listLineasProveedor;
	}

	public void setListLineasProveedor(List<LineasProveedor> listLineasProveedor) {
		this.listLineasProveedor = listLineasProveedor;
	}

	public List<BajaProductos> getListaBajaProducto() {
		return listaBajaProducto;
	}

	public void setListaBajaProducto(List<BajaProductos> listaBajaProducto) {
		this.listaBajaProducto = listaBajaProducto;
	}

	public Almacen getAlmacen() {
		return almacen;
	}

	public void setAlmacen(Almacen almacen) {
		this.almacen = almacen;
	}

	public Proveedor getProveedor() {
		return proveedor;
	}

	public void setProveedor(Proveedor proveedor) {
		this.proveedor = proveedor;
	}

	public Producto getProducto() {
		return producto;
	}

	public void setProducto(Producto producto) {
		this.producto = producto;
	}

	public LineasProveedor getLineaProveedor() {
		return lineaProveedor;
	}

	public void setLineaProveedor(LineasProveedor lineaProveedor) {
		this.lineaProveedor = lineaProveedor;
	}

	public List<AlmProducto> getListAlmProducto() {
		return listAlmProducto;
	}

	public void setListAlmProducto(List<AlmProducto> listAlmProducto) {
		this.listAlmProducto = listAlmProducto;
	}

	public AlmProducto getAlmacenProducto() {
		return almacenProducto;
	}

	public void setAlmacenProducto(AlmProducto almacenProducto) {
		this.almacenProducto = almacenProducto;
	}

}
