package bo.buffalo.util;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.net.URL;
import java.sql.Connection;
import java.sql.DriverManager;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import javax.inject.Inject;
import javax.naming.Context;
import javax.naming.InitialContext;
import javax.persistence.EntityManager;
import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.sql.DataSource;

import net.sf.jasperreports.engine.*;
import net.sf.jasperreports.engine.export.JRXlsExporter;
import net.sf.jasperreports.engine.export.JRXlsExporterParameter;
import net.sf.jasperreports.engine.util.JRLoader;

@WebServlet("/ReporteLibroComprasSFVExcel")
public class ReporteLibroComprasSFVExcel extends HttpServlet {

	private static final long serialVersionUID = -3280103162817601529L;
	
	@Inject
    private EntityManager em;
	
	protected void doGet(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {
		
		ServletOutputStream servletOutputStream = response.getOutputStream();
		JasperReport jasperReport;
		JasperPrint jasperPrint;
		
		Connection conn = null;
        
        Properties props = new Properties();
        OutputStream out = response.getOutputStream();
        
        try {
        	Context ctx = new InitialContext();
            DataSource ds = (DataSource) ctx.lookup("java:jboss/datasources/BuffaloDS");
            conn = ds.getConnection();
        	
        	if(conn!=null){
        		System.out.println("Conexion Exitosa JDBC com.edb.Driver...");
        	}else{
        		System.out.println("Error Conexion JDBC com.edb.Driver...");
        	}
        	
		} catch (Exception e) {
			// TODO: handle exception
			System.out.println("Error al conectar JDBC: "+e.getMessage());
		}
		
        try { 
        	 //CAPTURANDO PARAMETROS
            String pGestion = request.getParameter("pGestion");
    		String pMes = request.getParameter("pMes");
    		
    		System.out.println("LIBRO COMPRAS SFV EXCEL GENERANDO =============>  pGestion: "+pGestion+", pMes: "+pMes);
    			
    		System.out.println("Conexion em: "+em.isOpen());
    			
    		@SuppressWarnings("deprecation")
			String realPath = request.getRealPath("/");
    		System.out.println("Real Path: "+realPath);

    		// load JasperDesign from XML and compile it into JasperReport
    		System.out.println("Context getServletContext: "+request.getServletContext().getContextPath());
    		System.out.println("Context getServletPath: "+request.getServletPath());
    		System.out.println("Context getSession().getServletContext(): "+request.getSession().getServletContext().getRealPath("/"));
    			
    			
    		String urlPath = request.getRequestURL().toString();
    		urlPath = urlPath.substring(0, urlPath.length() - request.getRequestURI().length()) + request.getContextPath() + "/";
    		System.out.println("URL ::::: "+urlPath);
    		
    		String rutaReporte = urlPath+"resources/report/libro-compras-excel.jasper";
    		System.out.println("rutaReporte: "+rutaReporte);
    		
    		// create a map of parameters to pass to the report.       		
    		Map parameters = new HashMap();
    		parameters.put("pGestion", pGestion);
    		parameters.put("pMes", pMes);
    		
    		//find file .jasper
			jasperReport = (JasperReport)JRLoader.loadObject (new URL(rutaReporte));

			// fill JasperPrint using fillReport() method
			jasperPrint = JasperFillManager.fillReport(jasperReport,parameters, conn);    		
    		
			
			
			// -------------------------------------------------------
			   ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
			   JRXlsExporter exporterXLS = new JRXlsExporter();
			   
			   exporterXLS.setParameter(JRXlsExporterParameter.IS_FONT_SIZE_FIX_ENABLED,Boolean.TRUE);
			   exporterXLS.setParameter(JRXlsExporterParameter.JASPER_PRINT, jasperPrint);
			   exporterXLS.setParameter(JRXlsExporterParameter.IS_DETECT_CELL_TYPE, Boolean.TRUE);
			   exporterXLS.setParameter(JRXlsExporterParameter.IS_WHITE_PAGE_BACKGROUND, Boolean.FALSE);
			   exporterXLS.setParameter(JRXlsExporterParameter.IS_REMOVE_EMPTY_SPACE_BETWEEN_ROWS, Boolean.TRUE);
			   exporterXLS.setParameter(JRXlsExporterParameter.IS_REMOVE_EMPTY_SPACE_BETWEEN_COLUMNS, Boolean.TRUE);
			   exporterXLS.setParameter(JRXlsExporterParameter.IS_COLLAPSE_ROW_SPAN, Boolean.TRUE);
			   exporterXLS.setParameter(JRXlsExporterParameter.IGNORE_PAGE_MARGINS, Boolean.TRUE);
			   exporterXLS.setParameter(JRXlsExporterParameter.IS_ONE_PAGE_PER_SHEET, Boolean.FALSE);
			   exporterXLS.setParameter(JRXlsExporterParameter.OUTPUT_STREAM, byteArrayOutputStream);
			   exporterXLS.setParameter(JRXlsExporterParameter.IS_IGNORE_CELL_BORDER, Boolean.TRUE);
//			   exporterXLS.setParameter(JRXlsExporterParameter.IS_AUTO_DETECT_CELL_TYPE, Boolean.TRUE); 
//			   exporterXLS.setParameter(JRXlsExporterParameter.IS_AUTO_DETECT_CELL_TYPE, Boolean.TRUE); 
			
			   
			   try {
				   exporterXLS.exportReport();
			   } catch (JRException ex) {
				   System.out.println("No se pudo crear el excel..."+ex.getMessage());
			   }

			   response.setContentType("application/vnd.ms-excel");
			   response.setContentLength(byteArrayOutputStream.toByteArray().length);
			   response.setHeader("Content-disposition","attachment; filename=\"LibroComprasSFV.xls\"");
			   
			
				servletOutputStream = response.getOutputStream();
				servletOutputStream.write( byteArrayOutputStream.toByteArray());
				servletOutputStream.flush();
				servletOutputStream.close();
				
			    conn.close();

		} catch (Exception e) {
			// display stack trace in the browser
			e.printStackTrace();
			System.out.println("Error en ReporteLibroComprasSFVExcel: " + e.getMessage());
			StringWriter stringWriter = new StringWriter();
			PrintWriter printWriter = new PrintWriter(stringWriter);
			e.printStackTrace(printWriter);
			response.setContentType("text/plain");
			response.getOutputStream().print(stringWriter.toString());			
		} 

	}
	
}
