/*
* EstructuraProducto.java	1.0 2014/09/19
*
* Copyright (c) 2014 by QBIT SRL, Inc. All Rights Reserved.
* 
* QBIT SRL grants you ("Licensee") a non-exclusive, royalty free, license to use,
* modify and redistribute this software in source and binary code form,
* provided that i) this copyright notice and license appear on all copies of
* the software; and ii) Licensee does not utilize the software in a manner
* which is disparaging to QBIT SRL.
* 
* Autor: Isai Galarza
* 
*/
package bo.buffalo.util;

import java.io.Serializable;

public class EstructuraProducto implements Serializable{

	private static final long serialVersionUID = 8463224206632448844L;
	
	private String codigoProducto;
	private int codigoTipoProducto;
	private String codigoProveedor;
	private String codigoLinea;
	private String codigoOriginal;
	private String nombre1;
	private String nombre2;
	private String descripcion;
	private String codigoGrupo;
	private String codigoUnidad;
	private String codigoBaja;
	private String stockActual;
	private double stockMinimo;
	private double stockMaximo;
	private boolean alarmaStock;
	private double precioUnitarioVenta;
	private double precioUnitarioCompra;
	private double margenVenta;
	private int monedaProducto;
	private double precioDolar;
	private boolean actualizarPrecioDolar;
	private String fechaVencimiento;
	private boolean estado;
	private String fechaUltimoMov;
	private String usuario;
	private String fechaModif;
	private String codigoBarra;
	private String precioXDefecto;
	private String monedaXDefecto;
	private double precioUnitarioVenta2;
	private double precioUnitarioVenta3;
	private double precioUnitarioVentaCaja;
	private int cantidadUnidadesCaja;
	private String codigoUnidadCaja;
	private double stockComprometido;
	private String codigoContable;
	private String fechaRegistro;
	private String stRegistro;
	private int cantidad;
	private boolean selected;
	private double costoTotal;
	
	//constructor
	public EstructuraProducto(){
		
	}
	
	public EstructuraProducto(String codigoProducto, int codigoTipoProducto,
			String codigoProveedor, String codigoLinea, String codigoOriginal,
			String nombre1, String nombre2, String descripcion,
			String codigoGrupo, String codigoUnidad, String codigoBaja,
			String stockActual, double stockMinimo, double stockMaximo,
			boolean alarmaStock, double precioUnitarioVenta,
			double precioUnitarioCompra, double margenVenta,
			int monedaProducto, double precioDolar,
			boolean actualizarPrecioDolar, String fechaVencimiento,
			boolean estado, String fechaUltimoMov, String usuario,
			String fechaModif, String codigoBarra, String precioXDefecto,
			String monedaXDefecto, double precioUnitarioVenta2,
			double precioUnitarioVenta3, double precioUnitarioVentaCaja,
			int cantidadUnidadesCaja, String codigoUnidadCaja,
			double stockComprometido, String codigoContable,
			String fechaRegistro, String stRegistro) {
		super();
		this.codigoProducto = codigoProducto;
		this.codigoTipoProducto = codigoTipoProducto;
		this.codigoProveedor = codigoProveedor;
		this.codigoLinea = codigoLinea;
		this.codigoOriginal = codigoOriginal;
		this.nombre1 = nombre1;
		this.nombre2 = nombre2;
		this.descripcion = descripcion;
		this.codigoGrupo = codigoGrupo;
		this.codigoUnidad = codigoUnidad;
		this.codigoBaja = codigoBaja;
		this.stockActual = stockActual;
		this.stockMinimo = stockMinimo;
		this.stockMaximo = stockMaximo;
		this.alarmaStock = alarmaStock;
		this.precioUnitarioVenta = precioUnitarioVenta;
		this.precioUnitarioCompra = precioUnitarioCompra;
		this.margenVenta = margenVenta;
		this.monedaProducto = monedaProducto;
		this.precioDolar = precioDolar;
		this.actualizarPrecioDolar = actualizarPrecioDolar;
		this.fechaVencimiento = fechaVencimiento;
		this.estado = estado;
		this.fechaUltimoMov = fechaUltimoMov;
		this.usuario = usuario;
		this.fechaModif = fechaModif;
		this.codigoBarra = codigoBarra;
		this.precioXDefecto = precioXDefecto;
		this.monedaXDefecto = monedaXDefecto;
		this.precioUnitarioVenta2 = precioUnitarioVenta2;
		this.precioUnitarioVenta3 = precioUnitarioVenta3;
		this.precioUnitarioVentaCaja = precioUnitarioVentaCaja;
		this.cantidadUnidadesCaja = cantidadUnidadesCaja;
		this.codigoUnidadCaja = codigoUnidadCaja;
		this.stockComprometido = stockComprometido;
		this.codigoContable = codigoContable;
		this.fechaRegistro = fechaRegistro;
		this.stRegistro = stRegistro;
	}
	
	public EstructuraProducto(String codigoProducto, int codigoTipoProducto,
			String codigoProveedor, String codigoLinea, String codigoOriginal,
			String nombre1, String nombre2, String descripcion,
			String codigoGrupo, String codigoUnidad, String codigoBaja,
			String stockActual, double stockMinimo, double stockMaximo,
			boolean alarmaStock, double precioUnitarioVenta,
			double precioUnitarioCompra, double margenVenta,
			int monedaProducto, double precioDolar,
			boolean actualizarPrecioDolar, String fechaVencimiento,
			boolean estado, String fechaUltimoMov, String usuario,
			String fechaModif, String codigoBarra, String precioXDefecto,
			String monedaXDefecto, double precioUnitarioVenta2,
			double precioUnitarioVenta3, double precioUnitarioVentaCaja,
			int cantidadUnidadesCaja, String codigoUnidadCaja,
			double stockComprometido, String codigoContable,
			String fechaRegistro, String stRegistro, int cantidad, boolean selected) {
		super();
		this.codigoProducto = codigoProducto;
		this.codigoTipoProducto = codigoTipoProducto;
		this.codigoProveedor = codigoProveedor;
		this.codigoLinea = codigoLinea;
		this.codigoOriginal = codigoOriginal;
		this.nombre1 = nombre1;
		this.nombre2 = nombre2;
		this.descripcion = descripcion;
		this.codigoGrupo = codigoGrupo;
		this.codigoUnidad = codigoUnidad;
		this.codigoBaja = codigoBaja;
		this.stockActual = stockActual;
		this.stockMinimo = stockMinimo;
		this.stockMaximo = stockMaximo;
		this.alarmaStock = alarmaStock;
		this.precioUnitarioVenta = precioUnitarioVenta;
		this.precioUnitarioCompra = precioUnitarioCompra;
		this.margenVenta = margenVenta;
		this.monedaProducto = monedaProducto;
		this.precioDolar = precioDolar;
		this.actualizarPrecioDolar = actualizarPrecioDolar;
		this.fechaVencimiento = fechaVencimiento;
		this.estado = estado;
		this.fechaUltimoMov = fechaUltimoMov;
		this.usuario = usuario;
		this.fechaModif = fechaModif;
		this.codigoBarra = codigoBarra;
		this.precioXDefecto = precioXDefecto;
		this.monedaXDefecto = monedaXDefecto;
		this.precioUnitarioVenta2 = precioUnitarioVenta2;
		this.precioUnitarioVenta3 = precioUnitarioVenta3;
		this.precioUnitarioVentaCaja = precioUnitarioVentaCaja;
		this.cantidadUnidadesCaja = cantidadUnidadesCaja;
		this.codigoUnidadCaja = codigoUnidadCaja;
		this.stockComprometido = stockComprometido;
		this.codigoContable = codigoContable;
		this.fechaRegistro = fechaRegistro;
		this.stRegistro = stRegistro;
		this.cantidad = cantidad;
		this.selected = selected;
	}
	
	//get and set
	public String getCodigoProducto() {
		return codigoProducto;
	}
	public void setCodigoProducto(String codigoProducto) {
		this.codigoProducto = codigoProducto;
	}
	public int getCodigoTipoProducto() {
		return codigoTipoProducto;
	}
	public void setCodigoTipoProducto(int codigoTipoProducto) {
		this.codigoTipoProducto = codigoTipoProducto;
	}
	public String getCodigoProveedor() {
		return codigoProveedor;
	}
	public void setCodigoProveedor(String codigoProveedor) {
		this.codigoProveedor = codigoProveedor;
	}
	public String getCodigoLinea() {
		return codigoLinea;
	}
	public void setCodigoLinea(String codigoLinea) {
		this.codigoLinea = codigoLinea;
	}
	public String getCodigoOriginal() {
		return codigoOriginal;
	}
	public void setCodigoOriginal(String codigoOriginal) {
		this.codigoOriginal = codigoOriginal;
	}
	public String getNombre1() {
		return nombre1;
	}
	public void setNombre1(String nombre1) {
		this.nombre1 = nombre1;
	}
	public String getNombre2() {
		return nombre2;
	}
	public void setNombre2(String nombre2) {
		this.nombre2 = nombre2;
	}
	public String getDescripcion() {
		return descripcion;
	}
	public void setDescripcion(String descripcion) {
		this.descripcion = descripcion;
	}
	public String getCodigoGrupo() {
		return codigoGrupo;
	}
	public void setCodigoGrupo(String codigoGrupo) {
		this.codigoGrupo = codigoGrupo;
	}
	public String getCodigoUnidad() {
		return codigoUnidad;
	}
	public void setCodigoUnidad(String codigoUnidad) {
		this.codigoUnidad = codigoUnidad;
	}
	public String getCodigoBaja() {
		return codigoBaja;
	}
	public void setCodigoBaja(String codigoBaja) {
		this.codigoBaja = codigoBaja;
	}
	public String getStockActual() {
		return stockActual;
	}
	public void setStockActual(String stockActual) {
		this.stockActual = stockActual;
	}
	public double getStockMinimo() {
		return stockMinimo;
	}
	public void setStockMinimo(double stockMinimo) {
		this.stockMinimo = stockMinimo;
	}
	public double getStockMaximo() {
		return stockMaximo;
	}
	public void setStockMaximo(double stockMaximo) {
		this.stockMaximo = stockMaximo;
	}
	public boolean isAlarmaStock() {
		return alarmaStock;
	}
	public void setAlarmaStock(boolean alarmaStock) {
		this.alarmaStock = alarmaStock;
	}
	public double getPrecioUnitarioVenta() {
		return precioUnitarioVenta;
	}
	public void setPrecioUnitarioVenta(double precioUnitarioVenta) {
		this.precioUnitarioVenta = precioUnitarioVenta;
	}
	public double getPrecioUnitarioCompra() {
		return precioUnitarioCompra;
	}
	public void setPrecioUnitarioCompra(double precioUnitarioCompra) {
		this.precioUnitarioCompra = precioUnitarioCompra;
	}
	public double getMargenVenta() {
		return margenVenta;
	}
	public void setMargenVenta(double margenVenta) {
		this.margenVenta = margenVenta;
	}
	public int getMonedaProducto() {
		return monedaProducto;
	}
	public void setMonedaProducto(int monedaProducto) {
		this.monedaProducto = monedaProducto;
	}
	public double getPrecioDolar() {
		return precioDolar;
	}
	public void setPrecioDolar(double precioDolar) {
		this.precioDolar = precioDolar;
	}
	public boolean isActualizarPrecioDolar() {
		return actualizarPrecioDolar;
	}
	public void setActualizarPrecioDolar(boolean actualizarPrecioDolar) {
		this.actualizarPrecioDolar = actualizarPrecioDolar;
	}
	public String getFechaVencimiento() {
		return fechaVencimiento;
	}
	public void setFechaVencimiento(String fechaVencimiento) {
		this.fechaVencimiento = fechaVencimiento;
	}
	public boolean isEstado() {
		return estado;
	}
	public void setEstado(boolean estado) {
		this.estado = estado;
	}
	public String getFechaUltimoMov() {
		return fechaUltimoMov;
	}
	public void setFechaUltimoMov(String fechaUltimoMov) {
		this.fechaUltimoMov = fechaUltimoMov;
	}
	public String getUsuario() {
		return usuario;
	}
	public void setUsuario(String usuario) {
		this.usuario = usuario;
	}
	public String getFechaModif() {
		return fechaModif;
	}
	public void setFechaModif(String fechaModif) {
		this.fechaModif = fechaModif;
	}
	public String getCodigoBarra() {
		return codigoBarra;
	}
	public void setCodigoBarra(String codigoBarra) {
		this.codigoBarra = codigoBarra;
	}
	public String getPrecioXDefecto() {
		return precioXDefecto;
	}
	public void setPrecioXDefecto(String precioXDefecto) {
		this.precioXDefecto = precioXDefecto;
	}
	public String getMonedaXDefecto() {
		return monedaXDefecto;
	}
	public void setMonedaXDefecto(String monedaXDefecto) {
		this.monedaXDefecto = monedaXDefecto;
	}
	public double getPrecioUnitarioVenta2() {
		return precioUnitarioVenta2;
	}
	public void setPrecioUnitarioVenta2(double precioUnitarioVenta2) {
		this.precioUnitarioVenta2 = precioUnitarioVenta2;
	}
	public double getPrecioUnitarioVenta3() {
		return precioUnitarioVenta3;
	}
	public void setPrecioUnitarioVenta3(double precioUnitarioVenta3) {
		this.precioUnitarioVenta3 = precioUnitarioVenta3;
	}
	public double getPrecioUnitarioVentaCaja() {
		return precioUnitarioVentaCaja;
	}
	public void setPrecioUnitarioVentaCaja(double precioUnitarioVentaCaja) {
		this.precioUnitarioVentaCaja = precioUnitarioVentaCaja;
	}
	public int getCantidadUnidadesCaja() {
		return cantidadUnidadesCaja;
	}
	public void setCantidadUnidadesCaja(int cantidadUnidadesCaja) {
		this.cantidadUnidadesCaja = cantidadUnidadesCaja;
	}
	public String getCodigoUnidadCaja() {
		return codigoUnidadCaja;
	}
	public void setCodigoUnidadCaja(String codigoUnidadCaja) {
		this.codigoUnidadCaja = codigoUnidadCaja;
	}
	public double getStockComprometido() {
		return stockComprometido;
	}
	public void setStockComprometido(double stockComprometido) {
		this.stockComprometido = stockComprometido;
	}
	public String getCodigoContable() {
		return codigoContable;
	}
	public void setCodigoContable(String codigoContable) {
		this.codigoContable = codigoContable;
	}
	public String getFechaRegistro() {
		return fechaRegistro;
	}
	public void setFechaRegistro(String fechaRegistro) {
		this.fechaRegistro = fechaRegistro;
	}
	public String getStRegistro() {
		return stRegistro;
	}
	public void setStRegistro(String stRegistro) {
		this.stRegistro = stRegistro;
	}

	public boolean isSelected() {
		return selected;
	}

	public void setSelected(boolean selected) {
		this.selected = selected;
	}

	public int getCantidad() {
		return cantidad;
	}

	public void setCantidad(int cantidad) {
		this.cantidad = cantidad;
	}

	public double getCostoTotal() {
		try {
			costoTotal = precioUnitarioVenta * cantidad;
			return costoTotal;
		} catch (Exception e) {
			// TODO: handle exception
			System.err.println("Error en getCostoTotal: " + e.getMessage());
			return 0;
		}
		
	}

	public void setCostoTotal(double costoTotal) {
		this.costoTotal = costoTotal;
	}
	
	
}
