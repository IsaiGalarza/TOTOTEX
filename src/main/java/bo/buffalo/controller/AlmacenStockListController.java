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
import bo.buffalo.data.UsuarioRepository;
import bo.buffalo.model.AlmProducto;
import bo.buffalo.model.Almacen;
import bo.buffalo.model.Producto;
import bo.buffalo.model.ProductoProveedor;
import bo.buffalo.model.Usuario;
import bo.buffalo.structure.StructureCatalogoPrecios;

@Named(value = "almacenStockListController")
@ConversationScoped
public class AlmacenStockListController implements Serializable {

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
	UsuarioRepository usuarioRepository;
	private Usuario usuarioSession;

	@Inject
	@Push(topic = PUSH_CDI_TOPIC)
	Event<String> pushEventSucursal;

	private String tituloPanel = "Registrar AlmProducto";
	private AlmProducto selectedAlmProducto;
	private AlmProducto newAlmProducto;

	private List<AlmProducto> listaAlmacenStock = new ArrayList<AlmProducto>();
	@Inject
	private AlmacenProductoRepository almacenProductoRepository;

	@Inject
	private AlmacenRepository almacenRepository;

	private AlmProducto selectedCatalogo;

	private List<Almacen> listAlmacen = new ArrayList<Almacen>();
	private Almacen almacen = new Almacen();

	private List<StructureCatalogoPrecios> listCatalogoResumen = new ArrayList<StructureCatalogoPrecios>();

	private String estado = "DETALLADO";

	@Produces
	@Named
	public List<AlmProducto> getListaAlmacenStock() {
		return listaAlmacenStock;
	}

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

		listAlmacen = almacenRepository.traerAlmacenActivas();

	}
	
	public Producto obtenerProducto(int productoID){
		return em.find(Producto.class, productoID);
	}

	public void selectAlmacen() {
		try {
			System.out.println("Ingreso a selectAlmacen");
			almacen = em.find(Almacen.class, almacen.getId());
			if (estado.equals("DETALLADO")) {
				listaAlmacenStock = almacenProductoRepository
						.findProductosForAlmacen(almacen);
			} else {
				setListCatalogoResumen(almacenProductoRepository
						.findResumenProductosForAlmacen(almacen));
			}
			System.out.println("size: " + listaAlmacenStock.size());
		} catch (Exception e) {
			System.out.println("Error en selectAlmacen : " + e.getMessage());
		}

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

	// SELECT PRESENTACION CLICK
	public void onRowSelectAlmProductoClick(SelectEvent event) {
		try {
			AlmProducto pedido = (AlmProducto) event.getObject();
			System.out
					.println("onRowSelectAlmProductoClick  " + pedido.getId());
			selectedAlmProducto = pedido;
			newAlmProducto = em.find(AlmProducto.class, pedido.getId());
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
	
	 private StreamedContent file;
	
	 private StreamedContent streamedContent;
	 
	 public StreamedContent getStreamedContent(){
	    	try {
				System.out.println("Ingreso a descargarPDF...");
				HttpServletRequest request = (HttpServletRequest) facesContext.getExternalContext().getRequest();  
				String urlPath = request.getRequestURL().toString();
				urlPath = urlPath.substring(0, urlPath.length() - request.getRequestURI().length()) + request.getContextPath() + "/";
				System.out.println("urlPath >> "+urlPath);
	
//				String pEstadoString = "%";
				String pUsuario = usuarioSession.getLogin();
				int pIdAlmacen = almacen.getId();
				String pOpcion = this.estado;
					
				String urlPDFreporte = urlPath+"ReporteInventarioAlmacen?pIdAlmacen="+pIdAlmacen+ "&pUsuario="+pUsuario+ "&pEstado="+pOpcion;
				System.out.println("URL Reporte PDF: "+urlPDFreporte);
				
				URL url = new URL(urlPDFreporte);
							
				// Read the PDF from the URL and save to a local file
				InputStream is1 = url.openStream();
				File f = stream2file(is1);
				System.out.println("Size Bytes: "+f.length());
				InputStream stream = new FileInputStream(f);  
				streamedContent = new DefaultStreamedContent(stream, "application/pdf", "ReporteInventarioAlmacen.pdf");
				setFile(new DefaultStreamedContent(stream, "application/pdf", "ReporteInventarioAlmacen.pdf"));
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
	 
	public String getTituloPanel() {
		return tituloPanel;
	}

	public void setTituloPanel(String tituloPanel) {
		this.tituloPanel = tituloPanel;
	}

	public AlmProducto getSelectedAlmProducto() {
		return selectedAlmProducto;
	}

	public void setSelectedAlmProducto(AlmProducto selectedAlmProducto) {
		this.selectedAlmProducto = selectedAlmProducto;
	}

	public AlmProducto getNewAlmProducto() {
		return newAlmProducto;
	}

	public void setNewAlmProducto(AlmProducto newAlmProducto) {
		this.newAlmProducto = newAlmProducto;
	}

	public AlmProducto getSelectedCatalogo() {
		return selectedCatalogo;
	}

	public void setSelectedCatalogo(AlmProducto selectedCatalogo) {
		this.selectedCatalogo = selectedCatalogo;
	}

	public List<Almacen> getListAlmacen() {
		return listAlmacen;
	}

	public void setListAlmacen(List<Almacen> listAlmacen) {
		this.listAlmacen = listAlmacen;
	}

	public Almacen getAlmacen() {
		return almacen;
	}

	public void setAlmacen(Almacen almacen) {
		this.almacen = almacen;
	}

	public List<StructureCatalogoPrecios> getListCatalogoResumen() {
		return listCatalogoResumen;
	}

	public void setListCatalogoResumen(
			List<StructureCatalogoPrecios> listCatalogoResumen) {
		this.listCatalogoResumen = listCatalogoResumen;
	}

	public String getEstado() {
		return estado;
	}

	public void setEstado(String estado) {
		this.estado = estado;
	}

	public StreamedContent getFile() {
		return file;
	}

	public void setFile(StreamedContent file) {
		this.file = file;
	}

}
