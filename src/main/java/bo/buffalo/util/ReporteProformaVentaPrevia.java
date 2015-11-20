package bo.buffalo.util;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.net.URL;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import javax.faces.context.FacesContext;
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

import net.sf.jasperreports.engine.JasperExportManager;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.JasperReport;
import net.sf.jasperreports.engine.util.JRLoader;
import bo.buffalo.model.ProformaVenta;

@WebServlet("/ReporteProformaVentaPrevia")
public class ReporteProformaVentaPrevia extends HttpServlet {
	
	@Inject
    private EntityManager em;
	
	@Inject
    private FacesContext facesContext;
	
	protected void doGet(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {
		
		ServletOutputStream servletOutputStream = response.getOutputStream();
		Connection conn = null;
		JasperReport jasperReport;
		JasperPrint jasperPrint;

		Properties props = new Properties();
        FileInputStream fis = null;
		
        Statement stmt = null;
        ResultSet rs = null;
        
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
            
        	try { 
        		String idProformaVenta = request.getParameter("idProformaVenta");
        		String pUsuario = request.getParameter("pUsuario");
        		String pEstado= request.getParameter("pEstado");
        		
        		
        		ProformaVenta factura = em.find(ProformaVenta.class, new Integer(idProformaVenta));
        		
        		if(factura!=null){
        			
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
        			
        			String SUBREPORT_DIR = urlPath+"resources/report/Proforma/";
        			System.out.println("SUBREPORT_DIR: "+SUBREPORT_DIR);			


        			// create a map of parameters to pass to the report.
        			Map parameters = new HashMap();
        			parameters.put("SUBREPORT_DIR", SUBREPORT_DIR);
        			parameters.put("ID", new Integer(idProformaVenta));
        			parameters.put("usuario", pUsuario);
        			String rutaReporte ="";
        			if (pEstado.equals("PREPARADO")) {
        				rutaReporte= urlPath+"resources/report/Proforma/reportProformaVentaPreparado.jasper";
					}else if (pEstado.equals("FARMACO")) {
						rutaReporte= urlPath+"resources/report/Proforma/reportProformaVentaFarmaco.jasper";
					}
        		
        			System.out.println("rutaReporte: "+rutaReporte);
        			
        			//find file .jasper
        			jasperReport = (JasperReport)JRLoader.loadObject (new URL(rutaReporte));

        			// fill JasperPrint using fillReport() method
        			jasperPrint = JasperFillManager.fillReport(jasperReport,parameters, conn);
        			
        			//save report to path
        			//JasperExportManager.exportReportToPdfFile(jasperPrint,"resources/PDF/ProformaVenta"+idProformaVenta+".pdf");
        			response.setContentType("application/pdf");
            		JasperExportManager.exportReportToPdfStream(jasperPrint,servletOutputStream);

        			servletOutputStream.flush();
        			servletOutputStream.close();
        			
        		}else{
        			System.out.println("Factura no encontrada x ID: "+idProformaVenta);
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
