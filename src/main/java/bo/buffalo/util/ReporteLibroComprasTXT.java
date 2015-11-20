package bo.buffalo.util;

import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.net.URL;
import java.sql.Connection;
import java.sql.DriverManager;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
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

import bo.buffalo.data.CompraRepository;
import bo.buffalo.data.FacturaRepository;
import bo.buffalo.model.Compra;
import bo.buffalo.model.Factura;
import net.sf.jasperreports.engine.*;
import net.sf.jasperreports.engine.util.JRLoader;

@WebServlet("/ReporteLibroComprasTXT")
public class ReporteLibroComprasTXT extends HttpServlet {

	private static final long serialVersionUID = -3280103162817601529L;
	
	@Inject
    private EntityManager em;
	
	@Inject
    CompraRepository compraRepository;
	
	public String formatearFecha(Date date){
    	try {
    		String DATE_FORMAT = "dd/MM/yyyy";
			SimpleDateFormat sdf = new SimpleDateFormat(DATE_FORMAT);
    		return sdf.format(date);
    		
		} catch (Exception e) {
			// TODO: handle exception
			System.out.println("Error en formatearFecha: "+e.getMessage());
			return null;
		}
	}
	
	public static String round(double value) {
	    try {
	    	DecimalFormat df = new DecimalFormat("####0.00");
//	    	System.out.println("Value: " + df.format(value));
	    	return df.format(value);
		} catch (Exception e) {
			// TODO: handle exception
			System.out.println("Error en round: "+e.getMessage());
			return "0.00";
		}
	}
	
	protected void doGet(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {
		
		ServletOutputStream servletOutputStream = response.getOutputStream();

//        OutputStream out = response.getOutputStream();
       
        try { 
        	 //CAPTURANDO PARAMETROS
            String pGestion = request.getParameter("pGestion");
    		String pMes = request.getParameter("pMes");
    		
    		System.out.println("LIBRO VENTAS TXT GENERANDO =============>  pGestion: "+pGestion+", pMes: "+pMes);
    		StringWriter stringWriter = new StringWriter();
    		List<Compra> listaCompras = new ArrayList<Compra>();
    		
    		if(!pGestion.isEmpty() && !pMes.isEmpty()){
    			listaCompras = compraRepository.traerComprasPeriodoFiscal(pGestion, pMes);
    			
    			/*
				 * Campos: TIPOFACTURA | NIT | RAZONSOCIAL | NUMEROFACTURA |
				 * NUMEROPOLIZA | NUMERODEAUTORIZACION | FECHA|IMPORTE TOTAL
				 * FACTURA|IMPORTE ICE|IMPORTE EXCENTO|IMPORTE SUJETO A CRED.
				 * FISCAL | C REDITOFISCAL|CODIGO DE CONTROL Ejemplo:
				 * 1|1028627025|NOMBRE
				 * EMPRESA.|5228|0|7928155105|13/12/2005|2000|500|500|1000|130|
				 * A1-B2-C3-D4-E5
				 */
    			
    			if(!listaCompras.isEmpty()){
    				for(Compra compra : listaCompras){
    					String fechaCompra = formatearFecha(compra.getFechaFactura());
        				stringWriter.append(compra.getTipoCompra()+"|"+compra.getNitProveedor()+"|"+compra.getRazonSocial()+"|"+compra.getNumeroFactura()+
        						"|"+compra.getNumeroDUI()+"|"+compra.getNumeroAutorizacion()+"|"+fechaCompra+"|"+round(compra.getImporteTotal())+
        						"|"+round(compra.getImporteICE())+"|"+round(compra.getImporteExcentos())+"|"+round(compra.getImporteBaseCreditoFiscal())+"|"+round(compra.getCreditoFiscal())+
        						"|"+compra.getCodigoControl());
        				stringWriter.append("\n");
        			}
    			}else{
    				stringWriter.append("No existen Datos.");
    			}
    			
    		}else{
    			stringWriter.append("No existen Parametros.");
    		}
    		
    		System.out.println("Conexion em: "+em.isOpen());
 
			
    		//save report to path
			response.setContentType("text/plain");
			response.getOutputStream().print(stringWriter.toString());

    		servletOutputStream.flush();
    		servletOutputStream.close();
    	
		} catch (Exception e) {
			// display stack trace in the browser
			e.printStackTrace();
			System.out.println("Error en ReporteLibroComprasTXT: " + e.getMessage());
			StringWriter stringWriter = new StringWriter();
			PrintWriter printWriter = new PrintWriter(stringWriter);
			e.printStackTrace(printWriter);
			response.setContentType("text/plain");
			response.getOutputStream().print(stringWriter.toString());			
		} 
        
        

	}
	
}
