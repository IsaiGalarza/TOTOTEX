package bo.buffalo.controller;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.Serializable;
import java.net.URL;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.enterprise.context.Conversation;
import javax.enterprise.context.ConversationScoped;
import javax.enterprise.event.Event;
import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
import javax.inject.Inject;
import javax.inject.Named;
import javax.persistence.EntityManager;
import javax.servlet.http.HttpServletRequest;

import org.apache.commons.io.IOUtils;
import org.primefaces.event.SelectEvent;
import org.primefaces.model.DefaultStreamedContent;
import org.primefaces.model.StreamedContent;
import org.richfaces.cdi.push.Push;

import bo.buffalo.data.AlmacenProductoRepository;
import bo.buffalo.data.AlmacenRepository;
import bo.buffalo.data.DetallePedidoMovRepository;
import bo.buffalo.data.ParametroSistemaRepository;
import bo.buffalo.data.PedidoMovRepository;
import bo.buffalo.data.ProductoProveedorRepository;
import bo.buffalo.data.ProductoRepository;
import bo.buffalo.data.UsuarioRepository;
import bo.buffalo.model.AlmProducto;
import bo.buffalo.model.Almacen;
import bo.buffalo.model.DetallePedidoMov;
import bo.buffalo.model.PedidoMov;
import bo.buffalo.model.Producto;
import bo.buffalo.model.ProductoProveedor;
import bo.buffalo.model.Proveedor;
import bo.buffalo.model.Usuario;
import bo.buffalo.service.DetallePedidoMovRegistration;
import bo.buffalo.service.PedidoMovRegistration;

@Named(value = "traspasoController")
@ConversationScoped
public class TraspasoController implements Serializable {

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

	

    private StreamedContent file;  
	 private StreamedContent streamedContent;
	 
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
	private Usuario usuarioSession;

	@Inject
	@Push(topic = PUSH_CDI_TOPIC)
	Event<String> pushEventSucursal;

	private boolean modificar = false;
	private boolean nuevo = false;
	private String tituloPanel = "Registrar Traspaso";
	private PedidoMov selectedPedidoMov;
	private PedidoMov newPedidoMov;
	@Inject
	private ProductoProveedorRepository productoProveedorRepository;
	private Integer idProductoProveedor;
	private Integer cantidad = 0;

	private List<PedidoMov> listaPedidoMov;
	private List<AlmProducto> listProducto = new ArrayList<AlmProducto>();

	private List<DetallePedidoMov> listaPedido = new ArrayList<DetallePedidoMov>();


	private PedidoMov selectedPedido;

	private Producto producto = new Producto();

	@Inject
	private AlmacenRepository almacenRepository;
	private List<Almacen> listAlmacen = new ArrayList<Almacen>();

	private ProductoProveedor selectedProductoProveedor;

	private AlmProducto selectAlmacenProducto;

	private List<Proveedor> listProveedor = new ArrayList<Proveedor>();
	@Inject
	private AlmacenProductoRepository almacenProductoRepository;

	private double total = 0;
	private double totalDolares=0;

	private DetallePedidoMov selectedDetallePedido;

	private Almacen alm;

	private List<PedidoMov> listaPedidoProcesadas = new ArrayList<PedidoMov>();
	private List<PedidoMov> listaPedidoAnuladas = new ArrayList<PedidoMov>();
	private List<PedidoMov> listaPedidoTodos = new ArrayList<PedidoMov>();

	private double stockExistente;

	@Inject
	private ProductoRepository productoRepository;

	private List<Producto> listaProducto;

	private List<ProductoProveedor> listProductoProveedor = new ArrayList<ProductoProveedor>();

	private List<AlmProducto> listAlmacenProducto = new ArrayList<AlmProducto>();
	private @Inject ParametroSistemaRepository parametroSistemaRepository;
	private double tipoCambio;

	public List<PedidoMov> getListaPedidoMov() {
		return listaPedidoMov;
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

		listaPedidoMov = pedidoRepository
				.findAllInternoOrderedByID(usuarioSession);

		setListaPedidoProcesadas(pedidoRepository
				.findInternoProcesadasOrderedByID(usuarioSession));
		setListaPedidoAnuladas(pedidoRepository
				.findInternoAnuladasOrderedByID(usuarioSession));
		setListaPedidoTodos(pedidoRepository
				.findAllInternOrderedByID(usuarioSession));
		// traer todos las pedidoes ordenados por ID Desc
		listAlmacen = almacenRepository
				.findAlmacensDiferentsUser(usuarioSession);
		modificar = false;
		nuevo = false;
		listaPedido.clear();
		alm = almacenRepository.findAlmacenForUser(usuarioSession);
		listProveedor = almacenProductoRepository.findProveedorForAlmacen(alm);
		try {
			tipoCambio=Double.valueOf(parametroSistemaRepository.findByKey("PV").getValor());
		} catch (Exception e) {
			System.out.println(e.toString());
			tipoCambio=7;
		}
	}

	public void listarProducto() {
		try {
			System.out.println("Ingreso a listarProducto");
			Almacen alm1 = em.find(Almacen.class, newPedidoMov.getAlmOut()
					.getId());
			listProducto = productoProveedorRepository
					.traerProductoProveedors(alm1);
			limpiarCamposProducto();

		} catch (Exception e) {
			System.err.println("Error en listarProducto : " + e.getMessage());
			e.getStackTrace();
		}

	}

	
	public void crearPredido() {
		newPedidoMov = new PedidoMov();
		newPedidoMov.setEstado("AC");
		newPedidoMov.setObservacion("TRASPASO DE PRODUCTOS");
		newPedidoMov.setTipoPedido("INT");
		newPedidoMov.setFechaRegistro(new Date());
		newPedidoMov.setEntrega("NEW");
		newPedidoMov.setPreelaborado("MATERIA PRIMA");
		newPedidoMov.setUsuarioRegistro(usuarioSession.getLogin());
		newPedidoMov.setAlmIn(alm);
		newPedidoMov.setProveedor(null);
		newPedidoMov.setCorrelativo(pedidoRepository.maxOrdenTraspaso());
		listaPedido.clear();
		limpiarCamposProducto();

		// tituloPanel
		tituloPanel = "Registrar Pedido";
		nuevo = true;
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

	private void calculateTotals() {
		System.out.println("Ingreso a calculateTotals");
		double totals = 0;
		Integer index = 1;
		for (DetallePedidoMov value : listaPedido) {
			totals += value.getTotal();
			value.setCorrelativo(index);
			index++;
		}
		System.out.println("Total:" + totals);
		newPedidoMov.setTotalSus(totals/tipoCambio);
		newPedidoMov.setTotal(totals);
	}
	
	
	
	public void agregarDetalle() {
		try {
			System.out.println("Ingreso a agregarDetalle...");
			DetallePedidoMov det = new DetallePedidoMov();
			det.setCantidad(cantidad);
			det.setEstado("AC");
			det.setFechaRegistro(new Date());
			det.setLineasProveedor(selectedProductoProveedor
					.getLineasProveedor());
			det.setProveedor(selectedProductoProveedor.getProveedor());
			det.setPrecioVenta(selectedProductoProveedor.getProducto().getPrecio());
			det.setUnidadMedida("Unidad");
			det.setUsuarioRegistro(usuarioSession.getName());
			det.setProducto(selectedProductoProveedor.getProducto());
			det.setTotal(selectedProductoProveedor.getProducto().getPrecio()
					* cantidad);
			det.setTotalDolares(det.getTotal()/tipoCambio);
			det.setCantidadEntrega(0);
			det.setCantidadEntregado(0);
			det.setTotalEntregado(0);
			listaPedido.add(det);
			limpiarCamposProducto();
			calculateTotals();
		} catch (Exception e) {
			System.err.println("Error en agregarDetalle :" + e.getMessage());
		}
	}

	public void selectedDetalle() {
		System.out.println("Igreso a selectedDetalle..");
		if (stockExistente < cantidad) {
			FacesMessage m = new FacesMessage(FacesMessage.SEVERITY_INFO,
					"No hay suficiente Stock !", stockExistente + " < "
							+ cantidad);
			facesContext.addMessage(null, m);
		} else {
			setTotal(selectedProductoProveedor.getProducto().getPrecio()
					* cantidad);
			setTotalDolares(total/tipoCambio);
		}
	}

	// SELECT PRESENTACION CLICK
	public void onRowSelectPedidoMovClick(SelectEvent event) {
		try {
			PedidoMov pedido = (PedidoMov) event.getObject();
			System.out.println("onRowSelectPedidoMovClick  " + pedido.getId());
			selectedPedidoMov = pedido;
			newPedidoMov = selectedPedidoMov;
			newPedidoMov.setFechaRegistro(new Date());
			newPedidoMov.setUsuarioRegistro(usuarioSession.getLogin());
			
				listaPedido = detallePedidoRepository
						.findAllOrderedByID(selectedPedidoMov);

			tituloPanel = "Modificar Traspaso";
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
			AlmProducto producto = (AlmProducto) event.getObject();
			System.out.println("onRowSelectProductoClick  " + producto.getId());
			selectAlmacenProducto = producto;

			selectedProductoProveedor = productoProveedorRepository
					.findProductoProveedor(producto.getProducto(),
							producto.getProveedor(),
							producto.getLineaProvedor());
			stockExistente = 0;
			stockExistente = almacenProductoRepository.findByProducto(
					selectedProductoProveedor.getProducto(),
					newPedidoMov.getAlmOut(),
					selectedProductoProveedor.getLineasProveedor(),
					selectedProductoProveedor.getProveedor()).getStock();
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
			System.out.println("Error in onRowSelectPedidoMovClick: "
					+ e.getMessage());
		}
	}

	// SELECT PRESENTACION CLICK
	public void onRowSelectDetalleClick(SelectEvent event) {
		try {
			DetallePedidoMov detalle = (DetallePedidoMov) event.getObject();
			System.out.println("onRowSelectDetalleClick  " + detalle.getId());
			setSelectedDetallePedido(detalle);
			

		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
			System.out.println("Error in onRowSelectDetalleClick: "
					+ e.getMessage());
		}
	}

	private void limpiarCamposProducto() {
		idProductoProveedor = null;
		selectedProductoProveedor = new ProductoProveedor();
		cantidad = 0;
		total = 0;
		setTotalDolares(0);
	}

	public void registrarPedidoMov() {
		try {
			System.out.println("Ingreso a registrarPedidoMov: "
					+ newPedidoMov.getId());
			newPedidoMov.setUsuario(usuarioSession);
			if (newPedidoMov.getTipoPedido().equals("INT")) {
				newPedidoMov.setAlmOut(em.find(Almacen.class, this.newPedidoMov
						.getAlmOut().getId()));
				newPedidoMov.setUsuarioOut(newPedidoMov.getAlmOut()
						.getUsuario());
			} else {
				newPedidoMov.setAlmOut(null);
				newPedidoMov.setUsuarioOut(null);
			}

			pedidoRegistration.register(newPedidoMov);			
				for (DetallePedidoMov value : listaPedido) {
					value.setPedidoMov(newPedidoMov);
					detallePedidoRegistration.register(value);
				}

			FacesMessage m = new FacesMessage(FacesMessage.SEVERITY_INFO,
					"Registrado!", "Traspaso registrado con exito!");
			facesContext.addMessage(null, m);
			pushEventSucursal.fire(String.format(
					"Nueva PedidoMov Registrada: %s (id: %d)",
					newPedidoMov.getObservacion(), newPedidoMov.getId()));
			initNewPedidoMov();
		} catch (Exception e) {
			String errorMessage = getRootErrorMessage(e);
			FacesMessage m = new FacesMessage(FacesMessage.SEVERITY_ERROR,
					errorMessage, "Registro Incorrecto.");
			facesContext.addMessage(null, m);
		}
	}

	public void modificarPedidoMov() {
		try {
			System.out.println("Ingreso a modificarPedidoMov: "
					+ newPedidoMov.getId());
			if (newPedidoMov.getTipoPedido().equals("INT")) {
				newPedidoMov.setAlmOut(em.find(Almacen.class, this.newPedidoMov
						.getAlmOut().getId()));
				newPedidoMov.setUsuarioOut(newPedidoMov.getAlmOut()
						.getUsuario());
			} else {
				newPedidoMov.setAlmOut(null);
				newPedidoMov.setUsuarioOut(null);
			}
			pedidoRegistration.updated(newPedidoMov);
			pushEventSucursal.fire(String.format(
					"PedidoMov Modificada: %s (id: %d)",
					newPedidoMov.getObservacion(), newPedidoMov.getId()));

			detallePedidoRegistration.removerForPedido(newPedidoMov);
			
				for (DetallePedidoMov value : listaPedido) {
					value.setId(null);
					value.setPedidoMov(newPedidoMov);
					detallePedidoRegistration.register(value);
				}
			initNewPedidoMov();
			FacesMessage m = new FacesMessage(FacesMessage.SEVERITY_INFO,
					"Modificado!", "Modificado correctamente!");
			facesContext.addMessage(null, m);

		} catch (Exception e) {
			String errorMessage = getRootErrorMessage(e);
			FacesMessage m = new FacesMessage(FacesMessage.SEVERITY_ERROR,
					errorMessage, "Modificado Incorrecto.");
			facesContext.addMessage(null, m);
		}
	}

	public void eliminarPedidoMov() {
		try {
			System.out.println("Ingreso a eliminarPedidoMov: "
					+ newPedidoMov.getId());
			pedidoRegistration.remover(newPedidoMov);
			pushEventSucursal.fire(String.format(
					"PedidoMov Borrada: %s (id: %d)",
					newPedidoMov.getObservacion(), newPedidoMov.getId()));
			initNewPedidoMov();
			FacesMessage m = new FacesMessage(FacesMessage.SEVERITY_ERROR,
					"Eliminado!", "Eliminado con exito!.");
			facesContext.addMessage(null, m);

		} catch (Exception e) {
			String errorMessage = getRootErrorMessage(e);
			FacesMessage m = new FacesMessage(FacesMessage.SEVERITY_ERROR,
					errorMessage, "Borrado Incorrecto.");
			facesContext.addMessage(null, m);
		}
	}

	public List<String> completeDescripcionProducto(String query) {
		try {
			System.out.println("Ingreso a completeDescripcionProducto..."
					+ query);
			listaProducto = productoRepository
					.buscarProductoPreElaboradoNombre(query);
			List<String> results = new ArrayList<String>();
			for (Producto producto : listaProducto) {
				results.add(producto.getNombreProducto());
			}
			return results;
		} catch (Exception e) {
			// TODO: handle exception
			System.out.println("Error en completeDescripcionProducto: "
					+ e.getMessage());
			return null;
		}
	}

	public void eliminarDetallePedido() {
		try {
			System.out.println("Ingreso eliminarProductoProveedor");
			if (selectedDetallePedido != null) {
				for (int i = 0; i < listaPedido.size(); i++) {
					if (selectedDetallePedido.getCorrelativo().equals(
							listaPedido.get(i).getCorrelativo())) {
						listaPedido.remove(i);
						System.out.println("fue Eliminado");
					}
				}
			}
			calculateTotals();
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
			System.out.println("Error en destroyProductoProveedor: "
					+ e.getMessage());
		}
	}
	
	
	
	 public StreamedContent getStreamedContent(){
	    	try {
				System.out.println("Ingreso a descargarPDF...");
				HttpServletRequest request = (HttpServletRequest) facesContext.getExternalContext().getRequest();  
				String urlPath = request.getRequestURL().toString();
				urlPath = urlPath.substring(0, urlPath.length() - request.getRequestURI().length()) + request.getContextPath() + "/";
				System.out.println("urlPath >> "+urlPath);
				

				String pUsuario = usuarioSession.getLogin();
				int pSucursal = usuarioSession.getSucursal().getId();
				
				String urlPDFreporte = urlPath+"ReporteOrdenTraspaso?pUsuario="+pUsuario+"&pSucursal="+pSucursal+"&pId="+newPedidoMov.getId(); //&pRutaImagen="+pRutaImagen
				System.out.println("URL Reporte PDF: "+urlPDFreporte);
				
				URL url = new URL(urlPDFreporte);
							
				// Read the PDF from the URL and save to a local file
				InputStream is1 = url.openStream();
				File f = stream2file(is1);
				System.out.println("Size Bytes: "+f.length());
				InputStream stream = new FileInputStream(f);  
				streamedContent = new DefaultStreamedContent(stream, "application/pdf", "ReporteOrdenTraspaso.pdf");
				setFile(new DefaultStreamedContent(stream, "application/pdf", "ReporteOrdenTraspaso.pdf"));
				return streamedContent;
			} catch (Exception e) {
				// TODO: handle exception
				e.printStackTrace();
				System.out.println("Error en descargarPDF: "+e.getMessage());
				return null;
			}
	 }
	 
	 private static File stream2file (InputStream in) throws IOException {            
			
		    final File tempFile = File.createTempFile("ReporteVentas", ".pdf");
		    tempFile.deleteOnExit();
		
		    try (FileOutputStream out = new FileOutputStream(tempFile)) {
		        IOUtils.copy(in, out);
		    }
		
		    return tempFile;            
		}

	public void onItemSelect(SelectEvent event) {
		FacesMessage m = new FacesMessage(FacesMessage.SEVERITY_INFO,
				"Producto Encontrado !", event.getObject().toString());
		facesContext.addMessage(null, m);
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

	public List<AlmProducto> getListProducto() {
		return listProducto;
	}

	public void setListProducto(List<AlmProducto> listProducto) {
		this.listProducto = listProducto;
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

	public List<Proveedor> getListProveedor() {
		return listProveedor;
	}

	public void setListProveedor(List<Proveedor> listProveedor) {
		this.listProveedor = listProveedor;
	}

	public List<PedidoMov> getListaPedidoProcesadas() {
		return listaPedidoProcesadas;
	}

	public void setListaPedidoProcesadas(List<PedidoMov> listaPedidoProcesadas) {
		this.listaPedidoProcesadas = listaPedidoProcesadas;
	}

	public List<PedidoMov> getListaPedidoAnuladas() {
		return listaPedidoAnuladas;
	}

	public void setListaPedidoAnuladas(List<PedidoMov> listaPedidoAnuladas) {
		this.listaPedidoAnuladas = listaPedidoAnuladas;
	}

	public List<PedidoMov> getListaPedidoTodos() {
		return listaPedidoTodos;
	}

	public void setListaPedidoTodos(List<PedidoMov> listaPedidoTodos) {
		this.listaPedidoTodos = listaPedidoTodos;
	}

	public double getStockExistente() {
		return stockExistente;
	}

	public void setStockExistente(double stockExistente) {
		this.stockExistente = stockExistente;
	}

	public AlmProducto getSelectAlmacenProducto() {
		return selectAlmacenProducto;
	}

	public void setSelectAlmacenProducto(AlmProducto selectAlmacenProducto) {
		this.selectAlmacenProducto = selectAlmacenProducto;
	}

	public Producto getProducto() {
		return producto;
	}

	public void setProducto(Producto producto) {
		this.producto = producto;
	}

	public List<Producto> getListaProducto() {
		return listaProducto;
	}

	public void setListaProducto(List<Producto> listaProducto) {
		this.listaProducto = listaProducto;
	}

	public List<ProductoProveedor> getListProductoProveedor() {
		return listProductoProveedor;
	}

	public void setListProductoProveedor(
			List<ProductoProveedor> listProductoProveedor) {
		this.listProductoProveedor = listProductoProveedor;
	}

	public List<AlmProducto> getListAlmacenProducto() {
		return listAlmacenProducto;
	}

	public void setListAlmacenProducto(List<AlmProducto> listAlmacenProducto) {
		this.listAlmacenProducto = listAlmacenProducto;
	}

	public StreamedContent getFile() {
		return file;
	}

	public void setFile(StreamedContent file) {
		this.file = file;
	}

	public double getTipoCambio() {
		return tipoCambio;
	}

	public void setTipoCambio(double tipoCambio) {
		this.tipoCambio = tipoCambio;
	}

	public double getTotalDolares() {
		return totalDolares;
	}

	public void setTotalDolares(double totalDolares) {
		this.totalDolares = totalDolares;
	}

	

}
