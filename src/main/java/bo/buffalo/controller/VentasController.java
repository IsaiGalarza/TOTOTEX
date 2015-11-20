package bo.buffalo.controller;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.Serializable;
import java.net.URL;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Properties;

import javax.annotation.PostConstruct;
import javax.enterprise.context.ConversationScoped;
import javax.enterprise.event.Event;
import javax.enterprise.inject.Produces;
import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
import javax.inject.Inject;
import javax.inject.Named;
import javax.naming.NamingException;
import javax.persistence.EntityManager;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JasperExportManager;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.JasperReport;
import net.sf.jasperreports.engine.JasperRunManager;
import net.sf.jasperreports.engine.util.JRLoader;

import org.apache.commons.io.IOUtils;
import org.primefaces.model.DefaultStreamedContent;
import org.primefaces.model.StreamedContent;
import org.richfaces.cdi.push.Push;

import bo.buffalo.data.FacturaRepository;
import bo.buffalo.data.UsuarioRepository;
import bo.buffalo.model.Factura;
import bo.buffalo.model.Sucursal;
import bo.buffalo.model.Usuario;

@Named(value="ventasController")
@ConversationScoped
public class VentasController implements Serializable{

	private static final long serialVersionUID = 8338389839784358081L;

	public static final String PUSH_CDI_TOPIC = "pushCdi";

    @Inject
    private FacesContext facesContext;
   
	@Inject
    private EntityManager em;
    
    @Inject
    private FacturaRepository facturaRepository;
    
    @Inject
    @Push(topic = PUSH_CDI_TOPIC)
    Event<String> pushEventSucursal;
    
    @Inject 
    UsuarioRepository usuarioRepository;
    

    
    private Usuario usuarioSession;
    
    
    private StreamedContent file;  
    
  
      
    public StreamedContent getFile() {  
        return file;  
    }  
  
    public void setFile(StreamedContent file) {  
        this.file = file;  
    }  
    
    
    private int periodo;
    private int gestion;
    private Date fechaInicial = getPrimerDiaDelMes();
    private Date fechaFinal = getUltimoDiaDelMes();
	private String estadoFactura = "%";
	private int sucursalID=3;
	private String loginUser = "ygalarza";
	private Factura factura;
	
	private List<Factura> listaFacturas;
	private int cantidadVentas;
	private double totalFacturadoBolivianos;
	
	//Modal Export PDF
	private String estadoFacturaPDF;
	private String usuarioSucursalPDF;

    // @Named provides access the return value via the EL variable name "listaFacturas" in the UI (e.g.
    // Facelets or JSP view)
    @Produces
    @Named
    public List<Factura> getListaFacturas() {
        return listaFacturas;
    }
	
    HttpServletRequest request; 
    
	@PostConstruct
    public void initVentas() {
//		listaFacturas = facturaRepository.findAllOrderedByFechaRegistro();
		request = (HttpServletRequest) facesContext.getExternalContext().getRequest(); 
        System.out.println("initFacturacion*********************************");
        System.out.println("request.getClass().getName():"+request.getClass().getName());
        System.out.println("isVentas:"+request.isUserInRole("ventas"));
        System.out.println("remoteUser:"+request.getRemoteUser());
        System.out.println("userPrincipalName:"+(request.getUserPrincipal()==null?"null":request.getUserPrincipal().getName()));
        System.out.println("initFacturacion*********************************");       
    	
        usuarioSession = usuarioRepository.findByLogin(request.getUserPrincipal().getName());
        System.out.println("Sucursal Usuario: "+usuarioSession.getSucursal().getNombre());
        
//        InputStream stream = request.get ((ServletContext)FacesContext.getCurrentInstance().getExternalContext().getContext()).getResourceAsStream("/resources/demo/images/optimus.jpg");
//        file2 = new DefaultStreamedContent(stream, "image/jpg", "downloaded_optimus.jpg");
		
		
		listaFacturas = facturaRepository.buscarFacturasReporteVentas(this.getFechaInicial(), this.getFechaFinal(), this.getEstadoFactura(), usuarioSession.getSucursal().getId());
		System.out.println("Cantidad de Facturas Encontradas: "+listaFacturas.size());
    }
	
	public void iniciarModalExportarPDF(){
		try {
			System.out.println("Ingreso a iniciarModalExportarPDF");
			this.setUsuarioSucursalPDF("%");
			this.setEstadoFacturaPDF("%");
			
		} catch (Exception e) {
			// TODO: handle exception
			System.out.println("Error en iniciarModalExportarPDF: "+e.getMessage());
		}
	}
	
	public String obtenerFecha(Date fecha) {
		try {
			String DATE_FORMAT = "dd/MM/yyyy";
			SimpleDateFormat sdf = new SimpleDateFormat(DATE_FORMAT);
			return sdf.format(fecha);

		} catch (Exception e) {
			// TODO: handle exception
			System.out.println("Error en obtenerFecha: " + e.getMessage());
			return "NULL";
		}
	}
	
	 public static Date getPrimerDiaDelMes() {
	        Calendar cal = Calendar.getInstance();
	        cal.set(cal.get(Calendar.YEAR), 
	                cal.get(Calendar.MONTH),
	                cal.getActualMinimum(Calendar.DAY_OF_MONTH),
	                cal.getMinimum(Calendar.HOUR_OF_DAY),
	                cal.getMinimum(Calendar.MINUTE),
	                cal.getMinimum(Calendar.SECOND));
	        return cal.getTime();
	 }
	 
	 public static void main(String[] args){
//		 VentasController ventas = new VentasController();
//		 ventas.generarReportePDF();
	 }
	 
	 public String exportarPDF(){
		 
		 int fechaInicial = 20141201;
		 int fechaFinal = 20141203;
		 String estado = "V";
		 String estadoLiteral = "Valida";
		 
		 //Fill Map with params values
         HashMap<String, Object> hm = new HashMap<String, Object>(); 
         hm.put("pFechaInicial", fechaInicial);
         hm.put("pFechaFinal", fechaFinal);
         hm.put("pEstado", estado);
         hm.put("pUsuario", "ygalarza");
         hm.put("pDateInicial", "01/12/2014");
         hm.put("pDateFinal", "03/12/2014");
         hm.put("pEstadoString", estadoLiteral);
         hm.put("pSucursal", this.getSucursalID());
         hm.put("pUsuarioFacturacion", "ygalarza");

	      Properties props = new Properties();
	      Connection conexion = null;
	      try {
	        props.load(this.getClass().getClassLoader().getResourceAsStream("/bo/medipiel/util/db.properties"));
	            
	    	String url = props.getProperty("POSTGRE_DB_URL");
	        String user = props.getProperty("POSTGRE_DB_USERNAME");
	        String password = props.getProperty("POSTGRE_DB_PASSWORD");
	        String driver= props.getProperty("POSTGRE_DB_DRIVER_CLASS");
	            
	        Class.forName(driver);
	        conexion = DriverManager.getConnection(url,user, password);
	        	
	        if(conexion!=null){
	        	System.out.println("Conexion Exitosa JDBC com.edb.Driver...");
	        }else{
	        	System.out.println("Error Conexion JDBC com.edb.Driver...");
	        }
	        	
	      } catch (Exception e) {
				// TODO: handle exception
				System.out.println("Error al conectar JDBC: "+e.getMessage());
	      }
	     
	      try {
			conexion.setAutoCommit(true);
		} catch (SQLException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		 
         try {
			
        	 String reportPath = FacesContext.getCurrentInstance().getExternalContext().getRealPath("/resources/report/ReporteVentas.jasper");
             JasperPrint jasperPrint = JasperFillManager.fillReport(reportPath, hm, conexion);
             HttpServletResponse httpServletResponse = (HttpServletResponse) FacesContext.getCurrentInstance().getExternalContext().getResponse();
             httpServletResponse.addHeader("Content-disposition", "attachment; filename=report.pdf");
             ServletOutputStream servletOutputStream = httpServletResponse.getOutputStream();
             JasperExportManager.exportReportToPdfStream(jasperPrint, servletOutputStream);
             FacesContext.getCurrentInstance().responseComplete();
        	 
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}
         
         return "quit";
	 }
	 
	 public String viewReportPDF() throws SQLException, JRException, IOException, NamingException {
         
		 int fechaInicial = 20141201;
		 int fechaFinal = 20141203;
		 String estado = "V";
		 String estadoLiteral = "Valida";
		 
		 //Fill Map with params values
         HashMap<String, Object> hm = new HashMap<String, Object>(); 
         hm.put("pFechaInicial", fechaInicial);
         hm.put("pFechaFinal", fechaFinal);
         hm.put("pEstado", estado);
         hm.put("pUsuario", "ygalarza");
         hm.put("pDateInicial", "01/12/2014");
         hm.put("pDateFinal", "03/12/2014");
         hm.put("pEstadoString", estadoLiteral);
         hm.put("pSucursal", this.getSucursalID());
         hm.put("pUsuarioFacturacion", "ygalarza");

         
	       //Connect with local datasource
	      Properties props = new Properties();
	      Connection conexion = null;
	      try {
	        props.load(this.getClass().getClassLoader().getResourceAsStream("/bo/medipiel/util/db.properties"));
	            
	    	String url = props.getProperty("POSTGRE_DB_URL");
	        String user = props.getProperty("POSTGRE_DB_USERNAME");
	        String password = props.getProperty("POSTGRE_DB_PASSWORD");
	        String driver= props.getProperty("POSTGRE_DB_DRIVER_CLASS");
	            
	        Class.forName(driver);
	        conexion = DriverManager.getConnection(url,user, password);
	        	
	        if(conexion!=null){
	        	System.out.println("Conexion Exitosa JDBC com.edb.Driver...");
	        }else{
	        	System.out.println("Error Conexion JDBC com.edb.Driver...");
	        }
	        	
	      } catch (Exception e) {
				// TODO: handle exception
				System.out.println("Error al conectar JDBC: "+e.getMessage());
	      }
	     
	        conexion.setAutoCommit(true);
	      
	        JasperReport jasperReport;
		    JasperPrint jasperPrint;
			
			String urlPath = request.getRequestURL().toString();
	  		urlPath = urlPath.substring(0, urlPath.length() - request.getRequestURI().length()) + request.getContextPath() + "/";
	  		System.out.println("URL ::::: "+urlPath);
	  		
	  		String rutaReporte = urlPath+"resources/report/ReporteVentas.jasper";
	  		System.out.println("rutaReporte: "+rutaReporte);
	  			
	  		//find file .jasper
	  		jasperReport = (JasperReport)JRLoader.loadObject (new URL(rutaReporte));
				
	  		// fill JasperPrint using fillReport() method
	  		System.out.println("conn open: "+conexion.isClosed());
	  		jasperPrint = JasperFillManager.fillReport(jasperReport, hm, conexion);
              
			byte[] bytes = JasperExportManager.exportReportToPdf(jasperPrint);
			FacesContext context = FacesContext.getCurrentInstance();
			HttpServletResponse response = (HttpServletResponse) context
			.getExternalContext().getResponse();
			/***********************************************************************
			* Pour afficher une bo�te de dialogue pour enregistrer le fichier sous
			* le nom rapport.pdf
			**********************************************************************/
			response.addHeader("Content-disposition",
			"attachment;filename=reporte.pdf");
			response.setContentLength(bytes.length);
			response.getOutputStream().write(bytes);
			response.setContentType("application/pdf");
			context.responseComplete();
			return null;
		}    
	 
	 public String generarReportePDF(){
		 try {
			System.out.println("Ingreso a generarReportePDF...");
			
			int fechaInicial = 20141201;
			int fechaFinal = 20141203;
			String estado = "V";
			String estadoLiteral = "Valida";
		
			
			Map<String, Object> parameters = new HashMap<String, Object>();
    		parameters.put("pFechaInicial", fechaInicial);
    		parameters.put("pFechaFinal", fechaFinal);
    		parameters.put("pEstado", estado);
//    		parameters.put("pUsuario", usuarioSession.getLogin());
    		parameters.put("pUsuario", "ygalarza");
    		parameters.put("pDateInicial", "01/12/2014");
    		parameters.put("pDateFinal", "03/12/2014");
    		parameters.put("pEstadoString", estadoLiteral);
    		parameters.put("pSucursal", this.getSucursalID());
    		parameters.put("pUsuarioFacturacion", "ygalarza");
    		
			String resultado = runPdfReport(parameters, "ReporteVentas", "ReporteVentas"+this.getSucursalID()+"-"+fechaInicial+"-"+fechaFinal);
			
			System.out.println("Termino de generar PDF: "+resultado);
			
			return resultado;
			
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
			System.out.println("Error en generarReportePDF: "+e.getMessage());
			return null;
		}
	 }
	 
	 public String runPdfReport(Map<String, Object> param, String reportFile, String pdfName) {
		 
			String exito = "NO";
			try {

				FacesContext ctx = FacesContext.getCurrentInstance();
				// JRBeanCollectionDataSource ds = new
				// JRBeanCollectionDataSource(getResultList());
				
				
		        //TODO write your implementation code here:
		    	 Locale.setDefault(Locale.ENGLISH); // use this for change NLS
		    	 
				System.out.println("Llenando pdf......."+reportFile+".jasper");
				
				Properties props = new Properties();
			    Connection conexion = null;
			    
			    try {
			        props.load(this.getClass().getClassLoader().getResourceAsStream("/bo/medipiel/util/db.properties"));
			            
			    	String url = props.getProperty("POSTGRE_DB_URL");
			        String user = props.getProperty("POSTGRE_DB_USERNAME");
			        String password = props.getProperty("POSTGRE_DB_PASSWORD");
			        String driver= props.getProperty("POSTGRE_DB_DRIVER_CLASS");
			            
			        Class.forName(driver);
			        conexion = DriverManager.getConnection(url,user, password);
			        	
			        if(conexion!=null){
			        	System.out.println("Conexion Exitosa JDBC com.edb.Driver...");
			        }else{
			        	System.out.println("Error Conexion JDBC com.edb.Driver...");
			        }
			        	
			      } catch (Exception e) {
						// TODO: handle exception
						System.out.println("Error al conectar JDBC: "+e.getMessage());
			      }
			     
			      conexion.setAutoCommit(true);
				
				
				try {
					if(reportFile==null){
						System.out.println("Error cargando el archvio, no existe....");
					}
				} catch (Exception e) {
					// TODO: handle exception
					System.out.println("Error cargando el archvio113....");
					e.getMessage();
					e.printStackTrace();
				}
				
				if(param == null)
				{
					System.out.println("Parametro es null....");
				}
				//System.out.println("Paso 11111");	
				JasperReport jasperReport;
				JasperPrint jasperPrint;
				
				String urlPath = request.getRequestURL().toString();
	    		urlPath = urlPath.substring(0, urlPath.length() - request.getRequestURI().length()) + request.getContextPath() + "/";
	    		System.out.println("URL ::::: "+urlPath);
	    		
	    		String rutaReporte = urlPath+"resources/report/"+reportFile+".jasper";
	    		System.out.println("rutaReporte: "+rutaReporte);
	    			
	    		//find file .jasper
	    		jasperReport = (JasperReport)JRLoader.loadObject (new URL(rutaReporte));
				
	    		// fill JasperPrint using fillReport() method
	    		System.out.println("conn open: "+conexion.isClosed());
	    		jasperPrint = JasperFillManager.fillReport(jasperReport, param, conexion);
				// cambiar conn - new JREmptyDataSource() 
				byte[] bytes = JasperRunManager.runReportToPdf(jasperReport, param, conexion);
//				byte[] bytes = JasperRunManager.runReportToPdf("/bo/medipiel/util/"+reportFile+".jasper", param, conn);
				
				//System.out.println("Paso 12");
				HttpServletResponse response = (HttpServletResponse) ctx
						.getExternalContext().getResponse();
				//System.out.println("Paso 13");
				response.setContentType("application/pdf");
				//System.out.println("Paso 14");
				response.addHeader("Content-Disposition", "attachment; filename="+pdfName+".pdf");
//				response.setHeader("Content-Disposition", "attachment; filename=report.pdf");
				//System.out.println("Paso 15");
				response.setContentLength(bytes.length);
				//System.out.println("Paso 16");
				ServletOutputStream servletOutputStream = response.getOutputStream();
				servletOutputStream.write(bytes, 0, bytes.length);
				
				
				JasperExportManager.exportReportToPdfStream(jasperPrint, servletOutputStream); 
				FacesContext.getCurrentInstance().responseComplete(); 

				
				servletOutputStream.flush();
				servletOutputStream.close();
//				FacesContext.getCurrentInstance().responseComplete();
				
				InputStream stream = this.getClass().getResourceAsStream("ft.pdf");  
		        file = new DefaultStreamedContent(stream, "application/pdf", "ReporteVentas.pdf");  
		        
				
//				//save report to path
	    		JasperExportManager.exportReportToPdfFile(jasperPrint,"C:/etiquetas/"+pdfName+".pdf");
	    		response.setContentType("application/pdf");
	    		JasperExportManager.exportReportToPdfStream(jasperPrint,servletOutputStream);

				
				//System.out.println("Paso 20");
				exito = "SI";
//				conn.close();
			} catch (java.sql.SQLException ex) {
				
				
				
				System.out.println("Error SQL Conexion "+ex.getMessage());
				exito = "NO";
				ex.printStackTrace();
				
				
				FacesMessage msg = new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error, Revice Conexion.", "No se pudo establecer la conexion.");
				FacesContext.getCurrentInstance().addMessage(null, msg);

			} catch (JRException jre) {
				System.out.println("Paso JRException ");
				exito = "NO";
				jre.printStackTrace();
			} catch (IOException e) {
				System.out.println("Paso IOException");
				exito = "NO";
				e.printStackTrace();
			} catch (Exception e4) {
				System.out.println("Paso Exception");
				System.out.println(e4.getMessage());
				exito = "NO";
				e4.printStackTrace();
			} finally {
		
			}
			return exito;
		}
	 
	 
	 
//	  public StreamedContent  downloadAttachment(ArchivoMaterialFicha obj) throws IOException {
//   	  InputStream is = new ByteArrayInputStream(obj.getContenido());
//   	    System.out.println("size file : "+obj.getContenido().length);
//   	    StreamedContent image = new DefaultStreamedContent(is, obj.getTipo(), obj.getNombre());
//   	    //StreamedContent image = new DefaultStreamedContent(is);
//   	    System.out.println("dans le convertisseur : "+image.getContentType());
//   	    return image;
//   }
//	 
	 
	 private StreamedContent file2;
     

	    public StreamedContent getFile2() {
	        return file2;
	    }
	 
	 private StreamedContent streamedContent;
	 
	 public StreamedContent getStreamedContent(){
	    	try {
				System.out.println("Ingreso a descargarPDF...");
				HttpServletRequest request = (HttpServletRequest) facesContext.getExternalContext().getRequest();  
				String urlPath = request.getRequestURL().toString();
				urlPath = urlPath.substring(0, urlPath.length() - request.getRequestURI().length()) + request.getContextPath() + "/";
				System.out.println("urlPath >> "+urlPath);
				
				String pFechaInicial = obtenerFormatoYYYYMMDD(this.getFechaInicial());
				String pFechaFinal = obtenerFormatoYYYYMMDD(this.getFechaFinal());
				String pEstado = this.getEstadoFactura();
//				String pEstadoString = "%";
				String pEstadoString = null;
				String pUsuario = usuarioSession.getLogin();
				String pDateInicial = obtenerFormatoDDMMYYYY(this.getFechaInicial());
				String pDateFinal = obtenerFormatoDDMMYYYY(this.getFechaFinal());
				int pSucursal = usuarioSession.getSucursal().getId();
				String pUsuarioFacturacion = "%";

				
				if(pUsuarioFacturacion.equals("%")){
					pUsuarioFacturacion = "*";
				}
				
				
				if(pEstado.equals("%")){
					pEstado = "*";
					pEstadoString = "Todos";
				}else{
					if(this.getEstadoFactura().equals("V")){
						pEstadoString = "VALIDA";
					}
					if(this.getEstadoFactura().equals("A")){
						pEstadoString = "ANULADA";
					}
					if(this.getEstadoFactura().equals("C")){
						pEstadoString = "EMITIDA POR CONTINGENCIA";
					}
					if(this.getEstadoFactura().equals("E")){
						pEstadoString = "EXTRAVIADA";
					}
					if(this.getEstadoFactura().equals("N")){
						pEstadoString = "NO UTILIZADA";
					}
				}
				
				String urlPDFreporte = urlPath+"ReporteVentas?pFechaInicial="+pFechaInicial+"&pFechaFinal="+pFechaFinal+"&pEstado="+pEstado
						+ "&pUsuario="+pUsuario+"&pDateInicial="+pDateInicial+"&pDateFinal="+pDateFinal+"&pEstadoString="+pEstadoString+"&pSucursal="+pSucursal+"&pUsuarioFacturacion="+pUsuarioFacturacion; //&pRutaImagen="+pRutaImagen
				System.out.println("URL Reporte PDF: "+urlPDFreporte);
				
				URL url = new URL(urlPDFreporte);
							
				// Read the PDF from the URL and save to a local file
				InputStream is1 = url.openStream();
				File f = stream2file(is1);
				System.out.println("Size Bytes: "+f.length());
				InputStream stream = new FileInputStream(f);  
				streamedContent = new DefaultStreamedContent(stream, "application/pdf", "ReporteVentas.pdf");
				file = new DefaultStreamedContent(stream, "application/pdf", "ReporteVentas.pdf");
				return streamedContent;
			} catch (Exception e) {
				// TODO: handle exception
				e.printStackTrace();
				System.out.println("Error en descargarPDF: "+e.getMessage());
				return null;
			}
	 }
	 
	 public Sucursal getSucursal(int sucursalID){
		try {
			return em.find(Sucursal.class, sucursalID);
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
			System.out.println("Error en getSucursal: "+e.getMessage());
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
	 
	 private static File stream2file (InputStream in) throws IOException {            
			
		    final File tempFile = File.createTempFile("ReporteVentas", ".pdf");
		    tempFile.deleteOnExit();
		
		    try (FileOutputStream out = new FileOutputStream(tempFile)) {
		        IOUtils.copy(in, out);
		    }
		
		    return tempFile;            
		}
	 

	 public static Date getUltimoDiaDelMes() {
	        Calendar cal = Calendar.getInstance();
	        cal.set(cal.get(Calendar.YEAR),
	                cal.get(Calendar.MONTH),
	                cal.getActualMaximum(Calendar.DAY_OF_MONTH),
	                cal.getMaximum(Calendar.HOUR_OF_DAY),
	                cal.getMaximum(Calendar.MINUTE),
	                cal.getMaximum(Calendar.SECOND));
	        return cal.getTime();
	  }
	
	public void buscarVentas(){
		System.out.println("Ingreos a buscar ventas....");
		System.out.println("Estado: "+this.getEstadoFactura());
		System.out.println("Fecha Inicial: "+this.getFechaInicial());
		System.out.println("Fecha Final: "+this.getFechaFinal());
		listaFacturas.clear();
		
		try {
			listaFacturas = facturaRepository.buscarFacturasReporteVentas(this.getFechaInicial(), this.getFechaFinal(), this.getEstadoFactura(), usuarioSession.getSucursal().getId());
			System.out.println("Cantidad Facturas: "+listaFacturas.size());
		} catch (Exception e) {
			// TODO: handle exception
			listaFacturas.clear();
			System.out.println("Error en buscarVentas: "+e.getMessage());
		}
	}
	
	//get and set
	public int getPeriodo() {
		return periodo;
	}

	public void setPeriodo(int periodo) {
		this.periodo = periodo;
	}

	public int getGestion() {
		return gestion;
	}

	public void setGestion(int gestion) {
		this.gestion = gestion;
	}

	public Date getFechaInicial() {
		return fechaInicial;
	}

	public void setFechaInicial(Date fechaInicial) {
		this.fechaInicial = fechaInicial;
	}

	public Date getFechaFinal() {
		return fechaFinal;
	}

	public void setFechaFinal(Date fechaFinal) {
		this.fechaFinal = fechaFinal;
	}

	public String getEstadoFactura() {
		return estadoFactura;
	}

	public void setEstadoFactura(String estadoFactura) {
		this.estadoFactura = estadoFactura;
	}

	public int getSucursalID() {
		return sucursalID;
	}

	public void setSucursalID(int sucursalID) {
		this.sucursalID = sucursalID;
	}

	public Factura getFactura() {
		return factura;
	}

	public void setFactura(Factura factura) {
		this.factura = factura;
	}

	public double getTotalFacturadoBolivianos() {
		try {
			totalFacturadoBolivianos = 0;
			for(Factura factura : listaFacturas){
				totalFacturadoBolivianos += factura.getTotalFacturado();
			}
			totalFacturadoBolivianos = round(totalFacturadoBolivianos, 0);
			return totalFacturadoBolivianos;
		} catch (Exception e) {
			// TODO: handle exception
			System.err.println("Error en getTotalFacturadoBolivianos: " + e.getMessage());
			return 0;
		}
	}
	
	public static double round(double value, int places) {
	    if (places < 0) throw new IllegalArgumentException();

	    long factor = (long) Math.pow(10, places);
	    value = value * factor;
	    long tmp = Math.round(value);
	    return (double) tmp / factor;
	}

	public void setTotalFacturadoBolivianos(double totalFacturadoBolivianos) {
		this.totalFacturadoBolivianos = totalFacturadoBolivianos;
	}

	public int getCantidadVentas() {
		try {
			cantidadVentas = listaFacturas.size();
			return cantidadVentas;
		} catch (Exception e) {
			// TODO: handle exception
			System.err.println("Error en getCantidadVentas: " + e.getMessage());
			return 0;
		}
	}

	public void setCantidadVentas(int cantidadVentas) {
		this.cantidadVentas = cantidadVentas;
	}

	public Usuario getUsuarioSession() {
		return usuarioSession;
	}

	public void setUsuarioSession(Usuario usuarioSession) {
		this.usuarioSession = usuarioSession;
	}

	public String getLoginUser() {
		return loginUser;
	}

	public void setLoginUser(String loginUser) {
		this.loginUser = loginUser;
	}

	public void setStreamedContent(StreamedContent streamedContent) {
		this.streamedContent = streamedContent;
	}

	public String getEstadoFacturaPDF() {
		return estadoFacturaPDF;
	}

	public void setEstadoFacturaPDF(String estadoFacturaPDF) {
		this.estadoFacturaPDF = estadoFacturaPDF;
	}

	public String getUsuarioSucursalPDF() {
		return usuarioSucursalPDF;
	}

	public void setUsuarioSucursalPDF(String usuarioSucursalPDF) {
		this.usuarioSucursalPDF = usuarioSucursalPDF;
	}

}
