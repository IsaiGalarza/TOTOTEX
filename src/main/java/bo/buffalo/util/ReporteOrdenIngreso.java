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
import javax.persistence.EntityManager;
import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import net.sf.jasperreports.engine.*;
import net.sf.jasperreports.engine.util.JRLoader;



//--datasource
import javax.sql.DataSource;
import javax.naming.Context;
import javax.naming.InitialContext;

@WebServlet("/ReporteOrdenIngreso")
public class ReporteOrdenIngreso extends HttpServlet {

	/**
	 * 
	 */
	private static final long serialVersionUID = 7433548559308805940L;
	@Inject
	private EntityManager em;

	protected void doGet(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {

		ServletOutputStream servletOutputStream = response.getOutputStream();
		JasperReport jasperReport;
		JasperPrint jasperPrint;

		Connection conn = null;

		/* Properties props = new Properties(); */

		try {
			/*
			 * props.load(this.getClass().getClassLoader().getResourceAsStream(
			 * "/bo/medipiel/util/db.properties"));
			 * 
			 * String url = props.getProperty("POSTGRE_DB_URL"); String user =
			 * props.getProperty("POSTGRE_DB_USERNAME"); String password =
			 * props.getProperty("POSTGRE_DB_PASSWORD"); String driver=
			 * props.getProperty("POSTGRE_DB_DRIVER_CLASS");
			 * 
			 * Class.forName(driver); conn =
			 * DriverManager.getConnection(url,user, password);
			 */

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
			System.out.println("Error al conectar JDBC: " + e.getMessage());
		}

		try {
			String pUsuario = request.getParameter("pUsuario");
			String pSucursal = request.getParameter("pSucursal");
			String pId = request.getParameter("pId");

			System.out.println("Parametros Reporte: ......");
			System.out.println("pUsuario: " + pUsuario);
			System.out.println("pSucursal: " + pSucursal);
			System.out.println("pId : " + pId);

			System.out.println("Conexion em: " + em.isOpen());

			@SuppressWarnings("deprecation")
			String realPath = request.getRealPath("/");
			System.out.println("Real Path: " + realPath);

			// load JasperDesign from XML and compile it into JasperReport
			System.out.println("Context getServletContext: "
					+ request.getServletContext().getContextPath());
			System.out.println("Context getServletPath: "
					+ request.getServletPath());
			System.out
					.println("Context getSession().getServletContext(): "
							+ request.getSession().getServletContext()
									.getRealPath("/"));

			String urlPath = request.getRequestURL().toString();
			urlPath = urlPath.substring(0, urlPath.length()
					- request.getRequestURI().length())
					+ request.getContextPath() + "/";
			System.out.println("URL ::::: " + urlPath);

			String rutaReporte = urlPath
					+ "resources/report/OrdenesIngreso/reporteOrdenIngreso.jasper";
			System.out.println("rutaReporte: " + rutaReporte);

			String rutaImagen = urlPath + "resources/gfx/Medipiel-Logo.jpg";
			System.out.println("RutaImagen: " + rutaImagen);

			String SUBREPORT_DIR = urlPath+"resources/report/OrdenesIngreso/";
			System.out.println("SUBREPORT_DIR: "+SUBREPORT_DIR);			


			// create a map of parameters to pass to the report.
			Map parameters = new HashMap();
			parameters.put("SUBREPORT_DIR", SUBREPORT_DIR);
			parameters.put("usuario", pUsuario);
			/*parameters.put("pSucursal", new Integer(pSucursal));*/
			parameters.put("ID", new Integer(pId));
			// parameters.put("pRutaImagen", rutaImagen);

			// find file .jasper
			jasperReport = (JasperReport) JRLoader.loadObject(new URL(
					rutaReporte));

			// fill JasperPrint using fillReport() method
			jasperPrint = JasperFillManager.fillReport(jasperReport,
					parameters, conn);

			// save report to path
			// JasperExportManager.exportReportToPdfFile(jasperPrint,"C:/etiquetas/Etiqueta+"+pCodigoPre+"-"+pNombreElaborado+".pdf");
			response.setContentType("application/pdf");
			JasperExportManager.exportReportToPdfStream(jasperPrint,
					servletOutputStream);

			servletOutputStream.flush();
			servletOutputStream.close();

		} catch (Exception e) {
			// display stack trace in the browser
			e.printStackTrace();
			System.out.println("Error al ingresar RerpoteVentas: "
					+ e.getMessage());
			StringWriter stringWriter = new StringWriter();
			PrintWriter printWriter = new PrintWriter(stringWriter);
			e.printStackTrace(printWriter);
			response.setContentType("text/plain");
			response.getOutputStream().print(stringWriter.toString());

		}

	}

}
