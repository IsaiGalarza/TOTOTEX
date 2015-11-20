//package bo.buffalo.util;
//
//import java.io.ByteArrayOutputStream;
//import java.io.IOException;
//import java.io.InputStream;
//import java.net.URL;
//import java.sql.Connection;
//import java.sql.DriverManager;
//import java.sql.ResultSet;
//import java.sql.SQLException;
//import java.sql.Statement;
//import java.util.ArrayList;
//import java.util.HashMap;
//import java.util.List;
//import java.util.Locale;
//import java.util.Map;
//import java.util.Properties;
//
//import javax.enterprise.context.ApplicationScoped;
//import javax.faces.application.FacesMessage;
//import javax.faces.context.FacesContext;
//import javax.inject.Inject;
//import javax.servlet.ServletOutputStream;
//import javax.servlet.http.HttpServletRequest;
//import javax.servlet.http.HttpServletResponse;
//
//import net.sf.jasperreports.engine.JRException;
//import net.sf.jasperreports.engine.JasperExportManager;
//import net.sf.jasperreports.engine.JasperFillManager;
//import net.sf.jasperreports.engine.JasperPrint;
//import net.sf.jasperreports.engine.JasperReport;
//import net.sf.jasperreports.engine.JasperRunManager;
//import net.sf.jasperreports.engine.export.JRCsvExporter;
//import net.sf.jasperreports.engine.export.JRCsvExporterParameter;
//import net.sf.jasperreports.engine.export.JRXlsExporter;
//import net.sf.jasperreports.engine.export.JRXlsExporterParameter;
//import net.sf.jasperreports.engine.util.JRLoader;
//import net.sf.jasperreports.view.JasperViewer;
//
//@ApplicationScoped
//public class UtilesReportes {
//
//	Connection conn = null;
//
//	public UtilesReportes() {
//
//		Properties props = new Properties();
//
//		try {
//			props.load(this.getClass().getClassLoader()
//					.getResourceAsStream("/bo/buffalo/util/db.properties"));
//
//			String url = props.getProperty("POSTGRE_DB_URL");
//			String user = props.getProperty("POSTGRE_DB_USERNAME");
//			String password = props.getProperty("POSTGRE_DB_PASSWORD");
//			String driver = props.getProperty("POSTGRE_DB_DRIVER_CLASS");
//
//			Class.forName(driver);
//			conn = DriverManager.getConnection(url, user, password);
//
//			if (conn != null) {
//				System.out.println("Conexion Exitosa JDBC com.edb.Driver...");
//			} else {
//				System.out.println("Error Conexion JDBC com.edb.Driver...");
//			}
//
//		} catch (Exception e) {
//			// TODO: handle exception
//			System.out.println("Error al conectar JDBC: " + e.getMessage());
//		}
//
//	}
//
//	/*
//	 * Acciones para reportes
//	 */
//	public String prepararPdf(Map parametrosEntrada, String archivoReporte,
//			String archivo, HttpServletRequest request) {
//		String resultado;
//		try {
//			// Map parametros = new HashMap();
//			// System.out.println("Inicio Generando PDF.........");
//			resultado = runPdfReport(parametrosEntrada, archivoReporte,
//					archivo, request);
//			// System.out.println("Impresion PDF finalizada....."+resultado);
//		} catch (Exception e) {
//			// TODO: handle exception
//			System.out.println(e.getMessage());
//			e.printStackTrace();
//			resultado = "error";
//		}
//		return resultado;
//	}
//
//	public String prepararXLS(Map parametrosEntrada, String archivoReporte,
//			String archivo, HttpServletRequest request) {
//		String resultado = "error excel";
//		try {
//			// System.out.println("Inicio Generando XLS.........");
//
//			resultado = runGenerateXLS(parametrosEntrada, archivoReporte,
//					archivo, request);
//			// System.out.println("Generacion de XLS finalizada....."+resultado);
//		} catch (Exception e) {
//			// TODO: handle exception
//			System.out.println(e.getMessage());
//			e.printStackTrace();
//			resultado = "error excel";
//		}
//		return resultado;
//	}
//
//	// public String prepararCSV(Map parametrosEntrada,String archivoReporte,
//	// String archivo){
//	// String resultado="error excel";
//	// try {
//	// //System.out.println("Inicio Generando XLS.........");
//	// resultado = runGenerateCSV(parametrosEntrada, archivoReporte, archivo);
//	// //System.out.println("Generacion de XLS finalizada....."+resultado);
//	// } catch (Exception e) {
//	// // TODO: handle exception
//	// System.out.println(e.getMessage());
//	// e.printStackTrace();
//	// resultado = "error excel";
//	// }
//	// return resultado;
//	// }
//	// public String prepararViewer(Map parametrosEntrada,String archivoReporte,
//	// String archivo){
//	// try {
//	// //System.out.println("Inicio Viewer.........");
//	// String resultado = runViewerReport(parametrosEntrada, archivoReporte,
//	// "Users.xls");
//	// //System.out.println("Generacion de Viewer finalizada....."+resultado);
//	// } catch (Exception e) {
//	// // TODO: handle exception
//	// System.out.println(e.getMessage());
//	// e.printStackTrace();
//	// return "error";
//	// }
//	// return "exito";
//	// }
//
//	public String runPdfReport(Map param, String reportFile, String pdfName,
//			HttpServletRequest request) {
//		String exito = "NO";
//		try {
//
//			FacesContext ctx = FacesContext.getCurrentInstance();
//			// JRBeanCollectionDataSource ds = new
//			// JRBeanCollectionDataSource(getResultList());
//
//			// TODO write your implementation code here:
//			Locale.setDefault(Locale.ENGLISH); // use this for change NLS
//
//			System.out.println("Llenando pdf......." + reportFile + ".jasper");
//
//			if (conn == null) {
//				FacesMessage msg = new FacesMessage(
//						FacesMessage.SEVERITY_ERROR, "Error, Revice Conexion.",
//						"No se pudo establecer la conexion.");
//				FacesContext.getCurrentInstance().addMessage(null, msg);
//				System.out.println("conexion null");
//				return "errorSystemparameter";
//			}
//
//			try {
//				if (reportFile == null) {
//					System.out
//							.println("Error cargando el archvio, no existe....");
//				}
//			} catch (Exception e) {
//				// TODO: handle exception
//				System.out.println("Error cargando el archvio113....");
//				e.getMessage();
//				e.printStackTrace();
//			}
//
//			if (param == null) {
//				System.out.println("Parametro es null....");
//			}
//			// System.out.println("Paso 11111");
//			JasperReport jasperReport;
//			JasperPrint jasperPrint;
//
//			String urlPath = request.getRequestURL().toString();
//			urlPath = urlPath.substring(0, urlPath.length()
//					- request.getRequestURI().length())
//					+ request.getContextPath() + "/";
//			System.out.println("URL ::::: " + urlPath);
//
//			String rutaReporte = urlPath + "resources/report/" + reportFile
//					+ ".jasper";
//			System.out.println("rutaReporte: " + rutaReporte);
//
//			// find file .jasper
//			jasperReport = (JasperReport) JRLoader.loadObject(new URL(
//					rutaReporte));
//
//			// fill JasperPrint using fillReport() method
//			System.out.println("conn open: " + conn.isClosed());
//			jasperPrint = JasperFillManager.fillReport(jasperReport, param,
//					conn);
//			// cambiar conn - new JREmptyDataSource()
//			byte[] bytes = JasperRunManager.runReportToPdf(jasperReport, param,
//					conn);
//			// byte[] bytes =
//			// JasperRunManager.runReportToPdf("/bo/medipiel/util/"+reportFile+".jasper",
//			// param, conn);
//
//			// System.out.println("Paso 12");
//			HttpServletResponse response = (HttpServletResponse) ctx
//					.getExternalContext().getResponse();
//			// System.out.println("Paso 13");
//			response.setContentType("application/pdf");
//			// System.out.println("Paso 14");
//			response.addHeader("Content-Disposition", "attachment; filename="
//					+ pdfName + ".pdf");
//			// response.setHeader("Content-Disposition",
//			// "attachment; filename=report.pdf");
//			// System.out.println("Paso 15");
//			response.setContentLength(bytes.length);
//			// System.out.println("Paso 16");
//			ServletOutputStream servletOutputStream = response
//					.getOutputStream();
//			servletOutputStream.write(bytes, 0, bytes.length);
//
//			JasperExportManager.exportReportToPdfStream(jasperPrint,
//					servletOutputStream);
//			FacesContext.getCurrentInstance().responseComplete();
//
//			servletOutputStream.flush();
//			servletOutputStream.close();
//			// FacesContext.getCurrentInstance().responseComplete();
//
//			// //save report to path
//			JasperExportManager.exportReportToPdfFile(jasperPrint,
//					"C:/etiquetas/" + pdfName + ".pdf");
//			response.setContentType("application/pdf");
//			JasperExportManager.exportReportToPdfStream(jasperPrint,
//					servletOutputStream);
//
//			// System.out.println("Paso 20");
//			exito = "SI";
//			// conn.close();
//		} catch (java.sql.SQLException ex) {
//
//			System.out.println("Error SQL Conexion " + ex.getMessage());
//			exito = "NO";
//			ex.printStackTrace();
//
//			FacesMessage msg = new FacesMessage(FacesMessage.SEVERITY_ERROR,
//					"Error, Revice Conexion.",
//					"No se pudo establecer la conexion.");
//			FacesContext.getCurrentInstance().addMessage(null, msg);
//
//		} catch (JRException jre) {
//			System.out.println("Paso JRException ");
//			exito = "NO";
//			jre.printStackTrace();
//		} catch (IOException e) {
//			System.out.println("Paso IOException");
//			exito = "NO";
//			e.printStackTrace();
//		} catch (Exception e4) {
//			System.out.println("Paso Exception");
//			System.out.println(e4.getMessage());
//			exito = "NO";
//			e4.printStackTrace();
//		} finally {
//
//		}
//		return exito;
//	}
//
//	public String runGenerateXLS(Map<String, Object> param, String reportFile,
//			String xlsName, HttpServletRequest request) {
//
//		Locale.setDefault(Locale.ENGLISH); // use this for change NLS
//
//		String exito = "";
//		try {
//			System.out
//					.println("Llenando excel......." + reportFile + ".jasper");
//
//			if (conn == null) {
//				FacesMessage msg = new FacesMessage(
//						FacesMessage.SEVERITY_ERROR, "Error, Revice Conexion.",
//						"No se pudo establecer la conexion.");
//				FacesContext.getCurrentInstance().addMessage(null, msg);
//				return "errorSystemparameter";
//			}
//			FacesContext ctx = FacesContext.getCurrentInstance();
//
//			// InputStream streamFile =
//			// (InputStream)ctx.getExternalContext().getResourceAsStream(reportFile+".jasper");
//
//			// ---- Generando archivo XLS INICIO ---- //
//			try {
//
//				// System.out.println("Paso 11111");
//				// HttpServletRequest request = (HttpServletRequest)
//				// facesContext.getExternalContext().getRequest();
//				JasperReport jasperReport;
//				String urlPath = request.getRequestURL().toString();
//				urlPath = urlPath.substring(0, urlPath.length()
//						- request.getRequestURI().length())
//						+ request.getContextPath() + "/";
//				System.out.println("URL ::::: " + urlPath);
//
//				String rutaReporte = urlPath + "resources/report/" + reportFile
//						+ ".jasper";
//				System.out.println("rutaReporte: " + rutaReporte);
//
//				// find file .jasper
//				jasperReport = (JasperReport) JRLoader.loadObject(new URL(
//						rutaReporte));
//
//				// cambiar conn - new JREmptyDataSource()
//				byte[] bytes = JasperRunManager.runReportToPdf(jasperReport,
//						param, conn);
//
//				// Poblamos la plantilla con los datos de la BD y parametros
//				// JasperPrint objJasperPrint =
//				// JasperFillManager.fillReport(reportFile+".jasper", param,
//				// conn);
//				JasperPrint objJasperPrint = JasperFillManager.fillReport(
//						jasperReport, param, conn);
//
//				// -------------------------------------------------------
//				ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
//				JRXlsExporter exporterXLS = new JRXlsExporter();
//
//				exporterXLS.setParameter(
//						JRXlsExporterParameter.IS_FONT_SIZE_FIX_ENABLED,
//						Boolean.TRUE);
//				exporterXLS.setParameter(JRXlsExporterParameter.JASPER_PRINT,
//						objJasperPrint);
//				exporterXLS.setParameter(
//						JRXlsExporterParameter.IS_DETECT_CELL_TYPE,
//						Boolean.TRUE);
//				exporterXLS.setParameter(
//						JRXlsExporterParameter.IS_WHITE_PAGE_BACKGROUND,
//						Boolean.FALSE);
//				exporterXLS
//						.setParameter(
//								JRXlsExporterParameter.IS_REMOVE_EMPTY_SPACE_BETWEEN_ROWS,
//								Boolean.TRUE);
//				exporterXLS
//						.setParameter(
//								JRXlsExporterParameter.IS_REMOVE_EMPTY_SPACE_BETWEEN_COLUMNS,
//								Boolean.TRUE);
//				exporterXLS.setParameter(
//						JRXlsExporterParameter.IS_COLLAPSE_ROW_SPAN,
//						Boolean.TRUE);
//				exporterXLS.setParameter(
//						JRXlsExporterParameter.IGNORE_PAGE_MARGINS,
//						Boolean.TRUE);
//				exporterXLS.setParameter(
//						JRXlsExporterParameter.IS_ONE_PAGE_PER_SHEET,
//						Boolean.FALSE);
//				exporterXLS.setParameter(JRXlsExporterParameter.OUTPUT_STREAM,
//						byteArrayOutputStream);
//				exporterXLS.setParameter(
//						JRXlsExporterParameter.IS_IGNORE_CELL_BORDER,
//						Boolean.TRUE);
//				// exporterXLS.setParameter(JRXlsExporterParameter.IS_AUTO_DETECT_CELL_TYPE,
//				// Boolean.TRUE);
//
//				try {
//					exporterXLS.exportReport();
//				} catch (JRException ex) {
//					System.out.println("No se pudo crear el excel..."
//							+ ex.getMessage());
//				}
//
//				HttpServletResponse response = (HttpServletResponse) ctx
//						.getExternalContext().getResponse();
//
//				response.setContentType("application/vnd.ms-excel");
//				response.setContentLength(byteArrayOutputStream.toByteArray().length);
//				response.setHeader("Content-disposition",
//						"attachment; filename=\"" + xlsName.toString()
//								+ ".xls\"");
//
//				ServletOutputStream servletOutputStream = response
//						.getOutputStream();
//				servletOutputStream.write(byteArrayOutputStream.toByteArray());
//				servletOutputStream.flush();
//				servletOutputStream.close();
//				FacesContext.getCurrentInstance().responseComplete();
//
//				exito = "si";
//				conn.close();
//			} catch (Exception e) {
//				exito = "no";
//				e.printStackTrace();
//				throw new Exception(e);
//			}
//
//			// ---- Generando archivo XLS FIN ---- //
//
//			exito = "SI";
//		} catch (JRException jre) {
//			exito = "NO";
//			jre.printStackTrace();
//		} catch (SQLException e2) {
//			exito = "NO";
//			e2.printStackTrace();
//		} catch (Exception e4) {
//			exito = "NO";
//			e4.printStackTrace();
//		} finally {
//			// System.out.println("Printer finish........"+exito);
//		}
//		return exito;
//	}
//}
