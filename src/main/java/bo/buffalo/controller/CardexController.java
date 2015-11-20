package bo.buffalo.controller;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.enterprise.context.Conversation;
import javax.enterprise.context.ConversationScoped;
import javax.enterprise.event.Event;
import javax.enterprise.inject.Produces;
import javax.faces.context.FacesContext;
import javax.inject.Inject;
import javax.inject.Named;
import javax.persistence.EntityManager;
import javax.servlet.http.HttpServletRequest;

import org.primefaces.event.SelectEvent;
import org.richfaces.cdi.push.Push;

import bo.buffalo.data.AlmacenProductoRepository;
import bo.buffalo.data.AlmacenRepository;
import bo.buffalo.data.CardexProductoRepository;
import bo.buffalo.data.UsuarioRepository;
import bo.buffalo.model.AlmProducto;
import bo.buffalo.model.Almacen;
import bo.buffalo.model.CardexProducto;
import bo.buffalo.model.LineasProveedor;
import bo.buffalo.model.Producto;
import bo.buffalo.model.ProductoProveedor;
import bo.buffalo.model.Proveedor;
import bo.buffalo.model.Usuario;

@Named(value = "cardexController")
@ConversationScoped
public class CardexController implements Serializable {

	
	private static final long serialVersionUID = -7819149623543804669L;

	public static final String PUSH_CDI_TOPIC = "pushCdi";

	@Inject
	private FacesContext facesContext;

	@Inject
	private EntityManager em;

	@Inject
	Conversation conversation;

	private boolean visible = false;

	@Inject
	UsuarioRepository usuarioRepository;
	private Usuario usuarioSession;

	@Inject
	@Push(topic = PUSH_CDI_TOPIC)
	Event<String> pushEventSucursal;

	private String tituloPanel = "Registrar AlmProducto";
	private CardexProducto selectedAlmProducto;
	private CardexProducto newAlmProducto;

	private List<CardexProducto> listaCardex = new ArrayList<CardexProducto>();

	private CardexProducto selectedCatalogo;

	private Date fechaFin = getUltimoDiaDelMes();
	private Date fechaIni = getPrimerDiaDelMes();

	private List<Producto> listProducto = new ArrayList<Producto>();
	private List<Proveedor> listProveedor = new ArrayList<Proveedor>();
	private List<LineasProveedor> listLineasProveedor = new ArrayList<LineasProveedor>();

	@Inject
	private CardexProductoRepository cardexProductoRepository;

	@Inject
	private AlmacenProductoRepository almacenProductoRepository;

	@Inject
	private AlmacenRepository almacenRepository;

	private List<AlmProducto> listAlmaceProducto = new ArrayList<AlmProducto>();

	private List<Almacen> listAlmacen = new ArrayList<Almacen>();

	private AlmProducto selectedAlmacenProducto;

	private Almacen almacen;

	private Integer idProducto;

	private Integer idProveedor;

	private Integer idLinea;

	private Producto producto = new Producto();

	private Proveedor proveedor = new Proveedor();

	private LineasProveedor lineaProveedor = new LineasProveedor();

	@PostConstruct
	public void initNewAlmProducto() {

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

		// traer todos las pedidoes ordenados por ID Desc

		/*setListaCardex(cardexProductoRepository
				.traerCardexProductosActivosInactivos());*/
		almacen = new Almacen();
		listAlmacen = almacenRepository.traerAlmacenActivas();
		visible = false;

	}

	public void beginConversation() {

		if (conversation.isTransient()) {
			System.out.println("beginning conversation : " + this.conversation);
			conversation.begin();
			System.out.println("---> Init Conversation");
		}
	}

	public void selectAlmacen() {
		try {
			System.out.println("Ingresa as selectAlmacen");

			listProducto = almacenProductoRepository
					.findProductoForAlmacen(almacen);
			visible = false;
		} catch (Exception e) {
			System.out.println("Error as selectAlmacen : " + e.getMessage());
		}
	}

	public static Date getPrimerDiaDelMes() {
		Calendar cal = Calendar.getInstance();
		cal.set(cal.get(Calendar.YEAR), cal.get(Calendar.MONTH),
				cal.getActualMinimum(Calendar.DAY_OF_MONTH),
				cal.getMinimum(Calendar.HOUR_OF_DAY),
				cal.getMinimum(Calendar.MINUTE),
				cal.getMinimum(Calendar.SECOND));
		return cal.getTime();
	}

	public static Date getUltimoDiaDelMes() {
		Calendar cal = Calendar.getInstance();
		cal.set(cal.get(Calendar.YEAR), cal.get(Calendar.MONTH),
				cal.getActualMaximum(Calendar.DAY_OF_MONTH),
				cal.getMaximum(Calendar.HOUR_OF_DAY),
				cal.getMaximum(Calendar.MINUTE),
				cal.getMaximum(Calendar.SECOND));
		return cal.getTime();
	}

	public void selectProducto() {
		try {
			System.out.println("Ingresa as selectAlmacen");
			producto = em.find(Producto.class, this.producto.getId());
			listProveedor = almacenProductoRepository
					.findProveedorProductoForAlmacen(almacen, producto);
			visible = false;
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
			visible = false;
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
			visible = false;
		} catch (Exception e) {
			System.out.println("Error as selectAlmacen : " + e.getMessage());
		}
	}

	public void consultarCardex() {
		if (almacen.getId() != 0 && producto.getId() != 0
				&& proveedor.getId() == 0 && lineaProveedor.getId() == 0) {// ALMACEN,
																			// PRODUCTO
			System.out.println("PRODUCTO");
			listaCardex = cardexProductoRepository
					.findForProductoProveedorLinea(almacen, producto, fechaIni,
							fechaFin);
		} else if (almacen.getId() != 0 && producto.getId() != 0
				&& proveedor.getId() != 0 && lineaProveedor.getId() == 0) {// ALMACEN,
																			// PRODUCTO,PROVEEDOR
			System.out.println("PROVEEDOR");
			listaCardex = cardexProductoRepository
					.findForProductoProveedorLinea(almacen, producto,
							proveedor, fechaIni, fechaFin);
		} else if (almacen.getId() != 0 && producto.getId() != 0
				&& proveedor.getId() != 0 && lineaProveedor.getId() != 0) {// ALMACEN,
																			// PRODUCTO,PROVEEDOR,
																			// LINEA
			System.out.println("LINEA");
			listaCardex = cardexProductoRepository
					.findForProductoProveedorLinea(almacen, producto,
							proveedor, lineaProveedor, fechaIni, fechaFin);
		}

	}

	public void endConversation() {
		if (!conversation.isTransient()) {
			conversation.end();
		}
	}

	// SELECT PRESENTACION CLICK
	public void onRowSelectAlmProductoClick(SelectEvent event) {
		try {
			CardexProducto pedido = (CardexProducto) event.getObject();
			System.out
					.println("onRowSelectAlmProductoClick  " + pedido.getId());
			selectedAlmProducto = pedido;
			newAlmProducto = em.find(CardexProducto.class, pedido.getId());
			newAlmProducto.setFechaRegistro(new Date());
			newAlmProducto.setUsuarioRegistro(usuarioSession.getLogin());

			tituloPanel = "Modificar Pedido";

		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
			System.out.println("Error in onRowSelectAlmProductoClick: "
					+ e.getMessage());
		}
	}

	// SELECT PRESENTACION CLICK
	public void onRowSelectAlmacenProductoClick(SelectEvent event) {
		try {
			AlmProducto pedido = (AlmProducto) event.getObject();
			System.out
					.println("onRowSelectAlmProductoClick  " + pedido.getId());
			selectedAlmacenProducto = pedido;
			/*listaCardex = cardexProductoRepository
					.findForProductoProveedorLinea(
							selectedAlmacenProducto.getAlmacen(),
							selectedAlmacenProducto.getProducto(),
							selectedAlmacenProducto.getProveedor(),
							selectedAlmacenProducto.getLineaProvedor());*/
			visible = true;

		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
			System.out.println("Error in onRowSelectAlmProductoClick: "
					+ e.getMessage());
		}
	}

	// SELECT PRESENTACION CLICK
	public void onRowSelectProductoClick(SelectEvent event) {
		try {
			ProductoProveedor producto = (ProductoProveedor) event.getObject();
			System.out.println("onRowSelectProductoClick  " + producto.getId());

		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
			System.out.println("Error in onRowSelectAlmProductoClick: "
					+ e.getMessage());
		}
	}

	public String getTituloPanel() {
		return tituloPanel;
	}

	public void setTituloPanel(String tituloPanel) {
		this.tituloPanel = tituloPanel;
	}

	public CardexProducto getSelectedAlmProducto() {
		return selectedAlmProducto;
	}

	public void setSelectedAlmProducto(CardexProducto selectedAlmProducto) {
		this.selectedAlmProducto = selectedAlmProducto;
	}

	public CardexProducto getNewAlmProducto() {
		return newAlmProducto;
	}

	public void setNewAlmProducto(CardexProducto newAlmProducto) {
		this.newAlmProducto = newAlmProducto;
	}

	public CardexProducto getSelectedCatalogo() {
		return selectedCatalogo;
	}

	public void setSelectedCatalogo(CardexProducto selectedCatalogo) {
		this.selectedCatalogo = selectedCatalogo;
	}

	@Produces
	@Named
	public List<CardexProducto> getListaCardex() {
		return listaCardex;
	}

	public void setListaCardex(List<CardexProducto> listaCardex) {
		this.listaCardex = listaCardex;
	}

	public List<AlmProducto> getListAlmaceProducto() {
		return listAlmaceProducto;
	}

	public void setListAlmaceProducto(List<AlmProducto> listAlmaceProducto) {
		this.listAlmaceProducto = listAlmaceProducto;
	}

	public Almacen getAlmacen() {
		return almacen;
	}

	public void setAlmacen(Almacen almacen) {
		this.almacen = almacen;
	}

	public List<Almacen> getListAlmacen() {
		return listAlmacen;
	}

	public void setListAlmacen(List<Almacen> listAlmacen) {
		this.listAlmacen = listAlmacen;
	}

	public AlmProducto getSelectedAlmacenProducto() {
		return selectedAlmacenProducto;
	}

	public void setSelectedAlmacenProducto(AlmProducto selectedAlmacenProducto) {
		this.selectedAlmacenProducto = selectedAlmacenProducto;
	}

	public boolean isVisible() {
		return visible;
	}

	public void setVisible(boolean visible) {
		this.visible = visible;
	}

	public Date getFechaFin() {
		return fechaFin;
	}

	public void setFechaFin(Date fechaFin) {
		this.fechaFin = fechaFin;
	}

	public Date getFechaIni() {
		return fechaIni;
	}

	public void setFechaIni(Date fechaIni) {
		this.fechaIni = fechaIni;
	}

	public List<Producto> getListProducto() {
		return listProducto;
	}

	public void setListProducto(List<Producto> listProducto) {
		this.listProducto = listProducto;
	}

	public List<Proveedor> getListProveedor() {
		return listProveedor;
	}

	public void setListProveedor(List<Proveedor> listProveedor) {
		this.listProveedor = listProveedor;
	}

	public List<LineasProveedor> getListLineasProveedor() {
		return listLineasProveedor;
	}

	public void setListLineasProveedor(List<LineasProveedor> listLineasProveedor) {
		this.listLineasProveedor = listLineasProveedor;
	}

	public Integer getIdProducto() {
		return idProducto;
	}

	public void setIdProducto(Integer idProducto) {
		this.idProducto = idProducto;
	}

	public Integer getIdProveedor() {
		return idProveedor;
	}

	public void setIdProveedor(Integer idProveedor) {
		this.idProveedor = idProveedor;
	}

	public Integer getIdLinea() {
		return idLinea;
	}

	public void setIdLinea(Integer idLinea) {
		this.idLinea = idLinea;
	}

	public Producto getProducto() {
		return producto;
	}

	public void setProducto(Producto producto) {
		this.producto = producto;
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

}
