package bo.buffalo.util;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.net.URL;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;
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
import net.sf.jasperreports.engine.util.JRLoader;

@WebServlet("/EtiquetaChica")
public class ReporteEtiquetaChica extends HttpServlet {

	private static final long serialVersionUID = 4507523677953570957L;

	@Inject
    private EntityManager em;
	
	protected void doGet(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {
		
		ServletOutputStream servletOutputStream = response.getOutputStream();
		JasperReport jasperReport;
		JasperPrint jasperPrint;
		
		Connection conn = null;
        
        Properties props = new Properties();
		
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
        		String pPaciente = request.getParameter("pPaciente");
        		String pCodigoPre = request.getParameter("pCodigoPre");
        		String pNombreElaborado = request.getParameter("pNombreElaborado");
        		String pPresentacion = request.getParameter("pPresentacion");
        		String pMedico = request.getParameter("pMedico");
        		String pUso = request.getParameter("pUso");
        		String pConservacion = request.getParameter("pConservacion");
        		String pFechaElaboracion = request.getParameter("pFechaElaboracion");
        		String pFechaVencimiento = request.getParameter("pFechaVencimiento");
        		
        		System.out.println("Parametros Reporte: ......");
        		System.out.println("pPaciente: "+pPaciente);
        		System.out.println("pCodigoPre: "+pCodigoPre);
        		System.out.println("pNombreElaborado: "+pNombreElaborado);
        		System.out.println("pPresentacion: "+pPresentacion);
        		System.out.println("pMedico: "+pMedico);
        		System.out.println("pUso: "+pUso);
        		System.out.println("pConservacion: "+pConservacion);
        		System.out.println("pFechaElaboracion: "+pFechaElaboracion);
        		System.out.println("pFechaVencimiento: "+pFechaVencimiento);
        			
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
        			
        		// create a map of parameters to pass to the report.
        		Map parameters = new HashMap();
        		parameters.put("pPaciente", pPaciente);
        		parameters.put("pCodigoPre", pCodigoPre);
        		parameters.put("pNombreElaborado", pNombreElaborado);
        		parameters.put("pPresentacion", pPresentacion);
        		parameters.put("pMedico", pMedico);
        		parameters.put("pUso", pUso);
        		parameters.put("pConservacion", pConservacion);
        		parameters.put("pFechaElaboracion", pFechaElaboracion);
        		parameters.put("pFechaVencimiento", pFechaVencimiento);
        		
        		String rutaReporte = urlPath+"resources/report/EtiquetaChica.jasper";
        		System.out.println("rutaReporte: "+rutaReporte);
        			
        		//find file .jasper
        		jasperReport = (JasperReport)JRLoader.loadObject (new URL(rutaReporte));

        		// fill JasperPrint using fillReport() method
        		jasperPrint = JasperFillManager.fillReport(jasperReport, parameters, conn);
        		
        			
        		//save report to path
//        		JasperExportManager.exportReportToPdfFile(jasperPrint,"C:/etiquetas/Etiqueta+"+pCodigoPre+"-"+pNombreElaborado+".pdf");
        		response.setContentType("application/pdf");
        		JasperExportManager.exportReportToPdfStream(jasperPrint,servletOutputStream);
        		

        		servletOutputStream.flush();
        		servletOutputStream.close();
        	
    		} catch (Exception e) {
    			// display stack trace in the browser
    			e.printStackTrace();
    			System.out.println("Error al ingresar ReporteEtiquetaChica: " + e.getMessage());
    			StringWriter stringWriter = new StringWriter();
    			PrintWriter printWriter = new PrintWriter(stringWriter);
    			e.printStackTrace(printWriter);
    			response.setContentType("text/plain");
    			response.getOutputStream().print(stringWriter.toString());
    			
    		} 
        
	}
	
}
