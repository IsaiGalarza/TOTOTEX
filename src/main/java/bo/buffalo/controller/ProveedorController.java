package bo.buffalo.controller;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

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

import bo.buffalo.data.GastosProveedorRepository;
import bo.buffalo.data.GastosRepository;
import bo.buffalo.data.MargenUtilidadRepository;
import bo.buffalo.data.ProductoProveedorRepository;
import bo.buffalo.data.ProveedorRepository;
import bo.buffalo.data.UsuarioRepository;
import bo.buffalo.model.Gastos;
import bo.buffalo.model.GastosProveedor;
import bo.buffalo.model.HistoriaMargenUtilidad;
import bo.buffalo.model.ProductoProveedor;
import bo.buffalo.model.Proveedor;
import bo.buffalo.model.Usuario;
import bo.buffalo.service.GastosProveedorRegistration;
import bo.buffalo.service.MargenUtilidadRegistration;
import bo.buffalo.service.ProductoProveedorRegistration;
import bo.buffalo.service.ProveedorRegistration;
import bo.buffalo.util.UtilCostosCalculation;

@Named(value = "proveedorController")
@ConversationScoped
public class ProveedorController implements Serializable {

	private static final long serialVersionUID = -1992094769231496851L;

	public static final String PUSH_CDI_TOPIC = "pushCdi";

	@Inject
	private FacesContext facesContext;

	@Inject
	private EntityManager em;

	@Inject
	Conversation conversation;

	@Inject
	private ProveedorRegistration proveedorRegistration;

	@Inject
	private ProveedorRepository proveedorRepository;

	@Inject
	UsuarioRepository usuarioRepository;
	private Usuario usuarioSession;

	@Inject
	@Push(topic = PUSH_CDI_TOPIC)
	Event<String> pushEventProveedor;

	private boolean modificar = false;
	private String tituloPanel = "Registrar Proveedor";
	private Proveedor selectedProveedor;
	private Proveedor newProveedor;

	private Integer max;
	private Integer min;

	private List<Proveedor> listaProveedores;

	// GASTOS
	private List<Gastos> listGastos = new ArrayList<Gastos>();
	private Gastos selectedGastos;
	@Inject
	private GastosRepository gastosRepository;

	// GASTOS PROVEEDOR
	private List<GastosProveedor> listGastosProveedor = new ArrayList<GastosProveedor>();
	private GastosProveedor selectedGastosProveedor;

	@Inject
	private GastosProveedorRegistration gastosProveedorRegistration;
	@Inject
	private GastosProveedorRepository gastosProveedorRepository;
	private boolean nuevo;
	private double margen_min; // margen de utilidad Minima
	private double margen_max;// margen de utilidad Maxima
	private @Inject MargenUtilidadRegistration margenUtilidadRegistration;
	private @Inject MargenUtilidadRepository margenUtilidadRepository;
	private List<HistoriaMargenUtilidad> margenUtilidad;
	private HistoriaMargenUtilidad historias = new HistoriaMargenUtilidad();
	private HistoriaMargenUtilidad newHistoria;
	private HistoriaMargenUtilidad newHistoria1;
	private boolean isnew;
	private @Inject ProductoProveedorRepository productoProveedorRepository;
	
	private List<ProductoProveedor> listProductoProveedor;
	private ProductoProveedor selectedProductoProveedor;
	private double precio;
	private boolean selectEstateProductoProveedor=false;
	private @Inject ProductoProveedorRegistration productoProveedorRegistration;
	// @Named provides access the return value via the EL variable name
	// "GrupoProducto" in the UI (e.g.
	// Facelets or JSP view)
	@Produces
	@Named
	public List<Proveedor> getListaProveedores() {
		return listaProveedores;
	}

	@PostConstruct
	public void initNewProveedores() {

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

		// traer todos las Proveedores ordenados por ID Desc
		listaProveedores = proveedorRepository.findAllOrderedByID();
		modificar = false;
		setNuevo(false);
		isnew = false;
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

	public void crearProveedor() {
		try {
			newProveedor = new Proveedor();
			newProveedor.setEstado("AC");
			newProveedor.setFechaRegistro(new Date());
			newProveedor.setUsuarioRegistro(usuarioSession.getLogin());

			// tituloPanel
			tituloPanel = "Registrar Proveedor";
			setNuevo(true);
			modificar = false;
			isnew = true;
			
			listGastosProveedor.clear();
			margenUtilidad.clear();

			newHistoria = new HistoriaMargenUtilidad();
			newHistoria.setEstado("AC");
			newHistoria.setFechaRegistro(new Date());
			newHistoria.setUsuarioRegistro(usuarioSession.getLogin());
		} catch (Exception e) {
			// TODO: handle exception
		}
	}
	
	public void verProductosProveedor(){
		if(selectedProveedor!=null){
			listProductoProveedor=productoProveedorRepository.traerTodosProductoProveedor(selectedProveedor);
			System.out.println("Cantidad de productos: "+listProductoProveedor.size());
		}else{
			FacesMessage m = new FacesMessage(FacesMessage.SEVERITY_INFO,
					"Seleccione!!", "Por Favor Seleccione un Proveedor.");
			facesContext.addMessage(null, m);
		}
	}

	public void selectProductoProveedor(){
		try {
			Map<String, String> params = FacesContext.getCurrentInstance().getExternalContext().getRequestParameterMap();
			int contentId = Integer.parseInt(params.get("contentId"));
			ProductoProveedor pr=productoProveedorRepository.findById(contentId);
			selectedProductoProveedor=pr;
			precio=selectedProductoProveedor.getPrecioUnitarioVenta();
			selectEstateProductoProveedor=true;
			System.out.println("Selec producto proveedor: "+pr.getId());
		} catch (Exception e) {
			System.out.println("Error select producto proveedor");
		}
		
	}
	public void closeDialogProductoProveedor(){
		System.out.println("Close dialog producto Proveedor");
		selectedProductoProveedor=null;
		selectEstateProductoProveedor=false;
	}
	
	public void updateProductoProveedor(){
		if(selectedProductoProveedor!=null){
			try {
				productoProveedorRegistration.updated(selectedProductoProveedor);
				listProductoProveedor=productoProveedorRepository.traerTodosProductoProveedor(selectedProductoProveedor.getProveedor());
				selectEstateProductoProveedor=false;
				FacesMessage m = new FacesMessage(FacesMessage.SEVERITY_INFO,
						"Modificado!", "Producto Proveedor Modificado");
				facesContext.addMessage(null, m);
				System.out.println("Actualizado Correctamente");
			} catch (Exception e) {
				FacesMessage m = new FacesMessage(FacesMessage.SEVERITY_INFO,
						"Error!", "No se pudo modificar el Producto Proveedor!");
				facesContext.addMessage(null, m);
				System.out.println("Ocurrio Un Error");
				System.out.println(e.toString());
			}
		}
	}
	
	public void calcularMargenUtilidadEdit() {
		try {
			System.out.println("Ingreso a calcularMargenUtilidad");
			double costo = 0;
			costo = proveedorRepository.obtenerCosto(selectedProductoProveedor
					.getProveedor());
			double margen = UtilCostosCalculation
					.calculateMargenUtilidad(selectedProductoProveedor, costo, precio,
							7);
			System.out.println("Margen de Utilidad : " + margen);
			selectedProductoProveedor.setUtilidadMax(margen);
		} catch (Exception e) {
			System.err.println("Error en calcularMargenUtilidad : "
					+ e.getMessage());
			e.getStackTrace();
		}
	}
	
	public void calcularPrecioVentaEdit() {
		try {
			System.out.println("Ingreso a calcularPrecioVenta");
			double costo = 0;
			costo = proveedorRepository.obtenerCosto(selectedProductoProveedor.getProveedor());
			double precioventa = (UtilCostosCalculation.calculatePrecioVenta(
					selectedProductoProveedor, costo, 7));
			System.out.println("Precio de Venta : " + precioventa);
			setPrecio(precioventa);
		} catch (Exception e) {
			System.err.println("Error en calcularPrecioVenta : "
					+ e.getMessage());
			e.getStackTrace();
		}
	}
	
	public void modificarEstado() {
		modificar = true;
		setNuevo(true);
		// traer GASTOS
		listGastosProveedor = gastosProveedorRepository
				.traerGastosProveedor(newProveedor);
		// traer todos los servicios ordenados por nombre

		margenUtilidad = margenUtilidadRepository
				.findForProveedorOrderedByID(newProveedor);
		max = newProveedor.getUtilidadMax();
		min = newProveedor.getUtilidadMin();

	}

	public void eliminarGastosProveedor(GastosProveedor gastosProveedor) {
		try {
			System.out.println("Ingreso a eliminarGastosProveedor");
			for (int i = 0; i < listGastosProveedor.size(); i++) {
				if (listGastosProveedor.get(i).getGastos().getId() == gastosProveedor
						.getGastos().getId()) {
					listGastosProveedor.remove(i);
				}
			}
		} catch (Exception e) {
			System.err.println("Error en eliminarGastosProveedor : "
					+ e.getMessage());
			e.getStackTrace();
		}
	}

	public void agregarGastos(Gastos gastos) {
		try {
			System.out.println("Ingreso a agregarGastos");
			GastosProveedor gastosProveedor = new GastosProveedor();
			gastosProveedor.setEstado("AC");
			gastosProveedor.setFechaRegistro(new Date());
			gastosProveedor.setUsuarioRegistro(usuarioSession.getName());
			gastosProveedor.setDescripcion(gastos.getDescripcion());
			gastosProveedor.setPorcentaje(gastos.getPorcentaje());
			gastosProveedor.setGastos(gastos);
			if (!existGatos(gastos)) {
				listGastosProveedor.add(gastosProveedor);
				removeGasto(gastos);
			}
			System.out.println("Size : " + listGastosProveedor.size());

		} catch (Exception e) {
			System.err.println("Error en agregarGastos :" + e.getMessage());
			e.getStackTrace();
		}
	}

	private boolean existGatos(Gastos gastos) {
		for (GastosProveedor value : listGastosProveedor) {
			if (value.getGastos().getId() == gastos.getId()) {
				return true;
			}
		}
		return false;
	}

	private void removeGasto(Gastos gastos) {
		for (int i = 0; i < listGastos.size(); i++) {
			if (listGastos.get(i).getId() == gastos.getId()) {
				listGastos.remove(i);
			}
		}
	}

	public void cargarListaCostos() {
		try {
			System.out.println("Ingreso a cargarListaCostos");
			listGastos = gastosRepository.traerGastos(newProveedor, isnew);
		} catch (Exception e) {
			System.err.println("Error en cargarListaCostos " + e.getMessage());
			e.getStackTrace();
		}
	}

	public void onRowSelectGastosProveedorClick(SelectEvent event) {
		try {
			GastosProveedor proveedor = (GastosProveedor) event.getObject();
			System.out.println("onRowSelectGastosProveedorClick  "
					+ proveedor.getGastos().getId());
			selectedGastosProveedor = proveedor;
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
			System.out.println("Error in onRowSelectGastosProveedorClick: "
					+ e.getMessage());
		}
	}

	public void onRowSelectGastosClick(SelectEvent event) {
		try {
			Gastos proveedor = (Gastos) event.getObject();
			System.out.println("onRowSelectGastosClick  " + proveedor.getId());
			selectedGastos = proveedor;
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
			System.out.println("Error in onRowSelectGastosClick: "
					+ e.getMessage());
		}
	}

	// SELECT PROVEEDOR CLICK
	public void onRowSelectProveedorClick(SelectEvent event) {
		try {
			Proveedor proveedor = (Proveedor) event.getObject();
			System.out.println("onRowSelectProveedorClick  "
					+ proveedor.getId());
			selectedProveedor = proveedor;
			newProveedor = em.find(Proveedor.class, proveedor.getId());
			newProveedor.setFechaRegistro(new Date());
			newProveedor.setUsuarioRegistro(usuarioSession.getLogin());

			tituloPanel = "Modificar Proveedor";
			modificar = true;
			setNuevo(false);
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
			System.out.println("Error in onRowSelectProveedorClick: "
					+ e.getMessage());
		}
	}

	// SELECT PROVEEDOR CLICK
	public void onRowSelectProveedorDBLClick(SelectEvent event) {
		try {
			Proveedor proveedor = (Proveedor) event.getObject();
			System.out.println("onRowSelectProveedorDBLClick  "
					+ proveedor.getId());
			selectedProveedor = proveedor;
			newProveedor = em.find(Proveedor.class, proveedor.getId());
			newProveedor.setFechaRegistro(new Date());
			newProveedor.setUsuarioRegistro(usuarioSession.getLogin());
			// TRAER GASTOS
			listGastosProveedor = gastosProveedorRepository
					.traerGastosProveedor(newProveedor);
			// traer todos los servicios ordenados por nombre
			margenUtilidad = margenUtilidadRepository
					.findForProveedorOrderedByID(newProveedor);
			max = newProveedor.getUtilidadMax();
			min = newProveedor.getUtilidadMin();

			tituloPanel = "Modificar Proveedor";
			modificar = true;
			setNuevo(true);

		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
			System.out.println("Error in onRowSelectProveedorDBLClick: "
					+ e.getMessage());
		}
	}

	public void registrarProveedor() {
		try {
			System.out.println("Ingreso a registrarProveedor: "
					+ newProveedor.getId());
			proveedorRegistration.register(newProveedor);
			for (GastosProveedor value : listGastosProveedor) {
				value.setProveedor(newProveedor);
				gastosProveedorRegistration.register(value);
			}
			
			FacesMessage m = new FacesMessage(FacesMessage.SEVERITY_INFO,
					"Registered!", "Registration successful");
			facesContext.addMessage(null, m);
			pushEventProveedor.fire(String.format(
					"Nuevo Proveedor Registrado: %s (id: %d)",
					newProveedor.getNombre(), newProveedor.getId()));

			// registrar Margen de Utilidad
			newHistoria = new HistoriaMargenUtilidad();
			newHistoria.setEstado("AC");
			newHistoria.setUsuarioRegistro(usuarioSession.getName());
			newHistoria.setFechaRegistro(new Date());
			newHistoria.setProveedor(newProveedor);
			newHistoria.setUtilidadMax(newProveedor.getUtilidadMax());
			newHistoria.setUtilidadMin(newProveedor.getUtilidadMin());
			margenUtilidadRegistration.register(newHistoria);

			initNewProveedores();

		} catch (Exception e) {
			String errorMessage = getRootErrorMessage(e);
			FacesMessage m = new FacesMessage(FacesMessage.SEVERITY_ERROR,
					errorMessage, "Registro Incorrecto.");
			facesContext.addMessage(null, m);
		}
	}

	public void modificarProveedor() {
		try {
			System.out.println("Ingreso a modificarProveedor: "
					+ newProveedor.getId());
			proveedorRegistration.updated(newProveedor);
			gastosProveedorRegistration.deleteAll(newProveedor);

			for (GastosProveedor value : listGastosProveedor) {
				value.setProveedor(newProveedor);
				value.setId(null);
				gastosProveedorRegistration.register(value);
			}
			
			newHistoria = new HistoriaMargenUtilidad();
			newHistoria.setEstado("AC");
			newHistoria.setUsuarioRegistro(usuarioSession.getName());
			newHistoria.setFechaRegistro(new Date());
			newHistoria.setProveedor(newProveedor);
			newHistoria.setUtilidadMax(max);
			newHistoria.setUtilidadMin(min);
		 if (!max.equals(newProveedor.getUtilidadMax())
					|| !min.equals(newProveedor.getUtilidadMin())) {				
				margenUtilidadRegistration.register(newHistoria);
			}
			System.out.println("termino");
			FacesMessage m = new FacesMessage(FacesMessage.SEVERITY_INFO,
					"Modificado!", "Modificado successful");
			facesContext.addMessage(null, m);
			pushEventProveedor.fire(String.format(
					"Proveedor Modificado: %s (id: %d)",
					newProveedor.getNombre(), newProveedor.getId()));

			initNewProveedores();
		} catch (Exception e) {
			String errorMessage = getRootErrorMessage(e);
			FacesMessage m = new FacesMessage(FacesMessage.SEVERITY_ERROR,
					errorMessage, "Modificado Incorrecto.");
			facesContext.addMessage(null, m);
			e.getStackTrace();
		}
	}

	public void eliminarProveedor() {
		try {
			System.out.println("Ingreso a eliminarProveedor: "
					+ newProveedor.getId());
			proveedorRegistration.remover(newProveedor);
			FacesMessage m = new FacesMessage(FacesMessage.SEVERITY_INFO,
					"Borrado!", "Borrado successful");
			facesContext.addMessage(null, m);
			pushEventProveedor.fire(String.format(
					"Proveedor Borrado: %s (id: %d)", newProveedor.getNombre(),
					newProveedor.getId()));
			initNewProveedores();

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

	public Proveedor getNewProveedor() {
		return newProveedor;
	}

	public void setNewProveedor(Proveedor newProveedor) {
		this.newProveedor = newProveedor;
	}

	public Proveedor getSelectedProveedor() {
		return selectedProveedor;
	}

	public void setSelectedProveedor(Proveedor selectedProveedor) {
		this.selectedProveedor = selectedProveedor;
	}

	public boolean isNuevo() {
		return nuevo;
	}

	public void setNuevo(boolean nuevo) {
		this.nuevo = nuevo;
	}

	public List<Gastos> getListGastos() {
		return listGastos;
	}

	public void setListGastos(List<Gastos> listGastos) {
		this.listGastos = listGastos;
	}

	public List<GastosProveedor> getListGastosProveedor() {
		return listGastosProveedor;
	}

	public void setListGastosProveedor(List<GastosProveedor> listGastosProveedor) {
		this.listGastosProveedor = listGastosProveedor;
	}

	public GastosProveedor getSelectedGastosProveedor() {
		return selectedGastosProveedor;
	}

	public void setSelectedGastosProveedor(
			GastosProveedor selectedGastosProveedor) {
		this.selectedGastosProveedor = selectedGastosProveedor;
	}

	public Gastos getSelectedGastos() {
		return selectedGastos;
	}

	public void setSelectedGastos(Gastos selectedGastos) {
		this.selectedGastos = selectedGastos;
	}

	public double getMargen_min() {
		return margen_min;
	}

	public void setMargen_min(double margen_min) {
		this.margen_min = margen_min;
	}

	public double getMargen_max() {
		return margen_max;
	}

	public void setMargen_max(double margen_max) {
		this.margen_max = margen_max;
	}

	public List<HistoriaMargenUtilidad> getMargenUtilidad() {
		return margenUtilidad;
	}

	public void setMargenUtilidad(List<HistoriaMargenUtilidad> margenUtilidad) {
		this.margenUtilidad = margenUtilidad;
	}

	public HistoriaMargenUtilidad getNewHistoria() {
		return newHistoria;
	}

	public void setNewHistoria(HistoriaMargenUtilidad newHistoria) {
		this.newHistoria = newHistoria;
	}

	public HistoriaMargenUtilidad getHistorias() {
		return historias;
	}

	public void setHistorias(HistoriaMargenUtilidad historias) {
		this.historias = historias;
	}

	public HistoriaMargenUtilidad getNewHistoria1() {
		return newHistoria1;
	}

	public void setNewHistoria1(HistoriaMargenUtilidad newHistoria1) {
		this.newHistoria1 = newHistoria1;
	}

	public List<ProductoProveedor> getListProductoProveedor() {
		return listProductoProveedor;
	}

	public void setListProductoProveedor(List<ProductoProveedor> listProductoProveedor) {
		this.listProductoProveedor = listProductoProveedor;
	}

	public ProductoProveedor getSelectedProductoProveedor() {
		return selectedProductoProveedor;
	}

	public void setSelectedProductoProveedor(ProductoProveedor selectedProductoProveedor) {
		this.selectedProductoProveedor = selectedProductoProveedor;
	}

	public double getPrecio() {
		return precio;
	}

	public void setPrecio(double precio) {
		this.precio = precio;
	}

	public boolean isSelectEstateProductoProveedor() {
		return selectEstateProductoProveedor;
	}

	public void setSelectEstateProductoProveedor(
			boolean selectEstateProductoProveedor) {
		this.selectEstateProductoProveedor = selectEstateProductoProveedor;
	}
	
	

}
