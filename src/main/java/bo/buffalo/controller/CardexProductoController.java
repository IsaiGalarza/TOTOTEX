package bo.buffalo.controller;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.Serializable;
import java.net.URL;
import java.text.SimpleDateFormat;
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

import org.apache.commons.io.IOUtils;
import org.primefaces.event.SelectEvent;
import org.primefaces.model.DefaultStreamedContent;
import org.primefaces.model.StreamedContent;
import org.richfaces.cdi.push.Push;

import bo.buffalo.data.AlmacenProductoRepository;
import bo.buffalo.data.AlmacenRepository;
import bo.buffalo.data.CardexProductoRepository;
import bo.buffalo.data.TipoProductoRepository;
import bo.buffalo.data.UsuarioRepository;
import bo.buffalo.model.AlmProducto;
import bo.buffalo.model.Almacen;
import bo.buffalo.model.CardexProducto;
import bo.buffalo.model.LineasProveedor;
import bo.buffalo.model.Producto;
import bo.buffalo.model.ProductoProveedor;
import bo.buffalo.model.Proveedor;
import bo.buffalo.model.TipoProducto;
import bo.buffalo.model.Usuario;
import bo.buffalo.structure.StructureCardexProducto;
import bo.buffalo.structure.StructureCatalogoPrecios;

@Named(value = "cardexProductoController")
@ConversationScoped
public class CardexProductoController implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 8445068788313397989L;

	public static final String PUSH_CDI_TOPIC = "pushCdi";

	@Inject
	private FacesContext facesContext;

	@Inject
	private EntityManager em;

	@Inject
	Conversation conversation;

	private boolean visible = false;

	private StreamedContent file;
	private StreamedContent streamedContent;

	@Inject
	UsuarioRepository usuarioRepository;
	private Usuario usuarioSession;

	@Inject
	@Push(topic = PUSH_CDI_TOPIC)
	Event<String> pushEventSucursal;

	private String tituloPanel = "Registrar AlmProducto";
	private CardexProducto selectedAlmProducto;
	private CardexProducto newAlmProducto;

	private List<StructureCardexProducto> listaCardexProducto = new ArrayList<StructureCardexProducto>();

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

	private List<TipoProducto> listTipoProducto = new ArrayList<TipoProducto>();
	private TipoProducto tipoProducto = new TipoProducto();
	@Inject
	private TipoProductoRepository tipoProductoRepository;

	private StructureCatalogoPrecios selectedAlmacenProducto;

	private Almacen almacen;

	private Integer idProducto;

	private Integer idProveedor;

	private Integer idLinea;

	private Producto producto = new Producto();

	private Proveedor proveedor = new Proveedor();

	private LineasProveedor lineaProveedor = new LineasProveedor();

	private List<StructureCatalogoPrecios> listAlmacenProducto = new ArrayList<StructureCatalogoPrecios>();

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

		almacen = almacenRepository.findAlmacenForUser(usuarioSession);
		listTipoProducto = tipoProductoRepository.findAllOrderedByID();
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
			setListAlmacenProducto(almacenProductoRepository
					.findTipoProductosForAlmacen(almacen, tipoProducto,
							fechaIni, fechaFin));

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

		System.out.println("Ingreso a consultarCardex");
		if (almacen == null || selectedAlmacenProducto == null)
			return;
		listaCardexProducto = cardexProductoRepository.findForAlmacenCardex(
				almacen, selectedAlmacenProducto.getIdProducto(), fechaIni,
				fechaFin);
	}

	public void endConversation() {
		if (!conversation.isTransient()) {
			conversation.end();
		}
	}

	public void limpiar() {
		System.out.println("Ingreso a limpiar");
		listaCardexProducto.clear();
		listAlmacenProducto.clear();
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
	public void onRowSelectAlmProductoListClick(SelectEvent event) {
		try {
			StructureCatalogoPrecios pedido = (StructureCatalogoPrecios) event
					.getObject();
			System.out.println("onRowSelectAlmProductoClick  ");
			selectedAlmacenProducto = pedido;
			consultarCardex();
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
			// selectedAlmacenProducto = pedido;
			/*
			 * listaCardex = cardexProductoRepository
			 * .findForProductoProveedorLinea(
			 * selectedAlmacenProducto.getAlmacen(),
			 * selectedAlmacenProducto.getProducto(),
			 * selectedAlmacenProducto.getProveedor(),
			 * selectedAlmacenProducto.getLineaProvedor());
			 */
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

	/**
	 * Reporte de Cardex de producto
	 * 
	 * @param e
	 * @return
	 */

	public StreamedContent getStreamedContent() {
		try {
			System.out.println("Ingreso a descargarPDF...");
			HttpServletRequest request = (HttpServletRequest) facesContext
					.getExternalContext().getRequest();
			String urlPath = request.getRequestURL().toString();
			urlPath = urlPath.substring(0, urlPath.length()
					- request.getRequestURI().length())
					+ request.getContextPath() + "/";
			System.out.println("urlPath >> " + urlPath);

			String pUsuario = usuarioSession.getLogin();
			/*DateFormat fecha = new SimpleDateFormat("dd/MM/yyyy");*/
			String fechaini =obtenerFormatoYYYYMMDD(this.fechaIni); 
			String fechafin =obtenerFormatoYYYYMMDD(this.fechaFin); 

			String urlPDFreporte = urlPath + "ReporteKardexProducto?pUsuario="
					+ pUsuario + "&pIdProducto="
					+ selectedAlmacenProducto.getIdProducto() 
					+ "&pfechaIni="	+ fechaini 
					+ "&pfechaFin=" + fechafin 
					+ "&pIdAlmacen="+ this.almacen.getId();
			// &pRutaImagen="+pRutaImagen
			System.out.println("URL Reporte PDF: " + urlPDFreporte);

			URL url = new URL(urlPDFreporte);

			// Read the PDF from the URL and save to a local file
			InputStream is1 = url.openStream();
			File f = stream2file(is1);
			System.out.println("Size Bytes: " + f.length());
			InputStream stream = new FileInputStream(f);
			streamedContent = new DefaultStreamedContent(stream,
					"application/pdf", "ReporteKardexProducto.pdf");
			setFile(new DefaultStreamedContent(stream, "application/pdf",
					"ReporteKardexProducto.pdf"));
			return streamedContent;
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
			System.out.println("Error en descargarPDF: " + e.getMessage());
			return null;
		}
	}
	

	 public String obtenerFormatoYYYYMMDD(Date date){
	    	try {
	    		String DATE_FORMAT = "yyyyMMdd";
				SimpleDateFormat sdf = new SimpleDateFormat(DATE_FORMAT);
	    		return sdf.format(date);
	    		
			} catch (Exception e) {
				// TODO: handle exception
				System.out.println("Error en obtenerFormatoYYYYMMDD: "+e.getMessage());
				return null;
			}
	 }
	 
	
	 public String obtenerFormatoDDMMYYYY(Date date){
	    	try {
	    		String DATE_FORMAT = "dd/MM/yyyy";
				SimpleDateFormat sdf = new SimpleDateFormat(DATE_FORMAT);
	    		return sdf.format(date);
	    		
			} catch (Exception e) {
				// TODO: handle exception
				System.out.println("Error en obtenerFormatoDDMMYYYY: "+e.getMessage());
				return null;
			}
	 }


	public String mesLiteralToNumber(String mesLiteral) {
		String mesNumber = "";
		switch (mesLiteral) {
		case "ene":
			mesNumber = "01";
			break;
		case "feb":
			mesNumber = "02";
			break;
		case "mar":
			mesNumber = "03";
			break;
		case "abr":
			mesNumber = "04";
			break;
		case "may":
			mesNumber = "05";
			break;
		case "jun":
			mesNumber = "06";
			break;
		case "jul":
			mesNumber = "07";
			break;
		case "ago":
			mesNumber = "08";
			break;
		case "sep":
			mesNumber = "09";
			break;
		case "oct":
			mesNumber = "10";
			break;
		case "nov":
			mesNumber = "11";
			break;
		case "dic":
			mesNumber = "12";
			break;
		default:
			break;
		}

		return mesNumber;
	}


	private static File stream2file(InputStream in) throws IOException {

		final File tempFile = File.createTempFile("ReporteVentas", ".pdf");
		tempFile.deleteOnExit();

		try (FileOutputStream out = new FileOutputStream(tempFile)) {
			IOUtils.copy(in, out);
		}

		return tempFile;
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
	public List<StructureCardexProducto> getListaCardexProducto() {
		return listaCardexProducto;
	}

	public void setListaCardexProducto(List<StructureCardexProducto> listaCardex) {
		this.listaCardexProducto = listaCardex;
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

	public StructureCatalogoPrecios getSelectedAlmacenProducto() {
		return selectedAlmacenProducto;
	}

	public void setSelectedAlmacenProducto(
			StructureCatalogoPrecios selectedAlmacenProducto) {
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

	public List<TipoProducto> getListTipoProducto() {
		return listTipoProducto;
	}

	public void setListTipoProducto(List<TipoProducto> listTipoProducto) {
		this.listTipoProducto = listTipoProducto;
	}

	public TipoProducto getTipoProducto() {
		return tipoProducto;
	}

	public void setTipoProducto(TipoProducto tipoProducto) {
		this.tipoProducto = tipoProducto;
	}

	public List<StructureCatalogoPrecios> getListAlmacenProducto() {
		return listAlmacenProducto;
	}

	public void setListAlmacenProducto(
			List<StructureCatalogoPrecios> listAlmacenProducto) {
		this.listAlmacenProducto = listAlmacenProducto;
	}

	public StreamedContent getFile() {
		return file;
	}

	public void setFile(StreamedContent file) {
		this.file = file;
	}
}
