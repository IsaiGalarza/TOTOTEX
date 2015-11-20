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
import bo.buffalo.data.DetalleOrdenCompraRepository;
import bo.buffalo.data.DetallePedidoMovRepository;
import bo.buffalo.data.MovimientoAlmacenRepository;
import bo.buffalo.data.OrdenCompraRepository;
import bo.buffalo.data.ParametroSistemaRepository;
import bo.buffalo.data.PedidoMovRepository;
import bo.buffalo.data.ProductoProveedorRepository;
import bo.buffalo.data.ProveedorRepository;
import bo.buffalo.data.UsuarioRepository;
import bo.buffalo.model.AlmProducto;
import bo.buffalo.model.Almacen;
import bo.buffalo.model.AtributoProducto;
import bo.buffalo.model.CardexProducto;
import bo.buffalo.model.DetalleOrdenCompra;
import bo.buffalo.model.HistorialCostos;
import bo.buffalo.model.OrdenCompra;
import bo.buffalo.model.Producto;
import bo.buffalo.model.ProductoProveedor;
import bo.buffalo.model.Proveedor;
import bo.buffalo.model.Usuario;
import bo.buffalo.service.AlmacenProductoRegistration;
import bo.buffalo.service.CardexProductoRegistration;
import bo.buffalo.service.DetalleOrdenCompraRegistration;
import bo.buffalo.service.DetallePedidoMovRegistration;
import bo.buffalo.service.HistorialCostosRegistration;
import bo.buffalo.service.OrdenCompraRegistration;
import bo.buffalo.service.PedidoMovRegistration;
import bo.buffalo.service.ProductoProveedorRegistration;
import bo.buffalo.service.ProductopRegistration;
import bo.buffalo.util.EstructuraCentralCostos;
import bo.buffalo.util.UtilCostosCalculation;

@Named(value = "ordenCompraController")
@ConversationScoped
public class OrdenCompraController implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 4864355816872513749L;

	public static final String PUSH_CDI_TOPIC = "pushCdi";

	@Inject
	private FacesContext facesContext;

	@Inject
	private EntityManager em;

	@Inject
	Conversation conversation;

//	@Inject
//	private PedidoMovRegistration pedidoRegistration;

//	@Inject
//	private DetallePedidoMovRegistration detallePedidoRegistration;
//	@Inject
//	private DetallePedidoMovRepository detallePedidoRepository;
	
	@Inject
	private DetalleOrdenCompraRegistration detalleOrdenCompraRegistration;
	@Inject
	private DetalleOrdenCompraRepository detalleOrdenCompraRepository;	
	
	@Inject
	private OrdenCompraRegistration ordenCompraRegistration;
	@Inject
	private OrdenCompraRepository ordenCompraRepository;

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
	private String tituloPanel = "Registrar OrdenCompra";
	private OrdenCompra selectedOrdenCompra;
	private OrdenCompra newOrdenCompra;

	/* private ProductoProveedor selectedProducto; */
	
	private @Inject ProductoProveedorRepository productoProveedorRepository;
	private Integer idProductoProveedor;
	private Integer cantidad = 1;
	private Integer bonificacion=0;

	private List<OrdenCompra> listaOrdenCompra;
	private List<ProductoProveedor> listProducto = new ArrayList<ProductoProveedor>();
	private List<DetalleOrdenCompra> listaDetalleOC = new ArrayList<DetalleOrdenCompra>();
	private OrdenCompra selectedOC;

	@Inject
	private AlmacenRepository almacenRepository;

	@Inject
	private AlmacenProductoRepository almacenProductoRepository;

	private List<Almacen> listAlmacen = new ArrayList<Almacen>();

	private ProductoProveedor selectedProductoProveedor;

	private double total = 0;
	
	//private double totalDolares=0;
	
	private double precioUnitario=0;

	private DetalleOrdenCompra selectedDetalleOC;

	private List<Proveedor> listProveedor = new ArrayList<Proveedor>();

	private Integer tabIndex;

	private List<OrdenCompra> listaEntregaPedido = new ArrayList<OrdenCompra>();
	@Inject
	private MovimientoAlmacenRepository movimientoAlmacenRepository;
	
	@Inject
	private  ProveedorRepository proveedorRepository;

	private Integer numeroEntrega;

	private double totalEntrega;
	
	private List<OrdenCompra> listaOrdenCompraProcesadas = new ArrayList<OrdenCompra>();
	private List<OrdenCompra> listaOrdenCompraAnuladas = new ArrayList<OrdenCompra>();
	private List<OrdenCompra> listaOrdenCompraTodos = new ArrayList<OrdenCompra>();
	private List<DetalleOrdenCompra> listAplicarPrecio;
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
	//private String moneda="$us." ;

	
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
	
	@PostConstruct
	public void initNewOrdenCompra() {
        try {
    		beginConversation();

    		HttpServletRequest request = (HttpServletRequest) facesContext
    				.getExternalContext().getRequest();
    		System.out
    				.println("[initNewOrdenCompra]init Tipo Producto*********************************");
    		System.out.println("[initNewOrdenCompra]request.getClass().getName():"
    				+ request.getClass().getName());
    		System.out.println("[initNewOrdenCompra]isVentas:" + request.isUserInRole("ventas"));
    		System.out.println("[initNewOrdenCompra]remoteUser:" + request.getRemoteUser());
    		System.out.println("[initNewOrdenCompra]userPrincipalName:"
    				+ (request.getUserPrincipal() == null ? "null" : request
    						.getUserPrincipal().getName()));

    		usuarioSession = usuarioRepository.findByLogin(request
    				.getUserPrincipal().getName());
    		System.out.println("[initNewOrdenCompra]Sucursal Usuario: "
    				+ usuarioSession.getSucursal().getNombre());
    		listProducto = productoProveedorRepository.traerProductoProveedors();    		
    		// traer todos las pedidoes ordenados por ID Desc
    		listaOrdenCompra = ordenCompraRepository
    				.findExternoNuevoOrderedByID(usuarioSession);
    		System.out.println("[initNewOrdenCompra] ListaOrdenCompra size="+listaOrdenCompra.size());    		
    		
    		listaOrdenCompraProcesadas = ordenCompraRepository.findExternoProcesadasOrderedByID(usuarioSession);
    		listaOrdenCompraAnuladas = ordenCompraRepository.findExternoAnuladasOrderedByID(usuarioSession);
    		listaOrdenCompraTodos = ordenCompraRepository.findAllExternoOrderedByID(usuarioSession);
    		
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
    		listaDetalleOC.clear();
    		listAlmacen= almacenRepository.findAlmacenForSucursal(usuarioSession.getSucursal());
    		//alm = almacenRepository.findAlmacenForUser(usuarioSession);
    		listProveedor= proveedorRepository.traerProveedoresActivas();
			
		} catch (Exception e) {
			System.out.println("[initNewOrdenCompra] Error:"+e.getMessage());
		}
		
		
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
		listaOrdenCompra = ordenCompraRepository
				.findExternoNuevoOrderedByID(usuarioSession);
		
		listaOrdenCompraProcesadas = ordenCompraRepository.findExternoProcesadasOrderedByID(usuarioSession);
		listaOrdenCompraAnuladas = ordenCompraRepository.findExternoAnuladasOrderedByID(usuarioSession);
		listaOrdenCompraTodos = ordenCompraRepository.findAllExternoOrderedByID(usuarioSession);		

		modificar = false;
		nuevo = false;
		estadoDetalleProcesar=false;
		estadoDetalleAnular=false;
		estadoDetalleTodas=false;
		activePendientes="active";
		activeProcesadas="";
		activeAnuladas="";
		activeTodas="";
		
		listaOrdenCompra.clear();
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
		listaOrdenCompra = ordenCompraRepository
				.findExternoNuevoOrderedByID(usuarioSession);
		
		listaOrdenCompraProcesadas = ordenCompraRepository.findExternoProcesadasOrderedByID(usuarioSession);
		listaOrdenCompraAnuladas = ordenCompraRepository.findExternoAnuladasOrderedByID(usuarioSession);
		listaOrdenCompraTodos = ordenCompraRepository.findAllExternoOrderedByID(usuarioSession);		

		modificar = false;
		nuevo = false;
		estadoDetalleProcesar=false;
		estadoDetalleAnular=false;
		estadoDetalleTodas=false;
		activePendientes="";
		activeProcesadas="active";
		activeAnuladas="";
		activeTodas="";
		listaOrdenCompra.clear();
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
		listaOrdenCompra = ordenCompraRepository
				.findExternoNuevoOrderedByID(usuarioSession);
		
		listaOrdenCompraProcesadas = ordenCompraRepository.findExternoProcesadasOrderedByID(usuarioSession);
		listaOrdenCompraAnuladas = ordenCompraRepository.findExternoAnuladasOrderedByID(usuarioSession);
		listaOrdenCompraTodos = ordenCompraRepository.findAllExternoOrderedByID(usuarioSession);
		

		modificar = false;
		nuevo = false;
		estadoDetalleProcesar=false;
		estadoDetalleAnular=false;
		estadoDetalleTodas=false;
		activePendientes="";
		activeProcesadas="";
		activeAnuladas="active";
		activeTodas="";
		
		listaOrdenCompra.clear();
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
		listaOrdenCompra = ordenCompraRepository
				.findExternoNuevoOrderedByID(usuarioSession);
		
		listaOrdenCompraProcesadas = ordenCompraRepository.findExternoProcesadasOrderedByID(usuarioSession);
		listaOrdenCompraAnuladas = ordenCompraRepository.findExternoAnuladasOrderedByID(usuarioSession);
		listaOrdenCompraTodos = ordenCompraRepository.findAllExternoOrderedByID(usuarioSession);	

		modificar = false;
		nuevo = false;
		estadoDetalleProcesar=false;
		estadoDetalleAnular=false;
		estadoDetalleTodas=false;
		activePendientes="";
		activeProcesadas="";
		activeAnuladas="";
		activeTodas="active";
		
		listaOrdenCompra.clear();
		listAlmacen= almacenRepository.findAlmacenForSucursal(usuarioSession.getSucursal());
		//alm = almacenRepository.findAlmacenForUser(usuarioSession);
		listProveedor= proveedorRepository.traerProveedoresActivas();
	}
	
	public void obtenerProveedores(){
		try {
			System.out.println("Ingreso a obtenerProveedores");
			setListProveedor(almacenProductoRepository.findProveedorForAlmacen(newOrdenCompra.getAlmacen()));
			
		} catch (Exception e) {
		System.err.println("Error en obtenerProveedores "+e.getMessage());
		e.getStackTrace();
		}
	}

	public void listarProducto() {
		try {
			System.out.println("[listarProducto] Ingreso a listarProducto");
			if(newOrdenCompra.getProveedor()!=null&&newOrdenCompra.getProveedor().getId()!=null){
				if(!listaDetalleOC.isEmpty()){
					if(listaDetalleOC.get(0).getProveedor().getId()!=newOrdenCompra.getProveedor().getId()){
						FacesMessage m = new FacesMessage(FacesMessage.SEVERITY_WARN,
								"Advertencia!", "Usted tiene productos seleccionados del proveedor "+listaDetalleOC.get(0).getProveedor().getNombre());
						facesContext.addMessage(null, m);
						return;
					}
				}
				Proveedor p= proveedorRepository.findById(newOrdenCompra.getProveedor().getId());
				newOrdenCompra.setProveedor(p);
				selectedDetalleOC=new DetalleOrdenCompra();
				System.out.println("Entro a proveedor distinto de null : ");
				System.out.println("Proveedor : "+newOrdenCompra.getProveedor().toString());
				listProducto = productoProveedorRepository.traerProductoProveedor(newOrdenCompra.getProveedor());
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

	public void crearOrdenCompra() {
		try {
			System.out.println("[crearOrdenCompra]Ingresa a crearPredido...");
			newOrdenCompra = new OrdenCompra();
			newOrdenCompra.setEstado("AC");
			newOrdenCompra.setFechaRegistro(new Date());
			newOrdenCompra.setObservacion("COMPRA");
			newOrdenCompra.setUsuarioRegistro(usuarioSession.getLogin());
			newOrdenCompra.setTipoDocumento("NA");
			newOrdenCompra.setCorrelativo(ordenCompraRepository.maxOrdenCompra());
			
			System.out.println("Correlativo: "+newOrdenCompra.getCorrelativo());
			try {
				//newOrdenCompra.setTipoCambio(Double.valueOf(parametroSistemaRepository.findByKey("PC").getValor()));
			} catch (Exception e) {
				System.out.println("Error en crearPredido : "+e.getMessage());
			}
			// traer todos las pedidoes ordenados por ID Desc
			listaEntregaPedido = ordenCompraRepository.findAllExternoOrderedByID(usuarioSession);

			limpiarCamposProducto();

			// tituloPanel
			tituloPanel = "Registrar Pedido";
			nuevo = true;
			modificar = false;
			
		} catch (Exception e) {
			System.out.println("[crearOrdenCompra] Error:"+e.getMessage());
		}
		

	}


	private void calculateTotals() {
		System.out.println("Ingreso a calculateTotals");
		double totalBolivianos = 0;
		//double totalDolares=0;
	
		Integer index = 1;
		//double totalDescuento=newPedidoMov.getDescuentoUno()+newPedidoMov.getDescuentoDos();
		//double totalDescuento=newOrdenCompra.getDescuentoUno();
		double totalDescuento=newOrdenCompra.getTotalDescuento();
		System.out.println("Total Descuento: "+totalDescuento);
		for (DetalleOrdenCompra value : listaDetalleOC) {
			//corregir
//			if(value.getMoneda().equals("US")){
//				value.setTotal(value.getTotalDolares()*newPedidoMov.getTipoCambio());
//			}else{
//				value.setTotalDolares(value.getTotal()/newPedidoMov.getTipoCambio());
//			}
			//value.setDescuentoUno(value.getPrecioProveedor()*newPedidoMov.getDescuentoUno());
			//value.setDescuentoDos(value.getPrecioProveedor()*newPedidoMov.getDescuentoDos());
			//value.setPrecioUnitario(value.getPrecioProveedor()-(value.getDescuentoUno()+value.getDescuentoDos()));
			totalBolivianos += value.getTotal();
			//totalDolares += value.getTotalDolares();
			value.setCorrelativo(index);
			index++;
		}
		System.out.println("Total Bolivianos:" + totalBolivianos);
		//System.out.println("Total Dolares:" + this.totalDolares);
		newOrdenCompra.setTotalImporte(totalBolivianos);
		//newPedidoMov.setTotalSus(totalDolares);
	}
	
	public void actualizarOrdenDetalle() throws UnknownHostException{
		System.out.println("Descuento: "+newOrdenCompra.getTotalDescuento());
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
		double totalDescuento=newOrdenCompra.getTotalDescuento();
//		if(selectedProductoProveedor.getTipoCambio().equals("US")){
//			setTotal((selectedProductoProveedor.getPrecioUnitarioCompra() * cantidad)*newOrdenCompra.getTipoCambio()
//					-(((selectedProductoProveedor.getPrecioUnitarioCompra() * cantidad)*newPedidoMov.getTipoCambio())*totalDescuento));
//			setTotalDolares((selectedProductoProveedor.getPrecioUnitarioCompra() * cantidad)
//					-((selectedProductoProveedor.getPrecioUnitarioCompra() * cantidad)*totalDescuento));
//			setPrecioUnitario(getTotalDolares()/(cantidad+bonificacion));
//			
//		}else{
//			setTotal((selectedProductoProveedor.getPrecioUnitarioCompra() * cantidad)-((selectedProductoProveedor.getPrecioUnitarioCompra() * cantidad)*totalDescuento));
//			setTotalDolares(((selectedProductoProveedor.getPrecioUnitarioCompra() * cantidad)/newPedidoMov.getTipoCambio())
//					-(((selectedProductoProveedor.getPrecioUnitarioCompra() * cantidad)/newPedidoMov.getTipoCambio())*totalDescuento));
//			setPrecioUnitario(getTotal()/(cantidad+bonificacion));
//		}
		//corregir
		setTotal(100);
		
		
	}

	// SELECT PRESENTACION CLICK
	public void onRowSelectPedidoMovClick(SelectEvent event) {
		try {
			OrdenCompra pedido = (OrdenCompra) event.getObject();
			System.out.println("onRowSelectPedidoMovClick  " + pedido.getId());
			selectedOrdenCompra = pedido;
			newOrdenCompra = em.find(OrdenCompra.class, pedido.getId());
			newOrdenCompra.setFechaRegistro(new Date());
			newOrdenCompra.setUsuarioRegistro(usuarioSession.getLogin());
			listaDetalleOC = detalleOrdenCompraRepository.findAllOrderedByID(newOrdenCompra);
			tituloPanel = "Modificar Orden  OC-"+newOrdenCompra.getId();
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
			pr.setId(newOrdenCompra.getProveedor().getId());
			newOrdenCompra.setProveedor(pr);
			System.out.println("Proveedor : "+ newOrdenCompra.getProveedor().toString());
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
			OrdenCompra oc = (OrdenCompra) event.getObject();
			System.out.println("onRowSelectPedidoMovClick  " + oc.getId());
			selectedOrdenCompra = oc;
			//corregir, no debe llamar directo a Entity am
			newOrdenCompra = em.find(OrdenCompra.class, oc.getId());
			listaDetalleOC = detalleOrdenCompraRepository.findAllOrderedByID(newOrdenCompra);
			tituloPanel = "Detalle Orden Procesado OC-"+newOrdenCompra.getId();
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
			pr.setId(newOrdenCompra.getProveedor().getId());
			newOrdenCompra.setProveedor(pr);
			System.out.println("Proveedor : "+ newOrdenCompra.getProveedor().toString());
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
			System.out.println("Error in onRowSelectPedidoMovClick: "
					+ e.getMessage());
		}
	}
	
	public void onRowSelectPedidoAnularMovClick(SelectEvent event) {
		try {
			OrdenCompra pedido = (OrdenCompra) event.getObject();
			System.out.println("onRowSelectPedidoMovClick  " + pedido.getId());
			selectedOrdenCompra = pedido;
			newOrdenCompra = em.find(OrdenCompra.class, pedido.getId());
			listaDetalleOC = detalleOrdenCompraRepository
					.findAllOrderedByID(newOrdenCompra);

			tituloPanel = "Detalle Orden Anulado OC-"+newOrdenCompra.getId();
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
			pr.setId(newOrdenCompra.getProveedor().getId());
			newOrdenCompra.setProveedor(pr);
			System.out.println("Proveedor : "+ newOrdenCompra.getProveedor().toString());
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
			System.out.println("Error in onRowSelectPedidoMovClick: "
					+ e.getMessage());
		}
	}
	
	public void onRowSelectPedidoTodasMovClick(SelectEvent event) {
		try {
			OrdenCompra pedido = (OrdenCompra) event.getObject();
			System.out.println("onRowSelectPedidoMovClick  " + pedido.getId());
			selectedOrdenCompra = pedido;
			newOrdenCompra = em.find(OrdenCompra.class, pedido.getId());
			listaDetalleOC = detalleOrdenCompraRepository
					.findAllOrderedByID(newOrdenCompra);

			tituloPanel = "Detalle Orden Compra "+newOrdenCompra.getEstado()+"  OC-"+newOrdenCompra.getId();
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
			pr.setId(newOrdenCompra.getProveedor().getId());
			newOrdenCompra.setProveedor(pr);
			System.out.println("Proveedor : "+ newOrdenCompra.getProveedor().toString());
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
			System.out.println("Error in onRowSelectPedidoMovClick: "
					+ e.getMessage());
		}
	}	

	public void procesarOrdenCompra() {
		try {
			System.out.println("[procesarPedido]Ingreso a modificarPedidoMov: "
					+ newOrdenCompra.getId());
			entregas();
			listAplicarPrecio=new ArrayList<>();	
			//cambiar estado a procesado cada detalle
			for (DetalleOrdenCompra value : listaDetalleOC) {
//				if(value.isAplicarPrecio()){
//					listAplicarPrecio.add(value);
//				}	
				double precio = value.getTotal();
				//double cant = value.getCantidad()+value.getBonificacion();
				value.setEstadoEntrega("COMPLETADO");
				value.setEstado("PR");			
				detalleOrdenCompraRegistration.updated(value);
				
				//HAY QUE REGISTRAR EL INGRESO AL ALMACEN
				//ingresoAlmacen(value, cant, precio, newPedidoMov);

			}
	
			newOrdenCompra.setFechaAprobacion(new Date());
			//newOrdenCompra.setEntrega("COM");
			newOrdenCompra.setEstado("PR");
			//falta usuario aprobacion
			ordenCompraRegistration.updated(newOrdenCompra);

			System.out.println("[procesarPedido]detalle Modificado.. ");
			
			
			if(!listAplicarPrecio.isEmpty()){
				
				RequestContext.getCurrentInstance().execute("dlgAplicarPrecio.show()");
			}
			
			//initNewPedidoMov();
			initNewOrdenCompra();
			FacesMessage m = new FacesMessage(FacesMessage.SEVERITY_INFO,
					"Modificado!", "Orden de Compra modificada Correctamente");
			facesContext.addMessage(null, m);
			pushEventSucursal.fire(String.format(
					"Orden de Compra Modificada: %s (id: %d)",
					newOrdenCompra.getObservacion(), newOrdenCompra.getId()));

		} catch (Exception e) {
			System.out.print("[procesarPedido] Error al intentar procesar0:"+e.getMessage());
			String errorMessage = getRootErrorMessage(e);
			FacesMessage m = new FacesMessage(FacesMessage.SEVERITY_ERROR,
					errorMessage, "No se pudo modificar el Pedido.");
			facesContext.addMessage(null, m);
		}
	}
	
	//actualiza los precios de los productos
	public void aplicarPrecio(){

		if(!listAplicarPrecio.isEmpty()){
			for (DetalleOrdenCompra detalle : listAplicarPrecio) {
//				Double precio=detalle.getPrecioVenta();
//				Producto p=detalle.getProducto();
//				modificarProducto(p,precio);
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
//			System.out.println("Ingreso a modificarProducto");
//			p.setPrecio(total);
//			p.setFechaRegistro(new Date());
//			p.setUsuarioRegistro(usuarioSession.getName());
//			productoRegistration.updated(p,new ArrayList<AtributoProducto>());
//			List<EstructuraCentralCostos> listEstructuraCentralCostos = new ArrayList<EstructuraCentralCostos>();
//			listEstructuraCentralCostos=cargarEstructuraCostos(p,listEstructuraCentralCostos);
//			modificarProductoProveedor(listEstructuraCentralCostos);
//			listEstructuraCentralCostos.clear();
//			reCalcularMargenUtilidad(p);

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
			OrdenCompra pedido = (OrdenCompra) event.getObject();
			System.out.println("onRowSelectPedidoMovClick  " + pedido.getId());
			selectedOrdenCompra = pedido;
			newOrdenCompra = em.find(OrdenCompra.class, pedido.getId());
			newOrdenCompra.setFechaRegistro(new Date());
			newOrdenCompra.setUsuarioRegistro(usuarioSession.getLogin());
			listaDetalleOC = detalleOrdenCompraRepository
					.findAllOrderedByID(newOrdenCompra);
			//revisar entregas??
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
//		System.out.println("Ingreso a entregas");
//		totalEntrega = 0;
//		for (DetallePedidoMov value : listaPedido) {
//
//			value.setCantidadEntregado(value.getCantidad()+value.getBonificacion());
//			
//			value.setTotalEntregado(value.getTotal());
//			totalEntrega += value.getTotal();
//		}
//		newPedidoMov.setTotalEntregado(totalEntrega);
//		System.out.println("Termino a entregas : " + totalEntrega);
	}
	
	public List<ProductoProveedor> completeSearhProducto(String query) {
		System.out.print("[completeSearhProducto] iniciando metodo..");
		try {
			listProducto = productoProveedorRepository.traerProductoProveedor(newOrdenCompra.getProveedor());
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
		if(selectedDetalleOC!=null){
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
			selectedDetalleOC=new DetalleOrdenCompra();
			//selectedDetalleOC.setAplicarPrecio(false);
			//selectedDetalleOC.setBonificacion(0);
			selectedDetalleOC.setCantidad(1);
			selectedDetalleOC.setProducto(selectedProductoProveedor.getProducto());
			selectedDetalleOC.setProveedor(selectedProductoProveedor.getProveedor());
			selectedDetalleOC.setEstado("AC");
			//selectedDetalleOC.setLineasProveedor(selectedProductoProveedor.getLineasProveedor());
			//selectedDetalleOC.setUnidadMedida("Unidad");
			selectedDetalleOC.setUsuarioRegistro(usuarioSession.getLogin());
			selectedDetalleOC.setFechaRegistro(new Date());
			//selectedDetalleOC.setPrecioProveedor(selectedProductoProveedor.getPrecioUnitarioCompra());
			selectedDetalleOC.setPrecioUnitario (selectedProductoProveedor.getPrecioUnitarioCompra());
			selectedDetalleOC.setDescuento(0.0);
			double costo= proveedorRepository.obtenerCosto(selectedDetalleOC.getProveedor());
			//double precioVenta=UtilCostosCalculation.calculatePrecioVenta(selectedProductoProveedor, costo, newOrdenCompra.getTipoCambio());
			//double margenUtilidad=UtilCostosCalculation.calculateMargenUtilidad(selectedProductoProveedor, costo, precioVenta, newPedidoMov.getTipoCambio());
//			selectedDetallePedido.setPrecioVenta(precioVenta);
//			selectedDetallePedido.setMargenUtilidad(margenUtilidad);
//			selectedDetallePedido.setTotalDolares(0);
			selectedDetalleOC.setTotal(0);
			selectedProductoProveedor.getPrecioUnitarioCompra();
			calcularDescuentos();
//			if(selectedProductoProveedor.getTipoCambio().equals("US")){
//				moneda="$us.";
//			}else{
//				moneda ="Bs.";
//			}
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
			System.out.println("Error in onRowSelectPedidoMovClick: "
					+ e.getMessage());
		}
	}
	
	
	public void calcularCantidad(){
//		double total=0;
//		double cantidad=selectedDetallePedido.getBonificacion()+selectedDetallePedido.getCantidad();
//		if(selectedDetallePedido.getMoneda().equals("US")){
//			total=selectedDetallePedido.getTotalDolares();
//			selectedDetallePedido.setTotal(total*newPedidoMov.getTipoCambio());
//		}else{
//			total=selectedDetallePedido.getTotal();
//			selectedDetallePedido.setTotalDolares(total/newPedidoMov.getTipoCambio());
//		}
//		if(cantidad>0){
//			if(total>0){
//				selectedProductoProveedor.getPrecioUnitarioCompra();
//			}
//		}else{
//			FacesContext.getCurrentInstance().addMessage(null, new FacesMessage("La cantidad Tiene que ser Mayor a 0"));
//		}
	}
	
	public void calcularMargenUtilidad(){
		try {
//			double precioVenta=selectedDetalleOC.getPrecioVenta();
//			double costo= proveedorRepository.obtenerCosto(selectedDetallePedido.getProveedor());
//			if(precioVenta>=0){
//				double margenUtilidad=UtilCostosCalculation.calculateMargenUtilidad(selectedProductoProveedor, costo, precioVenta, newPedidoMov.getTipoCambio());
//				selectedDetallePedido.setMargenUtilidad(margenUtilidad/100);
//			}else{
//				double precioV=UtilCostosCalculation.calculatePrecioVenta(selectedProductoProveedor, costo, newPedidoMov.getTipoCambio(),selectedDetallePedido.getMargenUtilidad());
//				selectedDetallePedido.setPrecioVenta(precioV);
//				FacesContext.getCurrentInstance().addMessage(null, new FacesMessage("El Precio de Venta debe ser Mayor o Igual a 0"));
//			}
		} catch (Exception e) {
			System.out.println("Error Calcular MU : "+e.toString());
		}
	}
	
	public void calcularPrecioVenta(){
	  try {
//		  if(selectedDetallePedido.getMargenUtilidad()>=0){
//				double costo= proveedorRepository.obtenerCosto(selectedDetallePedido.getProveedor());
//				double precioVenta=UtilCostosCalculation.calculatePrecioVenta(selectedProductoProveedor, costo, newPedidoMov.getTipoCambio(),selectedDetallePedido.getMargenUtilidad());
//				selectedDetallePedido.setPrecioVenta(precioVenta);
//			}else{
//				double costo= proveedorRepository.obtenerCosto(selectedDetallePedido.getProveedor());
//				double precioVenta=UtilCostosCalculation.calculatePrecioVenta(selectedProductoProveedor, costo, newPedidoMov.getTipoCambio());
//				double margenUtilidad=UtilCostosCalculation.calculateMargenUtilidad(selectedProductoProveedor, costo, precioVenta, newPedidoMov.getTipoCambio());
//				selectedDetallePedido.setMargenUtilidad(margenUtilidad);
//				FacesContext.getCurrentInstance().addMessage(null, new FacesMessage("El Margen De Utilidad debe ser Mayor o Igual a 0"));
//			}
		} catch (Exception e) {
			System.out.println("Error Calcular Precio Venta "+e.toString());
		}	
		
	}
	
	public void calcularDescuentos(){
		double precioUnitario=0;
		double descuentoUno=0;
		double descuentoDos=0;
//		precioUnitario=selectedProductoProveedor.getPrecioUnitarioCompra();
//		if(aplicardescuentoUno){
//			descuentoUno=precioUnitario*newPedidoMov.getDescuentoUno();
//		}
////		if(aplicardescuentoDos){
////			descuentoDos=precioUnitario*newPedidoMov.getDescuentoDos();
////		}
//		selectedDetallePedido.setPrecioUnitario(selectedProductoProveedor.getPrecioUnitarioCompra()-(descuentoUno+descuentoDos));
//		selectedDetallePedido.setDescuentoUno(descuentoUno);
//		selectedDetallePedido.setDescuentoDos(descuentoDos);
	    System.out.println("[calcularDescuentos]Calculando descuento....");
	}
	
	
	public void agregarDetalle() {
		try {
			System.out.println("Ingreso a agregarDetalle...");
			if(selectedDetalleOC.getTotal()>0){
				calcularDescuentos();
				//selectedDetalleOC.setAplicarPrecio(aplicarPrecioVenta);
				listaDetalleOC.add(selectedDetalleOC);
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
		listaDetalleOC.clear();
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
		initNewOrdenCompra();
		if (tabIndex.equals(0)) {
			listaOrdenCompra = ordenCompraRepository
					.findExternoNuevoOrderedByID(usuarioSession);
		}
		if (tabIndex.equals(1)) {
			listaOrdenCompra = ordenCompraRepository
					.findExternoProcesadasOrderedByID(usuarioSession);
		}
		if (tabIndex.equals(2)) {
			listaOrdenCompra = ordenCompraRepository
					.findExternoAnuladasOrderedByID(usuarioSession);
		}
		if (tabIndex.equals(3)) {
			listaOrdenCompra = ordenCompraRepository
					.findAllExternoOrderedByID(usuarioSession);
		}
		System.out.println("Size : "+listaOrdenCompra.size());
	}

	// SELECT PRESENTACION CLICK
	public void onRowSelectDetalleClick(SelectEvent event) {
		try {
			DetalleOrdenCompra detalle = (DetalleOrdenCompra) event.getObject();
			System.out.println("onRowSelectDetalleClick  " + detalle.getId());
			
			//setSelectedDetallePedido(detalle);
			setSelectedDetalleOC(detalle);

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
		
	}

	public void registrarOrdenCompra() {
		try {
			System.out.println("[registrarOrdenCompra] Ingreso a registrarOrdenCompra: "
					+ newOrdenCompra.getId());
			newOrdenCompra.setUsuario(usuarioSession);
			if(listaDetalleOC.size()>0){
				ordenCompraRegistration.register(newOrdenCompra);	
				for (DetalleOrdenCompra value : listaDetalleOC) {
					value.setOrdenCompra(newOrdenCompra);
					detalleOrdenCompraRegistration.register(value);
				}
				
				FacesMessage m = new FacesMessage(FacesMessage.SEVERITY_INFO,
						"Registrado!", "Pedido Registrado Correctamente");
				facesContext.addMessage(null, m);
				pushEventSucursal.fire(String.format(
						"Nueva PedidoMov Registrada: %s (id: %d)",
						newOrdenCompra.getObservacion(), newOrdenCompra.getId()));
				initNewOrdenCompra();
			}else{
				FacesMessage m = new FacesMessage(FacesMessage.SEVERITY_INFO,
						"Advertencia!", "El detalle de la orden no puede estar vacia!");
				facesContext.addMessage(null, m);
			}
		} catch (Exception e) {
			System.out.println("[registrarOrdenCompra] Error:"+e.getMessage());
			String errorMessage = getRootErrorMessage(e);
			FacesMessage m = new FacesMessage(FacesMessage.SEVERITY_ERROR,
					errorMessage, "El Pedido no fue Registrado");
			facesContext.addMessage(null, m);
		}
	}

	public void modificarPedidoEntrega() {
		try {
//			System.out.println("Ingreso a modificarPedidoEntrega: "
//					+ newOrdenCompra.getId());
//			for (DetalleOrdenCompra value : listaDetalleOC) {

//				double precio = value.getCantidadEntregado()
//						* value.getPrecioVenta();
//				double cant = value.getCantidadEntregado()+value.getBonificacion();
				//value.setEstadoEntrega("COMPLETADO");
//
//				detallePedidoRegistration.updated(value);
//				ingresoAlmacen(value, cant, precio, newPedidoMov);
//
//			}
//
//			newPedidoMov.setEntrega("COM");
//			newPedidoMov.setEstado("PR");
//			pedidoRegistration.updated(newPedidoMov);
//
//			System.out.println("detalle Modificado.. ");
//			initNewPedidoMov();
//			initTabProcesar();
//			FacesMessage m = new FacesMessage(FacesMessage.SEVERITY_INFO,
//					"Modificado!", "Pedido de entrega Modificado Correctamente.");
//			facesContext.addMessage(null, m);
//			pushEventSucursal.fire(String.format(
//					"PedidoMov Modificada: %s (id: %d)",
//					newPedidoMov.getObservacion(), newPedidoMov.getId()));

		} catch (Exception e) {
			String errorMessage = getRootErrorMessage(e);
			FacesMessage m = new FacesMessage(FacesMessage.SEVERITY_ERROR,
					errorMessage,
					"No se pudo modificar el Pedido de Entrega.");
			facesContext.addMessage(null, m);
		}
	}

	private void ingresoAlmacen(DetalleOrdenCompra detail, double cant,
			double monto, DetalleOrdenCompra pedido) {
//		try {
//
//			System.out.println("Ingreso a ingresoAlmacen..");
//			AlmProducto almProd;
//			CardexProducto cardex = new CardexProducto();
//			if (almacenProductoRepository.existProducto(detail.getProducto(),
//					newPedidoMov.getAlmIn(), detail.getLineasProveedor(),
//					detail.getProveedor())) {
//				System.out.println("existe:" + cant + " , " + monto);
//				almProd = almacenProductoRepository.findByProducto(
//						detail.getProducto(), newPedidoMov.getAlmIn(),
//						detail.getLineasProveedor(), detail.getProveedor());
//				almProd.setAlmacen(newPedidoMov.getAlmIn());
//				almProd.setEstado("AC");
//				almProd.setProveedor(detail.getProveedor());
//				almProd.setLineaProvedor(detail.getLineasProveedor());
//				almProd.setProducto(detail.getProducto());
//				almProd.setStock(almProd.getStock() + cant);
//				almacenProductoRegistration.updated(almProd);
//				System.out.println("Actualizo AlmacenProducto Ingreso");
//			} else {
//
//				almProd = new AlmProducto();
//				System.out.println("no sexiste:" + cant + " , " + monto);
//				almProd.setAlmacen(newPedidoMov.getAlmIn());
//				almProd.setEstado("AC");
//				almProd.setStock(cant);
//				almProd.setUsuarioRegistro(usuarioSession.getName());
//				almProd.setFechaRegistro(new Date());
//				almProd.setProducto(detail.getProducto());
//				almProd.setProveedor(detail.getProveedor());
//				almProd.setLineaProvedor(detail.getLineasProveedor());
//				almacenProductoRegistration.register(almProd);
//				System.out.println("Rgistro AlmacenProducto Ingreso");
//			}
//			cardex.setTransaccion("COMPRA DE PRODUCTO : "
//					+ newPedidoMov.getObservacion() + " , OC-"
//					+ newPedidoMov.getCorrelativo());
//			cardex.setAlmacen(pedido.getAlmIn());
//			cardex.setEstado("AC");
//			cardex.setPrecioCompra(productoProveedorRepository
//					.findProductoProveedor(detail.getProducto(),
//							detail.getProveedor(), detail.getLineasProveedor())
//					.getPrecioUnitarioCompra());
//			cardex.setFechaRegistro(new Date());
//			cardex.setUsuarioRegistro(usuarioSession.getName());
//			cardex.setProducto(detail.getProducto());
//			cardex.setPrecioVenta(detail.getPrecioVenta());
//			cardex.setCantidad(cant);
//			cardex.setStockAnterior(almProd.getStock() - cant);
//			cardex.setStockActual(almProd.getStock());
//			cardex.setNumeroTransaccion(pedido.getId());
//			cardex.setProveedor(detail.getProveedor());
//			cardex.setLineaProveedor(detail.getLineasProveedor());
//			cardex.setTipoMovimiento("INGRESO");
//			cardex.setMovimiento("OC");//ORDEN DE COMPRA
//			// CARDEX
//			cardexProductoRegistration.register(cardex);
//			System.out.println("Ingreso Registrado.. cardex");
//		} catch (Exception e) {
//			System.err.println("Error en ingresoAlmacen :" + e.getMessage());
//		}
	}

	public void modificarOrdenCompra() {
		try {
			System.out.println("Ingreso a modificarPedidoMov: "
					+ newOrdenCompra.getId());
//			if (newOrdenCompra.getTipoPedido().equals("INT")) {
//				newOrdenCompra.setAlmOut(em.find(Almacen.class, this.newOrdenCompra
//						.getAlmOut().getId()));
//				newOrdenCompra.setUsuarioOut(newOrdenCompra.getAlmOut()
//						.getUsuario());
//			} else {
//				newPedidoMov.setAlmOut(null);
//				newPedidoMov.setUsuarioOut(null);
//			}
			ordenCompraRegistration.updated(newOrdenCompra);
			FacesMessage m = new FacesMessage(FacesMessage.SEVERITY_INFO,
					"Modificado!", "El pedido se Modifico Correctamente");
			facesContext.addMessage(null, m);
			pushEventSucursal.fire(String.format(
					"PedidoMov Modificada: %s (id: %d)",
					newOrdenCompra.getObservacion(), newOrdenCompra.getId()));
			detalleOrdenCompraRegistration.removerForPedido(newOrdenCompra);			
			for (DetalleOrdenCompra value : listaDetalleOC) {
				value.setId(null);
				value.setOrdenCompra(newOrdenCompra);
				detalleOrdenCompraRegistration.register(value);

			}
			initNewOrdenCompra();

		} catch (Exception e) {
			String errorMessage = getRootErrorMessage(e);
			FacesMessage m = new FacesMessage(FacesMessage.SEVERITY_ERROR,
					errorMessage, "El pedido no pudo ser Modificado.");
			facesContext.addMessage(null, m);
		}
	}

	public void eliminarOrdenCompra() {
		try {
			System.out.println("Ingreso a eliminarPedidoMov: "
					+ newOrdenCompra.getId());
			for (DetalleOrdenCompra value : listaDetalleOC) {
				value.setEstadoEntrega("ANULADO");
				value.setEstado("RM");			
				detalleOrdenCompraRegistration.updated(value);
			}
			newOrdenCompra.setEstado("RM");
			ordenCompraRegistration.updated(newOrdenCompra);
			FacesMessage m = new FacesMessage(FacesMessage.SEVERITY_INFO,
					"Borrado!", "Borrado successful");
			facesContext.addMessage(null, m);
			pushEventSucursal.fire(String.format(
					"PedidoMov Borrada: %s (id: %d)",
					newOrdenCompra.getObservacion(), newOrdenCompra.getId()));
			initNewOrdenCompra();

		} catch (Exception e) {
			String errorMessage = getRootErrorMessage(e);
			FacesMessage m = new FacesMessage(FacesMessage.SEVERITY_ERROR,
					errorMessage, "Borrado Incorrecto.");
			facesContext.addMessage(null, m);
		}
	}

	public void eliminarDetalleOrdenCompra() {
		try {
			System.out.println("Ingreso eliminarDetalleOrdenCompra");
			if (selectedDetalleOC != null) {
				for (int i = 0; i < listaDetalleOC.size(); i++) {
					if (selectedDetalleOC.getCorrelativo().equals(
					    listaDetalleOC.get(i).getCorrelativo())) {
						listaDetalleOC.remove(i);
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
			String urlPDFreporte = urlPath+"ReporteOrdenIngreso?pUsuario="+pUsuario+"&pSucursal="+pSucursal+"&pId="+newOrdenCompra.getId(); //&pRutaImagen="+pRutaImagen
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

				String urlPDFreporte = urlPath+"ReporteOrdenIngreso?pUsuario="+pUsuario+"&pSucursal="+pSucursal+"&pId="+newOrdenCompra.getId(); //&pRutaImagen="+pRutaImagen
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

	

	public List<ProductoProveedor> getListProducto() {
		return listProducto;
	}

	public void setListProducto(List<ProductoProveedor> listProducto) {
		this.listProducto = listProducto;
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

	public DetalleOrdenCompra getSelectedDetalleOC() {
		return selectedDetalleOC;
	}

	public void setSelectedDetalleOC(DetalleOrdenCompra selectedDetalleOC) {
		this.selectedDetalleOC = selectedDetalleOC;
	}

	public OrdenCompra getSelectedOrdenCompra() {
		return selectedOrdenCompra;
	}

	public void setSelectedOrdenCompra(OrdenCompra selectedOrdenCompra) {
		this.selectedOrdenCompra = selectedOrdenCompra;
	}

	public OrdenCompra getNewOrdenCompra() {
		return newOrdenCompra;
	}

	public void setNewOrdenCompra(OrdenCompra newOrdenCompra) {
		this.newOrdenCompra = newOrdenCompra;
	}
	
	public List<OrdenCompra> getListaOrdenCompra() {
		return listaOrdenCompra;
	}

	public void setListaOrdenCompra(List<OrdenCompra> listaOrdenCompra) {
		this.listaOrdenCompra = listaOrdenCompra;
	}

	public List<DetalleOrdenCompra> getListaDetalleOC() {
		return listaDetalleOC;
	}

	public void setListaDetalleOC(List<DetalleOrdenCompra> listaDetalleOC) {
		this.listaDetalleOC = listaDetalleOC;
	}

	public OrdenCompra getSelectedOC() {
		return selectedOC;
	}

	public void setSelectedOC(OrdenCompra selectedOC) {
		this.selectedOC = selectedOC;
	}

	public List<OrdenCompra> getListaEntregaPedido() {
		return listaEntregaPedido;
	}

	public void setListaEntregaPedido(List<OrdenCompra> listaEntregaPedido) {
		this.listaEntregaPedido = listaEntregaPedido;
	}

	public MovimientoAlmacenRepository getMovimientoAlmacenRepository() {
		return movimientoAlmacenRepository;
	}

	public void setMovimientoAlmacenRepository(
			MovimientoAlmacenRepository movimientoAlmacenRepository) {
		this.movimientoAlmacenRepository = movimientoAlmacenRepository;
	}

	public List<OrdenCompra> getListaOrdenCompraProcesadas() {
		return listaOrdenCompraProcesadas;
	}

	public void setListaOrdenCompraProcesadas(
			List<OrdenCompra> listaOrdenCompraProcesadas) {
		this.listaOrdenCompraProcesadas = listaOrdenCompraProcesadas;
	}

	public List<OrdenCompra> getListaOrdenCompraAnuladas() {
		return listaOrdenCompraAnuladas;
	}

	public void setListaOrdenCompraAnuladas(
			List<OrdenCompra> listaOrdenCompraAnuladas) {
		this.listaOrdenCompraAnuladas = listaOrdenCompraAnuladas;
	}

	public List<OrdenCompra> getListaOrdenCompraTodos() {
		return listaOrdenCompraTodos;
	}

	public void setListaOrdenCompraTodos(List<OrdenCompra> listaOrdenCompraTodos) {
		this.listaOrdenCompraTodos = listaOrdenCompraTodos;
	}

	public List<DetalleOrdenCompra> getListAplicarPrecio() {
		return listAplicarPrecio;
	}

	public void setListAplicarPrecio(List<DetalleOrdenCompra> listAplicarPrecio) {
		this.listAplicarPrecio = listAplicarPrecio;
	}
	
	
	

}
