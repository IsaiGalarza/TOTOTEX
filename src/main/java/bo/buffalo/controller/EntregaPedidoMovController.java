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
import bo.buffalo.data.DetallePedidoMovRepository;
import bo.buffalo.data.MovimientoAlmacenRepository;
import bo.buffalo.data.PedidoMovRepository;
import bo.buffalo.data.ProductoProveedorRepository;
import bo.buffalo.data.ProductoRepository;
import bo.buffalo.data.UsuarioRepository;
import bo.buffalo.model.AlmProducto;
import bo.buffalo.model.Almacen;
import bo.buffalo.model.CardexProducto;
import bo.buffalo.model.DetallePedidoMov;
import bo.buffalo.model.PedidoMov;
import bo.buffalo.model.Producto;
import bo.buffalo.model.ProductoProveedor;
import bo.buffalo.model.Usuario;
import bo.buffalo.service.AlmacenProductoRegistration;
import bo.buffalo.service.CardexProductoRegistration;
import bo.buffalo.service.DetallePedidoMovRegistration;
import bo.buffalo.service.MovimientoAlmacenRegistration;
import bo.buffalo.service.PedidoMovRegistration;

@Named(value = "entregaPedidoMovController")
@ConversationScoped
public class EntregaPedidoMovController implements Serializable {

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
	private PedidoMovRegistration pedidoRegistration;

	@Inject
	private DetallePedidoMovRegistration detallePedidoRegistration;
	@Inject
	private DetallePedidoMovRepository detallePedidoRepository;
	@Inject
	private PedidoMovRepository pedidoRepository;

	@Inject
	UsuarioRepository usuarioRepository;

	@Inject
	MovimientoAlmacenRegistration movimientoAlmacenRegistration;

	@Inject
	CardexProductoRegistration cardexProductoRegistration;
	private Usuario usuarioSession;

	@Inject
	@Push(topic = PUSH_CDI_TOPIC)
	Event<String> pushEventSucursal;

	private boolean modificar = false;
	private boolean nuevo = false;
	private String tituloPanel = "Registrar PedidoMov";
	private PedidoMov selectedPedidoMov;
	private PedidoMov newPedidoMov;
	private double totalEntrega;
	private Producto selectedProducto;
	@Inject
	private ProductoProveedorRepository productoProveedorRepository;
	private Integer idProductoProveedor;
	private Integer cantidad = 0;

	private List<PedidoMov> listaEntregaPedido = new ArrayList<PedidoMov>();
	
	private List<Producto> listProducto = new ArrayList<Producto>();
	@Inject
	private ProductoRepository productoRepository;

	private List<DetallePedidoMov> listaPedido = new ArrayList<DetallePedidoMov>();
	private PedidoMov selectedPedido;

	@Inject
	private AlmacenRepository almacenRepository;
	private List<Almacen> listAlmacen = new ArrayList<Almacen>();

	private ProductoProveedor selectedProductoProveedor;

	private double total = 0;

	private DetallePedidoMov selectedDetallePedido;


	@Inject
	private AlmacenProductoRepository almacenProductoRepository;

	@Inject
	private MovimientoAlmacenRepository movimientoAlmacenRepository;
	private Integer numeroEntrega;

	@Inject
	private AlmacenProductoRegistration almacenProductoRegistration;

	// @Named provides access the return value via the EL variable name
	// "servicios" in the UI (e.g.
	// Facelets or JSP view)

	@Produces
	@Named
	public List<PedidoMov> getListaEntregaPedido() {
		return listaEntregaPedido;
	}

	@PostConstruct
	public void initNewPedidoMov() {

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
		listProducto = productoRepository.traerProductos();

		// traer todos las pedidoes ordenados por ID Desc
		listaEntregaPedido = pedidoRepository
				.findAllPedidoAprovedExterno(usuarioSession);

		listAlmacen = almacenRepository
				.findAlmacensDiferentsUser(usuarioSession);
		modificar = false;
		nuevo = false;
		totalEntrega = 0;
		listaPedido.clear();
		limpiarCamposProducto();
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

	public void selectedDetalle() {
		System.out.println("Igreso a selectedDetalle..");
		setSelectedProductoProveedor(em.find(ProductoProveedor.class,
				idProductoProveedor));
		setTotal(selectedProductoProveedor.getPrecioUnitarioVenta() * cantidad);
	}

	// SELECT PRESENTACION CLICK
	public void onRowSelectPedidoMovClick(SelectEvent event) {
		try {
			PedidoMov pedido = (PedidoMov) event.getObject();
			System.out.println("onRowSelectPedidoMovClick  " + pedido.getId());
			selectedPedidoMov = pedido;
			newPedidoMov = em.find(PedidoMov.class, pedido.getId());
			newPedidoMov.setFechaRegistro(new Date());
			newPedidoMov.setUsuarioRegistro(usuarioSession.getLogin());
			listaPedido = detallePedidoRepository
					.findAllOrderedByID(newPedidoMov);
			entregas();
			numeroEntrega = movimientoAlmacenRepository.numeroDeEntrega();
			System.out.println("Numero Entrega : " + numeroEntrega);
			tituloPanel = "Entrega Nro : " + numeroEntrega;
			modificar = true;
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
			System.out.println("Error in onRowSelectPedidoMovClick: "
					+ e.getMessage());
		}
	}

	// SELECT PRESENTACION CLICK
	public void onRowSelectProductoClick(SelectEvent event) {
		try {
			limpiarCamposProducto();
			Producto producto = (Producto) event.getObject();
			System.out.println("onRowSelectProductoClick  " + producto.getId());

			selectedProducto = producto;

			selectedProducto.setListProductoProveedors(productoProveedorRepository
					.findAllOrderedByIDForProducto(selectedProducto));

		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
			System.out.println("Error in onRowSelectPedidoMovClick: "
					+ e.getMessage());
		}
	}

	private void entregas() {
		System.out.println("Ingreso a entregas");
		totalEntrega = 0;
		for (DetallePedidoMov value : listaPedido) {

			value.setCantidadEntregado(value.getCantidad());
			value.setTotalEntregado(value.getPrecioVenta()
					* value.getCantidadEntregado());
			totalEntrega += value.getCantidadEntregado()
					* value.getPrecioVenta();
		}
		newPedidoMov.setTotalEntregado(totalEntrega);
		System.out.println("Termino a entregas : " + totalEntrega);
	}

	// SELECT PRESENTACION CLICK
	public void onRowSelectDetalleClick(SelectEvent event) {
		try {
			DetallePedidoMov detalle = (DetallePedidoMov) event.getObject();
			System.out.println("onRowSelectDetalleClick  " + detalle.getId());

			setSelectedDetallePedido(detalle);

			selectedProducto.setListProductoProveedors(productoProveedorRepository
					.findAllOrderedByIDForProducto(selectedProducto));

		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
			System.out.println("Error in onRowSelectDetalleClick: "
					+ e.getMessage());
		}
	}

	private void limpiarCamposProducto() {
		idProductoProveedor = null;
		selectedProducto = new Producto();
		selectedProductoProveedor = new ProductoProveedor();
		cantidad = 0;
		total = 0;
	}

	public void modificarPedidoMov() {
		try {
			System.out.println("Ingreso a modificarPedidoMov: "
					+ newPedidoMov.getId());
			for (DetallePedidoMov value : listaPedido) {

				double precio = value.getCantidadEntregado()
						* value.getPrecioVenta();
				double cant = value.getCantidadEntregado();
				value.setEstadoEntrega("COMPLETADO");
				detallePedidoRegistration.updated(value);
				ingresoAlmacen(value, cant, precio, newPedidoMov);

			}

			newPedidoMov.setEntrega("COM");
			newPedidoMov.setEstado("PR");
			pedidoRegistration.updated(newPedidoMov);

			System.out.println("detalle Modificado.. ");
			initNewPedidoMov();
			FacesMessage m = new FacesMessage(FacesMessage.SEVERITY_INFO,
					"Modificado!", "Modificado successful");
			facesContext.addMessage(null, m);
			pushEventSucursal.fire(String.format(
					"PedidoMov Modificada: %s (id: %d)",
					newPedidoMov.getObservacion(), newPedidoMov.getId()));

		} catch (Exception e) {
			String errorMessage = getRootErrorMessage(e);
			FacesMessage m = new FacesMessage(FacesMessage.SEVERITY_ERROR,
					errorMessage, "Modificado Incorrecto.");
			facesContext.addMessage(null, m);
		}
	}

	/**
	 * Altualiza el Stock de Producto en el Almacen en el caso como Entrada de
	 * Productos
	 * 
	 * @param detail
	 * @param cant
	 * @param monto
	 */
	private void ingresoAlmacen(DetallePedidoMov detail, double cant,
			double monto, PedidoMov pedido) {
		try {

			System.out.println("Ingreso a ingresoAlmacen..");
			AlmProducto almProd;
			CardexProducto cardex = new CardexProducto();
			if (almacenProductoRepository.existProducto(detail.getProducto(),
					newPedidoMov.getAlmIn(), detail.getLineasProveedor(),
					detail.getProveedor())) {
				System.out.println("existe:" + cant + " , " + monto);
				almProd = almacenProductoRepository.findByProducto(
						detail.getProducto(), newPedidoMov.getAlmIn(),
						detail.getLineasProveedor(), detail.getProveedor());
				almProd.setAlmacen(newPedidoMov.getAlmIn());
				almProd.setEstado("AC");
				almProd.setProveedor(detail.getProveedor());
				almProd.setLineaProvedor(detail.getLineasProveedor());
				almProd.setProducto(detail.getProducto());
				almProd.setStock(almProd.getStock() + cant);
				almacenProductoRegistration.updated(almProd);
				System.out.println("Actualizo AlmacenProducto Ingreso");
			} else {

				almProd = new AlmProducto();
				System.out.println("no sexiste:" + cant + " , " + monto);
				almProd.setAlmacen(newPedidoMov.getAlmIn());
				almProd.setEstado("AC");
				almProd.setStock(cant);
				almProd.setUsuarioRegistro(usuarioSession.getName());
				almProd.setFechaRegistro(new Date());
				almProd.setProducto(detail.getProducto());
				almProd.setProveedor(detail.getProveedor());
				almProd.setLineaProvedor(detail.getLineasProveedor());
				almacenProductoRegistration.register(almProd);
				System.out.println("Rgistro AlmacenProducto Ingreso");
			}
			cardex.setTransaccion("COMPRA DE PRODUCTOS");
			cardex.setAlmacen(pedido.getAlmIn());
			cardex.setEstado("AC");
			cardex.setPrecioCompra(productoProveedorRepository
					.findProductoProveedor(detail.getProducto(),
							detail.getProveedor(), detail.getLineasProveedor())
					.getPrecioUnitarioCompra());
			cardex.setFechaRegistro(new Date());
			cardex.setDocumento("OC-"+newPedidoMov.getCorrelativo());
			cardex.setUsuarioRegistro(usuarioSession.getName());
			cardex.setProducto(detail.getProducto());
			cardex.setPrecioVenta(detail.getPrecioVenta());
			cardex.setCantidad(cant);
			cardex.setStockAnterior(almProd.getStock() - cant);
			cardex.setStockActual(almProd.getStock());
			cardex.setNumeroTransaccion(pedido.getId());
			cardex.setProveedor(detail.getProveedor());
			cardex.setLineaProveedor(detail.getLineasProveedor());
			cardex.setTipoMovimiento("INGRESO");
			cardex.setMovimiento("OC");
			// CARDEX
			cardexProductoRegistration.register(cardex);
			System.out.println("Ingreso Registrado.. cardex");
		} catch (Exception e) {
			System.err.println("Error en ingresoAlmacen :" + e.getMessage());
		}
	}



	public void eliminarPedidoMov() {
		try {
			System.out.println("Ingreso a eliminarPedidoMov: "
					+ newPedidoMov.getId());
			pedidoRegistration.remover(newPedidoMov);
			FacesMessage m = new FacesMessage(FacesMessage.SEVERITY_INFO,
					"Borrado!", "Borrado successful");
			facesContext.addMessage(null, m);
			pushEventSucursal.fire(String.format(
					"PedidoMov Borrada: %s (id: %d)",
					newPedidoMov.getObservacion(), newPedidoMov.getId()));
			initNewPedidoMov();

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

	public PedidoMov getSelectedPedidoMov() {
		return selectedPedidoMov;
	}

	public void setSelectedPedidoMov(PedidoMov selectedPedidoMov) {
		this.selectedPedidoMov = selectedPedidoMov;
	}

	public PedidoMov getNewPedidoMov() {
		return newPedidoMov;
	}

	public void setNewPedidoMov(PedidoMov newPedidoMov) {
		this.newPedidoMov = newPedidoMov;
	}

	public List<Producto> getListProducto() {
		return listProducto;
	}

	public void setListProducto(List<Producto> listProducto) {
		this.listProducto = listProducto;
	}

	public Producto getSelectedProducto() {
		return selectedProducto;
	}

	public void setSelectedProducto(Producto selectedProducto) {
		this.selectedProducto = selectedProducto;
	}

	public List<DetallePedidoMov> getListaPedido() {
		return listaPedido;
	}

	public void setListaPedido(List<DetallePedidoMov> listaPedido) {
		this.listaPedido = listaPedido;
	}

	public PedidoMov getSelectedPedido() {
		return selectedPedido;
	}

	public void setSelectedPedido(PedidoMov selectedPedido) {
		this.selectedPedido = selectedPedido;
	}

	public Integer getIdProductoProveedor() {
		return idProductoProveedor;
	}

	public void setIdProductoProveedor(Integer idProductoProveedor) {
		this.idProductoProveedor = idProductoProveedor;
	}

	public Integer getCantidad() {
		return cantidad;
	}

	public void setCantidad(Integer cantidad) {
		this.cantidad = cantidad;
	}

	public ProductoProveedor getSelectedProductoProveedor() {
		return selectedProductoProveedor;
	}

	public void setSelectedProductoProveedor(
			ProductoProveedor selectedProductoProveedor) {
		this.selectedProductoProveedor = selectedProductoProveedor;
	}

	public double getTotal() {
		return total;
	}

	public void setTotal(double total) {
		this.total = total;
	}

	public DetallePedidoMov getSelectedDetallePedido() {
		return selectedDetallePedido;
	}

	public void setSelectedDetallePedido(DetallePedidoMov selectedDetallePedido) {
		this.selectedDetallePedido = selectedDetallePedido;
	}

	public boolean isNuevo() {
		return nuevo;
	}

	public void setNuevo(boolean nuevo) {
		this.nuevo = nuevo;
	}

	public List<Almacen> getListAlmacen() {
		return listAlmacen;
	}

	public void setListAlmacen(List<Almacen> listAlmacen) {
		this.listAlmacen = listAlmacen;
	}

	public double getTotalEntrega() {
		return totalEntrega;
	}

	public void setTotalEntrega(double totalEntrega) {
		this.totalEntrega = totalEntrega;
	}

	public Integer getNumeroEntrega() {
		return numeroEntrega;
	}

	public void setNumeroEntrega(Integer numeroEntrega) {
		this.numeroEntrega = numeroEntrega;
	}

}
