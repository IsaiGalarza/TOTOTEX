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
import javax.faces.context.FacesContext;
import javax.inject.Inject;
import javax.inject.Named;
import javax.persistence.EntityManager;
import javax.servlet.http.HttpServletRequest;

import org.apache.commons.io.IOUtils;
import org.primefaces.model.DefaultStreamedContent;
import org.primefaces.model.StreamedContent;
import org.richfaces.cdi.push.Push;

import bo.buffalo.data.FacturaRepository;
import bo.buffalo.data.SucursalRepository;
import bo.buffalo.data.UsuarioRepository;
import bo.buffalo.model.Empresa;
import bo.buffalo.model.Sucursal;
import bo.buffalo.model.Usuario;


@Named(value="exportacionController")
@ConversationScoped
public class ExportacionController implements Serializable{

	private static final long serialVersionUID = 2065838514008187936L;

	public static final String PUSH_CDI_TOPIC = "pushCdi";

    @Inject
    private FacesContext facesContext;
    
    @Inject
    private EntityManager em;
    
    @Inject 
	Conversation conversation;
    
    @Inject
    FacturaRepository facturaRepository;
    
    @Inject 
    SucursalRepository sucursalRepository;
    
    @Inject 
    UsuarioRepository usuarioRepository;

	private Usuario usuarioSession;
    private Empresa empresa;
    
    @Inject
    @Push(topic = PUSH_CDI_TOPIC)
    Event<String> pushEventSucursal;
    
    //parametros filtros
    private int sucursalID;
    private String mes = obtenerMes(new Date());
    private String gestion = obtenerGestion(new Date());
    private Integer numeroFolio;

    private List<String> listaGestiones = new ArrayList<String>();
    private List<Sucursal> listaSucursales = new ArrayList<Sucursal>();
    
    
	//reporte LIBRO VENTAS
	private StreamedContent streamedLibroVentas; //OK PDF
	private StreamedContent streamedLibroVentasSFV; //OK PDF
	
	private StreamedContent streamedLibroVentasDaVinci; //OK EXCEL
	private StreamedContent streamedLibroVentasSFVExcel; //OK EXCEL
	
	private StreamedContent streamedLibroVentasTXT; // TXT PENDIENTE
	
	
	//reporte LIBRO COMPRAS
	private StreamedContent streamedLibroCompras; //PDF
	private StreamedContent streamedLibroComprasSFV; //PDF
	
	private StreamedContent streamedLibroComprasDaVinci; //EXCEL
	private StreamedContent streamedLibroComprasSFVExcel; //EXCEL
	
	private StreamedContent streamedLibroComprasTXT; //TXT
	
	
	//vista previa Ventas
	private String urlVentasNSF;
	private String urlVentasSFV;
	
	//vista previa compras
	private String urlComprasNSF;
	private String urlComprasSFV;
	
	
	@PostConstruct
    public void initNewExportacion() {
		
		// initConversation();
    	beginConversation();
    	
    	HttpServletRequest request = (HttpServletRequest) facesContext
				.getExternalContext().getRequest();
		System.out.println("init NewProforma*********************************");
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
		
		//indicar sucursal x defecto
		sucursalID = usuarioSession.getSucursal().getId();
		
		empresa = em.find(Empresa.class, usuarioSession.getSucursal().getEmpresa().getId());
		
		//actualizar lista de sucursales
		listaSucursales.clear();
		listaSucursales = sucursalRepository.traerSucursalesActivas();
		
		//actualizar gestiones disponibles facturadas
		listaGestiones = facturaRepository.traerGestionesFacturadas();
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
	
	
	public Sucursal getSucursal(int sucursalId){
		try {
			return em.find(Sucursal.class, sucursalId);
		} catch (Exception e) {
			// TODO: handle exception
			System.out.println("Error en getSucursal: "+e.getMessage());
			return null;
		}
	}
	
	private static File stream2file (InputStream in) throws IOException {            
		
	    final File tempFile = File.createTempFile("Reporte", ".pdf");
	    tempFile.deleteOnExit();
	
	    try (FileOutputStream out = new FileOutputStream(tempFile)) {
	        IOUtils.copy(in, out);
	    }
	
	    return tempFile;            
	}
	
	private static File stream2fileTXT (InputStream in) throws IOException {            
		
	    final File tempFile = File.createTempFile("Reporte", ".txt");
	    tempFile.deleteOnExit();
	
	    try (FileOutputStream out = new FileOutputStream(tempFile)) {
	        IOUtils.copy(in, out);
	    }
	
	    return tempFile;            
	}
	
	private static File stream2fileExcel(InputStream in) throws IOException {            
		
	    final File tempFile = File.createTempFile("Reporte", ".xls");
	    tempFile.deleteOnExit();
	
	    try (FileOutputStream out = new FileOutputStream(tempFile)) {
	        IOUtils.copy(in, out);
	    }
	
	    return tempFile;            
	}
	
	public StreamedContent getStreamedLibroVentas() {
		try {
			System.out.println("Ingreso a descargar LibroVentasNotariado...");
			HttpServletRequest request = (HttpServletRequest) facesContext.getExternalContext().getRequest();  
			String urlPath = request.getRequestURL().toString();
			urlPath = urlPath.substring(0, urlPath.length() - request.getRequestURI().length()) + request.getContextPath() + "/";
			System.out.println("urlPath >> "+urlPath);
			
			//http://localhost:8080/buffalo/ReporteLibroVentasNotariado?pGestion=2015&pMes=01&pSucursal=3
			String urlPDFreporte = urlPath+"ReporteLibroVentasNotariado?pGestion="+this.getGestion()+"&pMes="+this.getMes()+"&pSucursal="+this.getSucursalID()+"&pFolio="+this.getNumeroFolio();
			System.out.println("URL Reporte PDF: "+urlPDFreporte);
			
			URL url = new URL(urlPDFreporte);
						
			// Read the PDF from the URL and save to a local file
			InputStream is1 = url.openStream();
			File f = stream2file(is1);
			System.out.println("Size Bytes: "+f.length());
			InputStream stream = new FileInputStream(f);  
			streamedLibroVentas = new DefaultStreamedContent(stream, "application/pdf", "LibroVentasNotariado.pdf");
			return streamedLibroVentas;
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
			System.out.println("Error en getStreamedLibroVentas: "+e.getMessage());
			return null;
		}
	}
	
	public String obtenerMes(Date fecha){
    	try {
    		String DATE_FORMAT = "MM";
			SimpleDateFormat sdf = new SimpleDateFormat(DATE_FORMAT);
    		return sdf.format(fecha);
    		
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
			System.out.println("Error en obtenerMes: "+e.getMessage());
			return null;
		}
	}
	
	public String obtenerGestion(Date fecha){
    	try {
    		String DATE_FORMAT = "yyyy";
			SimpleDateFormat sdf = new SimpleDateFormat(DATE_FORMAT);
    		return sdf.format(fecha);
    		
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
			System.out.println("Error en obtenerGestion: "+e.getMessage());
			return null;
		}
	}


	public void setStreamedLibroVentas(StreamedContent streamedLibroVentas) {
		this.streamedLibroVentas = streamedLibroVentas;
	}


	public int getSucursalID() {
		return sucursalID;
	}


	public void setSucursalID(int sucursalID) {
		this.sucursalID = sucursalID;
	}


	public String getMes() {
		return mes;
	}


	public void setMes(String mes) {
		this.mes = mes;
	}


	public String getGestion() {
		return gestion;
	}


	public void setGestion(String gestion) {
		this.gestion = gestion;
	}


	public List<Sucursal> getListaSucursales() {
		return listaSucursales;
	}


	public void setListaSucursales(List<Sucursal> listaSucursales) {
		this.listaSucursales = listaSucursales;
	}


	public List<String> getListaGestiones() {
		return listaGestiones;
	}


	public void setListaGestiones(List<String> listaGestiones) {
		this.listaGestiones = listaGestiones;
	}


	public Integer getNumeroFolio() {
		return numeroFolio;
	}


	public void setNumeroFolio(Integer numeroFolio) {
		this.numeroFolio = numeroFolio;
	}

	public StreamedContent getStreamedLibroCompras() {
		
		try {
			System.out.println("Ingreso a descargar getStreamedLibroCompras...");
			HttpServletRequest request = (HttpServletRequest) facesContext.getExternalContext().getRequest();  
			String urlPath = request.getRequestURL().toString();
			urlPath = urlPath.substring(0, urlPath.length() - request.getRequestURI().length()) + request.getContextPath() + "/";
			System.out.println("urlPath >> "+urlPath);
			
			//http://localhost:8080/buffalo/ReporteLibroComprasNotariado?pGestion=2015&pMes=01&pSucursal=3
			String urlPDFreporte = urlPath+"ReporteLibroComprasNotariado?pGestion="+this.getGestion()+"&pMes="+this.getMes()+"&pSucursal="+this.getSucursalID()+"&pFolio="+this.getNumeroFolio();
			System.out.println("URL Reporte PDF: "+urlPDFreporte);
			
			URL url = new URL(urlPDFreporte);
						
			// Read the PDF from the URL and save to a local file
			InputStream is1 = url.openStream();
			File f = stream2file(is1);
			System.out.println("Size Bytes: "+f.length());
			InputStream stream = new FileInputStream(f);  
			streamedLibroCompras = new DefaultStreamedContent(stream, "application/pdf", "LibroComprasNotariado.pdf");
			return streamedLibroCompras;
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
			System.out.println("Error en getStreamedLibroCompras: "+e.getMessage());
			return null;
		}
		
	}


	public void setStreamedLibroCompras(StreamedContent streamedLibroCompras) {
		this.streamedLibroCompras = streamedLibroCompras;
	}


	public StreamedContent getStreamedLibroVentasDaVinci() {
		
		try {
			System.out.println("Ingreso a descargar getStreamedLibroVentasDaVinci...");
			HttpServletRequest request = (HttpServletRequest) facesContext.getExternalContext().getRequest();  
			String urlPath = request.getRequestURL().toString();
			urlPath = urlPath.substring(0, urlPath.length() - request.getRequestURI().length()) + request.getContextPath() + "/";
			System.out.println("urlPath >> "+urlPath);
			
			//http://localhost:8080/buffalo/ReporteLibroVentasDaVinci?pGestion=2015&pMes=01
			String urlPDFreporte = urlPath+"ReporteLibroVentasDaVinci?pGestion="+this.getGestion()+"&pMes="+this.getMes();
			System.out.println("URL Reporte PDF: "+urlPDFreporte);
			
			URL url = new URL(urlPDFreporte);
						
			// Read the PDF from the URL and save to a local file
			InputStream is1 = url.openStream();
			File f = stream2fileExcel(is1);
			System.out.println("Size Bytes: "+f.length());
			InputStream stream = new FileInputStream(f);  
			streamedLibroVentasDaVinci = new DefaultStreamedContent(stream, "application/vnd.ms-excel", "LibroVentasDaVinci.xls");
			return streamedLibroVentasDaVinci;
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
			System.out.println("Error en getStreamedLibroVentasDaVinci: "+e.getMessage());
			return null;
		}
		
	}


	public void setStreamedLibroVentasDaVinci(StreamedContent streamedLibroVentasDaVinci) {
		this.streamedLibroVentasDaVinci = streamedLibroVentasDaVinci;
	}


	public StreamedContent getStreamedLibroVentasTXT() {
		
		try {
			System.out.println("Ingreso a descargar getStreamedLibroVentasTXT...");
			HttpServletRequest request = (HttpServletRequest) facesContext.getExternalContext().getRequest();  
			String urlPath = request.getRequestURL().toString();
			urlPath = urlPath.substring(0, urlPath.length() - request.getRequestURI().length()) + request.getContextPath() + "/";
			System.out.println("urlPath >> "+urlPath);
			
			//http://localhost:8080/buffalo/ReporteLibroVentasTXT?pGestion=2015&pMes=01
			String urlPDFreporte = urlPath+"ReporteLibroVentasTXT?pGestion="+this.getGestion()+"&pMes="+this.getMes();
			System.out.println("URL Reporte PDF: "+urlPDFreporte);
			
			URL url = new URL(urlPDFreporte);
						
			// Read the PDF from the URL and save to a local file
			InputStream is1 = url.openStream();
			File f = stream2fileTXT(is1);
			System.out.println("Size Bytes: "+f.length());
			InputStream stream = new FileInputStream(f);  
			//ventas_mmaaaa_9999999999.TXT
			streamedLibroVentasTXT = new DefaultStreamedContent(stream, "text/plain", "ventas_"+this.getMes()+this.getGestion()+"_"+empresa.getNit()+".txt");
			return streamedLibroVentasTXT;
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
			System.out.println("Error en getStreamedLibroVentasTXT: "+e.getMessage());
			return null;
		}
		
	}


	public void setStreamedLibroVentasTXT(StreamedContent streamedLibroVentasTXT) {
		this.streamedLibroVentasTXT = streamedLibroVentasTXT;
	}


	public StreamedContent getStreamedLibroVentasSFV() {
		
		try {
			System.out.println("Ingreso a descargar getStreamedLibroVentasSFV...");
			HttpServletRequest request = (HttpServletRequest) facesContext.getExternalContext().getRequest();  
			String urlPath = request.getRequestURL().toString();
			urlPath = urlPath.substring(0, urlPath.length() - request.getRequestURI().length()) + request.getContextPath() + "/";
			System.out.println("urlPath >> "+urlPath);
			
			//http://localhost:8080/buffalo/ReporteLibroVentasNotariado?pGestion=2015&pMes=01&pSucursal=3
			String urlPDFreporte = urlPath+"ReporteLibroVentasSFV?pGestion="+this.getGestion()+"&pMes="+this.getMes();
			System.out.println("URL Reporte PDF: "+urlPDFreporte);
			
			URL url = new URL(urlPDFreporte);
						
			// Read the PDF from the URL and save to a local file
			InputStream is1 = url.openStream();
			File f = stream2file(is1);
			System.out.println("Size Bytes: "+f.length());
			InputStream stream = new FileInputStream(f);  
			streamedLibroVentasSFV = new DefaultStreamedContent(stream, "application/pdf", "LibroVentasSFV.pdf");
			return streamedLibroVentasSFV;
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
			System.out.println("Error en getStreamedLibroVentasSFV: "+e.getMessage());
			return null;
		}
		
	}


	public void setStreamedLibroVentasSFV(StreamedContent streamedLibroVentasSFV) {
		this.streamedLibroVentasSFV = streamedLibroVentasSFV;
	}


	public StreamedContent getStreamedLibroComprasDaVinci() {
		
		try {
			System.out.println("Ingreso a descargar getStreamedLibroComprasDaVinci...");
			HttpServletRequest request = (HttpServletRequest) facesContext.getExternalContext().getRequest();  
			String urlPath = request.getRequestURL().toString();
			urlPath = urlPath.substring(0, urlPath.length() - request.getRequestURI().length()) + request.getContextPath() + "/";
			System.out.println("urlPath >> "+urlPath);
			
			//http://localhost:8080/buffalo/ReporteLibroComprasDaVinci?pGestion=2015&pMes=01
			String urlPDFreporte = urlPath+"ReporteLibroComprasDaVinci?pGestion="+this.getGestion()+"&pMes="+this.getMes();
			System.out.println("URL Reporte PDF: "+urlPDFreporte);
			
			URL url = new URL(urlPDFreporte);
						
			// Read the PDF from the URL and save to a local file
			InputStream is1 = url.openStream();
			File f = stream2fileExcel(is1);
			System.out.println("Size Bytes: "+f.length());
			InputStream stream = new FileInputStream(f);  
			streamedLibroComprasDaVinci = new DefaultStreamedContent(stream, "application/vnd.ms-excel", "LibroComprasDaVinci.xls");
			return streamedLibroComprasDaVinci;
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
			System.out.println("Error en getStreamedLibroComprasDaVinci: "+e.getMessage());
			return null;
		}
		
	}


	public void setStreamedLibroComprasDaVinci(
			StreamedContent streamedLibroComprasDaVinci) {
		this.streamedLibroComprasDaVinci = streamedLibroComprasDaVinci;
	}


	public StreamedContent getStreamedLibroComprasTXT() {
		
		try {
			System.out.println("Ingreso a descargar getStreamedLibroComprasTXT...");
			HttpServletRequest request = (HttpServletRequest) facesContext.getExternalContext().getRequest();  
			String urlPath = request.getRequestURL().toString();
			urlPath = urlPath.substring(0, urlPath.length() - request.getRequestURI().length()) + request.getContextPath() + "/";
			System.out.println("urlPath >> "+urlPath);
			
			//http://localhost:8080/buffalo/ReporteLibroComprasTXT?pGestion=2015&pMes=01
			String urlPDFreporte = urlPath+"ReporteLibroComprasTXT?pGestion="+this.getGestion()+"&pMes="+this.getMes();
			System.out.println("URL Reporte PDF: "+urlPDFreporte);
			
			URL url = new URL(urlPDFreporte);
						
			// Read the PDF from the URL and save to a local file
			InputStream is1 = url.openStream();
			File f = stream2fileTXT(is1);
			System.out.println("Size Bytes: "+f.length());
			InputStream stream = new FileInputStream(f);  
			//compras_mmaaaa_9999999999.TXT
			streamedLibroComprasTXT = new DefaultStreamedContent(stream, "text/plain", "compras_"+this.getMes()+this.getGestion()+"_"+empresa.getNit()+".txt");
			return streamedLibroComprasTXT;
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
			System.out.println("Error en getStreamedLibroComprasTXT: "+e.getMessage());
			return null;
		}
		
	}


	public void setStreamedLibroComprasTXT(StreamedContent streamedLibroComprasTXT) {
		this.streamedLibroComprasTXT = streamedLibroComprasTXT;
	}


	public StreamedContent getStreamedLibroComprasSFV() {
		
		try {
			System.out.println("Ingreso a descargar getStreamedLibroComprasSFV...");
			HttpServletRequest request = (HttpServletRequest) facesContext.getExternalContext().getRequest();  
			String urlPath = request.getRequestURL().toString();
			urlPath = urlPath.substring(0, urlPath.length() - request.getRequestURI().length()) + request.getContextPath() + "/";
			System.out.println("urlPath >> "+urlPath);
			
			//http://localhost:8080/buffalo/ReporteLibroComprasSFV?pGestion=2015&pMes=01
			String urlPDFreporte = urlPath+"ReporteLibroComprasSFV?pGestion="+this.getGestion()+"&pMes="+this.getMes();
			System.out.println("URL Reporte PDF: "+urlPDFreporte);
			
			URL url = new URL(urlPDFreporte);
						
			// Read the PDF from the URL and save to a local file
			InputStream is1 = url.openStream();
			File f = stream2file(is1);
			System.out.println("Size Bytes: "+f.length());
			InputStream stream = new FileInputStream(f);  
			streamedLibroComprasSFV = new DefaultStreamedContent(stream, "application/pdf", "LibroComprasSFV.pdf");
			return streamedLibroComprasSFV;
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
			System.out.println("Error en getStreamedLibroComprasSFV: "+e.getMessage());
			return null;
		}
	
	}


	public void setStreamedLibroComprasSFV(StreamedContent streamedLibroComprasSFV) {
		this.streamedLibroComprasSFV = streamedLibroComprasSFV;
	}


	public StreamedContent getStreamedLibroVentasSFVExcel() {
		
		try {
			System.out.println("Ingreso a descargar getStreamedLibroVentasSFVExcel...");
			HttpServletRequest request = (HttpServletRequest) facesContext.getExternalContext().getRequest();  
			String urlPath = request.getRequestURL().toString();
			urlPath = urlPath.substring(0, urlPath.length() - request.getRequestURI().length()) + request.getContextPath() + "/";
			System.out.println("urlPath >> "+urlPath);
			
			//http://localhost:8080/buffalo/ReporteLibroVentasSFVExcel?pGestion=2015&pMes=01
			String urlPDFreporte = urlPath+"ReporteLibroVentasSFVExcel?pGestion="+this.getGestion()+"&pMes="+this.getMes();
			System.out.println("URL Reporte PDF: "+urlPDFreporte);
			
			URL url = new URL(urlPDFreporte);
						
			// Read the PDF from the URL and save to a local file
			InputStream is1 = url.openStream();
			File f = stream2fileExcel(is1);
			System.out.println("Size Bytes: "+f.length());
			InputStream stream = new FileInputStream(f);  
			streamedLibroVentasSFVExcel = new DefaultStreamedContent(stream, "application/vnd.ms-excel", "LibroVentasSFV.xls");
			return streamedLibroVentasSFVExcel;
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
			System.out.println("Error en getStreamedLibroVentasSFVExcel: "+e.getMessage());
			return null;
		}
	
	}


	public void setStreamedLibroVentasSFVExcel(
			StreamedContent streamedLibroVentasSFVExcel) {
		this.streamedLibroVentasSFVExcel = streamedLibroVentasSFVExcel;
	}


	public StreamedContent getStreamedLibroComprasSFVExcel() {
		
		try {
			System.out.println("Ingreso a descargar getStreamedLibroComprasSFVExcel...");
			HttpServletRequest request = (HttpServletRequest) facesContext.getExternalContext().getRequest();  
			String urlPath = request.getRequestURL().toString();
			urlPath = urlPath.substring(0, urlPath.length() - request.getRequestURI().length()) + request.getContextPath() + "/";
			System.out.println("urlPath >> "+urlPath);
			
			//http://localhost:8080/buffalo/ReporteLibroComprasSFVExcel?pGestion=2015&pMes=01
			String urlPDFreporte = urlPath+"ReporteLibroComprasSFVExcel?pGestion="+this.getGestion()+"&pMes="+this.getMes();
			System.out.println("URL Reporte PDF: "+urlPDFreporte);
			
			URL url = new URL(urlPDFreporte);
						
			// Read the PDF from the URL and save to a local file
			InputStream is1 = url.openStream();
			File f = stream2fileExcel(is1);
			System.out.println("Size Bytes: "+f.length());
			InputStream stream = new FileInputStream(f);  
			streamedLibroComprasSFVExcel = new DefaultStreamedContent(stream, "application/vnd.ms-excel", "LibroComprasSFV.xls");
			return streamedLibroComprasSFVExcel;
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
			System.out.println("Error en getStreamedLibroComprasSFVExcel: "+e.getMessage());
			return null;
		}
		
	}


	public void setStreamedLibroComprasSFVExcel(
			StreamedContent streamedLibroComprasSFVExcel) {
		this.streamedLibroComprasSFVExcel = streamedLibroComprasSFVExcel;
	}
	
	
	public void armarURLVentasNSF(){
		try {
			System.out.println("Ingreso a armarURLVentasNSF...");
			HttpServletRequest request = (HttpServletRequest) facesContext.getExternalContext().getRequest();  
			String urlPath = request.getRequestURL().toString();
			urlPath = urlPath.substring(0, urlPath.length() - request.getRequestURI().length()) + request.getContextPath() + "/";
			System.out.println("urlPath >> "+urlPath);
			
			//http://localhost:8080/buffalo/ReporteLibroVentasNotariado?pGestion=2015&pMes=01&pSucursal=3
			urlVentasNSF = urlPath+"ReporteLibroVentasNotariado?pGestion="+this.getGestion()+"&pMes="+this.getMes()+"&pSucursal="+this.getSucursalID()+"&pFolio="+this.getNumeroFolio();
			System.out.println("URL Reporte urlVentasNSF: "+urlVentasNSF);
			//http://localhost:8080/buffalo/ReporteLibroVentasNotariado?pGestion=2015&pMes=01&pSucursal=3

		} catch (Exception e) {
			// TODO: handle exception
			System.out.println("Error en armarURLVentasNSF: "+e.getMessage());
		}
	}
	
	public void armarURLComprasNSF(){
		try {
			System.out.println("Ingreso a armarURLComprasNSF...");
			HttpServletRequest request = (HttpServletRequest) facesContext.getExternalContext().getRequest();  
			String urlPath = request.getRequestURL().toString();
			urlPath = urlPath.substring(0, urlPath.length() - request.getRequestURI().length()) + request.getContextPath() + "/";
			System.out.println("urlPath >> "+urlPath);
			
			//http://localhost:8080/buffalo/ReporteLibroComprasNotariado?pGestion=2015&pMes=01&pSucursal=3
			urlComprasNSF = urlPath+"ReporteLibroComprasNotariado?pGestion="+this.getGestion()+"&pMes="+this.getMes()+"&pSucursal="+this.getSucursalID()+"&pFolio="+this.getNumeroFolio();
			System.out.println("URL Reporte urlComprasNSF: "+urlComprasNSF);

		} catch (Exception e) {
			// TODO: handle exception
			System.out.println("Error en armarURLComprasNSF: "+e.getMessage());
		}
	}
	
	public void armarURLVentasSFV(){
		try {
			System.out.println("Ingreso a armarURLVentasSFV...");
			HttpServletRequest request = (HttpServletRequest) facesContext.getExternalContext().getRequest();  
			String urlPath = request.getRequestURL().toString();
			urlPath = urlPath.substring(0, urlPath.length() - request.getRequestURI().length()) + request.getContextPath() + "/";
			System.out.println("urlPath >> "+urlPath);
			
			urlVentasSFV = urlPath+"ReporteLibroVentasSFV?pGestion="+this.getGestion()+"&pMes="+this.getMes();
			System.out.println("URL Reporte urlVentasSFV: "+urlVentasNSF);

		} catch (Exception e) {
			// TODO: handle exception
			System.out.println("Error en armarURLVentasSFV: "+e.getMessage());
		}
	}
	
	
	public void armarURLComprasSFV(){
		try {
			System.out.println("Ingreso a descargar getStreamedLibroComprasSFV...");
			HttpServletRequest request = (HttpServletRequest) facesContext.getExternalContext().getRequest();  
			String urlPath = request.getRequestURL().toString();
			urlPath = urlPath.substring(0, urlPath.length() - request.getRequestURI().length()) + request.getContextPath() + "/";
			System.out.println("urlPath >> "+urlPath);
			
			//http://localhost:8080/buffalo/ReporteLibroComprasSFV?pGestion=2015&pMes=01
			urlComprasSFV = urlPath+"ReporteLibroComprasSFV?pGestion="+this.getGestion()+"&pMes="+this.getMes();
			System.out.println("URL Reporte urlVentasSFV: "+urlVentasNSF);
			

		} catch (Exception e) {
			// TODO: handle exception
			System.out.println("Error en armarURLComprasSFV: "+e.getMessage());
		}
	}
	
	
	
	
	
	

	public String getUrlVentasNSF() {
		return urlVentasNSF;
	}


	public void setUrlVentasNSF(String urlVentasNSF) {
		this.urlVentasNSF = urlVentasNSF;
	}


	public String getUrlVentasSFV() {
		return urlVentasSFV;
	}


	public void setUrlVentasSFV(String urlVentasSFV) {
		this.urlVentasSFV = urlVentasSFV;
	}


	public String getUrlComprasNSF() {
		return urlComprasNSF;
	}


	public void setUrlComprasNSF(String urlComprasNSF) {
		this.urlComprasNSF = urlComprasNSF;
	}


	public String getUrlComprasSFV() {
		return urlComprasSFV;
	}


	public void setUrlComprasSFV(String urlComprasSFV) {
		this.urlComprasSFV = urlComprasSFV;
	}


	public Empresa getEmpresa() {
		return empresa;
	}


	public void setEmpresa(Empresa empresa) {
		this.empresa = empresa;
	}


	public Usuario getUsuarioSession() {
		return usuarioSession;
	}


	public void setUsuarioSession(Usuario usuarioSession) {
		this.usuarioSession = usuarioSession;
	}


}
