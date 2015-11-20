package bo.buffalo.controller;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.enterprise.context.Conversation;
import javax.enterprise.context.ConversationScoped;
import javax.enterprise.event.Event;
import javax.faces.context.FacesContext;
import javax.inject.Inject;
import javax.inject.Named;
import javax.servlet.http.HttpServletRequest;

import org.primefaces.event.SelectEvent;
import org.richfaces.cdi.push.Push;

import bo.buffalo.data.HistorialCostosRepository;
import bo.buffalo.data.MargenUtilidadRepository;
import bo.buffalo.data.ProductoProveedorRepository;
import bo.buffalo.data.ProductopRepository;
import bo.buffalo.data.ProveedorRepository;
import bo.buffalo.data.UsuarioRepository;
import bo.buffalo.model.AtributoProducto;
import bo.buffalo.model.GastosProveedor;
import bo.buffalo.model.HistoriaMargenUtilidad;
import bo.buffalo.model.HistorialCostos;
import bo.buffalo.model.Producto;
import bo.buffalo.model.ProductoProveedor;
import bo.buffalo.model.Proveedor;
import bo.buffalo.model.Usuario;
import bo.buffalo.service.HistorialCostosRegistration;
import bo.buffalo.service.MargenUtilidadRegistration;
import bo.buffalo.service.ProductoProveedorRegistration;
import bo.buffalo.service.ProductopRegistration;
import bo.buffalo.util.EstructuraCentralCostos;

@Named(value = "centralCostoFarmacoController")
@ConversationScoped
public class CentralCostoFarmacoController implements Serializable {

	private static final long serialVersionUID = -1992094769231496851L;

	public static final String PUSH_CDI_TOPIC = "pushCdi";

	@Inject
	private FacesContext facesContext;

	@Inject
	Conversation conversation;


	@Inject
	private ProveedorRepository proveedorRepository;

	@Inject
	UsuarioRepository usuarioRepository;
	private Usuario usuarioSession;

	@Inject
	@Push(topic = PUSH_CDI_TOPIC)
	Event<String> pushEventProveedor;

	// PRODUCTO
	private Producto selectedProducto;
	private List<Producto> listProducto = new ArrayList<Producto>();
	@Inject
	private ProductopRegistration productoRegistration;

	@Inject
	private ProductopRepository productopRepository;

	private Proveedor selectedProveedor;
	private List<Proveedor> listaProveedores;

	private double total;


	// GASTOS PROVEEDOR
	private List<GastosProveedor> listGastosProveedor = new ArrayList<GastosProveedor>();
	private GastosProveedor selectedGastosProveedor;
	
	@Inject
	MargenUtilidadRegistration margenUtilidadRegistration;

	@Inject
	MargenUtilidadRepository margenUtilidadRepository;

	private List<HistoriaMargenUtilidad> margenUtilidad;
	private HistoriaMargenUtilidad historias = new HistoriaMargenUtilidad();

	private HistoriaMargenUtilidad newHistoria1;

	private List<EstructuraCentralCostos> listEstructuraCentralCostos = new ArrayList<EstructuraCentralCostos>();

	// Producto Proveedor
	private List<ProductoProveedor> listProductoProveedor = new ArrayList<ProductoProveedor>();
	@Inject
	private ProductoProveedorRepository productoProveedorRepository;

	@Inject
	private ProductoProveedorRegistration productoProveedorRegistration;
	
	//historial de costos
	
	@Inject
	private HistorialCostosRegistration historialCostosRegistration;
	
	@Inject 
	private HistorialCostosRepository historialCostosRepository;
	 
	private List<HistorialCostos> listHistorialCostos= new ArrayList<HistorialCostos>();
	
	private double precioVenta;

	private double precioCompra;	
	
	private boolean selected;


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
		listProducto = productopRepository.findAllFarmacoOrderedByDate();

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

	private void modificarProductoProveedor() {
		try {
			System.out.println("Ingreso a modificarProductoProveedor");
			for (EstructuraCentralCostos value : listEstructuraCentralCostos) {
				value.getProductoProveedor().setFechaRegistro(new Date());
				value.getProductoProveedor().setUsuarioRegistro(
						usuarioSession.getName());
				value.getProductoProveedor().setPrecioUnitarioVenta(
						value.getPrecioFinal());
				productoProveedorRegistration.updated(value
						.getProductoProveedor());
			}
		} catch (Exception e) {
			System.err.println("Error en modificarProductoProveedor : "
					+ e.getMessage());
			e.getStackTrace();
		}
	}

	public void modificarProducto() {
		try {
			System.out.println("Ingreso a modificarProducto");
			selectedProducto.setPrecio(total);
			selectedProducto.setFechaRegistro(new Date());
			selectedProducto.setUsuarioRegistro(usuarioSession.getName());
			productoRegistration.updated(selectedProducto,new ArrayList<AtributoProducto>());
			modificarProductoProveedor();
			listProducto = productopRepository.findAllFarmacoOrderedByDate();
			if (precioVenta!=selectedProducto.getPrecio()) {
				HistorialCostos historialCostos= new HistorialCostos();
				historialCostos.setObservacion("MODIFICADO POR LA CENTRAL DE COSTOS");
				historialCostos.setProducto(selectedProducto);
				historialCostos.setEstado("AC");
				historialCostos.setFechaRegistro(new Date());
				historialCostos.setUsuarioRegistro(usuarioSession.getName());
				historialCostos.setPrecioVenta(selectedProducto.getPrecio());
				historialCostos.setPrecioCompra(precioCompra);
				historialCostosRegistration.register(historialCostos);
				listHistorialCostos=historialCostosRepository.traerHistorialCostossActivos(selectedProducto);
			}
			listEstructuraCentralCostos.clear();
			cargarEstructuraCostos();
			calcularPrecioVenta();
			calcularPrecioCompra();
			establecerCosto();
			selected=false;
		} catch (Exception e) {
			System.err
					.println("Error en modificarProducto : " + e.getMessage());
			e.getStackTrace();
		}
	}

	private void cargarEstructuraCostos() {
		try {
			System.out.println("Ingreso a cargarEstructuraCostos");
			listEstructuraCentralCostos.clear();
			Integer index = 0;
			double costo = 0;
			double utilMax = 0;
			double precioVenta1 = 0;
			double precioFinal = 0;
			for (ProductoProveedor value : listProductoProveedor) {
				costo = proveedorRepository.obtenerCosto(value.getProveedor());
				utilMax = value.getProveedor().getUtilidadMax();
				precioVenta1 = value.getPrecioUnitarioCompra()+value.getPrecioUnitarioCompra()  * (utilMax* 0.01);
				precioFinal = precioVenta1;
				listEstructuraCentralCostos.add(new EstructuraCentralCostos(
						index, value, costo, utilMax, precioVenta1,
						precioFinal, 0));

			}

		} catch (Exception e) {
			System.err.println("Error en cargarEstructuraCostos : "
					+ e.getMessage());
			e.getStackTrace();
		}
	}

	public void calcularPrecioVenta() {
		System.out.println("Ingreso a calcularTotal");
		total = 0;
		for (EstructuraCentralCostos value : listEstructuraCentralCostos) {
			if (total < value.getPrecioFinal()) {
				total = value.getPrecioFinal();
			}
		}
	}
	
	public void calcularPrecioCompra() {
		System.out.println("Ingreso a calcularPrecioCompra");
		precioCompra = 0;
		for (EstructuraCentralCostos value : listEstructuraCentralCostos) {
			if (precioCompra < value.getProductoProveedor().getPrecioUnitarioCompra()) {
				precioCompra = value.getProductoProveedor().getPrecioUnitarioCompra();
			}
		}
	}


	public void establecerCosto() {
		System.out.println("Ingreso a establecerCosto");
		for (EstructuraCentralCostos value : listEstructuraCentralCostos) {
			value.setPrecioEstablecido(total);
		}
	}

	public void onRowSelectProductoClick(SelectEvent event) {
		try {
			System.out.println("Ingreso a onRowSelectProductoClick");
			selectedProducto = (Producto) event.getObject();
			listProductoProveedor = productoProveedorRepository
					.traerProductoProveedor(selectedProducto);
			cargarEstructuraCostos();
			precioVenta= selectedProducto.getPrecio();
			listHistorialCostos= historialCostosRepository.traerHistorialCostossActivos(selectedProducto);
			calcularPrecioVenta();
			calcularPrecioCompra();
			establecerCosto();
			selected=true;
		} catch (Exception e) {
			e.printStackTrace();
			System.err.println("Error en onRowSelectProductoClick : "
					+ e.getMessage());
			e.getStackTrace();
		}
	}


	public Proveedor getSelectedProveedor() {
		return selectedProveedor;
	}

	public void setSelectedProveedor(Proveedor selectedProveedor) {
		this.selectedProveedor = selectedProveedor;
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

	public List<HistoriaMargenUtilidad> getMargenUtilidad() {
		return margenUtilidad;
	}

	public void setMargenUtilidad(List<HistoriaMargenUtilidad> margenUtilidad) {
		this.margenUtilidad = margenUtilidad;
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

	public Producto getSelectedProducto() {
		return selectedProducto;
	}

	public void setSelectedProducto(Producto selectedProducto) {
		this.selectedProducto = selectedProducto;
	}

	public List<Producto> getListProducto() {
		return listProducto;
	}

	public void setListProducto(List<Producto> listProducto) {
		this.listProducto = listProducto;
	}

	public List<EstructuraCentralCostos> getListEstructuraCentralCostos() {
		return listEstructuraCentralCostos;
	}

	public void setListEstructuraCentralCostos(
			List<EstructuraCentralCostos> listEstructuraCentralCostos) {
		this.listEstructuraCentralCostos = listEstructuraCentralCostos;
	}

	public double getTotal() {
		return total;
	}

	public void setTotal(double total) {
		this.total = total;
	}

	public List<HistorialCostos> getListHistorialCostos() {
		return listHistorialCostos;
	}

	public void setListHistorialCostos(List<HistorialCostos> listHistorialCostos) {
		this.listHistorialCostos = listHistorialCostos;
	}

	public double getPrecioVenta() {
		return precioVenta;
	}

	public void setPrecioVenta(double precioVenta) {
		this.precioVenta = precioVenta;
	}

	public double getPrecioCompra() {
		return precioCompra;
	}

	public void setPrecioCompra(double precioCompra) {
		this.precioCompra = precioCompra;
	}

	public boolean isSelected() {
		return selected;
	}

	public void setSelected(boolean selected) {
		this.selected = selected;
	}
}
