package bo.buffalo.report;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import java.sql.*;

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

import bo.buffalo.model.ReciboLavanderia;
import net.sf.jasperreports.engine.*;
import net.sf.jasperreports.engine.util.JRLoader;

@WebServlet("/ReportOrdenProduccion")
public class ReportOrdenProduccion extends HttpServlet {

	private static final long serialVersionUID = -3761741719284144934L;

	@Inject
    private EntityManager em;
	
	protected void doGet(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {
		
		ServletOutputStream servletOutputStream = response.getOutputStream();
		Connection conn = null;
		JasperReport jasperReport;
		JasperPrint jasperPrint;

		Statement stmt = null;
        ResultSet rs = null;
        
        try {
        	Context ctx = new InitialContext();
            DataSource ds = (DataSource) ctx.lookup("java:jboss/datasources/TototexDS");
            conn = ds.getConnection();
        	
        	if(conn!=null){
        		System.out.println("Conexion Exitosa JDBC com.edb.Driver...");
        	}else{
        		System.out.println("Error Conexion JDBC com.edb.Driver...");
        	}
        	
		} catch (Exception e) {
			System.out.println("Error al conectar JDBC: "+e.getMessage());
		}
        
        
        try {
            
        	try { 
        		String idReciboLavanderia = request.getParameter("pId");
        		String pUsuario = request.getParameter("pUsuario");
        		
        		ReciboLavanderia reciboLavanderia = em.find(ReciboLavanderia.class, new Integer(idReciboLavanderia));
        		
        		if(reciboLavanderia!=null){
        			
        			System.out.println("Conexion em: "+em.isOpen());
        			
        			String realPath = request.getRealPath("/");
        			System.out.println("Real Path: "+realPath);

        			// load JasperDesign from XML and compile it into JasperReport
        			System.out.println("Context getServletContext: "+request.getServletContext().getContextPath());
        			System.out.println("Context getServletPath: "+request.getServletPath());
        			System.out.println("Context getSession().getServletContext(): "+request.getSession().getServletContext().getRealPath("/"));
        			
        			
        			String urlPath = request.getRequestURL().toString();
        			urlPath = urlPath.substring(0, urlPath.length() - request.getRequestURI().length()) + request.getContextPath() + "/";
        			System.out.println("URL ::::: "+urlPath);
        			
        			Integer ID_FT= reciboLavanderia.getId();
        			String logo = urlPath+"ServletImagelogo?pId="+reciboLavanderia.getId();
        			System.out.println("path logo: "+logo);
        			
        			
        			// create a map of parameters to pass to the report.
        			Map parameters = new HashMap();
        			parameters.put("pIdOrdenProduccion", ID_FT);
        			parameters.put("pDirPhoto", logo);
        			parameters.put("pUsuario", pUsuario);
        			
        			String rutaReporte = urlPath+"resources/report/produccion/orden-produccion.jasper";
        			System.out.println("rutaReporte: "+rutaReporte);
        			
        			//find file .jasper
        			jasperReport = (JasperReport)JRLoader.loadObject (new URL(rutaReporte));

        			// fill JasperPrint using fillReport() method
        			jasperPrint = JasperFillManager.fillReport(jasperReport,parameters, conn);
        			
        			//save report to path
//        			JasperExportManager.exportReportToPdfFile(jasperPrint,"/ticket/Ticket-NRO-"+ticket.getId()+".pdf");
        			response.setContentType("application/pdf");
        			JasperExportManager.exportReportToPdfStream(jasperPrint,servletOutputStream);

        			servletOutputStream.flush();
        			servletOutputStream.close();
        			
        		}else{
        			System.out.println("ReciboLavanderia no encontrada x ID: "+idReciboLavanderia);
        		}
    			
    		} catch (Exception e) {
    			// display stack trace in the browser
    			e.printStackTrace();
    			System.out.println("Error al ingresar JasperReportServlet: " + e.getMessage());
    			StringWriter stringWriter = new StringWriter();
    			PrintWriter printWriter = new PrintWriter(stringWriter);
    			e.printStackTrace(printWriter);
    			response.setContentType("text/plain");
    			response.getOutputStream().print(stringWriter.toString());
    		} 
        	
        	
        } catch (Exception e) {
            e.printStackTrace();
            
            
        }finally{
                try {
                    if(rs != null) rs.close();
                    if(stmt != null) stmt.close();
                    if(conn != null) conn.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
        }
		
	}
	
}
