package bo.buffalo.util;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.Random;


public class MSAccessPedido{
	
	
	public List<EstructuraDetallePedido> buscarNotasVenta(String codigoNotaVenta){
		
		List<EstructuraDetallePedido> listaPedido = new ArrayList<EstructuraDetallePedido>();
		Connection con = null;
	    try {
	      Class.forName("sun.jdbc.odbc.JdbcOdbcDriver") ;

	      // Connect with a url string
	      con = DriverManager.getConnection("jdbc:odbc:vfarmDB");
	      System.out.println("Connection ok..."+new Date());
	      
	      Statement stmt = con.createStatement();
	      ResultSet rs = stmt.executeQuery("SELECT VentasCaja.CodigoVenta AS NotaVenta, VentasCaja.CodigoProforma AS Proforma, VentasCaja.FechaSysModif AS FechaPago, Clientes.CodigoCliente AS CodigoCliente, Clientes.NombreCliente AS Cliente, SUM([Preparados2.CostoPreparado]) as TotalCosto"
	      		+ " FROM (Unidades INNER JOIN Productos ON Unidades.CodigoUnidad = Productos.CodigoUnidad) INNER JOIN (((Medicos INNER JOIN ((((Preparados1 INNER JOIN Preparados2 ON Preparados1.CodigoPreparado = Preparados2.CodigoPreparado) INNER JOIN Preparados1C ON (Preparados2.NumeroDeCopia = Preparados1C.NumeroDeCopia) AND (Preparados2.CodigoPreparado = Preparados1C.CodigoPreparado) AND (Preparados1.CodigoPreparado = Preparados1C.CodigoPreparado)) INNER JOIN VentasCaja ON Preparados1C.CodigoProforma = VentasCaja.CodigoProforma) INNER JOIN Clientes ON Preparados1.CodigoCliente = Clientes.CodigoCliente) ON Medicos.CodigoMedico = Preparados1.CodigoMedico) INNER JOIN VentasProforma ON (Preparados1C.CodigoProforma = VentasProforma.CodigoProforma) AND (Clientes.CodigoCliente = VentasProforma.CodigoCliente) AND (Preparados1.CodigoPreparado = VentasProforma.CodigoPreparado)) INNER JOIN VentasDetalle ON (VentasProforma.CodigoProforma = VentasDetalle.CodigoProforma) AND (VentasProforma.CodigoProforma = VentasDetalle.CodigoProforma) AND (VentasDetalle.CodigoProforma = VentasCaja.CodigoProforma)) ON Productos.CodigoProducto = VentasDetalle.CodigoProducto"
	      		+ " WHERE (((VentasCaja.CodigoVenta)='"+codigoNotaVenta+"') AND  ((VentasCaja.EstadoVenta)>0) AND ((Preparados1C.PreparadoMedico)=True) AND ((VentasCaja.TipoVenta)=1))"
	      		+ " group by VentasCaja.CodigoVenta, VentasCaja.CodigoProforma, VentasCaja.FechaSysModif, Clientes.CodigoCliente, Clientes.NombreCliente ORDER BY VentasCaja.FechaSysModif DESC;");
	      
	      int count = 0;
	      while (rs.next()) {
	    	  
	    	  if(count < 50){
	    		  String notaVenta = rs.getString("NotaVenta");
		    	  String proforma = rs.getString("Proforma");
		    	  String fechaPago = rs.getString("FechaPago");
		    	  String codigoCliente = rs.getString("CodigoCliente");
		    	  String cliente = rs.getString("Cliente");
		    	  double montoTotal = Double.valueOf(rs.getString("TotalCosto"));
		    	  
		    	  System.out.println(notaVenta + "|" + proforma+ "|" + fechaPago + "|" + fechaPago + "|" + codigoCliente + 
		    			  "|" + cliente + "|" + montoTotal +"\n");
		    	  
		    	  
		    	  listaPedido.add(new EstructuraDetallePedido(obtenerRandom(), fechaPago, notaVenta, proforma, codigoCliente, cliente, montoTotal));
		    	  count++;
	    	  }else{
	    		  System.out.println("Salir... Break...");
	    		  break;
	    		  
	    	  }
	    	  
	      }
	      
	      con.close();
	      
	      
	      return listaPedido;

	    } catch (Exception e) {
	      System.err.println("Error en traerUltimasNotasVenta, Exception: "+e.getMessage());
	      return null;
	    }
	}	
	
	public List<EstructuraDetallePedido> buscarNotasVentaConcat(String codigoNotaVenta){
		
		codigoNotaVenta="NV".concat(codigoNotaVenta);
		
		List<EstructuraDetallePedido> listaPedido = new ArrayList<EstructuraDetallePedido>();
		Connection con = null;
	    try {
	      Class.forName("sun.jdbc.odbc.JdbcOdbcDriver") ;

	      // Connect with a url string
	      con = DriverManager.getConnection("jdbc:odbc:vfarmDB");
	      System.out.println("Connection ok..."+new Date());
	      
	      Statement stmt = con.createStatement();
	      
	      ResultSet rs = stmt.executeQuery("SELECT VentasCaja.CodigoVenta AS NotaVenta, VentasCaja.CodigoProforma AS Proforma, VentasCaja.FechaSysModif AS FechaPago, Clientes.CodigoCliente AS CodigoCliente, Clientes.NombreCliente AS Cliente, SUM([Preparados2.CostoPreparado]) as TotalCosto"
	      		+ " FROM (Unidades INNER JOIN Productos ON Unidades.CodigoUnidad = Productos.CodigoUnidad) INNER JOIN (((Medicos INNER JOIN ((((Preparados1 INNER JOIN Preparados2 ON Preparados1.CodigoPreparado = Preparados2.CodigoPreparado) INNER JOIN Preparados1C ON (Preparados2.NumeroDeCopia = Preparados1C.NumeroDeCopia) AND (Preparados2.CodigoPreparado = Preparados1C.CodigoPreparado) AND (Preparados1.CodigoPreparado = Preparados1C.CodigoPreparado)) INNER JOIN VentasCaja ON Preparados1C.CodigoProforma = VentasCaja.CodigoProforma) INNER JOIN Clientes ON Preparados1.CodigoCliente = Clientes.CodigoCliente) ON Medicos.CodigoMedico = Preparados1.CodigoMedico) INNER JOIN VentasProforma ON (Preparados1C.CodigoProforma = VentasProforma.CodigoProforma) AND (Clientes.CodigoCliente = VentasProforma.CodigoCliente) AND (Preparados1.CodigoPreparado = VentasProforma.CodigoPreparado)) INNER JOIN VentasDetalle ON (VentasProforma.CodigoProforma = VentasDetalle.CodigoProforma) AND (VentasProforma.CodigoProforma = VentasDetalle.CodigoProforma) AND (VentasDetalle.CodigoProforma = VentasCaja.CodigoProforma)) ON Productos.CodigoProducto = VentasDetalle.CodigoProducto"
	      		+ " WHERE (((VentasCaja.CodigoVenta)='"+codigoNotaVenta+"') AND  ((VentasCaja.EstadoVenta)>0) AND ((Preparados1C.PreparadoMedico)=True) AND ((VentasCaja.TipoVenta)=1))"
	      		+ " group by VentasCaja.CodigoVenta, VentasCaja.CodigoProforma, VentasCaja.FechaSysModif, Clientes.CodigoCliente, Clientes.NombreCliente ORDER BY VentasCaja.FechaSysModif DESC;");
	      
	      int count = 0;
	      while (rs.next()) {
	    	  
	    	  if(count < 50){
	    		  String notaVenta = rs.getString("NotaVenta");
		    	  String proforma = rs.getString("Proforma");
		    	  String fechaPago = rs.getString("FechaPago");
		    	  String codigoCliente = rs.getString("CodigoCliente");
		    	  String cliente = rs.getString("Cliente");
		    	  double montoTotal = Double.valueOf(rs.getString("TotalCosto"));
		    	  
		    	  System.out.println(notaVenta + "|" + proforma+ "|" + fechaPago + "|" + fechaPago + "|" + codigoCliente + 
		    			  "|" + cliente + "|" + montoTotal +"\n");
		    	  
		    	  
		    	  listaPedido.add(new EstructuraDetallePedido(obtenerRandom(), fechaPago, notaVenta, proforma, codigoCliente, cliente, montoTotal));
		    	  count++;
	    	  }else{
	    		  System.out.println("Salir... Break...");
	    		  break;
	    		  
	    	  }
	    	  
	      }
	      
	      con.close();
	      
	      
	      return listaPedido;

	    } catch (Exception e) {
	      System.err.println("Error en traerUltimasNotasVenta, Exception: "+e.getMessage());
	      return null;
	    }
	}	
	
	public List<EstructuraDetallePedido> buscarDetalleNotaVenta(String codigoVenta){
		
		List<EstructuraDetallePedido> listaPedido = new ArrayList<EstructuraDetallePedido>();
		Connection con = null;
	    try {
	      Class.forName("sun.jdbc.odbc.JdbcOdbcDriver") ;

	      // Connect with a url string
	      con = DriverManager.getConnection("jdbc:odbc:vfarmDB");
	      System.out.println("Connection ok..."+new Date());
	      
	      Statement stmt = con.createStatement();
	      
	      ResultSet rs = stmt.executeQuery("SELECT Preparados1.CodigoPreparado AS CodReceta, Preparados2.CodigoSubPreparado AS CodSubPrep, Preparados1C.Fecha AS FechaReceta, VentasCaja.FechaSysModif AS FechaPago, VentasCaja.CodigoVenta AS NotaVenta, VentasCaja.CodigoProforma AS Proforma, Preparados2.Descripcion AS NomPreparado, Clientes.CodigoCliente, Clientes.NombreCliente AS Cliente, Medicos.CodigoMedico, Medicos.NombreMedico AS Medico, Preparados2.CostoPreparado, Preparados2.CostoPreparado-((Preparados2.CostoPreparado*(VentasCaja.ValorDescuento*100)/VentasProforma.ValorPreparado)/100) AS ValorPreparado, IIf(VentasCaja.MonedaVenta=1,'Bs.','US$') AS Moneda, VentasCaja.EstadoVenta, VentasDetalle.PrecioPublico, Preparados2.CantidadEnvase, [Preparados2].[CostoPreparado]/[Preparados2].[CantidadEnvase] AS PrecioUnitPreElab, VentasCaja.ValorDescuento"
	      		+ " FROM (Unidades INNER JOIN Productos ON Unidades.CodigoUnidad = Productos.CodigoUnidad) INNER JOIN (((Medicos INNER JOIN ((((Preparados1 INNER JOIN Preparados2 ON Preparados1.CodigoPreparado = Preparados2.CodigoPreparado) INNER JOIN Preparados1C ON (Preparados1.CodigoPreparado = Preparados1C.CodigoPreparado) AND (Preparados2.CodigoPreparado = Preparados1C.CodigoPreparado) AND (Preparados2.NumeroDeCopia = Preparados1C.NumeroDeCopia)) INNER JOIN VentasCaja ON Preparados1C.CodigoProforma = VentasCaja.CodigoProforma) INNER JOIN Clientes ON Preparados1.CodigoCliente = Clientes.CodigoCliente) ON Medicos.CodigoMedico = Preparados1.CodigoMedico) INNER JOIN VentasProforma ON (Preparados1.CodigoPreparado = VentasProforma.CodigoPreparado) AND (Clientes.CodigoCliente = VentasProforma.CodigoCliente) AND (Preparados1C.CodigoProforma = VentasProforma.CodigoProforma)) INNER JOIN VentasDetalle ON (VentasCaja.CodigoProforma = VentasDetalle.CodigoProforma) AND (VentasProforma.CodigoProforma = VentasDetalle.CodigoProforma) AND (VentasProforma.CodigoProforma = VentasDetalle.CodigoProforma)) ON Productos.CodigoProducto = VentasDetalle.CodigoProducto"
	      		+ " WHERE (((VentasCaja.CodigoVenta) Like '%"+codigoVenta+"%') AND ((VentasCaja.EstadoVenta)>0) "
	      				+ " AND ((Preparados1C.PreparadoMedico)=True) AND ((VentasCaja.TipoVenta)=1));");
	      
	      	//ResultSet rs = stmt.executeQuery("SELECT * FROM Productos WHERE (((Productos.Nombre2) Like '%"+descripcion+"%'));");
	      
	      while (rs.next()) {
	    	  String codReceta = rs.getString("CodReceta");
	    	  String codSubPrep = rs.getString("CodSubPrep");
	    	  String fechaReceta = rs.getString("FechaReceta");
	    	  String fechaPago = rs.getString("FechaPago");
	    	  String notaVenta = rs.getString("NotaVenta");
	    	  String proforma = rs.getString("Proforma");
	    	  String nomPreparado = rs.getString("NomPreparado");
	    	  String codigoCliente = rs.getString("CodigoCliente");
	    	  String cliente = rs.getString("Cliente");
	    	  String codigoMedico = rs.getString("CodigoMedico");
	    	  String medico = rs.getString("Medico");
	    	  String costoPreparado = rs.getString("CostoPreparado");
	    	  String valorDescuento = rs.getString("ValorDescuento");
	    	  String valorPreparado = rs.getString("ValorPreparado");
	    	  String moneda = rs.getString("Moneda");
	    	  String estadoVenta = rs.getString("EstadoVenta");
	    	  String precioPublico = rs.getString("PrecioPublico");
	    	  String cantidadEnvase = rs.getString("CantidadEnvase");
	    	  String precioUnitPreElab = rs.getString("PrecioUnitPreElab");
	    	  
	    	  
	    	  System.out.println(codReceta + "|" + codSubPrep+ "|" + fechaReceta + "|" + fechaPago + "|" + notaVenta + 
	    			  "|" + proforma + "|" + nomPreparado + "|" + codigoCliente+ "|" + cliente + 
	    			  "|" + codigoMedico + "|" + medico + "|" + costoPreparado + "|" + valorPreparado + 
	    			  "|" + moneda + "|" + estadoVenta + "|" + precioPublico + 
	    			  "|" + cantidadEnvase + "|" + precioUnitPreElab + "|" + valorDescuento + "\n");
	    	  
	    	  
	    	  listaPedido.add(new EstructuraDetallePedido(codReceta, codSubPrep, fechaReceta, fechaPago, notaVenta, proforma, 
	    			  nomPreparado, codigoCliente, cliente, codigoMedico, medico, new Double(costoPreparado), new Double(valorDescuento), new Double(valorPreparado), 
	    			  moneda, estadoVenta, new Double(precioPublico), new Double(cantidadEnvase), new Double(precioUnitPreElab), true));
	      }
	      
	      con.close();
	      
	      
	      return listaPedido;

	    } catch (Exception e) {
	      System.err.println("Error en buscarDetalle, Exception: "+e.getMessage());
	      return null;
	    }
	}	
	
	public List<EstructuraDetallePedido> selecccionarNotaVenta(String codigoVenta){
		
		List<EstructuraDetallePedido> listaPedido = new ArrayList<EstructuraDetallePedido>();
		Connection con = null;
	    try {
	      Class.forName("sun.jdbc.odbc.JdbcOdbcDriver") ;

	      // Connect with a url string
	      con = DriverManager.getConnection("jdbc:odbc:vfarmDB");
	      System.out.println("Connection ok..."+new Date());
	      
	      Statement stmt = con.createStatement();
	      
	      ResultSet rs = stmt.executeQuery("SELECT Preparados1.CodigoPreparado AS CodReceta, Preparados2.CodigoSubPreparado AS CodSubPrep, Preparados1C.Fecha AS FechaReceta, VentasCaja.FechaSysModif AS FechaPago, VentasCaja.CodigoVenta AS NotaVenta, VentasCaja.CodigoProforma AS Proforma, Preparados2.Descripcion AS NomPreparado, Clientes.CodigoCliente, Clientes.NombreCliente AS Cliente, Medicos.CodigoMedico, Medicos.NombreMedico AS Medico, Preparados2.CostoPreparado, Preparados2.CostoPreparado-((Preparados2.CostoPreparado*(VentasCaja.ValorDescuento*100)/VentasProforma.ValorPreparado)/100) AS ValorPreparado, IIf(VentasCaja.MonedaVenta=1,'Bs.','US$') AS Moneda, VentasCaja.EstadoVenta, VentasDetalle.PrecioPublico, Preparados2.CantidadEnvase, [Preparados2].[CostoPreparado]/[Preparados2].[CantidadEnvase] AS PrecioUnitPreElab, VentasCaja.ValorDescuento"
	      		+ " FROM (Unidades INNER JOIN Productos ON Unidades.CodigoUnidad = Productos.CodigoUnidad) INNER JOIN (((Medicos INNER JOIN ((((Preparados1 INNER JOIN Preparados2 ON Preparados1.CodigoPreparado = Preparados2.CodigoPreparado) INNER JOIN Preparados1C ON (Preparados1.CodigoPreparado = Preparados1C.CodigoPreparado) AND (Preparados2.CodigoPreparado = Preparados1C.CodigoPreparado) AND (Preparados2.NumeroDeCopia = Preparados1C.NumeroDeCopia)) INNER JOIN VentasCaja ON Preparados1C.CodigoProforma = VentasCaja.CodigoProforma) INNER JOIN Clientes ON Preparados1.CodigoCliente = Clientes.CodigoCliente) ON Medicos.CodigoMedico = Preparados1.CodigoMedico) INNER JOIN VentasProforma ON (Preparados1.CodigoPreparado = VentasProforma.CodigoPreparado) AND (Clientes.CodigoCliente = VentasProforma.CodigoCliente) AND (Preparados1C.CodigoProforma = VentasProforma.CodigoProforma)) INNER JOIN VentasDetalle ON (VentasCaja.CodigoProforma = VentasDetalle.CodigoProforma) AND (VentasProforma.CodigoProforma = VentasDetalle.CodigoProforma) AND (VentasProforma.CodigoProforma = VentasDetalle.CodigoProforma)) ON Productos.CodigoProducto = VentasDetalle.CodigoProducto"
	      		+ " WHERE  (((VentasCaja.CodigoVenta)= '"+codigoVenta+"') AND ((VentasCaja.EstadoVenta)>0) "
	      				+ " AND ((Preparados1C.PreparadoMedico)=True) AND ((VentasCaja.TipoVenta)=1));");

	      while (rs.next()) {
	    	  String codReceta = rs.getString("CodReceta");
	    	  String codSubPrep = rs.getString("CodSubPrep");
	    	  String fechaReceta = rs.getString("FechaReceta");
	    	  String fechaPago = rs.getString("FechaPago");
	    	  String notaVenta = rs.getString("NotaVenta");
	    	  String proforma = rs.getString("Proforma");
	    	  String nomPreparado = rs.getString("NomPreparado");
	    	  String codigoCliente = rs.getString("CodigoCliente");
	    	  String cliente = rs.getString("Cliente");
	    	  String codigoMedico = rs.getString("CodigoMedico");
	    	  String medico = rs.getString("Medico");
	    	  String costoPreparado = rs.getString("CostoPreparado");
	    	  String valorDescuento = rs.getString("ValorDescuento");
	    	  String valorPreparado = rs.getString("ValorPreparado");
	    	  String moneda = rs.getString("Moneda");
	    	  String estadoVenta = rs.getString("EstadoVenta");
	    	  String precioPublico = rs.getString("PrecioPublico");
	    	  String cantidadEnvase = rs.getString("CantidadEnvase");
	    	  String precioUnitPreElab = rs.getString("PrecioUnitPreElab");
	    	  
	    	  
	    	  System.out.println(codReceta + "|" + codSubPrep+ "|" + fechaReceta + "|" + fechaPago + "|" + notaVenta + 
	    			  "|" + proforma + "|" + nomPreparado + "|" + codigoCliente+ "|" + cliente + 
	    			  "|" + codigoMedico + "|" + medico + "|" + costoPreparado + "|" + valorPreparado + 
	    			  "|" + moneda + "|" + estadoVenta + "|" + precioPublico + 
	    			  "|" + cantidadEnvase + "|" + precioUnitPreElab + "|" + valorDescuento + "\n");
	    	  
	    	  
	    	  listaPedido.add(new EstructuraDetallePedido(codReceta, codSubPrep, fechaReceta, fechaPago, notaVenta, proforma, 
	    			  nomPreparado, codigoCliente, cliente, codigoMedico, medico, new Double(costoPreparado), new Double(valorDescuento), new Double(valorPreparado), 
	    			  moneda, estadoVenta, new Double(precioPublico), new Double(cantidadEnvase), new Double(precioUnitPreElab), true));
	      }
	      
	      con.close();
	      
	      
	      return listaPedido;

	    } catch (Exception e) {
	      System.err.println("Error en buscarDetalle, Exception: "+e.getMessage());
	      return null;
	    }
	}	
	
	
	public List<EstructuraDetallePedido> traerUltimasNotasVenta(){
		
		List<EstructuraDetallePedido> listaPedido = new ArrayList<EstructuraDetallePedido>();
		Connection con = null;
	    try {
	      Class.forName("sun.jdbc.odbc.JdbcOdbcDriver") ;

	      // Connect with a url string
	      con = DriverManager.getConnection("jdbc:odbc:vfarmDB");
	      System.out.println("Connection ok..."+new Date());
	      
	      Statement stmt = con.createStatement();
	      
	      ResultSet rs = stmt.executeQuery("SELECT VentasCaja.CodigoVenta AS NotaVenta, VentasCaja.CodigoProforma AS Proforma, VentasCaja.FechaSysModif AS FechaPago, Clientes.CodigoCliente AS CodigoCliente, Clientes.NombreCliente AS Cliente, SUM([Preparados2.CostoPreparado]) as TotalCosto"
	      		+ " FROM (Unidades INNER JOIN Productos ON Unidades.CodigoUnidad = Productos.CodigoUnidad) INNER JOIN (((Medicos INNER JOIN ((((Preparados1 INNER JOIN Preparados2 ON Preparados1.CodigoPreparado = Preparados2.CodigoPreparado) INNER JOIN Preparados1C ON (Preparados2.NumeroDeCopia = Preparados1C.NumeroDeCopia) AND (Preparados2.CodigoPreparado = Preparados1C.CodigoPreparado) AND (Preparados1.CodigoPreparado = Preparados1C.CodigoPreparado)) INNER JOIN VentasCaja ON Preparados1C.CodigoProforma = VentasCaja.CodigoProforma) INNER JOIN Clientes ON Preparados1.CodigoCliente = Clientes.CodigoCliente) ON Medicos.CodigoMedico = Preparados1.CodigoMedico) INNER JOIN VentasProforma ON (Preparados1C.CodigoProforma = VentasProforma.CodigoProforma) AND (Clientes.CodigoCliente = VentasProforma.CodigoCliente) AND (Preparados1.CodigoPreparado = VentasProforma.CodigoPreparado)) INNER JOIN VentasDetalle ON (VentasProforma.CodigoProforma = VentasDetalle.CodigoProforma) AND (VentasProforma.CodigoProforma = VentasDetalle.CodigoProforma) AND (VentasDetalle.CodigoProforma = VentasCaja.CodigoProforma)) ON Productos.CodigoProducto = VentasDetalle.CodigoProducto"
	      		+ " WHERE (((VentasCaja.EstadoVenta)>0) AND ((Preparados1C.PreparadoMedico)=True) AND ((VentasCaja.TipoVenta)=1))"
	      		+ " group by VentasCaja.CodigoVenta, VentasCaja.CodigoProforma, VentasCaja.FechaSysModif, Clientes.CodigoCliente, Clientes.NombreCliente ORDER BY VentasCaja.FechaSysModif DESC;");
	      
	      int count = 0;
	      while (rs.next()) {
	    	  
	    	  if(count < 50){
	    		  String notaVenta = rs.getString("NotaVenta");
		    	  String proforma = rs.getString("Proforma");
		    	  String fechaPago = rs.getString("FechaPago");
		    	  String codigoCliente = rs.getString("CodigoCliente");
		    	  String cliente = rs.getString("Cliente");
		    	  double montoTotal = Double.valueOf(rs.getString("TotalCosto"));
		    	  
		    	  System.out.println(notaVenta + "|" + proforma+ "|" + fechaPago + "|" + fechaPago + "|" + codigoCliente + 
		    			  "|" + cliente + "|" + montoTotal +"\n");
		    	  
		    	  
		    	  listaPedido.add(new EstructuraDetallePedido(obtenerRandom(), fechaPago, notaVenta, proforma, codigoCliente, cliente, montoTotal));
		    	  count++;
	    	  }else{
	    		  System.out.println("Salir... Break...");
	    		  break;
	    		  
	    	  }
	    	  
	      }
	      
	      con.close();
	      
	      
	      return listaPedido;

	    } catch (Exception e) {
	      System.err.println("Error en traerUltimasNotasVenta, Exception: "+e.getMessage());
	      return null;
	    }
	}	
	
	public static Date incrementarMeses(Date fecha, int incremento){
		try {
			System.out.println("Ingresar incrementarMeses..."+fecha+" ---- "+incremento);
			
			return fecha;
			
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
			System.out.println("Error en incrementarMeses: "+e.getMessage());
			return null;
		}
	}
	
	public int obtenerRandom(){
		return randInt(100000, 999999);
	}
	
	
	public int randInt(int min, int max) {
	    Random rand = new Random();
	    int randomNum = rand.nextInt((max - min) + 1) + min;
	    return randomNum;
	}

	
	public List<EstructuraProducto> buscarProducto(String descripcion){
		
		List<EstructuraProducto> listaProducto = new ArrayList<EstructuraProducto>();
		Connection con = null;
	    try {
	      Class.forName("sun.jdbc.odbc.JdbcOdbcDriver") ;

	      // Connect with a url string
	      con = DriverManager.getConnection("jdbc:odbc:vfarmDB");
	      System.out.println("Connection ok..."+new Date());
	      
	      Statement stmt = con.createStatement();
	      
	      ResultSet rs = stmt.executeQuery("SELECT * FROM Productos WHERE (((Productos.Nombre2) Like '%"+descripcion+"%'));");

	      while (rs.next()) {

	    		 String codigoProducto = rs.getString("CodigoProducto");
	    		 int codigoTipoProducto = Integer.valueOf(rs.getString("CodigoTipoProducto"));
	    		 String codigoProveedor = rs.getString("CodigoProveedor");
	    		 String codigoLinea = rs.getString("CodigoLinea");
	    		 String codigoOriginal = rs.getString("CodigoOriginal");
	    		 String nombre1 = rs.getString("Nombre1");
	    		 String nombre2 = rs.getString("Nombre2");
	    		 String descripcionProducto = rs.getString("Descripcion");
	    		 String codigoGrupo = rs.getString("CodigoGrupo");
	    		 String codigoUnidad = rs.getString("CodigoUnidad");
	    		 String codigoBaja = rs.getString("CodigoBaja");
	    		 String stockActual = rs.getString("StockActual");
	    		 double stockMinimo = Double.valueOf(rs.getString("StockMinimo"));
	    		 double stockMaximo = Double.valueOf(rs.getString("StockMaximo"));
	    		 boolean alarmaStock = Boolean.valueOf(rs.getString("AlarmaStockMinimo"));
	    		 double precioUnitarioVenta = Double.valueOf(rs.getString("PrecioUnitarioVenta"));
	    		 double precioUnitarioCompra = Double.valueOf(rs.getString("PrecioUnitarioCompra"));
	    		 double margenVenta = Double.valueOf(rs.getString("MargenVenta"));
	    		 int monedaProducto = Integer.valueOf(rs.getString("MonedaProducto"));
	    		 double precioDolar = Double.valueOf(rs.getString("PrecioDolar"));
	    		 boolean actualizarPrecioDolar = Boolean.valueOf(rs.getString("ActualizaPrecioDolar"));
	    		 String fechaVencimiento = rs.getString("FechaVencimiento");
	    		 boolean estado = Boolean.valueOf(rs.getString("Estado"));
	    		 String fechaUltimoMov = rs.getString("FechaUltimoMov");
	    		 String usuario = rs.getString("Usuario");
	    		 String fechaModif = rs.getString("FechaModif");
	    		 String codigoBarra = rs.getString("CodigoBarra");
	    		 String precioXDefecto = rs.getString("PrecioXDefecto");
	    		 String monedaXDefecto = rs.getString("MonedaXDefecto");
	    		 double precioUnitarioVenta2 = Double.valueOf(rs.getString("PrecioUnitarioVenta2"));
	    		 double precioUnitarioVenta3 = Double.valueOf(rs.getString("PrecioUnitarioVenta3"));
	    		 double precioUnitarioVentaCaja = Double.valueOf(rs.getString("PrecioUnitarioVentaCaja"));
	    		 int cantidadUnidadesCaja = Integer.valueOf(rs.getString("CantidadUnidadesCaja"));
	    		 String codigoUnidadCaja = rs.getString("CodigoUnidadCaja");
	    		 double stockComprometido = Double.valueOf(rs.getString("StockComprometido"));
	    		 String codigoContable = rs.getString("CodigoContable");
	    		 String fechaRegistro = rs.getString("FechaRegistro");
	    		 String stRegistro = rs.getString("STRegistro");

	    		 System.out.println(codigoProducto + "|" + codigoTipoProducto+ "|" + codigoProveedor + "|" + codigoLinea + "|" + codigoOriginal + 
	    			  "|" + nombre1 + "|" + nombre2 + "|" + descripcionProducto+ "|" + codigoGrupo + 
	    			  "|" + codigoUnidad + "|" + codigoBaja + "|" + stockActual + "|" + stockMinimo + 
	    			  "|" + stockMaximo + "|" + alarmaStock + "|" + precioUnitarioVenta + 
	    			  "|" + precioUnitarioCompra + "|" + margenVenta + "|" + monedaProducto + 
	    			  "|" + precioDolar + "|" + actualizarPrecioDolar + "|" + fechaVencimiento + 
	    			  "|" + estado + "|" + fechaUltimoMov + "|" + usuario + 
	    			  "|" + fechaModif + "|" + codigoBarra + "|" + precioXDefecto + "|" + monedaXDefecto + "|" + precioUnitarioVenta2 + "|" + precioUnitarioVenta3 + 
	    			  "|" + precioUnitarioVentaCaja + "|" + cantidadUnidadesCaja + "|" + codigoUnidadCaja + "|" + stockComprometido + "|" + codigoContable + "|" + fechaRegistro + 
	    			  "|" + stRegistro + "\n");
	    	  
	    	  
	    	  listaProducto.add(new EstructuraProducto(codigoProducto, codigoTipoProducto, codigoProveedor, codigoLinea, codigoOriginal, nombre1, nombre2, descripcionProducto, codigoGrupo, codigoUnidad, codigoBaja, stockActual, stockMinimo, stockMaximo, alarmaStock, precioUnitarioVenta, precioUnitarioCompra, margenVenta, monedaProducto, precioDolar, actualizarPrecioDolar, fechaVencimiento, estado, fechaUltimoMov, usuario, fechaModif, codigoBarra, precioXDefecto, monedaXDefecto, precioUnitarioVenta2, precioUnitarioVenta3, precioUnitarioVentaCaja, cantidadUnidadesCaja, codigoUnidadCaja, stockComprometido, codigoContable, fechaRegistro, stRegistro, 1, false));
	      }
	      
	      con.close();
	      
	      
	      return listaProducto;

	    } catch (Exception e) {
	      e.printStackTrace();
	      System.err.println("Error en buscarProducto, Exception: "+e.getMessage());
	      return null;
	    }
	}
	
	
	public List<EstructuraProducto> buscarUltimos100Producto(){
		
		List<EstructuraProducto> listaProducto = new ArrayList<EstructuraProducto>();
		Connection con = null;
	    try {
	      Class.forName("sun.jdbc.odbc.JdbcOdbcDriver") ;

	      // Connect with a url string
	      con = DriverManager.getConnection("jdbc:odbc:vfarmDB");
	      System.out.println("Connection ok..."+new Date());
	      
	      Statement stmt = con.createStatement();
	      
	      ResultSet rs = stmt.executeQuery("SELECT * FROM Productos;");
	      int count = 0;
	      while (rs.next()) {

	    		 String codigoProducto = rs.getString("CodigoProducto");
	    		 int codigoTipoProducto = Integer.valueOf(rs.getString("CodigoTipoProducto"));
	    		 String codigoProveedor = rs.getString("CodigoProveedor");
	    		 String codigoLinea = rs.getString("CodigoLinea");
	    		 String codigoOriginal = rs.getString("CodigoOriginal");
	    		 String nombre1 = rs.getString("Nombre1");
	    		 String nombre2 = rs.getString("Nombre2");
	    		 String descripcionProducto = rs.getString("Descripcion");
	    		 String codigoGrupo = rs.getString("CodigoGrupo");
	    		 String codigoUnidad = rs.getString("CodigoUnidad");
	    		 String codigoBaja = rs.getString("CodigoBaja");
	    		 String stockActual = rs.getString("StockActual");
	    		 double stockMinimo = Double.valueOf(rs.getString("StockMinimo"));
	    		 double stockMaximo = Double.valueOf(rs.getString("StockMaximo"));
	    		 boolean alarmaStock = Boolean.valueOf(rs.getString("AlarmaStockMinimo"));
	    		 double precioUnitarioVenta = Double.valueOf(rs.getString("PrecioUnitarioVenta"));
	    		 double precioUnitarioCompra = Double.valueOf(rs.getString("PrecioUnitarioCompra"));
	    		 double margenVenta = Double.valueOf(rs.getString("MargenVenta"));
	    		 int monedaProducto = Integer.valueOf(rs.getString("MonedaProducto"));
	    		 double precioDolar = Double.valueOf(rs.getString("PrecioDolar"));
	    		 boolean actualizarPrecioDolar = Boolean.valueOf(rs.getString("ActualizaPrecioDolar"));
	    		 String fechaVencimiento = rs.getString("FechaVencimiento");
	    		 boolean estado = Boolean.valueOf(rs.getString("Estado"));
	    		 String fechaUltimoMov = rs.getString("FechaUltimoMov");
	    		 String usuario = rs.getString("Usuario");
	    		 String fechaModif = rs.getString("FechaModif");
	    		 String codigoBarra = rs.getString("CodigoBarra");
	    		 String precioXDefecto = rs.getString("PrecioXDefecto");
	    		 String monedaXDefecto = rs.getString("MonedaXDefecto");
	    		 double precioUnitarioVenta2 = Double.valueOf(rs.getString("PrecioUnitarioVenta2"));
	    		 double precioUnitarioVenta3 = Double.valueOf(rs.getString("PrecioUnitarioVenta3"));
	    		 double precioUnitarioVentaCaja = Double.valueOf(rs.getString("PrecioUnitarioVentaCaja"));
	    		 int cantidadUnidadesCaja = Integer.valueOf(rs.getString("CantidadUnidadesCaja"));
	    		 String codigoUnidadCaja = rs.getString("CodigoUnidadCaja");
	    		 double stockComprometido = Double.valueOf(rs.getString("StockComprometido"));
	    		 String codigoContable = rs.getString("CodigoContable");
	    		 String fechaRegistro = rs.getString("FechaRegistro");
	    		 String stRegistro = rs.getString("STRegistro");

	    		 System.out.println(codigoProducto + "|" + codigoTipoProducto+ "|" + codigoProveedor + "|" + codigoLinea + "|" + codigoOriginal + 
	    			  "|" + nombre1 + "|" + nombre2 + "|" + descripcionProducto+ "|" + codigoGrupo + 
	    			  "|" + codigoUnidad + "|" + codigoBaja + "|" + stockActual + "|" + stockMinimo + 
	    			  "|" + stockMaximo + "|" + alarmaStock + "|" + precioUnitarioVenta + 
	    			  "|" + precioUnitarioCompra + "|" + margenVenta + "|" + monedaProducto + 
	    			  "|" + precioDolar + "|" + actualizarPrecioDolar + "|" + fechaVencimiento + 
	    			  "|" + estado + "|" + fechaUltimoMov + "|" + usuario + 
	    			  "|" + fechaModif + "|" + codigoBarra + "|" + precioXDefecto + "|" + monedaXDefecto + "|" + precioUnitarioVenta2 + "|" + precioUnitarioVenta3 + 
	    			  "|" + precioUnitarioVentaCaja + "|" + cantidadUnidadesCaja + "|" + codigoUnidadCaja + "|" + stockComprometido + "|" + codigoContable + "|" + fechaRegistro + 
	    			  "|" + stRegistro + "\n");
	    	  
	    	  
	    	  listaProducto.add(new EstructuraProducto(codigoProducto, codigoTipoProducto, codigoProveedor, codigoLinea, codigoOriginal, nombre1, nombre2, descripcionProducto, codigoGrupo, codigoUnidad, codigoBaja, stockActual, stockMinimo, stockMaximo, alarmaStock, precioUnitarioVenta, precioUnitarioCompra, margenVenta, monedaProducto, precioDolar, actualizarPrecioDolar, fechaVencimiento, estado, fechaUltimoMov, usuario, fechaModif, codigoBarra, precioXDefecto, monedaXDefecto, precioUnitarioVenta2, precioUnitarioVenta3, precioUnitarioVentaCaja, cantidadUnidadesCaja, codigoUnidadCaja, stockComprometido, codigoContable, fechaRegistro, stRegistro, 1, false));
	    	  
	    	  
	    	  if(count==100){
	    		  break;
	    	  }
	    	  
	    	  count++;
	      }
	      
	      con.close();
	      
	      
	      return listaProducto;

	    } catch (Exception e) {
	      e.printStackTrace();
	      System.err.println("Error en buscarProducto, Exception: "+e.getMessage());
	      return null;
	    }
	}
	
	
	
	public String obtenerFormatoMMDDYYYY(Date date){
    	try {
    		String DATE_FORMAT = "MM/dd/yyyy";
			SimpleDateFormat sdf = new SimpleDateFormat(DATE_FORMAT);
    		return sdf.format(date);
    		
		} catch (Exception e) {
			// TODO: handle exception
			System.out.println("Error en obtenerFormatoMMDDYYYY: "+e.getMessage());
			return null;
		}
	}
		

	public static Date convertirFecha(String cadenaFecha){
		try {
			System.out.println("Ingreso a ConvertirFecha: "+cadenaFecha);
//			 String cadenaFecha = "2014-12-03 00:00:00";
//			 SimpleDateFormat sdf = new SimpleDateFormat("MM/dd/yyyy H:mm:ss");
			 SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd 00:00:00");
	         Date dateElaboracion;
//	         System.out.println("Fecha String: "+cadenaFecha);
	         dateElaboracion = sdf.parse(cadenaFecha);
//	         System.out.println("Fecha: "+dateElaboracion);

	         return dateElaboracion;
		} catch (Exception e) {
			// TODO: handle exception
			System.out.println("Error en convertirFecha: "+cadenaFecha+e.getMessage());
			return null;
		}
	}
	
	public static Calendar dateToCalendar(Date date){ 
		  Calendar cal = Calendar.getInstance();
		  cal.setTime(date);
		  return cal;
	}
	

	public static void main(String[] args){
		try {
			 String cadenaFecha = "2014-06-25 00:00:00";
//			 SimpleDateFormat sdf = new SimpleDateFormat("MM/dd/yyyy H:mm:ss");
			 SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd 00:00:00");
	         Date dateElaboracion;

//	         System.out.println("Fecha String: "+cadenaFecha);
	         dateElaboracion = sdf.parse(cadenaFecha);

	         
	         Calendar fecha = dateToCalendar(dateElaboracion);
	         int gestion = fecha.get(Calendar.YEAR);
	         System.out.println("gestion: "+gestion);
	         
//	         traerEtiquetasFechaElaboracion(dateElaboracion, gestion);
	         
	         
	         
//	         if(dateElaboracion.equals(dateElaboracion2)){
//	        	 System.out.println("Fecha Correcta");
//	         }
	         
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}
		
		
         
//		traerEtiquetas(2014);
		
		
		
		
//		List<EstructuraProducto> lista = 
//		for(EstructuraProducto producto:lista){
//			System.out.println("producto: "+producto.getNombre2());
//		}
		
	}
	
}
