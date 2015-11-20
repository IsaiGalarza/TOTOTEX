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
import javax.faces.component.UISelectItem;
import javax.faces.context.FacesContext;
import javax.faces.event.AbortProcessingException;
import javax.faces.event.ValueChangeEvent;
import javax.faces.event.ValueChangeListener;
import javax.inject.Inject;
import javax.inject.Named;
import javax.persistence.EntityManager;
import javax.servlet.http.HttpServletRequest;

import org.primefaces.component.outputlabel.OutputLabel;
import org.primefaces.component.panelgrid.PanelGrid;
import org.primefaces.component.selectonemenu.SelectOneMenu;
import org.primefaces.context.RequestContext;
import org.primefaces.event.NodeSelectEvent;
import org.primefaces.event.NodeUnselectEvent;
import org.primefaces.event.SelectEvent;
import org.primefaces.model.DefaultTreeNode;
import org.primefaces.model.TreeNode;
import org.richfaces.cdi.push.Push;

import bo.buffalo.data.AtributoProductoRepository;
import bo.buffalo.data.AtributoRepository;
import bo.buffalo.data.AtributoTipoProductoRepository;
import bo.buffalo.data.ConversionesRepository;
import bo.buffalo.data.DetalleProductoRepository;
import bo.buffalo.data.FabricanteRepository;
import bo.buffalo.data.LineaProveedorRepository;
import bo.buffalo.data.MarcaRepository;
import bo.buffalo.data.ProductoProveedorRepository;
import bo.buffalo.data.ProductoRepository;
import bo.buffalo.data.ProductopRepository;
import bo.buffalo.data.ProveedorRepository;
import bo.buffalo.data.TipoProductoRepository;
import bo.buffalo.data.UnidadMedidaRepository;
import bo.buffalo.data.UsuarioRepository;
import bo.buffalo.model.Atributo;
import bo.buffalo.model.AtributoProducto;
import bo.buffalo.model.AtributoTipoProducto;
import bo.buffalo.model.CantidadUnidadPresentacion;
import bo.buffalo.model.DetalleProducto;
import bo.buffalo.model.Fabricante;
import bo.buffalo.model.HistorialCostos;
import bo.buffalo.model.LineasProveedor;
import bo.buffalo.model.Marca;
import bo.buffalo.model.Presentacion;
import bo.buffalo.model.Producto;
import bo.buffalo.model.ProductoProveedor;
import bo.buffalo.model.Proveedor;
import bo.buffalo.model.TipoProducto;
import bo.buffalo.model.UnidadMedida;
import bo.buffalo.model.Usuario;
import bo.buffalo.service.CantUnitPresRegistration;
import bo.buffalo.service.HistorialCostosRegistration;
import bo.buffalo.service.LineaProveedorRegistration;
import bo.buffalo.service.ProductoProveedorRegistration;
import bo.buffalo.service.ProductopRegistration;
import bo.buffalo.service.ProveedorRegistration;
import bo.buffalo.service.TipoProductoRegistration;
import bo.buffalo.service.UnidadMedidaRegistration;
import bo.buffalo.structure.EstructuraMateriaPrima;
import bo.buffalo.util.UtilCostosCalculation;

import com.ahosoft.utils.FacesUtil;

@Named(value = "productopController")
@ConversationScoped
public class ProductopController implements Serializable {

	private static final long serialVersionUID = -2745329186387328746L;

	public static final String PUSH_CDI_TOPIC = "pushCdi";

	@Inject
	private FacesContext facesContext;

	@Inject
	private EntityManager em;

	@Inject
	Conversation conversation;

	private double cps = 0;


	@Inject
	private ProductopRegistration productoRegistration;

	@Inject
	private ProductopRepository productopRepository;

	@Inject
	private TipoProductoRepository tipoProductoRepository;

	@Inject
	private LineaProveedorRepository lineasProveedorRepository;

	
	@Inject
	private MarcaRepository marcaRepository;
	
	@Inject
	private FabricanteRepository fabricanteRepository;
	@Inject

	private ProveedorRepository proveedorRepository;
	@Inject
	private DetalleProductoRepository detalleProductoRepository;

	private List<TipoProducto> listaTipoProducto = new ArrayList<TipoProducto>();

	private List<Proveedor> listaProveedor = new ArrayList<Proveedor>();
	private List<LineasProveedor> listalineasProveedor = new ArrayList<LineasProveedor>();

	private List<Presentacion> listaPresentacion = new ArrayList<Presentacion>();
	private List<UnidadMedida> listaUnidadMedida = new ArrayList<UnidadMedida>();
	@Inject
	private UnidadMedidaRepository unidadMedidaRepository;


	private int id_UnidadMedida;
	private int id_FormaFarmaceutica;

	@Inject
	UsuarioRepository usuarioRepository;
	private Usuario usuarioSession;

	@Inject
	@Push(topic = PUSH_CDI_TOPIC)
	Event<String> pushEventProducto;

	private boolean modificar = false;
	private boolean nuevo = false;
	private String tituloPanel = "Registrar Producto";
	private Producto selectedProducto;
	private Producto newProducto;
	
	private int proveedorID;
	private int lineaProveedorID;

	private double precio;
	
	private PanelGrid panelGrid;

	private List<Producto> productosp;


	// @Named provides access the return value via the EL variable name
	// "servicios" in the UI (e.g.
	// Facelets or JSP view)
	/**
	 * Producto Proveedor Fields
	 */

	@Inject
	private TipoProductoRegistration tipoProductoRegistration;


	@Inject
	private ProductoProveedorRegistration productoProveedorRegistration;
	@Inject
	private ProductoProveedorRepository productoProveedorRepository;
	private @Inject AtributoTipoProductoRepository atributoTipoProductoRepository;
	private @Inject AtributoRepository atributoRepository;
	private @Inject AtributoProductoRepository atributoProductoRepository;
	
	private List<ProductoProveedor> listProductoProveedor = new ArrayList<ProductoProveedor>();
	private ProductoProveedor selectedProductoProveedor;
	private ProductoProveedor productoProveedor;

	private TipoProducto tipoProducto;
	private Presentacion presentacion;
	private UnidadMedida unidadMedida;
	private CantidadUnidadPresentacion cantidadUnidadPresentacion;
	private Integer presentacionid;
	private Integer unidadMedidaid;
	private List<Marca> listaMarca= new ArrayList<Marca>();
	 
	private List<Fabricante> listaFabricante= new ArrayList<Fabricante>();

	@Inject
	private CantUnitPresRegistration cantidadUnidadPresentacionRegistration;
	private @Inject ProductoRepository productoRepository;
	private List<TipoProducto> selectedTipoProducto=new ArrayList<>();
	
	private TreeNode root;
	private TreeNode[] selectedNodes;

	// Componentes para Tipo Producto PreElaborados Internos
	private List<EstructuraMateriaPrima> listaEstructuraMateriaP = new ArrayList<EstructuraMateriaPrima>();
	private EstructuraMateriaPrima selectedEstructuraMateriaP;
	private DetalleProducto selectedDetalleProducto;
	private String criterioBusquedaProducto;
	private List<Producto> listaProductos = new ArrayList<Producto>();
	private List<DetalleProducto> listaComponentesProductoInterno;
	private List<AtributoProducto> listAtributoProducto;
	private List<AtributoProducto> listAtributoProductoDelete;
	
	private String tipoCambio;
	
	@Produces
	@Named
	public List<Producto> getProductosp() {
		return productosp;
	}


	
	

	@Produces
	@Named
	public List<DetalleProducto> getListaComponentesProductoInterno() {
		return listaComponentesProductoInterno;
	}


	@PostConstruct
	public void initNewProducto() {

		// initConversation();
		beginConversation();

		HttpServletRequest request = (HttpServletRequest) facesContext
				.getExternalContext().getRequest();
		System.out.println("init Producto*********************************");
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

		listaUnidadMedida = unidadMedidaRepository.findAllOrderedByID();
		listaProveedor = proveedorRepository.traerProveedoresActivas();
		listaTipoProducto = tipoProductoRepository.traerTipoProducto();
		listaMarca= marcaRepository.traerMarcaActivas();
		listaFabricante= fabricanteRepository.traerFabricanteActivas();

		// traer todos los servicios ordenados por nombre
		productosp = productopRepository.findAllOrderedByDateRegister();


		nuevo = false;
		modificar = false;
		tituloPanel = "Registrar Producto";
		productoProveedor = new ProductoProveedor();
		selectedTipoProducto=tipoProductoRepository.findAllOrderedByID();
		tipoProducto=new TipoProducto();
		this.listAtributoProducto=new ArrayList<>();
		this.listAtributoProductoDelete=new ArrayList<>();
		
		tipoProducto=tipoProductoRepository.findByParameterObject("descripcion","MATERIA PRIMA");
		if(tipoProducto!=null){
			System.out.println("Tipo producto encontrado: "+tipoProducto);
		}else{
			tipoProducto.setEstado("AC");
			tipoProducto.setDescripcion("MATERIA PRIMA");
			tipoProducto.setSigla("MP");
			tipoProducto.setFechaRegistro(new Date());
			tipoProducto.setUsuarioRegistro(usuarioSession.getLogin());
			try {
				tipoProductoRegistration.register(tipoProducto, new ArrayList<AtributoTipoProducto>());
			} catch (Exception e) {
				e.printStackTrace();
			}
			tipoProducto=tipoProductoRepository.findByParameterObject("descripcion","MATERIA PRIMA");
			System.out.println("Tipo producto No encontrado");
		}
		
		//cargarArbolSeleccionado();
	}

	@Inject
	private UnidadMedidaRegistration unidadMedidaRegistration;



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



	public int obtenerCorrelativoChar(String nombre) {

		System.out.println("Ingreso a obtenerCorrelativoChar");
		for (int i = 0; i < nombre.length(); i++) {
			char c = nombre.charAt(i);
			if (Character.isLowerCase(c))
				return (c - 96);
			else
				return (c - 64);
		}
		return 0;

	}




	// SELECT SERVICIO CLICK
	public void onRowSelectProductoClick(SelectEvent event) {
		try {
			newProducto = (Producto) event.getObject();
			System.out.println("onRowSelectProductoClick  "
					+ newProducto.getId());
			selectedProducto = newProducto;
	
			// actualizar campos
			precioVenta = newProducto.getPrecio();


			tituloPanel = "Modificar Producto";
			modificar = true;
			nuevo = false;
			System.out.println("modificar: " + modificar + ", nuevo: " + nuevo);
			listProductoProveedor.clear();
			listProductoProveedor = productoProveedorRepository
					.findAllOrderedByIDForProducto(newProducto);

			productoRegistration.updated(newProducto,new ArrayList<AtributoProducto>());
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
			System.out.println("Error in onRowSelectProductoClick: "
					+ e.getMessage());
		}
	}

	public void verModificarProforma() {
		try {
			System.out.println("Ingreso a verModificarProforma...");
			modificar = true;
			nuevo = true;
			tipoProducto=this.newProducto.getTipoProducto();
			cargarArbolSeleccionado();
			this.listAtributoProductoDelete=new ArrayList<>();
			this.listAtributoProducto=atributoProductoRepository.findAllByParameterObject("producto", newProducto);
			List<TreeNode> seleccionados=new ArrayList<>();
			obtenerSeleccionado(root, listAtributoProducto, seleccionados);
			System.out.println("Cantidad Seleccionados: "+seleccionados.size());
			selectedNodes=new TreeNode[seleccionados.size()];
			for (int i = 0; i < seleccionados.size(); i++) {
				selectedNodes[i]=seleccionados.get(i);
			}
			cargarArbolSeleccionado();
		} catch (Exception e) {
			// TODO: handle exception
			System.out.println("Error en verModificarProforma: "
					+ e.getMessage());
		}
	}
	
	private void obtenerSeleccionado(TreeNode n, List<AtributoProducto> listAtriProdu,List<TreeNode> nodos) {
		if(n.getChildren().size() == 0) {
			if(esElNodo(n, listAtriProdu)){
				nodos.add(n);
			}
		}else{
			for(TreeNode s: n.getChildren()) {
				obtenerSeleccionado(s, listAtriProdu,nodos);
			}
			if(esElNodo(n, listAtriProdu)){
				nodos.add(n);
			}
		}
	}
	
	private boolean esElNodo(TreeNode nodo, List<AtributoProducto> listAtriProdu){
		for (AtributoProducto atributoProducto : listAtriProdu) {
			if(atributoProducto.getAtributo().equals(nodo.getData())){
				return true;
			}
		}
		return false;
	}
	// SELECT SERVICIO CLICK
	public void onRowSelectProductoDBLClick(SelectEvent event) {
		try {
			Producto producto = (Producto) event.getObject();
			System.out.println("onRowSelectProductoClick  " + producto.getId());
			selectedProducto = producto;
			newProducto = em.find(Producto.class, producto.getId());
			newProducto.setFechaRegistro(new Date());
			newProducto.setUsuarioRegistro(usuarioSession.getLogin());

			// actualizar campos
			precioVenta = newProducto.getPrecio();
			

			// id_

			tituloPanel = "Modificar Producto";
			modificar = true;
			nuevo = true;
			listProductoProveedor.clear();
			listProductoProveedor = productoProveedorRepository
					.findAllOrderedByIDForProducto(newProducto);

			listaComponentesProductoInterno.clear();
			listaComponentesProductoInterno = detalleProductoRepository
					.findAllForProductoOrderedByID(newProducto.getId());
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
			System.out.println("Error in onRowSelectProductoClick: "
					+ e.getMessage());
		}
	}
	
	
	public void updateLineaProveedor() {
		try {
			System.out.println("Ingreso updateLineaProveedor...");
			Proveedor proveedor = em.find(Proveedor.class, this.getProveedorID());
			
			if (!existeProveedor(proveedor)) {
				this.setTipoCambio(proveedor.getTipoCambio());
				listalineasProveedor.clear();
					
				listalineasProveedor = lineasProveedorRepository
						.traerLineasProveedor(this.getProveedorID());
//				productoProveedor.setProveedor(em.find(Proveedor.class,
//						this.productoProveedor.getProveedor().getId()));
				productoProveedor.setTipoCambio(em.find(Proveedor.class, this.getProveedorID()).getTipoCambio());

			} else {
				System.out.println("El Proveedor ya esta Asignado");
				FacesMessage m = new FacesMessage(FacesMessage.SEVERITY_WARN,
						"Advertencia!",
						"El Proveedor ya esta Asignado | Nombre:"
								+ productoProveedor.getProveedor().getNombre()
								+ " Por, Favor Seleccione otro proveedor!");
				facesContext.addMessage(null, m);
				productoProveedor.getProveedor().setId(null);
			}

		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
			System.out.println("Error en updateLineaProveedor: "
					+ e.getMessage());
		}
	}

	private boolean existeProveedor(Proveedor pro) {
		System.out.println("Ingreso en existeProveedor ");
		for (ProductoProveedor value : listProductoProveedor) {
			if (value.getProveedor().getId() == pro.getId()) {
				return true;
			}
		}
		return false;
	}

	public void registrarProducto() {
		try {
			if(validarAtributos()){
				System.out.println("Ingreso a registrarProducto: "+ newProducto.getId());
	
				productoRegistration.register(newProducto,this.listAtributoProducto);
				
				FacesMessage m = new FacesMessage(FacesMessage.SEVERITY_INFO,
						"Producto Registrado!", newProducto.getNombreProducto());
				facesContext.addMessage(null, m);
	
				System.out.println("Registrado Detalle");
				for (ProductoProveedor value : listProductoProveedor) {
					value.setProducto(newProducto);
					value.setUsuarioRegistro(usuarioSession.getName());
					productoProveedorRegistration.register(value);
				}
				System.out.println("Registrado Proveedores");
				listProductoProveedor.clear();
	
				initNewProducto();
			}
		} catch (Exception e) {
			e.printStackTrace();
			String errorMessage = getRootErrorMessage(e);
			FacesMessage m = new FacesMessage(FacesMessage.SEVERITY_ERROR,
					errorMessage, "Registro Incorrecto.");
			facesContext.addMessage(null, m);
		}
	}
	
	public boolean validarAtributos(){
		if(this.newProducto.getTipoProducto()!=null){
			List<AtributoTipoProducto> atp=atributoTipoProductoRepository.findAllByParameterObject("tipoProducto", newProducto.getTipoProducto());
			if(!atp.isEmpty()){
				boolean r=false;
				for (AtributoTipoProducto atributoTipoProducto : atp) {
					if(atributoTipoProducto.isRequerido()){
				        List<TreeNode> nodos=this.root.getChildren();
				        for (TreeNode treeNode : nodos) {
							if(treeNode.getData().equals(atributoTipoProducto.getAtributo())){
								System.out.println("entrada booleana: "+r);
								r=buscarNodo(treeNode);
								System.out.println("salida booleanda: "+r);
								if(!r){
									FacesUtil.warmMessage("Seleccione un "+((Atributo)treeNode.getData()).getNombre());
									return false;
								}
							}
						}
					}
				}
			}else{
				return true;
			}
		}else{
			return false;
		}
		return true;
	}
	
	private boolean buscarNodo(TreeNode n) {
		if(n.getChildren().size() == 0) {
			if(existeNodoSeleccionado(n, this.selectedNodes)){
				System.out.println("Existe un seleccionado");
				return true;
			}
		}else{
			for(TreeNode s: n.getChildren()) {
				if(buscarNodo(s)){
					return true;
				}
			}
			if(existeNodoSeleccionado(n, this.selectedNodes)){
				System.out.println("Existe un seleccionado");
				return true;
			}
		}
		return false;
	}

	public List<String> completeText(String query) {

		List<String> results = new ArrayList<String>();
		for (int i = 0; i < 10; i++) {
			results.add(query + i);
		}

		return results;
	}

	public List<String> completeDescripcionProducto(String query) {
		try {
			System.out.println("Ingreso a completeDescripcionProducto..."
					+ query);
			List<Producto> listaProducto = productoRepository
					.buscarProductoNombre(query);
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

	public void onItemSelect(SelectEvent event) {
		FacesMessage m = new FacesMessage(FacesMessage.SEVERITY_ERROR,
				"Producto ya existe!", event.getObject().toString());
		facesContext.addMessage(null, m);
	}



	public void crearTipoProducto() {
		try {
			System.out.println("Ingreso a crearProducto: ");
			tipoProducto = new TipoProducto();
			tipoProducto.setEstado("AC");
			tipoProducto.setFechaRegistro(new Date());
			tipoProducto.setUsuarioRegistro(usuarioSession.getLogin());

		} catch (Exception e) {
			String errorMessage = getRootErrorMessage(e);
			FacesMessage m = new FacesMessage(FacesMessage.SEVERITY_ERROR,
					errorMessage, "Registro Incorrecto.");
			facesContext.addMessage(null, m);
		}
	}




	public void registrarUnidadMedida() {
		try {
			System.out.println("Ingreso a registrar unidad Medida...");

			unidadMedidaRegistration.register(unidadMedida);
			System.out.println("===== UNIDAD MEDIDDA REGISTRADA ======= OK");
			id_UnidadMedida = unidadMedida.getId();

			FacesMessage m = new FacesMessage(FacesMessage.SEVERITY_INFO,
					"Unidad Registrada!", unidadMedida.getDescripcion());
			facesContext.addMessage(null, m);

		} catch (Exception e) {
			// TODO: handle exception
			System.out.println("Error en registrarUnidadMedida: "
					+ e.getMessage());
			e.printStackTrace();
		}
	}

	public void registrarPresentacion() {
		try {
			System.out.println("Ingreso a registrarPresentacion: "
					+ cantidadUnidadPresentacion.getPresentacion().getId()
					+ " , "
					+ cantidadUnidadPresentacion.getUnidadMedida().getId());
			Presentacion p = em.find(Presentacion.class,
					this.cantidadUnidadPresentacion.getPresentacion().getId());
			System.out.println("Presentacion:" + p.getDescripcion());
			UnidadMedida u = em.find(UnidadMedida.class,
					this.cantidadUnidadPresentacion.getUnidadMedida().getId());
			System.out.println("Unidad Medida: " + u.getDescripcion());
			cantidadUnidadPresentacion.setPresentacion(p);
			cantidadUnidadPresentacion.setUnidadMedida(u);
			cantidadUnidadPresentacionRegistration
					.register(cantidadUnidadPresentacion);
			listaUnidadMedida = unidadMedidaRepository.findAllOrderedByID();

			unidadMedidaRegistration.register(unidadMedida);
			listaUnidadMedida = unidadMedidaRepository
					.traerUnidadMedidaActivas();
			FacesMessage m = new FacesMessage(FacesMessage.SEVERITY_INFO,
					"Unidad de Medida Registrada..!",
					"Unidad de Medida Registrada successful");
			facesContext.addMessage(null, m);
			pushEventProducto.fire(String.format(
					"Nuevo Presentacion Registrado: %s (id: %d)",
					tipoProducto.getDescripcion(), tipoProducto.getId()));

		} catch (Exception e) {
			String errorMessage = getRootErrorMessage(e);
			FacesMessage m = new FacesMessage(FacesMessage.SEVERITY_ERROR,
					errorMessage, "Registro Incorrecto.");
			facesContext.addMessage(null, m);
		}
	}

	public void crearPresentacion() {
		try {
			System.out.println("Ingreso a crearPresentacion: ");

			unidadMedida = new UnidadMedida();
			unidadMedida.setEstado("AC");
			unidadMedida.setFechaRegistro(new Date());
			unidadMedida.setUsuarioRegistro(usuarioSession.getLogin());

		} catch (Exception e) {
			String errorMessage = getRootErrorMessage(e);
			FacesMessage m = new FacesMessage(FacesMessage.SEVERITY_ERROR,
					errorMessage, "Registro Incorrecto.");
			facesContext.addMessage(null, m);
		}
	}

	public void crearUnidadMedida() {
		try {
			System.out.println("Ingreso a crearUnidadMedida: ");

			unidadMedida = new UnidadMedida();
			unidadMedida.setEstado("AC");
			unidadMedida.setFechaRegistro(new Date());
			unidadMedida.setUsuarioRegistro(usuarioSession.getLogin());

		} catch (Exception e) {
			String errorMessage = getRootErrorMessage(e);
			FacesMessage m = new FacesMessage(FacesMessage.SEVERITY_ERROR,
					errorMessage, "Registro Incorrecto.");
			facesContext.addMessage(null, m);
		}
	}






	public void crearProducto() {
		try {
			System.out.println("Ingreso a crearProducto: ");
			listProductoProveedor.clear();
			newProducto = new Producto();
			newProducto.setEstado("AC");
			newProducto.setFechaRegistro(new Date());
			newProducto.setUsuarioRegistro(usuarioSession.getLogin());
			newProducto.setTipoProducto(tipoProducto);
			limpiarCamposIDS();
			this.listAtributoProducto=new ArrayList<>();
			this.listAtributoProductoDelete=new ArrayList<>();
			selectedNodes=null;
			cargarArbolSeleccionado();
			// cambiarFormaFarmaceutica()
			nuevo = true;
			modificar = false;
			totalCantidad = 0;
			totalIncidencia = 0;

		} catch (Exception e) {
			System.out.println("Error cargar nuevo producto: "+e.getMessage());
			String errorMessage = getRootErrorMessage(e);
			FacesMessage m = new FacesMessage(FacesMessage.SEVERITY_ERROR,
					errorMessage, "Registro Incorrecto.");
			facesContext.addMessage(null, m);
		}
	}

	public void limpiarCamposIDS() {
		id_UnidadMedida = 0;
		id_FormaFarmaceutica = 0;
	}


	@Inject
	private HistorialCostosRegistration historialCostosRegistration;

	private double precioVenta;

	public void modificarProducto() {
		try {
			System.out.println("Ingreso a modificarProducto: "
					+ newProducto.getId());
			
			newProducto.setFechaRegistro(new Date());
			newProducto.setUsuarioRegistro(usuarioSession.getName());
			this.listAtributoProducto.addAll(this.listAtributoProductoDelete);
			productoRegistration.updated(newProducto,this.listAtributoProducto);
			
			productoProveedorRegistration.removerProductoProveedor(newProducto);

			for (ProductoProveedor value : listProductoProveedor) {
				value.setProducto(newProducto);
				value.setId(null);
				value.setUsuarioRegistro(usuarioSession.getName());
				productoProveedorRegistration.register(value);

				if (precioVenta != selectedProducto.getPrecio()) {
					HistorialCostos historialCostos = new HistorialCostos();
					historialCostos
							.setObservacion("MODIFICADO POR LA CENTRAL DE COSTOS");
					historialCostos.setProducto(selectedProducto);
					historialCostos.setEstado("AC");
					historialCostos.setFechaRegistro(new Date());
					historialCostos
							.setUsuarioRegistro(usuarioSession.getName());
					historialCostos
							.setPrecioVenta(selectedProducto.getPrecio());
					historialCostos.setPrecioCompra(value
							.getPrecioUnitarioCompra());
					historialCostos.setProveedor(value.getProveedor());
					historialCostos.setMargenMax(value.getUtilidadMax());
					historialCostos.setMargenMin(value.getUtilidadMin());
					historialCostosRegistration.register(historialCostos);

					/* total */
					/*
					 * listHistorialCostos = historialCostosRepository
					 * .traerHistorialCostossActivos(selectedProducto);
					 */
				}
			}
			listProductoProveedor.clear();

			FacesMessage m = new FacesMessage(FacesMessage.SEVERITY_INFO,
					"Producto Modificado!", newProducto.getNombreProducto());
			facesContext.addMessage(null, m);

			initNewProducto();

		} catch (Exception e) {
			String errorMessage = getRootErrorMessage(e);
			FacesMessage m = new FacesMessage(FacesMessage.SEVERITY_ERROR,
					errorMessage, "Modificado Incorrecto.");
			facesContext.addMessage(null, m);
		}
	}

	public void eliminarProducto() {
		try {
			System.out.println("Ingreso a eliminarProducto: "
					+ newProducto.getId());
			// Integer id=newProducto.getId();
			productoRegistration.remover(newProducto);
			FacesMessage m = new FacesMessage(FacesMessage.SEVERITY_INFO,
					"Producto Borrado!", newProducto.getNombreProducto());
			facesContext.addMessage(null, m);
			// pushEventSucursal.fire(String.format("Producto Borrado: %s (id: %d)",
			// newProducto.getNombreProducto(), newProducto.getId()));
			initNewProducto();
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

	public boolean ExisteEnListaDetalleProducto(Producto p) {
		try {
			for (int i = 0; i < listaComponentesProductoInterno.size(); i++) {
				DetalleProducto rp = listaComponentesProductoInterno.get(i);
				if (rp.getProductoCompuesto().getId().equals(p.getId())) {
					return true;
				}
			}
			return false;

		} catch (Exception e) {
			// TODO: handle exception
			return false;
		}

	}

	private void cargarListaEstructuraMateriaP() {
		try {
			System.out
					.println("[cargarListaEstructuraMateriaP] cantidad ListaProductos:"
							+ listaProductos.size());
			listaEstructuraMateriaP.clear();
			for (int i = 0; i < listaProductos.size(); i++) {
				if (!ExisteEnListaDetalleProducto(listaProductos.get(i))) {
					EstructuraMateriaPrima mp = new EstructuraMateriaPrima();
					mp.setProducto(listaProductos.get(i));
					mp.setIncidencia(0.0);
					listaEstructuraMateriaP.add(mp);
				}
			}

		} catch (Exception e) {
			System.out.println("[cargarListaEstructuraMateriaP] Error:"
					+ e.getMessage());
			// TODO: handle exception
		}

	}

	public void cargarComponente() {
		try {
			System.out
					.println("[cargarComponente]Ingreso a cargarComponente...");
			listaProductos.clear();
			if (modificar)
				listaProductos = productoRepository
						.traerProdExternosInternosExcludeProdId(newProducto
								.getId());
			else
				listaProductos = productoRepository.traerProdExternosInternos();

			cargarListaEstructuraMateriaP();

		} catch (Exception e) {
			// TODO: handle exception
			listaProductos.clear();
			System.out.println("Error en cargarComponente: " + e.getMessage());
		} finally {
			criterioBusquedaProducto = null;
		}
	}

	public void crearProductoProveedor() {
		try {
			System.out
					.println("[crearProductoProveedor]Ingreso a crearProductoProveedor...");
			productoProveedor = new ProductoProveedor();
			productoProveedor.setEstado("AC");
			productoProveedor.setFechaRegistro(new Date());
			productoProveedor.setTipoCambio("BS");
			productoProveedor.setUsuarioRegistro(usuarioSession.getSucursal()
					.getNombre());
			System.out.println("productoProveedor : " + productoProveedor);

		} catch (Exception e) {
			System.out.println("Error en crearProductoProveedor: "
					+ e.getMessage());
		}
	}

	private Proveedor proveedor;
	@Inject
	private ProveedorRegistration proveedorRegistration;

	public void crearProveedor() {
		try {
			System.out.println("Ingreso a crearProveedor");
			proveedor = new Proveedor();
			proveedor.setEstado("AC");
			proveedor.setFechaRegistro(new Date());
			proveedor.setUsuarioRegistro(usuarioSession.getLogin());
		} catch (Exception e) {
			System.err.println("Error en crearProveedor : " + e.getMessage());
			e.getStackTrace();
		}
	}

	private LineasProveedor lineaProveedor;

	public void crearLineaProveedor() {
		try {
			System.out.println("Ingreso a crearLineaProveedor");
			lineaProveedor = new LineasProveedor();
			lineaProveedor.setEstado("AC");
			lineaProveedor.setFechaRegistro(new Date());
			lineaProveedor.setUsuarioRegistro(usuarioSession.getLogin());
		} catch (Exception e) {
			System.err.println("Error en crearLineaProveedor : "
					+ e.getMessage());
			e.getStackTrace();
		}
	}

	public void registrarProveedor() {
		try {
			System.out.println("Ingreso a registrarProveedor: ");
			proveedorRegistration.register(proveedor);
			listaProveedor = proveedorRepository.traerProveedoresActivas();
		} catch (Exception e) {
			String errorMessage = getRootErrorMessage(e);
			FacesMessage m = new FacesMessage(FacesMessage.SEVERITY_ERROR,
					errorMessage, "Registro Incorrecto.");
			facesContext.addMessage(null, m);
		}
	}

	@Inject
	private LineaProveedorRegistration lineasProveedorRegistration;

	public void registrarLineasProveedor() {
		try {
			System.out.println("Ingreso a registrarLineasProveedor: ");

			// seteamos el proveedor
			lineaProveedor.setProveedor(productoProveedor.getProveedor());

			lineasProveedorRegistration.register(lineaProveedor);
			listalineasProveedor = lineasProveedorRepository
					.traerLineasProveedor(this.getProveedorID());

		} catch (Exception e) {
			String errorMessage = getRootErrorMessage(e);
			FacesMessage m = new FacesMessage(FacesMessage.SEVERITY_ERROR,
					errorMessage, "Registro Incorrecto.");
			facesContext.addMessage(null, m);
		}
	}

	public void calcularMargenUtilidad() {
		try {
			System.out.println("Ingreso a calcularMargenUtilidad");
			double costo = 0;
			costo = proveedorRepository.obtenerCosto(productoProveedor
					.getProveedor());
		

			productoProveedor.setUtilidadMax(UtilCostosCalculation
					.calculateMargenUtilidad(productoProveedor, costo, precio,
							7));
		} catch (Exception e) {
			System.err.println("Error en calcularMargenUtilidad : "
					+ e.getMessage());
			e.getStackTrace();
		}
	}

	public void calcularMargenUtilidadEdit() {
		try {
			System.out.println("Ingreso a calcularMargenUtilidad");
			double costo = 0;
			costo = proveedorRepository.obtenerCosto(selectedProductoProveedor
					.getProveedor());
			/*
			 * double margen = ((precio * 100) / selectedProductoProveedor
			 * .getPrecioUnitarioCompra()) - 100 - costo;
			 * System.out.println("Margen de Utilidad : " + margen);
			 */
			productoProveedor.setUtilidadMax(UtilCostosCalculation
					.calculateMargenUtilidad(selectedProductoProveedor, costo,
							precio, 7));
		} catch (Exception e) {
			System.err.println("Error en calcularMargenUtilidad : "
					+ e.getMessage());
			e.getStackTrace();
		}
	}

	public void calcularPrecioVenta() {
		try {
			System.out.println("Ingreso a calcularPrecioVenta");
			double costo = proveedorRepository.obtenerCosto(productoProveedor
					.getProveedor());
			System.out.println("Costos : " + costo);
			/*
			 * double precioventa = ((100 + costo +
			 * productoProveedor.getUtilidadMax()) / 100)
			 * productoProveedor.getPrecioUnitarioCompra();
			 * System.out.println("Precio de Venta : " + precioventa);
			 */

			setPrecio(UtilCostosCalculation.calculatePrecioVenta(
					productoProveedor, costo, 7));
		} catch (Exception e) {
			System.err.println("Error en calcularPrecioVenta : "
					+ e.getMessage());
			e.getStackTrace();
		}
	}

	public void calcularPrecioVentaEdit() {
		try {
			System.out.println("Ingreso a calcularPrecioVenta");
			double costo = proveedorRepository
					.obtenerCosto(selectedProductoProveedor.getProveedor());
			/*
			 * double precioventa = ((100 + costo + selectedProductoProveedor
			 * .getUtilidadMax()) / 100)
			 * selectedProductoProveedor.getPrecioUnitarioCompra();
			 * System.out.println("Precio de Venta : " + precioventa);
			 */
			setPrecio(UtilCostosCalculation.calculatePrecioVenta(
					selectedProductoProveedor, costo, 7));
		} catch (Exception e) {
			System.err.println("Error en calcularPrecioVenta : "
					+ e.getMessage());
			e.getStackTrace();
		}
	}

	public void agregarProductoProveedor() {
		try {
			System.out.println("Ingreso agregarProductoProveedor...");
			productoProveedor.setProveedor(em.find(Proveedor.class, this.getProveedorID()));
			productoProveedor.setLineasProveedor(em.find(LineasProveedor.class, this.getLineaProveedorID()));
			listProductoProveedor.add(productoProveedor);
			reCalcularMargenUtilidad();
			crearProductoProveedor();
			precio = 0;
		} catch (Exception e) {
			e.printStackTrace();
			System.out.println("Error en agregarProductoProveedor: "
					+ e.getMessage());
		}
	}

	public void reCalcularMargenUtilidad() {
		try {
			System.out.println("Ingreso a reCalcularMargenUtilidad");
			newProducto.setPrecio(precio);
			for (ProductoProveedor value : listProductoProveedor) {
				double costo = proveedorRepository.obtenerCosto(value
						.getProveedor());

				value.setUtilidadMaxReCal(UtilCostosCalculation
						.calculateMargenUtilidad(value, costo,
								newProducto.getPrecio(), 7));
				System.out.println("Margen Re" + value.getUtilidadMaxReCal());
				value.setPrecioUnitarioVenta(newProducto.getPrecio());
			}

		} catch (Exception e) {
			System.err.println("Error en reCalcularMargenUtilidad : "
					+ e.getMessage());
			e.getStackTrace();
		}
	}

	public void destroyProductoProveedor() {
		try {
			System.out.println("Ingreso destroyProductoProveedor");
			productoProveedor = null;
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
			System.out.println("Error en destroyProductoProveedor: "
					+ e.getMessage());
		}
	}

	public void eliminarProductoProveedor() {
		try {
			System.out.println("Ingreso eliminarProductoProveedor");
			if (selectedProductoProveedor != null) {
				for (int i = 0; i < listProductoProveedor.size(); i++) {
					if (selectedProductoProveedor.getId() == listProductoProveedor
							.get(i).getId()) {
						listProductoProveedor.remove(i);
						System.out.println("fue Eliminado");
						FacesMessage m = new FacesMessage(
								FacesMessage.SEVERITY_INFO, "Eliminado!",
								"El Producto Proveedor fue eliminado!!.");
						facesContext.addMessage(null, m);

					}
				}
			}
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
			System.out.println("Error en destroyProductoProveedor: "
					+ e.getMessage());
			FacesMessage m = new FacesMessage(FacesMessage.SEVERITY_ERROR,
					"Error!!", "No se pudo eliminar el Producto Proveedor.");
			facesContext.addMessage(null, m);
		}
	}

	public void productoProveedorSelect() {
		if (selectedProductoProveedor == null) {
			FacesMessage m = new FacesMessage(FacesMessage.SEVERITY_WARN,
					"Seleccione!",
					"Por Favor, seleccione un Producto Proveedor!!.");
			facesContext.addMessage(null, m);
		} else {

			precio = selectedProductoProveedor.getPrecioUnitarioVenta();

			RequestContext.getCurrentInstance().execute(
					"dlgEditProveedor.show()");
		}
	}

	public void actualizarProductoProveedor() {
		try {
			System.out.println("Ingreso eliminarProductoProveedor");
			if (selectedProductoProveedor != null) {
				selectedProductoProveedor.setPrecioUnitarioVenta(precio);
				for (int i = 0; i < listProductoProveedor.size(); i++) {
					if (selectedProductoProveedor.getId() == listProductoProveedor
							.get(i).getId()) {
						listProductoProveedor.remove(i);
						listProductoProveedor.add(selectedProductoProveedor);
						System.out.println("fue Eliminado");
						FacesMessage m = new FacesMessage(
								FacesMessage.SEVERITY_INFO, "Editado!",
								"El Producto Proveedor fue editado!!.");
						facesContext.addMessage(null, m);

					}
				}
			}
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
			System.out.println("Error en destroyProductoProveedor: "
					+ e.getMessage());
			FacesMessage m = new FacesMessage(FacesMessage.SEVERITY_ERROR,
					"Error!!", "No se pudo editar el Producto Proveedor.");
			facesContext.addMessage(null, m);
		}
	}

	public void quitarComponente() {
		try {
			System.out.println("Ingreso a quitarComponente...");
			// listaDetalleProducto.remove(selectedDetalleProducto);
			if (selectedDetalleProducto != null) {
				for (int i = 0; i < listaComponentesProductoInterno.size(); i++) {
					if (selectedDetalleProducto.getCorrelativo().equals(
							listaComponentesProductoInterno.get(i)
									.getCorrelativo())) {
						listaComponentesProductoInterno.remove(i);
					}
				}
			}
			//calcularTotal();

		} catch (Exception e) {
			// TODO: handle exception
			// listaProductos.clear();
			System.out.println("Error en quitarComponente: " + e.getMessage());
		} finally {
			// criterioBusquedaProducto = null;
		}
	}



	public void buscarProductosParaCargar() {
		try {
			listaProductos.clear();
			System.out.println("Ingreso a buscarProductosParaCargar... "
					+ this.getCriterioBusquedaProducto());
			if (!this.getCriterioBusquedaProducto().isEmpty()) {
				listaProductos = productoRepository
						.findAllExterInterForDescription(this
								.getCriterioBusquedaProducto().toUpperCase());
			} else {
				// recuperar todos
				listaProductos = productoRepository.traerProdExternosInternos();
			}

			cargarListaEstructuraMateriaP();
		} catch (Exception e) {
			// TODO: handle exception
			listaProductos.clear();
			listaEstructuraMateriaP.clear();

			System.err.println("Error en buscarProductosParaCargar: "
					+ e.getMessage());
		}
	}



	private double totalCantidad = 0;
	private double totalIncidencia = 0;

//	public void calcularTotal() {
//		totalCantidad = 0;
//		totalIncidencia = 0;
//		Integer index = 1;
//		for (DetalleProducto value : listaComponentesProductoInterno) {
//			cps -= value.getCantidad();
//			totalCantidad += value.getCantidad();
//			totalIncidencia += value.getIncidencia();
//			value.setCorrelativo(index);
//			index++;
//		}
//		System.out.println("Cantidad : " + totalCantidad + " Incidencia : "
//				+ totalIncidencia);
//	}



	

	@Inject
	private ConversionesRepository conversionesRepository;

	



	// SELECT RECETA-PRODUCTO CLICK
	public void onRowSelectProductoExtInterClick(SelectEvent event) {
		try {
			DetalleProducto recetaP = (DetalleProducto) event.getObject();
			System.out.println("onRowSelectDetalleProductoClick : ID = "
					+ recetaP.getId());
			selectedDetalleProducto = recetaP;

		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
			System.out.println("Error in onRowSelectProductoExtInterClick: "
					+ e.getMessage());
		}
	}

	// SELECT RECETA-PRODUCTO CLICK
	public void onRowSelectProductoProveedorClick(SelectEvent event) {
		try {
			ProductoProveedor recetaP = (ProductoProveedor) event.getObject();
			System.out.println("onRowSelectProductoProveedorClick : ID = "
					+ recetaP.getId());
			selectedProductoProveedor = recetaP;

		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
			System.out.println("Error in onRowSelectProductoProveedorClick: "
					+ e.getMessage());
		}
	}

	// get and set
	public Producto getNewProducto() {
		return newProducto;
	}

	public void setNewProducto(Producto newProducto) {
		this.newProducto = newProducto;
	}

	public String getTituloPanel() {
		return tituloPanel;
	}

	public void setTituloPanel(String tituloPanel) {
		this.tituloPanel = tituloPanel;
	}

	public Producto getSelectedProducto() {
		return selectedProducto;
	}

	public void setSelectedProducto(Producto selectedProducto) {
		this.selectedProducto = selectedProducto;
	}

	public boolean isModificar() {
		return modificar;
	}

	public void setModificar(boolean modificar) {
		this.modificar = modificar;
	}

	public List<TipoProducto> getListaTipoProducto() {
		return listaTipoProducto;
	}

	public void setListaTipoProducto(List<TipoProducto> listaTipoProducto) {
		this.listaTipoProducto = listaTipoProducto;
	}


	public List<Proveedor> getListaProveedor() {
		return listaProveedor;
	}

	public void setListaProveedor(List<Proveedor> listaProveedor) {
		this.listaProveedor = listaProveedor;
	}



	public List<LineasProveedor> getListalineasProveedor() {
		return listalineasProveedor;
	}

	public void setListalineasProveedor(
			List<LineasProveedor> listalineasProveedor) {
		this.listalineasProveedor = listalineasProveedor;
	}

	public DetalleProducto getSelectedDetalleProducto() {
		return selectedDetalleProducto;
	}

	public void setSelectedDetalleProducto(
			DetalleProducto selectedDetalleProducto) {
		this.selectedDetalleProducto = selectedDetalleProducto;
	}

	public String getCriterioBusquedaProducto() {
		return criterioBusquedaProducto;
	}

	public void setCriterioBusquedaProducto(String criterioBusquedaProducto) {
		this.criterioBusquedaProducto = criterioBusquedaProducto;
	}

	public EstructuraMateriaPrima getSelectedEstructuraMateriaP() {
		return selectedEstructuraMateriaP;
	}

	public void setSelectedEstructuraMateriaP(
			EstructuraMateriaPrima selectedEstructuraMateriaP) {
		this.selectedEstructuraMateriaP = selectedEstructuraMateriaP;
	}

	public List<EstructuraMateriaPrima> getListaEstructuraMateriaP() {
		return listaEstructuraMateriaP;
	}


	public void setListaEstructuraMateriaP(
			List<EstructuraMateriaPrima> listaEstructuraMateriaP) {
		this.listaEstructuraMateriaP = listaEstructuraMateriaP;
	}

	public List<ProductoProveedor> getListProductoProveedor() {
		return listProductoProveedor;
	}

	public void setListProductoProveedor(
			List<ProductoProveedor> listProductoProveedor) {
		this.listProductoProveedor = listProductoProveedor;
	}

	public ProductoProveedor getSelectedProductoProveedor() {
		return selectedProductoProveedor;
	}

	public void setSelectedProductoProveedor(
			ProductoProveedor selectedProductoProveedor) {
		this.selectedProductoProveedor = selectedProductoProveedor;
	}

	public boolean isNuevo() {
		return nuevo;
	}

	public void setNuevo(boolean nuevo) {
		this.nuevo = nuevo;
	}

	public ProductoProveedor getProductoProveedor() {
		return productoProveedor;
	}

	public void setProductoProveedor(ProductoProveedor productoProveedor) {
		this.productoProveedor = productoProveedor;
	}

	public TipoProducto getTipoProducto() {
		return tipoProducto;
	}

	public void setTipoProducto(TipoProducto tipoProducto) {
		this.tipoProducto = tipoProducto;
	}


	public Presentacion getPresentacion() {
		return presentacion;
	}

	public void setPresentacion(Presentacion presentacion) {
		this.presentacion = presentacion;
	}




	public CantidadUnidadPresentacion getCantidadUnidadPresentacion() {
		return cantidadUnidadPresentacion;
	}

	public void setCantidadUnidadPresentacion(
			CantidadUnidadPresentacion cantidadUnidadPresentacion) {
		this.cantidadUnidadPresentacion = cantidadUnidadPresentacion;
	}

	public List<Presentacion> getListaPresentacion() {
		return listaPresentacion;
	}

	public void setListaPresentacion(List<Presentacion> listaPresentacion) {
		this.listaPresentacion = listaPresentacion;
	}

	public List<UnidadMedida> getListaUnidadMedida() {
		return listaUnidadMedida;
	}

	public void setListaUnidadMedida(List<UnidadMedida> listaUnidadMedida) {
		this.listaUnidadMedida = listaUnidadMedida;
	}

	public Integer getPresentacionid() {
		return presentacionid;
	}

	public void setPresentacionid(Integer presentacionid) {
		this.presentacionid = presentacionid;
	}

	public Integer getUnidadMedidaid() {
		return unidadMedidaid;
	}

	public void setUnidadMedidaid(Integer unidadMedidaid) {
		this.unidadMedidaid = unidadMedidaid;
	}

	public UnidadMedida getUnidadMedida() {
		return unidadMedida;
	}

	public void setUnidadMedida(UnidadMedida unidadMedida) {
		this.unidadMedida = unidadMedida;
	}

	public int getId_UnidadMedida() {
		return id_UnidadMedida;
	}

	public void setId_UnidadMedida(int id_UnidadMedida) {
		this.id_UnidadMedida = id_UnidadMedida;
	}

	public Proveedor getProveedor() {
		return proveedor;
	}

	public void setProveedor(Proveedor proveedor) {
		this.proveedor = proveedor;
	}

	public LineasProveedor getLineaProveedor() {
		return lineaProveedor;
	}

	public void setLineaProveedor(LineasProveedor lineaProveedor) {
		this.lineaProveedor = lineaProveedor;
	}

	public int getId_FormaFarmaceutica() {
		return id_FormaFarmaceutica;
	}

	public void setId_FormaFarmaceutica(int id_FormaFarmaceutica) {
		this.id_FormaFarmaceutica = id_FormaFarmaceutica;
	}

	public double getPrecio() {
		return precio;
	}

	public void setPrecio(double precio) {
		this.precio = precio;
	}

	public double getCps() {
		return cps;
	}

	public void setCps(double cps) {
		this.cps = cps;
	}

	public double getTotalCantidad() {
		return totalCantidad;
	}

	public void setTotalCantidad(double totalCantidad) {
		this.totalCantidad = totalCantidad;
	}

	public double getTotalIncidencia() {
		return totalIncidencia;
	}

	public void setTotalIncidencia(double totalIncidencia) {
		this.totalIncidencia = totalIncidencia;
	}

	public List<Marca> getListaMarca() {
		return listaMarca;
	}

	public void setListaMarca(List<Marca> listaMarca) {
		this.listaMarca = listaMarca;
	}

	public List<Fabricante> getListaFabricante() {
		return listaFabricante;
	}

	public void setListaFabricante(List<Fabricante> listaFabricante) {
		this.listaFabricante = listaFabricante;
	}

	public int getProveedorID() {
		return proveedorID;
	}

	public void setProveedorID(int proveedorID) {
		this.proveedorID = proveedorID;
	}

	public int getLineaProveedorID() {
		return lineaProveedorID;
	}

	public void setLineaProveedorID(int lineaProveedorID) {
		this.lineaProveedorID = lineaProveedorID;
	}

	public String getTipoCambio() {
		return tipoCambio;
	}

	public void setTipoCambio(String tipoCambio) {
		this.tipoCambio = tipoCambio;
	}

	public void seleccionadaAlgo(){
		System.out.println("Acaba de seleccionar un campo");
	}

	public PanelGrid getPanelGrid() {
		FacesContext fc = FacesContext.getCurrentInstance();
		panelGrid=(PanelGrid)fc.getApplication().createComponent("org.primefaces.component.PanelGrid");
		panelGrid.setColumns(2);
		panelGrid.setLayout("grid");
		panelGrid.setStyleClass("ui-panelgrid-blank");
		panelGrid.setColumnClasses("ui-grid-col-3,ui-grid-col-9");
		OutputLabel label = new OutputLabel();
		SelectOneMenu selectOneMenu=new SelectOneMenu();
		selectedTipoProducto.add(0, new TipoProducto());
		selectOneMenu.setValue(selectedTipoProducto.get(0));
		selectOneMenu.setRequired(true);
		selectOneMenu.setRequiredMessage("Seleccione un tipo de producto");
		selectOneMenu.addValueChangeListener(new ValueChangeListener() {
			
			@Override
			public void processValueChange(ValueChangeEvent event)
					throws AbortProcessingException {
				System.out.println("New value: " + event.getNewValue());
				
			}
		});
		label.setValue("Tipo Producto");
	    List<TipoProducto> padre=tipoProductoRepository.findAllByParameterIsNull("tipoPadre");
	    UISelectItem item = new UISelectItem();
	    item.setItemLabel("Seleccione un valor.");
		item.setItemValue("");
		selectOneMenu.getChildren().add(item);
	    for (TipoProducto tipoProducto : padre) {
	    	UISelectItem items = new UISelectItem();
	    	items.setItemLabel(tipoProducto.getDescripcion());
	    	items.setItemValue(tipoProducto);
	    	selectOneMenu.getChildren().add(items);
		}
		if(selectedTipoProducto.size()==0){
			
		}else{
			
		}

		panelGrid.getChildren().add(label);
		panelGrid.getChildren().add(selectOneMenu);

		return panelGrid;
	}

	/* metodos arturo*/	
	private  void cargarHojas(TreeNode n, Atributo atributo) {
		if(tipoProducto!=null){
			List<Atributo> hijo=atributoRepository.findAllByParameterObject("atributoPadre", atributo);
			for (Atributo at : hijo) {
				TreeNode treeNode=new DefaultTreeNode(at, n);
				treeNode.setExpanded(true);
				treeNode.setSelected(false);
				if(existeNodoSeleccionado(treeNode, selectedNodes)){
					treeNode.setSelected(true);
				}
				cargarHojas(treeNode, at);
			}
		}	
	}
	
	public TreeNode cargarArbol(List<AtributoTipoProducto> atributoTipoProducto) {
		TreeNode root = new DefaultTreeNode("root", null);
		for (AtributoTipoProducto atributoTipoProducto2 : atributoTipoProducto) {
			TreeNode treeNode=new DefaultTreeNode(atributoTipoProducto2.getAtributo(), root);
			treeNode.setSelected(false);
			treeNode.setExpanded(true);
			if(existeNodoSeleccionado(treeNode, selectedNodes)){
				treeNode.setSelected(true);
			}
			cargarHojas(treeNode, atributoTipoProducto2.getAtributo());
		}

        return root;
    }
	
	public void cargarArbolSeleccionado(){
		
		if(tipoProducto.getId()!=null){
			System.out.println("Seleccionado "+tipoProducto);
			try {
				tipoProducto=tipoProductoRepository.findById(tipoProducto.getId());
				this.newProducto.setTipoProducto(tipoProducto);
				List<AtributoTipoProducto> atributoTipoProducto=atributoTipoProductoRepository.findAllByParameterObject("tipoProducto", tipoProducto);		
				this.root=cargarArbol(atributoTipoProducto);
			} catch (Exception e) {
				FacesUtil.warmMessage("Seleccione un tipo de producto");
			}
			
		}else{
			FacesUtil.warmMessage("Seleccione un tipo de producto");
		}
	}
	
	private void collapsarORexpandir(TreeNode n, boolean option) {
		if(n.getChildren().size() == 0) {
			n.setSelected(false);
		}else{
			for(TreeNode s: n.getChildren()) {
				collapsarORexpandir(s, option);
			}
			n.setExpanded(option);
			n.setSelected(false);
		}
	}
	
	public boolean existeNodoSeleccionado(TreeNode n,TreeNode[] select){
		if(select!=null){
			for (int i = 0; i < select.length; i++) {
				if(select[i].getData().equals(n.getData())){
					return true;
				}
			}
		}
		return false;
	}
	
	public void onNodeSelect(NodeSelectEvent event){
        System.out.println("Node Data ::"+event.getTreeNode().getData()+" :: Selected");
        TreeNode node=event.getTreeNode();
        for (int i = 0; i < selectedNodes.length; i++) {
        	System.out.println("Datos arbol: "+selectedNodes[i]);
		}
        if(node.getChildCount()>0){
        	FacesUtil.warmMessage("Seleccione un hijo, No puede seleccionar un atributo padre");
        	List<TreeNode> hijos= node.getChildren(); 
        	TreeNode[] auxTree=selectedNodes;
        	
        	for (int i = 0; i < auxTree.length; i++) {
        		if(auxTree[i].getData().equals(node.getData())){
        			this.selectedNodes=deleteComponent(this.selectedNodes,auxTree[i]);
        		}
        		for (TreeNode treeNodeHijo : hijos) {
        			if(treeNodeHijo.getData().equals(auxTree[i].getData())){
        				System.out.println("Hijos iguales i: "+i+" dato: "+treeNodeHijo.getData());
        				this.selectedNodes=deleteComponent(this.selectedNodes,auxTree[i]);
        			}
        			
        		}
			}

        }else{
             TreeNode padre=node.getParent();
             if(padre!=null){
            	 List<TreeNode> hijos=padre.getChildren();
            	 for (TreeNode treeNode : hijos) {
					if(!treeNode.getData().equals(node.getData())){
						if(existeNodoSeleccionado(treeNode, selectedNodes)){
							this.selectedNodes=deleteComponent(this.selectedNodes,treeNode);
						}
					}
				}
             }
        }
        for (int i = 0; i < selectedNodes.length; i++) {
        	System.out.println("Datos arbol nuevo: "+selectedNodes[i]);
        	
		}
        this.listAtributoProducto=cargarAtributoProducto(selectedNodes);
        System.out.println("Atributos producto seleccionados: "+listAtributoProducto);
        cargarArbolSeleccionado();
    }
 
	public void onNodeUnSelect(NodeUnselectEvent event){
        System.out.println("Node Data ::"+event.getTreeNode().getData()+" :: UnSelected");
        Atributo at=(Atributo) event.getTreeNode().getData();
        for (int i = 0; i < listAtributoProducto.size(); i++) {
			AtributoProducto ap=listAtributoProducto.get(i);
			if(ap.getAtributo().equals(at)){
				listAtributoProducto.remove(i);
				if(ap.getId()!=null){
					ap.setBaja(true);
					this.listAtributoProductoDelete.add(ap);
				}
				break;
			}
		}
        System.out.println("Lisa AtributoProducto: "+listAtributoProducto);
    }
	
	private TreeNode[] deleteComponent(TreeNode[] input, TreeNode node) {
		TreeNode[] resultado=new TreeNode[selectedNodes.length-1];
			int contador=0;
			for (int j = 0; j < selectedNodes.length; j++) {
				if(!selectedNodes[j].getData().equals(node.getData())){
					resultado[contador]=selectedNodes[j];
					contador++;
				}
			}
				return resultado;
	}
	
    private List<AtributoProducto> cargarAtributoProducto(TreeNode[] select){
    	List<AtributoProducto> resultado=new ArrayList<>();
    		for (int i = 0; i < select.length; i++) {
    			boolean existe=false;
				for (AtributoProducto atributoProducto : this.listAtributoProducto) {
					if(atributoProducto.getAtributo().equals(select[i].getData())){
						resultado.add(atributoProducto);
						existe=true;
					}
				}
				if(!existe){
					AtributoProducto ap=new AtributoProducto();
					ap.setAtributo((Atributo) select[i].getData());
					resultado.add(ap);
				}
			}
   
			for (AtributoProducto ap : listAtributoProducto) {
				if(!resultado.contains(ap)){
					ap.setBaja(true);
					this.listAtributoProductoDelete.add(ap);
					System.out.println("Lista de bajas atributo: "+ap.getAtributo());
				}
			}

    	return resultado;	
    }
    
	public void setPanelGrid(PanelGrid panelGrid) {
		this.panelGrid = panelGrid;
	}


	public List<TipoProducto> getSelectedTipoProducto() {
		return selectedTipoProducto;
	}


	public void setSelectedTipoProducto(List<TipoProducto> selectedTipoProducto) {
		this.selectedTipoProducto = selectedTipoProducto;
	}


	public TreeNode getRoot() {
		return root;
	}


	public void setRoot(TreeNode root) {
		this.root = root;
	}


	public TreeNode[] getSelectedNodes() {
		return selectedNodes;
	}


	public void setSelectedNodes(TreeNode[] selectedNodes) {
		this.selectedNodes = selectedNodes;
	}





	public List<AtributoProducto> getListAtributoProducto() {
		return listAtributoProducto;
	}





	public void setListAtributoProducto(List<AtributoProducto> listAtributoProducto) {
		this.listAtributoProducto = listAtributoProducto;
	}





	public List<AtributoProducto> getListAtributoProductoDelete() {
		return listAtributoProductoDelete;
	}





	public void setListAtributoProductoDelete(
			List<AtributoProducto> listAtributoProductoDelete) {
		this.listAtributoProductoDelete = listAtributoProductoDelete;
	}

}
