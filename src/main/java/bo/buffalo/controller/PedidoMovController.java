package bo.buffalo.controller;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.Serializable;
import java.net.URL;
import java.net.UnknownHostException;
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

import org.apache.commons.io.IOUtils;
import org.primefaces.component.tabview.TabView;
import org.primefaces.context.RequestContext;
import org.primefaces.event.SelectEvent;
import org.primefaces.event.TabChangeEvent;
import org.primefaces.model.DefaultStreamedContent;
import org.primefaces.model.StreamedContent;
import org.richfaces.cdi.push.Push;

import bo.buffalo.data.AlmacenProductoRepository;
import bo.buffalo.data.AlmacenRepository;
import bo.buffalo.data.DetallePedidoMovRepository;
import bo.buffalo.data.MovimientoAlmacenRepository;
import bo.buffalo.data.ParametroSistemaRepository;
import bo.buffalo.data.PedidoMovRepository;
import bo.buffalo.data.ProductoProveedorRepository;
import bo.buffalo.data.ProveedorRepository;
import bo.buffalo.data.UsuarioRepository;
import bo.buffalo.model.AlmProducto;
import bo.buffalo.model.Almacen;
import bo.buffalo.model.AtributoProducto;
import bo.buffalo.model.CardexProducto;
import bo.buffalo.model.DetallePedidoMov;
import bo.buffalo.model.HistorialCostos;
import bo.buffalo.model.PedidoMov;
import bo.buffalo.model.Producto;
import bo.buffalo.model.ProductoProveedor;
import bo.buffalo.model.Proveedor;
import bo.buffalo.model.Usuario;
import bo.buffalo.service.AlmacenProductoRegistration;
import bo.buffalo.service.CardexProductoRegistration;
import bo.buffalo.service.DetallePedidoMovRegistration;
import bo.buffalo.service.HistorialCostosRegistration;
import bo.buffalo.service.PedidoMovRegistration;
import bo.buffalo.service.ProductoProveedorRegistration;
import bo.buffalo.service.ProductopRegistration;
import bo.buffalo.util.EstructuraCentralCostos;
import bo.buffalo.util.UtilCostosCalculation;

@Named(value = "pedidoMovController")
@ConversationScoped
public class PedidoMovController implements Serializable {

	private static final long serialVersionUID = -7330438510418956051L;

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
	private Usuario usuarioSession;

	@Inject
	@Push(topic = PUSH_CDI_TOPIC)
	Event<String> pushEventSucursal;

	private boolean modificar = false;
	private boolean nuevo = false;
	private boolean estadoDetalleProcesar=false;
	private boolean estadoDetalleAnular=false;
	private boolean estadoDetalleTodas=false;
	private boolean aplicardescuentoUno=true;
	private boolean aplicardescuentoDos=true;
	private boolean aplicarPrecioVenta=false;
	private String activePendientes="active";
	private String activeProcesadas="";
	private String activeAnuladas="";
	private String activeTodas="";
	private String tituloPanel = "Registrar PedidoMov";
	private PedidoMov selectedPedidoMov;
	private PedidoMov newPedidoMov;

	/* private ProductoProveedor selectedProducto; */
	
	private @Inject ProductoProveedorRepository productoProveedorRepository;
	private Integer idProductoProveedor;
	private Integer cantidad = 1;
	private Integer bonificacion=0;

	private List<PedidoMov> listaPedidoMov;
	private List<ProductoProveedor> listProducto = new ArrayList<ProductoProveedor>();
	private List<DetallePedidoMov> listaPedido = new ArrayList<DetallePedidoMov>();
	private PedidoMov selectedPedido;

	@Inject
	private AlmacenRepository almacenRepository;

	@Inject
	private AlmacenProductoRepository almacenProductoRepository;

	private List<Almacen> listAlmacen = new ArrayList<Almacen>();

	private ProductoProveedor selectedProductoProveedor;

	private double total = 0;
	
	private double totalDolares=0;
	
	private double precioUnitario=0;

	private DetallePedidoMov selectedDetallePedido;

	private List<Proveedor> listProveedor = new ArrayList<Proveedor>();

	private Integer tabIndex;

	private List<PedidoMov> listaEntregaPedido = new ArrayList<PedidoMov>();
	@Inject
	private MovimientoAlmacenRepository movimientoAlmacenRepository;
	
	@Inject
	private  ProveedorRepository proveedorRepository;

	private Integer numeroEntrega;

	private double totalEntrega;
	
	private List<PedidoMov> listaPedidoProcesadas = new ArrayList<PedidoMov>();
	private List<PedidoMov> listaPedidoAnuladas = new ArrayList<PedidoMov>();
	private List<PedidoMov> listaPedidoTodos = new ArrayList<PedidoMov>();
	private List<DetallePedidoMov> listAplicarPrecio;
	private @Inject ProductopRegistration productoRegistration;
	// @Named provides access the return value via the EL variable name
	// "servicios" in the UI (e.g.
	// Facelets or JSP view)

	@Inject
	private AlmacenProductoRegistration almacenProductoRegistration;
	@Inject
	CardexProductoRegistration cardexProductoRegistration;
	
	
    private StreamedContent file;  
    private StreamedContent streamedContent;
    private @Inject ParametroSistemaRepository parametroSistemaRepository;
	private @Inject ProductoProveedorRegistration productoProveedorRegistration;
	private @Inject HistorialCostosRegistration historialCostosRegistration;
	private String moneda="$us." ;

	
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

	@Produces
	@Named
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
		listProducto = productoProveedorRepository.traerProductoProveedors();

		// traer todos las pedidoes ordenados por ID Desc
		listaPedidoMov = pedidoRepository
				.findExternoNuevoOrderedByID(usuarioSession);
		
		listaPedidoProcesadas = pedidoRepository.findExternoProcesadasOrderedByID(usuarioSession);
		listaPedidoAnuladas = pedidoRepository.findExternoAnuladasOrderedByID(usuarioSession);
		listaPedidoTodos = pedidoRepository.findAllExternoOrderedByID(usuarioSession);
		
	/*	listAlmacen = almacenRepositor
				.findAlmacensDiferentsUser(usuarioSession);*/
		modificar = false;
		nuevo = false;
		estadoDetalleProcesar=false;
		estadoDetalleProcesar=false;
		estadoDetalleAnular=false;
		estadoDetalleTodas=false;
		activePendientes="active";
		activeProcesadas="";
		activeAnuladas="";
		activeTodas="";
		listaPedido.clear();
		listAlmacen= almacenRepository.findAlmacenForSucursal(usuarioSession.getSucursal());
		//alm = almacenRepository.findAlmacenForUser(usuarioSession);
		listProveedor= proveedorRepository.traerProveedoresActivas();
		
	}
	
	public void updatePendientes(){
		beginConversation();

		HttpServletRequest request = (HttpServletRequest) facesContext
				.getExternalContext().getRequest();
		System.out.println("Update Pendientes");

		usuarioSession = usuarioRepository.findByLogin(request
				.getUserPrincipal().getName());
		System.out.println("Sucursal Usuario: "
				+ usuarioSession.getSucursal().getNombre());
		listProducto = productoProveedorRepository.traerProductoProveedors();

		// traer todos las pedidoes ordenados por ID Desc
		listaPedidoMov = pedidoRepository
				.findExternoNuevoOrderedByID(usuarioSession);
		
		listaPedidoProcesadas = pedidoRepository.findExternoProcesadasOrderedByID(usuarioSession);
		listaPedidoAnuladas = pedidoRepository.findExternoAnuladasOrderedByID(usuarioSession);
		listaPedidoTodos = pedidoRepository.findAllExternoOrderedByID(usuarioSession);		

		modificar = false;
		nuevo = false;
		estadoDetalleProcesar=false;
		estadoDetalleAnular=false;
		estadoDetalleTodas=false;
		activePendientes="active";
		activeProcesadas="";
		activeAnuladas="";
		activeTodas="";
		
		listaPedido.clear();
		listAlmacen= almacenRepository.findAlmacenForSucursal(usuarioSession.getSucursal());
		//alm = almacenRepository.findAlmacenForUser(usuarioSession);
		listProveedor= proveedorRepository.traerProveedoresActivas();
	}
	
	public void updateProcesadas(){
		beginConversation();

		HttpServletRequest request = (HttpServletRequest) facesContext
				.getExternalContext().getRequest();
		System.out.println("Update Pendientes");

		usuarioSession = usuarioRepository.findByLogin(request
				.getUserPrincipal().getName());
		System.out.println("Sucursal Usuario: "
				+ usuarioSession.getSucursal().getNombre());
		listProducto = productoProveedorRepository.traerProductoProveedors();

		// traer todos las pedidoes ordenados por ID Desc
		listaPedidoMov = pedidoRepository
				.findExternoNuevoOrderedByID(usuarioSession);
		
		listaPedidoProcesadas = pedidoRepository.findExternoProcesadasOrderedByID(usuarioSession);
		listaPedidoAnuladas = pedidoRepository.findExternoAnuladasOrderedByID(usuarioSession);
		listaPedidoTodos = pedidoRepository.findAllExternoOrderedByID(usuarioSession);		

		modificar = false;
		nuevo = false;
		estadoDetalleProcesar=false;
		estadoDetalleAnular=false;
		estadoDetalleTodas=false;
		activePendientes="";
		activeProcesadas="active";
		activeAnuladas="";
		activeTodas="";
		listaPedido.clear();
		listAlmacen= almacenRepository.findAlmacenForSucursal(usuarioSession.getSucursal());
		//alm = almacenRepository.findAlmacenForUser(usuarioSession);
		listProveedor= proveedorRepository.traerProveedoresActivas();	
	}
	
	public void updateAnuladas(){
		beginConversation();

		HttpServletRequest request = (HttpServletRequest) facesContext
				.getExternalContext().getRequest();
		System.out.println("Update Pendientes");

		usuarioSession = usuarioRepository.findByLogin(request
				.getUserPrincipal().getName());
		System.out.println("Sucursal Usuario: "
				+ usuarioSession.getSucursal().getNombre());
		listProducto = productoProveedorRepository.traerProductoProveedors();

		// traer todos las pedidoes ordenados por ID Desc
		listaPedidoMov = pedidoRepository
				.findExternoNuevoOrderedByID(usuarioSession);
		
		listaPedidoProcesadas = pedidoRepository.findExternoProcesadasOrderedByID(usuarioSession);
		listaPedidoAnuladas = pedidoRepository.findExternoAnuladasOrderedByID(usuarioSession);
		listaPedidoTodos = pedidoRepository.findAllExternoOrderedByID(usuarioSession);
		

		modificar = false;
		nuevo = false;
		estadoDetalleProcesar=false;
		estadoDetalleAnular=false;
		estadoDetalleTodas=false;
		activePendientes="";
		activeProcesadas="";
		activeAnuladas="active";
		activeTodas="";
		
		listaPedido.clear();
		listAlmacen= almacenRepository.findAlmacenForSucursal(usuarioSession.getSucursal());
		//alm = almacenRepository.findAlmacenForUser(usuarioSession);
		listProveedor= proveedorRepository.traerProveedoresActivas();	
	}
	
	public void updateTodas(){
		beginConversation();

		HttpServletRequest request = (HttpServletRequest) facesContext
				.getExternalContext().getRequest();
		System.out.println("Update Pendientes");

		usuarioSession = usuarioRepository.findByLogin(request
				.getUserPrincipal().getName());
		System.out.println("Sucursal Usuario: "
				+ usuarioSession.getSucursal().getNombre());
		listProducto = productoProveedorRepository.traerProductoProveedors();

		// traer todos las pedidoes ordenados por ID Desc
		listaPedidoMov = pedidoRepository
				.findExternoNuevoOrderedByID(usuarioSession);
		
		listaPedidoProcesadas = pedidoRepository.findExternoProcesadasOrderedByID(usuarioSession);
		listaPedidoAnuladas = pedidoRepository.findExternoAnuladasOrderedByID(usuarioSession);
		listaPedidoTodos = pedidoRepository.findAllExternoOrderedByID(usuarioSession);	

		modificar = false;
		nuevo = false;
		estadoDetalleProcesar=false;
		estadoDetalleAnular=false;
		estadoDetalleTodas=false;
		activePendientes="";
		activeProcesadas="";
		activeAnuladas="";
		activeTodas="active";
		
		listaPedido.clear();
		listAlmacen= almacenRepository.findAlmacenForSucursal(usuarioSession.getSucursal());
		//alm = almacenRepository.findAlmacenForUser(usuarioSession);
		listProveedor= proveedorRepository.traerProveedoresActivas();
	}
	
	public void obtenerProveedores(){
		try {
			System.out.println("Ingreso a obtenerProveedores");
			setListProveedor(almacenProductoRepository.findProveedorForAlmacen(newPedidoMov.getAlmIn()));
			
		} catch (Exception e) {
		System.err.println("Error en obtenerProveedores "+e.getMessage());
		e.getStackTrace();
		}
	}

	public void listarProducto() {
		try {
			System.out.println("[listarProducto] Ingreso a listarProducto");
			if(newPedidoMov.getProveedor()!=null&&newPedidoMov.getProveedor().getId()!=null){
				if(!listaPedido.isEmpty()){
					if(listaPedido.get(0).getProveedor().getId()!=newPedidoMov.getProveedor().getId()){
						FacesMessage m = new FacesMessage(FacesMessage.SEVERITY_WARN,
								"Advertencia!", "Usted tiene productos seleccionados del proveedor "+listaPedido.get(0).getProveedor().getNombre());
						facesContext.addMessage(null, m);
						return;
					}
				}
				Proveedor p= proveedorRepository.findById(newPedidoMov.getProveedor().getId());
				newPedidoMov.setProveedor(p);
				selectedDetallePedido=new DetallePedidoMov();
				System.out.println("Entro a proveedor distinto de null : ");
				System.out.println("Proveedor : "+newPedidoMov.getProveedor().toString());
				listProducto = productoProveedorRepository.traerProductoProveedor(newPedidoMov.getProveedor());
				System.out.println("Lista: "+ listProducto.size());
				limpiarCamposProducto();
				RequestContext.getCurrentInstance().execute("PF('dlgPreparado').show()");
			}else{
				System.out.println("No se encuentra proveedor");
				FacesMessage m = new FacesMessage(FacesMessage.SEVERITY_WARN,
						"Advertencia!", "Por Favor, Seleccione un Proveedor!");
				facesContext.addMessage(null, m);
			}

		} catch (Exception e) {
			System.err.println("[listarProducto]Error en listarProducto : " + e.getMessage());
			e.getStackTrace();
		}

	}

	public void crearPredido() {
		System.out.println("Ingresa a crearPredido");
		newPedidoMov = new PedidoMov();
		newPedidoMov.setEstado("AC");
		newPedidoMov.setFechaRegistro(new Date());
		newPedidoMov.setTipoPedido("EXT");
		newPedidoMov.setObservacion("COMPRA");
		newPedidoMov.setUsuarioRegistro(usuarioSession.getLogin());
		newPedidoMov.setTipoIngreso("CONTADO");
		newPedidoMov.setTipoDocumento("NA");
		newPedidoMov.setEntrega("NEW");
		newPedidoMov.setCorrelativo(pedidoRepository.maxOrdenCompra());
		System.out.println("Correlativo: "+newPedidoMov.getCorrelativo());
		try {
			newPedidoMov.setTipoCambio(Double.valueOf(parametroSistemaRepository.findByKey("PC").getValor()));
		} catch (Exception e) {
			System.out.println("Error en crearPredido : "+e.getMessage());
		}
		// traer todos las pedidoes ordenados por ID Desc
		listaEntregaPedido = pedidoRepository.findAllExternoOrderedByID(usuarioSession);

		limpiarCamposProducto();

		// tituloPanel
		tituloPanel = "Registrar Pedido";
		nuevo = true;
		modificar = false;

	}


	private void calculateTotals() {
		System.out.println("Ingreso a calculateTotals");
		double totalBolivianos = 0;
		double totalDolares=0;
	
		Integer index = 1;
		//double totalDescuento=newPedidoMov.getDescuentoUno()+newPedidoMov.getDescuentoDos();
		double totalDescuento=newPedidoMov.getDescuentoUno();
		System.out.println("Total Descuento: "+totalDescuento);
		for (DetallePedidoMov value : listaPedido) {
			if(value.getMoneda().equals("US")){
				value.setTotal(value.getTotalDolares()*newPedidoMov.getTipoCambio());
			}else{
				value.setTotalDolares(value.getTotal()/newPedidoMov.getTipoCambio());
			}
			value.setDescuentoUno(value.getPrecioProveedor()*newPedidoMov.getDescuentoUno());
			//value.setDescuentoDos(value.getPrecioProveedor()*newPedidoMov.getDescuentoDos());
			value.setPrecioUnitario(value.getPrecioProveedor()-(value.getDescuentoUno()+value.getDescuentoDos()));
			totalBolivianos += value.getTotal();
			totalDolares += value.getTotalDolares();
			value.setCorrelativo(index);
			index++;
		}
		System.out.println("Total Bolivianos:" + totalBolivianos);
		System.out.println("Total Dolares:" + this.totalDolares);
		newPedidoMov.setTotal(totalBolivianos);
		newPedidoMov.setTotalSus(totalDolares);
	}
	
	public void actualizarOrdenDetalle() throws UnknownHostException{
		System.out.println("Descuento Uno: "+newPedidoMov.getDescuentoUno());
		//System.out.println("Descuento Dos: "+newPedidoMov.getDescuentoDos());
		try {
			FacesContext context = FacesContext.getCurrentInstance();
			HttpServletRequest request = (HttpServletRequest) context.getExternalContext().getRequest();
			System.out.println(request.getRemoteUser());
			System.out.println(request.getRemoteAddr());
		} catch (Exception e) {
			System.out.println("No se pudo optener datos");
		}
			
		calculateTotals();

	}
	


	public void selectedDetalle() {
		System.out.println("Igreso a selectedDetalle..");
		//double totalDescuento=newPedidoMov.getDescuentoDos()+newPedidoMov.getDescuentoUno();
		double totalDescuento=newPedidoMov.getDescuentoUno();
		if(selectedProductoProveedor.getTipoCambio().equals("US")){
			setTotal((selectedProductoProveedor.getPrecioUnitarioCompra() * cantidad)*newPedidoMov.getTipoCambio()
					-(((selectedProductoProveedor.getPrecioUnitarioCompra() * cantidad)*newPedidoMov.getTipoCambio())*totalDescuento));
			setTotalDolares((selectedProductoProveedor.getPrecioUnitarioCompra() * cantidad)
					-((selectedProductoProveedor.getPrecioUnitarioCompra() * cantidad)*totalDescuento));
			setPrecioUnitario(getTotalDolares()/(cantidad+bonificacion));
			
		}else{
			setTotal((selectedProductoProveedor.getPrecioUnitarioCompra() * cantidad)-((selectedProductoProveedor.getPrecioUnitarioCompra() * cantidad)*totalDescuento));
			setTotalDolares(((selectedProductoProveedor.getPrecioUnitarioCompra() * cantidad)/newPedidoMov.getTipoCambio())
					-(((selectedProductoProveedor.getPrecioUnitarioCompra() * cantidad)/newPedidoMov.getTipoCambio())*totalDescuento));
			setPrecioUnitario(getTotal()/(cantidad+bonificacion));
		}
		
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
			listaPedido = detallePedidoRepository.findAllOrderedByID(newPedidoMov);
			tituloPanel = "Modificar Orden  OC-"+newPedidoMov.getId();
			modificar = true;
			nuevo = false;
			estadoDetalleAnular=false;
			estadoDetalleTodas=false;
			estadoDetalleProcesar = false;
			activePendientes="active";
			activeProcesadas="";
			activeAnuladas="";
			activeTodas="";
			Proveedor pr= new Proveedor();
			pr.setId(newPedidoMov.getProveedor().getId());
			newPedidoMov.setProveedor(pr);
			System.out.println("Proveedor : "+ newPedidoMov.getProveedor().toString());
			calculateTotals();					
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
			System.out.println("Error in onRowSelectPedidoMovClick: "
					+ e.getMessage());
		}
	}
	
	public void onRowSelectPedidoProcesadoMovClick(SelectEvent event) {
		try {
			PedidoMov pedido = (PedidoMov) event.getObject();
			System.out.println("onRowSelectPedidoMovClick  " + pedido.getId());
			selectedPedidoMov = pedido;
			newPedidoMov = em.find(PedidoMov.class, pedido.getId());
			listaPedido = detallePedidoRepository.findAllOrderedByID(newPedidoMov);
			tituloPanel = "Detalle Orden Procesado OC-"+newPedidoMov.getId();
			estadoDetalleProcesar = true;
			modificar = false;
			nuevo = false;
			estadoDetalleAnular=false;
			estadoDetalleTodas=false;
			activePendientes="";
			activeProcesadas="active";
			activeAnuladas="";
			activeTodas="";
			Proveedor pr= new Proveedor();
			pr.setId(newPedidoMov.getProveedor().getId());
			newPedidoMov.setProveedor(pr);
			System.out.println("Proveedor : "+ newPedidoMov.getProveedor().toString());

		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
			System.out.println("Error in onRowSelectPedidoMovClick: "
					+ e.getMessage());
		}
	}
	
	public void onRowSelectPedidoAnularMovClick(SelectEvent event) {
		try {
			PedidoMov pedido = (PedidoMov) event.getObject();
			System.out.println("onRowSelectPedidoMovClick  " + pedido.getId());
			selectedPedidoMov = pedido;
			newPedidoMov = em.find(PedidoMov.class, pedido.getId());
			listaPedido = detallePedidoRepository
					.findAllOrderedByID(newPedidoMov);

			tituloPanel = "Detalle Orden Anulado OC-"+newPedidoMov.getId();
			estadoDetalleProcesar = false;
			modificar = false;
			nuevo = false;
			estadoDetalleAnular=true;
			estadoDetalleTodas=false;
			activePendientes="";
			activeProcesadas="";
			activeAnuladas="active";
			activeTodas="";
			Proveedor pr= new Proveedor();
			pr.setId(newPedidoMov.getProveedor().getId());
			newPedidoMov.setProveedor(pr);
			System.out.println("Proveedor : "+ newPedidoMov.getProveedor().toString());

		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
			System.out.println("Error in onRowSelectPedidoMovClick: "
					+ e.getMessage());
		}
	}
	
	public void onRowSelectPedidoTodasMovClick(SelectEvent event) {
		try {
			PedidoMov pedido = (PedidoMov) event.getObject();
			System.out.println("onRowSelectPedidoMovClick  " + pedido.getId());
			selectedPedidoMov = pedido;
			newPedidoMov = em.find(PedidoMov.class, pedido.getId());
			listaPedido = detallePedidoRepository
					.findAllOrderedByID(newPedidoMov);

			tituloPanel = "Detalle Pedido "+newPedidoMov.getEstado()+"  OC-"+newPedidoMov.getId();
			estadoDetalleProcesar = false;
			modificar = false;
			nuevo = false;
			estadoDetalleAnular=false;
			estadoDetalleTodas=true;
			activePendientes="";
			activeProcesadas="";
			activeAnuladas="";
			activeTodas="active";
			Proveedor pr= new Proveedor();
			pr.setId(newPedidoMov.getProveedor().getId());
			newPedidoMov.setProveedor(pr);
			System.out.println("Proveedor : "+ newPedidoMov.getProveedor().toString());
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
			System.out.println("Error in onRowSelectPedidoMovClick: "
					+ e.getMessage());
		}
	}	

	public void procesarPedido() {
		try {
			System.out.println("[procesarPedido]Ingreso a modificarPedidoMov: "
					+ newPedidoMov.getId());
			entregas();
			listAplicarPrecio=new ArrayList<>();	
			for (DetallePedidoMov value : listaPedido) {
				if(value.isAplicarPrecio()){
					listAplicarPrecio.add(value);
				}
	
				double precio = value.getTotal();
				double cant = value.getCantidad()+value.getBonificacion();
				value.setEstadoEntrega("COMPLETADO");
				value.setEstado("PR");			
				detallePedidoRegistration.updated(value);
				ingresoAlmacen(value, cant, precio, newPedidoMov);

			}
	
			newPedidoMov.setFechaAprobacion(new Date());
			newPedidoMov.setEntrega("COM");
			newPedidoMov.setEstado("PR");
			pedidoRegistration.updated(newPedidoMov);

			System.out.println("[procesarPedido]detalle Modificado.. ");
			
			
			if(!listAplicarPrecio.isEmpty()){
				
				RequestContext.getCurrentInstance().execute("dlgAplicarPrecio.show()");
			}
			
			initNewPedidoMov();
			FacesMessage m = new FacesMessage(FacesMessage.SEVERITY_INFO,
					"Modificado!", "Pedido Modificado Correctamente");
			facesContext.addMessage(null, m);
			pushEventSucursal.fire(String.format(
					"PedidoMov Modificada: %s (id: %d)",
					newPedidoMov.getObservacion(), newPedidoMov.getId()));

		} catch (Exception e) {
			System.out.print("[procesarPedido] Error al intentar procesar0:"+e.getMessage());
			String errorMessage = getRootErrorMessage(e);
			FacesMessage m = new FacesMessage(FacesMessage.SEVERITY_ERROR,
					errorMessage, "No se pudo modificar el Pedido.");
			facesContext.addMessage(null, m);
		}
	}
	
	public void aplicarPrecio(){
		if(!listAplicarPrecio.isEmpty()){
			for (DetallePedidoMov detalle : listAplicarPrecio) {
				Double precio=detalle.getPrecioVenta();
				Producto p=detalle.getProducto();
				modificarProducto(p,precio);
			}
			FacesMessage m = new FacesMessage(FacesMessage.SEVERITY_INFO,
					"Aplicado", "Los precios de Venta se aplicaron a los Productos.");
			facesContext.addMessage(null, m);
			
		}else{
			FacesMessage m = new FacesMessage(FacesMessage.SEVERITY_INFO,
					"No Aplicado", "La Lista de Producto Precio Se encuntra Vacia.");
			facesContext.addMessage(null, m);
		}
	}

	public void noAplicarPrecio(){
		FacesMessage m = new FacesMessage(FacesMessage.SEVERITY_INFO,
				"No Aplicado", "Los precios de Venta no se aplicaron.");
		facesContext.addMessage(null, m);
	}
	
	private void modificarProducto(Producto p,double total) {
		try {
			System.out.println("Ingreso a modificarProducto");
			p.setPrecio(total);
			p.setFechaRegistro(new Date());
			p.setUsuarioRegistro(usuarioSession.getName());
			productoRegistration.updated(p,new ArrayList<AtributoProducto>());
			List<EstructuraCentralCostos> listEstructuraCentralCostos = new ArrayList<EstructuraCentralCostos>();
			listEstructuraCentralCostos=cargarEstructuraCostos(p,listEstructuraCentralCostos);
			modificarProductoProveedor(listEstructuraCentralCostos);
			listEstructuraCentralCostos.clear();
			reCalcularMargenUtilidad(p);

		} catch (Exception e) {
			System.err
					.println("Error en modificarProducto : " + e.getMessage());
			e.getStackTrace();
		}
	}
	
	public void reCalcularMargenUtilidad(Producto p) {
		try {
			System.out.println("Ingreso a reCalcularMargenUtilidad");
			List<ProductoProveedor> listProductoProveedor = new ArrayList<ProductoProveedor>();
			listProductoProveedor=productoProveedorRepository.findAllOrderedByIDForProducto(p);
			for (ProductoProveedor value : listProductoProveedor) {
				double costo = 0;
				costo = proveedorRepository.obtenerCosto(value.getProveedor());
				double margen=0;
				if (value.getTipoCambio().equals("BS")) {
					margen = ((p.getPrecio() * 100) / value
							.getPrecioUnitarioCompra()) - 100 - costo;
				}else{
					margen = ((p.getPrecio() * 100) / value
							.getPrecioUnitarioCompra()*7) - 100 - costo;
				}
				System.out.println("Margen de Utilidad : " + margen);
				
				value.setUtilidadMax(margen);
				productoProveedorRegistration.updated(value);
				
					HistorialCostos historialCostos = new HistorialCostos();
					historialCostos
							.setObservacion("MODIFICADO POR LA CENTRAL DE COSTOS");
					historialCostos.setProducto(p);
					historialCostos.setEstado("AC");
					historialCostos.setFechaRegistro(new Date());
					historialCostos
							.setUsuarioRegistro(usuarioSession.getName());
					historialCostos
							.setPrecioVenta(p.getPrecio());
					historialCostos.setPrecioCompra(value
							.getPrecioUnitarioCompra());
					historialCostos.setProveedor(value.getProveedor());
					historialCostos.setMargenMax(value.getUtilidadMax());
					historialCostos.setMargenMin(value.getUtilidadMin());
					historialCostosRegistration.register(historialCostos);


				
			}
		} catch (Exception e) {
			System.err.println("Error en reCalcularMargenUtilidad : "
					+ e.getMessage());
			e.getStackTrace();
		}
	}
	
	private List<EstructuraCentralCostos> cargarEstructuraCostos(Producto p,List<EstructuraCentralCostos> listEstructuraCentralCostos) {
		try {
			System.out.println("Ingreso a cargarEstructuraCostos");
			listEstructuraCentralCostos.clear();
			Integer index = 0;
			double costo = 0;
			double utilMax = 0;
			List<ProductoProveedor> listProductoProveedor = new ArrayList<ProductoProveedor>();
			listProductoProveedor=productoProveedorRepository.findAllOrderedByIDForProducto(p);
			for (ProductoProveedor value : listProductoProveedor) {
				costo = proveedorRepository.obtenerCosto(value.getProveedor());
				utilMax = value.getUtilidadMax();

				costo = proveedorRepository.obtenerCosto(value.getProveedor());
				double precioventa = 0;
				if (value.getTipoCambio().equals("BS")) {
					precioventa = ((100 + costo + utilMax) / 100)
							* value.getPrecioUnitarioCompra();
				} else{
					precioventa = ((100 + costo + utilMax) / 100)
							* (value.getPrecioUnitarioCompra()*7);
				}
					
				System.out.println("Precio de Venta : " + precioventa);
				listEstructuraCentralCostos.add(new EstructuraCentralCostos(
						index, value, costo, utilMax, precioventa, p.getPrecio(), 0));
			}
			return listEstructuraCentralCostos;
		} catch (Exception e) {
			System.err.println("Error en cargarEstructuraCostos : "
					+ e.getMessage());
			e.getStackTrace();
			return new ArrayList<EstructuraCentralCostos>();
		}
	}
	
	private void modificarProductoProveedor(List<EstructuraCentralCostos> listEstructuraCentralCostos) {
		try {
			System.out.println("Ingreso a modificarProductoProveedor");
			for (EstructuraCentralCostos value : listEstructuraCentralCostos) {
				value.getProductoProveedor().setFechaRegistro(new Date());
				value.getProductoProveedor().setUsuarioRegistro(
						usuarioSession.getName());
				value.getProductoProveedor().setPrecioUnitarioVenta(total);
				productoProveedorRegistration.updated(value
						.getProductoProveedor());
			}
		} catch (Exception e) {
			System.err.println("Error en modificarProductoProveedor : "
					+ e.getMessage());
			e.getStackTrace();
		}
	}
	
	// SELECT PRESENTACION CLICK
	public void onRowSelectPedidoEntregaClick(SelectEvent event) {
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

	private void entregas() {
		System.out.println("Ingreso a entregas");
		totalEntrega = 0;
		for (DetallePedidoMov value : listaPedido) {

			value.setCantidadEntregado(value.getCantidad()+value.getBonificacion());
			
			value.setTotalEntregado(value.getTotal());
			totalEntrega += value.getTotal();
		}
		newPedidoMov.setTotalEntregado(totalEntrega);
		System.out.println("Termino a entregas : " + totalEntrega);
	}
	
	public List<ProductoProveedor> completeSearhProducto(String query) {
		System.out.print("[completeSearhProducto] iniciando metodo..");
		try {
			listProducto = productoProveedorRepository.traerProductoProveedor(newPedidoMov.getProveedor());
	        List<ProductoProveedor> filteredThemes = new ArrayList<ProductoProveedor>();
	         
	        for (int i = 0; i < listProducto.size(); i++) {
	        	ProductoProveedor skin = listProducto.get(i);
	            if(skin.getProducto().getNombreProducto().toLowerCase().startsWith(query)) {
	                filteredThemes.add(skin);
	            }
	        }
	         
	        return filteredThemes;
			
		} catch (Exception e) {
			System.out.print("[completeSearhProducto] error:"+e.getMessage());
			e.printStackTrace();
			return null;
		}
		
    }
	
	public void addMessage() {
		if(selectedDetallePedido!=null){
			if(aplicarPrecioVenta){
				FacesContext.getCurrentInstance().addMessage(null, new FacesMessage("El precio de venta se aplicara una vez procesado el pedido"));
			}
		}else{
			FacesContext.getCurrentInstance().addMessage(null, new FacesMessage("El precio de venta no se aplicara"));
		}	
    }
     
	// SELECT PRESENTACION CLICK
	public void onRowSelectProductoClick(SelectEvent event) {
		try {
			limpiarCamposProducto();
			aplicardescuentoDos=true;
			aplicardescuentoUno=true;
			aplicarPrecioVenta=false;
			System.out.println("onRowSelectProductoClick  " + event.getObject());
			ProductoProveedor pp=productoProveedorRepository.findById(Integer.valueOf(""+event.getObject()) );
			selectedProductoProveedor=pp;
			System.out.println(selectedProductoProveedor.getId());
			selectedDetallePedido=new DetallePedidoMov();
			selectedDetallePedido.setAplicarPrecio(false);
			selectedDetallePedido.setBonificacion(0);
			selectedDetallePedido.setCantidad(1);
			selectedDetallePedido.setProducto(selectedProductoProveedor.getProducto());
			selectedDetallePedido.setProveedor(selectedProductoProveedor.getProveedor());
			selectedDetallePedido.setEstado("AC");
			selectedDetallePedido.setLineasProveedor(selectedProductoProveedor.getLineasProveedor());
			selectedDetallePedido.setUnidadMedida("Unidad");
			selectedDetallePedido.setUsuarioRegistro(usuarioSession.getLogin());
			selectedDetallePedido.setFechaRegistro(new Date());
			selectedDetallePedido.setPrecioProveedor(selectedProductoProveedor.getPrecioUnitarioCompra());
			selectedDetallePedido.setPrecioUnitario(selectedProductoProveedor.getPrecioUnitarioCompra());
			selectedDetallePedido.setDescuentoUno(0);
			selectedDetallePedido.setDescuentoDos(0);
			selectedDetallePedido.setMoneda(selectedProductoProveedor.getTipoCambio());
			selectedDetallePedido.setCantidadEntrega(0);
			selectedDetallePedido.setCantidadEntregado(0);
			selectedDetallePedido.setTotalEntregado(0);
			double costo= proveedorRepository.obtenerCosto(selectedDetallePedido.getProveedor());
			double precioVenta=UtilCostosCalculation.calculatePrecioVenta(selectedProductoProveedor, costo, newPedidoMov.getTipoCambio());
			double margenUtilidad=UtilCostosCalculation.calculateMargenUtilidad(selectedProductoProveedor, costo, precioVenta, newPedidoMov.getTipoCambio());
			selectedDetallePedido.setPrecioVenta(precioVenta);
			selectedDetallePedido.setMargenUtilidad(margenUtilidad);
			selectedDetallePedido.setTotalDolares(0);
			selectedDetallePedido.setTotal(0);
			selectedProductoProveedor.getPrecioUnitarioCompra();
			calcularDescuentos();
			if(selectedProductoProveedor.getTipoCambio().equals("US")){
				moneda="$us.";
			}else{
				moneda ="Bs.";
			}
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
			System.out.println("Error in onRowSelectPedidoMovClick: "
					+ e.getMessage());
		}
	}
	
	
	public void calcularCantidad(){
		double total=0;
		double cantidad=selectedDetallePedido.getBonificacion()+selectedDetallePedido.getCantidad();
		if(selectedDetallePedido.getMoneda().equals("US")){
			total=selectedDetallePedido.getTotalDolares();
			selectedDetallePedido.setTotal(total*newPedidoMov.getTipoCambio());
		}else{
			total=selectedDetallePedido.getTotal();
			selectedDetallePedido.setTotalDolares(total/newPedidoMov.getTipoCambio());
		}
		if(cantidad>0){
			if(total>0){
				selectedProductoProveedor.getPrecioUnitarioCompra();
			}
		}else{
			FacesContext.getCurrentInstance().addMessage(null, new FacesMessage("La cantidad Tiene que ser Mayor a 0"));
		}
	}
	
	public void calcularMargenUtilidad(){
		try {
			double precioVenta=selectedDetallePedido.getPrecioVenta();
			double costo= proveedorRepository.obtenerCosto(selectedDetallePedido.getProveedor());
			if(precioVenta>=0){
				double margenUtilidad=UtilCostosCalculation.calculateMargenUtilidad(selectedProductoProveedor, costo, precioVenta, newPedidoMov.getTipoCambio());
				selectedDetallePedido.setMargenUtilidad(margenUtilidad/100);
			}else{
				double precioV=UtilCostosCalculation.calculatePrecioVenta(selectedProductoProveedor, costo, newPedidoMov.getTipoCambio(),selectedDetallePedido.getMargenUtilidad());
				selectedDetallePedido.setPrecioVenta(precioV);
				FacesContext.getCurrentInstance().addMessage(null, new FacesMessage("El Precio de Venta debe ser Mayor o Igual a 0"));
			}
		} catch (Exception e) {
			System.out.println("Error Calcular MU : "+e.toString());
		}
	}
	
	public void calcularPrecioVenta(){
	  try {
		  if(selectedDetallePedido.getMargenUtilidad()>=0){
				double costo= proveedorRepository.obtenerCosto(selectedDetallePedido.getProveedor());
				double precioVenta=UtilCostosCalculation.calculatePrecioVenta(selectedProductoProveedor, costo, newPedidoMov.getTipoCambio(),selectedDetallePedido.getMargenUtilidad());
				selectedDetallePedido.setPrecioVenta(precioVenta);
			}else{
				double costo= proveedorRepository.obtenerCosto(selectedDetallePedido.getProveedor());
				double precioVenta=UtilCostosCalculation.calculatePrecioVenta(selectedProductoProveedor, costo, newPedidoMov.getTipoCambio());
				double margenUtilidad=UtilCostosCalculation.calculateMargenUtilidad(selectedProductoProveedor, costo, precioVenta, newPedidoMov.getTipoCambio());
				selectedDetallePedido.setMargenUtilidad(margenUtilidad);
				FacesContext.getCurrentInstance().addMessage(null, new FacesMessage("El Margen De Utilidad debe ser Mayor o Igual a 0"));
			}
		} catch (Exception e) {
			System.out.println("Error Calcular Precio Venta "+e.toString());
		}	
		
	}
	
	public void calcularDescuentos(){
		double precioUnitario=0;
		double descuentoUno=0;
		double descuentoDos=0;
		precioUnitario=selectedProductoProveedor.getPrecioUnitarioCompra();
		if(aplicardescuentoUno){
			descuentoUno=precioUnitario*newPedidoMov.getDescuentoUno();
		}
//		if(aplicardescuentoDos){
//			descuentoDos=precioUnitario*newPedidoMov.getDescuentoDos();
//		}
		selectedDetallePedido.setPrecioUnitario(selectedProductoProveedor.getPrecioUnitarioCompra()-(descuentoUno+descuentoDos));
		selectedDetallePedido.setDescuentoUno(descuentoUno);
		selectedDetallePedido.setDescuentoDos(descuentoDos);
	    System.out.println("[calcularDescuentos]Calculando descuento....");
	}
	
	
	public void agregarDetalle() {
		try {
			System.out.println("Ingreso a agregarDetalle...");
			if(selectedDetallePedido.getTotal()>0){
				calcularDescuentos();
				selectedDetallePedido.setAplicarPrecio(aplicarPrecioVenta);
				listaPedido.add(selectedDetallePedido);
				limpiarCamposProducto();
				calculateTotals();
			}else{
				FacesContext.getCurrentInstance().addMessage(null, new FacesMessage("El Total Item Tiene que ser Mayor a 0"));
			}
		} catch (Exception e) {
			System.err.println("Error en agregarDetalle :" + e.toString());
		}
	}

	public void initTabProcesar() {

		modificar = false;
		nuevo = false;
		totalEntrega = 0;
		listaPedido.clear();
		limpiarCamposProducto();
	}

	public void onTabChange(TabChangeEvent event) {
		TabView tabView = (TabView) event.getComponent();
		tabIndex = tabView.getChildren().indexOf(event.getTab());
		System.out.println("IndexTab : " + tabIndex);
		seleccionarConsulta();

	}
	
	public void seleccionarConsulta(){
		System.out.println("Ingreso a seleccionarConsulta");
		initNewPedidoMov();
		if (tabIndex.equals(0)) {
			listaPedidoMov = pedidoRepository
					.findExternoNuevoOrderedByID(usuarioSession);
		}
		if (tabIndex.equals(1)) {
			listaPedidoMov = pedidoRepository
					.findExternoProcesadasOrderedByID(usuarioSession);
		}
		if (tabIndex.equals(2)) {
			listaPedidoMov = pedidoRepository
					.findExternoAnuladasOrderedByID(usuarioSession);
		}
		if (tabIndex.equals(3)) {
			listaPedidoMov = pedidoRepository
					.findAllExternoOrderedByID(usuarioSession);
		}
		System.out.println("Size : "+listaPedidoMov.size());
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
		cantidad = 1;
		bonificacion=0;
		total = 0;
		totalDolares=0;
	}

	public void registrarPedidoMov() {
		try {
			System.out.println("[registrarPedidoMov] Ingreso a registrarPedidoMov: "
					+ newPedidoMov.getId());
			newPedidoMov.setUsuario(usuarioSession);
			if(listaPedido.size()>0){
				
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
						"Registrado!", "Pedido Registrado Correctamente");
				facesContext.addMessage(null, m);
				pushEventSucursal.fire(String.format(
						"Nueva PedidoMov Registrada: %s (id: %d)",
						newPedidoMov.getObservacion(), newPedidoMov.getId()));
				initNewPedidoMov();
			}else{
				FacesMessage m = new FacesMessage(FacesMessage.SEVERITY_INFO,
						"Advertencia!", "El detalle de la orden no puede estar vacia!");
				facesContext.addMessage(null, m);
			}
		} catch (Exception e) {
			System.out.println("[registrarPedidoMov] Error:"+e.getMessage());
			String errorMessage = getRootErrorMessage(e);
			FacesMessage m = new FacesMessage(FacesMessage.SEVERITY_ERROR,
					errorMessage, "El Pedido no fue Registrado");
			facesContext.addMessage(null, m);
		}
	}

	public void modificarPedidoEntrega() {
		try {
			System.out.println("Ingreso a modificarPedidoEntrega: "
					+ newPedidoMov.getId());
			for (DetallePedidoMov value : listaPedido) {

				double precio = value.getCantidadEntregado()
						* value.getPrecioVenta();
				double cant = value.getCantidadEntregado()+value.getBonificacion();
				value.setEstadoEntrega("COMPLETADO");

				detallePedidoRegistration.updated(value);
				ingresoAlmacen(value, cant, precio, newPedidoMov);

			}

			newPedidoMov.setEntrega("COM");
			newPedidoMov.setEstado("PR");
			pedidoRegistration.updated(newPedidoMov);

			System.out.println("detalle Modificado.. ");
			initNewPedidoMov();
			initTabProcesar();
			FacesMessage m = new FacesMessage(FacesMessage.SEVERITY_INFO,
					"Modificado!", "Pedido de entrega Modificado Correctamente.");
			facesContext.addMessage(null, m);
			pushEventSucursal.fire(String.format(
					"PedidoMov Modificada: %s (id: %d)",
					newPedidoMov.getObservacion(), newPedidoMov.getId()));

		} catch (Exception e) {
			String errorMessage = getRootErrorMessage(e);
			FacesMessage m = new FacesMessage(FacesMessage.SEVERITY_ERROR,
					errorMessage,
					"No se pudo modificar el Pedido de Entrega.");
			facesContext.addMessage(null, m);
		}
	}

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
			cardex.setTransaccion("COMPRA DE PRODUCTO : "
					+ newPedidoMov.getObservacion() + " , OC-"
					+ newPedidoMov.getCorrelativo());
			cardex.setAlmacen(pedido.getAlmIn());
			cardex.setEstado("AC");
			cardex.setPrecioCompra(productoProveedorRepository
					.findProductoProveedor(detail.getProducto(),
							detail.getProveedor(), detail.getLineasProveedor())
					.getPrecioUnitarioCompra());
			cardex.setFechaRegistro(new Date());
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
			cardex.setMovimiento("OC");//ORDEN DE COMPRA
			// CARDEX
			cardexProductoRegistration.register(cardex);
			System.out.println("Ingreso Registrado.. cardex");
		} catch (Exception e) {
			System.err.println("Error en ingresoAlmacen :" + e.getMessage());
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
			FacesMessage m = new FacesMessage(FacesMessage.SEVERITY_INFO,
					"Modificado!", "El pedido se Modifico Correctamente");
			facesContext.addMessage(null, m);
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

		} catch (Exception e) {
			String errorMessage = getRootErrorMessage(e);
			FacesMessage m = new FacesMessage(FacesMessage.SEVERITY_ERROR,
					errorMessage, "El pedido no pudo ser Modificado.");
			facesContext.addMessage(null, m);
		}
	}

	public void eliminarPedidoMov() {
		try {
			System.out.println("Ingreso a eliminarPedidoMov: "
					+ newPedidoMov.getId());
			for (DetallePedidoMov value : listaPedido) {
				value.setEstadoEntrega("ANULADO");
				value.setEstado("RM");			
				detallePedidoRegistration.updated(value);
			}
			newPedidoMov.setEstado("RM");
			pedidoRegistration.updated(newPedidoMov);
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
	
	
	
	
	/**
	 * Imprimir Proforma Preprado
	 */
	public String getURL(){
		try{
			System.out.println("Ingreso a Ver PDF...");
			HttpServletRequest request = (HttpServletRequest) facesContext.getExternalContext().getRequest();  
			String urlPath = request.getRequestURL().toString();
			urlPath = urlPath.substring(0, urlPath.length() - request.getRequestURI().length()) + request.getContextPath() + "/";
			System.out.println("urlPath >> "+urlPath);
			String pUsuario = usuarioSession.getLogin();
			int pSucursal = usuarioSession.getSucursal().getId();
			String urlPDFreporte = urlPath+"ReporteOrdenIngreso?pUsuario="+pUsuario+"&pSucursal="+pSucursal+"&pId="+newPedidoMov.getId(); //&pRutaImagen="+pRutaImagen
			System.out.println("getURL() -> "+urlPDFreporte);
			return urlPDFreporte;
		}catch(Exception e){
			System.out.println("getURL error: "+e.getMessage());
			return "error";
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

				String urlPDFreporte = urlPath+"ReporteOrdenIngreso?pUsuario="+pUsuario+"&pSucursal="+pSucursal+"&pId="+newPedidoMov.getId(); //&pRutaImagen="+pRutaImagen
				System.out.println("URL Reporte PDF: "+urlPDFreporte);
				
				URL url = new URL(urlPDFreporte);
							
				// Read the PDF from the URL and save to a local file
				InputStream is1 = url.openStream();
				File f = stream2file(is1);
				System.out.println("Size Bytes: "+f.length());
				InputStream stream = new FileInputStream(f);  
				streamedContent = new DefaultStreamedContent(stream, "application/pdf", "reporteOrdenIngreso.pdf");
				setFile(new DefaultStreamedContent(stream, "application/pdf", "reporteOrdenIngreso.pdf"));
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

	public List<ProductoProveedor> getListProducto() {
		return listProducto;
	}

	public void setListProducto(List<ProductoProveedor> listProducto) {
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

	public Integer getTabIndex() {
		return tabIndex;
	}

	public void setTabIndex(Integer tabIndex) {
		this.tabIndex = tabIndex;
	}

	public List<PedidoMov> getListaEntregaPedido() {
		return listaEntregaPedido;
	}

	public void setListaEntregaPedido(List<PedidoMov> listaEntregaPedido) {
		this.listaEntregaPedido = listaEntregaPedido;
	}

	public Integer getNumeroEntrega() {
		return numeroEntrega;
	}

	public void setNumeroEntrega(Integer numeroEntrega) {
		this.numeroEntrega = numeroEntrega;
	}

	public double getTotalEntrega() {
		return totalEntrega;
	}

	public void setTotalEntrega(double totalEntrega) {
		this.totalEntrega = totalEntrega;
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

	public StreamedContent getFile() {
		return file;
	}

	public void setFile(StreamedContent file) {
		this.file = file;
	}

	public boolean isEstadoDetalleProcesar() {
		return estadoDetalleProcesar;
	}

	public void setEstadoDetalleProcesar(boolean estadoDetalleProcesar) {
		this.estadoDetalleProcesar = estadoDetalleProcesar;
	}

	public boolean isEstadoDetalleAnular() {
		return estadoDetalleAnular;
	}

	public void setEstadoDetalleAnular(boolean estadoDetalleAnular) {
		this.estadoDetalleAnular = estadoDetalleAnular;
	}

	public boolean isEstadoDetalleTodas() {
		return estadoDetalleTodas;
	}

	public void setEstadoDetalleTodas(boolean estadoDetalleTodas) {
		this.estadoDetalleTodas = estadoDetalleTodas;
	}

	public String getActivePendientes() {
		return activePendientes;
	}

	public void setActivePendientes(String activePendientes) {
		this.activePendientes = activePendientes;
	}

	public String getActiveProcesadas() {
		return activeProcesadas;
	}

	public void setActiveProcesadas(String activeProcesadas) {
		this.activeProcesadas = activeProcesadas;
	}

	public String getActiveAnuladas() {
		return activeAnuladas;
	}

	public void setActiveAnuladas(String activeAnuladas) {
		this.activeAnuladas = activeAnuladas;
	}

	public String getActiveTodas() {
		return activeTodas;
	}

	public void setActiveTodas(String activeTodas) {
		this.activeTodas = activeTodas;
	}


	public Integer getBonificacion() {
		return bonificacion;
	}

	public void setBonificacion(Integer bonificacion) {
		this.bonificacion = bonificacion;
	}

	public double getTotalDolares() {
		return totalDolares;
	}

	public void setTotalDolares(double totalDolares) {
		this.totalDolares = totalDolares;
	}

	public double getPrecioUnitario() {
		return precioUnitario;
	}

	public void setPrecioUnitario(double precioUnitario) {
		this.precioUnitario = precioUnitario;
	}

	public boolean isAplicardescuentoUno() {
		return aplicardescuentoUno;
	}

	public void setAplicardescuentoUno(boolean aplicardescuentoUno) {
		this.aplicardescuentoUno = aplicardescuentoUno;
	}

	public boolean isAplicardescuentoDos() {
		return aplicardescuentoDos;
	}

	public void setAplicardescuentoDos(boolean aplicardescuentoDos) {
		this.aplicardescuentoDos = aplicardescuentoDos;
	}

	public boolean isAplicarPrecioVenta() {
		return aplicarPrecioVenta;
	}

	public void setAplicarPrecioVenta(boolean aplicarPrecioVenta) {
		this.aplicarPrecioVenta = aplicarPrecioVenta;
	}

	public List<DetallePedidoMov> getListAplicarPrecio() {
		return listAplicarPrecio;
	}

	public void setListAplicarPrecio(List<DetallePedidoMov> listAplicarPrecio) {
		this.listAplicarPrecio = listAplicarPrecio;
	}

	public String getMoneda() {
		return moneda;
	}

	public void setMoneda(String moneda) {
		this.moneda = moneda;
	}
	
	
	

}
