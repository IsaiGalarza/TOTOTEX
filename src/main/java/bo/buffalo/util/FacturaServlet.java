package bo.buffalo.util;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.sql.DataSource;

import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JRExporterParameter;
import net.sf.jasperreports.engine.JasperExportManager;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.JasperReport;
import net.sf.jasperreports.engine.design.JasperDesign;
import net.sf.jasperreports.engine.export.JRXlsExporter;
import net.sf.jasperreports.engine.util.JRLoader;


@WebServlet("/factura")
public class FacturaServlet extends HttpServlet {

	private static final long serialVersionUID = -8293842468288122208L;
	
	
	protected void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException
			{
		
			String idFactura = request.getParameter("idFactura");
        	System.out.println("ID Factura: " + idFactura);
		
			ServletOutputStream servletOutputStream = response.getOutputStream();
			Connection conn = null;
			JasperReport jasperReport;
			JasperPrint jasperPrint;
			JasperDesign jasperDesign;
			try
			{
				Context ctx = new InitialContext();
	            DataSource ds = (DataSource) ctx.lookup("java:jboss/datasources/BuffaloDS");
	            conn = ds.getConnection();
	        	
	        	if(conn!=null){
	        		System.out.println("Conexion Exitosa JDBC com.edb.Driver...");
	        	}else{
	        		System.out.println("Error Conexion JDBC com.edb.Driver...");
	        	}       	
			 
			// create a map of parameters to pass to the report.
			Map parameters = new HashMap();
			parameters.put("pFactura", 36);
			parameters.put("pCodeQR", "http://www.caniem.org/congresogie/img/qr_img.png");
			 
			// load JasperDesign from XML and compile it into JasperReport
			jasperReport = (JasperReport)JRLoader.loadObject ("/report/factura.jasper");
			//jasperDesign = JRXmlLoader.load("C:/jasperReports/demo.jrxml");
			//jasperReport = JasperCompileManager.compileReport(jasperDesign);
			 
			// fill JasperPrint using fillReport() method
			jasperPrint = JasperFillManager.fillReport(jasperReport,parameters,conn);
			 
			JasperExportManager.exportReportToPdfFile(jasperPrint,
			"C:/jasperReports/demo.pdf");
			response.setContentType("application/pdf");
			//for creating report in excel format
			JRXlsExporter exporterXls = new JRXlsExporter();
			exporterXls.setParameter(JRExporterParameter.JASPER_PRINT, jasperPrint);
			exporterXls.setParameter(JRExporterParameter.OUTPUT_FILE_NAME,
			"C:/jasperReports/demo.xls");
			exporterXls.exportReport();
			JasperExportManager.exportReportToPdfStream(jasperPrint, servletOutputStream);
			 
			servletOutputStream.flush();
			servletOutputStream.close();
			}
			catch(SQLException sqle)
			{
			 System.err.println(sqle.getMessage());
			}
			catch (JRException e)
			{
			// display stack trace in the browser
			StringWriter stringWriter = new StringWriter();
			PrintWriter printWriter = new PrintWriter(stringWriter);
			e.printStackTrace(printWriter);
			response.setContentType("text/plain");
			response.getOutputStream().print(stringWriter.toString());
			} catch (NamingException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			finally
			{
			//close the connection.
				if(conn != null)
				{
					try { conn.close(); }
						catch (Exception ignored) {}
					}
				}
		}
	
	
	
	/*
	@Override
    protected void doGet(HttpServletRequest request,
            HttpServletResponse response) throws ServletException, IOException {
		
		try {
			 String idFactura = request.getParameter("idFactura");
		        System.out.println("ID Factura: " + idFactura);
		        
		        if(!idFactura.isEmpty()){
		        	
		        	
		        	
		        }else{
		        	System.out.println("Esperando idFactura...");
		        }

		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
			System.out.println("Error en generarFactura: "+e.getMessage());
		}
		
		ServletOutputStream out;
		// Creamos un objecto jasper con el fichero previamente compilado
		JasperReport jasperReport = (JasperReport)JRLoader.loadObject ("/report/factura.jasper");

		// Generamos el informe pasandole como parametros el objecto creado anteriormente jasperReport, parameters que es el hashmap
		// creado anteriormente con todos los parametros que necesitemos enviarle al informe y conn una conexión activa con vuestro
		// servidor de BD
		byte[] fichero = JasperRunManager.runReportToPdf (jasperReport, parameters, conn);

		// Y enviamos el pdf a la salida del navegador como podríamos hacer con cualquier otro pdf
		response.setContentType ("application/pdf");
		response.setHeader ("Content-disposition", "inline; filename=factura.pdf");
		response.setHeader ("Cache-Control", "max-age=30");
		response.setHeader ("Pragma", "No-cache");
		response.setDateHeader ("Expires", 0);
		response.setContentLength (fichero.length);
		out = response.getOutputStream ();

		out.write (fichero, 0, fichero.length);
		out.flush ();
		out.close ();
		
    }*/
	 
}
