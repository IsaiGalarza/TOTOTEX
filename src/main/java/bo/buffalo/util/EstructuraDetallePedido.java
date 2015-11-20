/*
* EstructuraDetallePedido.java	1.0 2014/09/19
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

public class EstructuraDetallePedido implements Serializable{

	private static final long serialVersionUID = -4288871422781266352L;
	
	private int id;
	private  String codReceta;
	private  String codSubPrep;
	private  String fechaReceta;
	private  String fechaPago;
	private  String notaVenta;
	private  String proforma;
	private  String nomPreparado;
	private  String codigoCliente;
	private  String cliente;
	private  String codigoMedico;
	private  String medico;
	private  double costoPreparado;
	private  double valorDescuento;
	private  double valorPreparado;
	private  String moneda;
	private  String estadoVenta;
	private  double precioPublico;
	private  double cantidadEnvase;
	private  double precioUnitPreElab;
	private  double montoTotal;
	private boolean selected;
	
	public EstructuraDetallePedido(){
		
	}
	
	//constructor
	public EstructuraDetallePedido(String codReceta, String codSubPrep,
			String fechaReceta, String fechaPago, String notaVenta,
			String proforma, String nomPreparado, String codigoCliente,
			String cliente, String codigoMedico, String medico,
			double costoPreparado, double valorDescuento, double valorPreparado, String moneda,
			String estadoVenta, double precioPublico, double cantidadEnvase,
			double precioUnitPreElab) {
		super();
		this.codReceta = codReceta;
		this.codSubPrep = codSubPrep;
		this.fechaReceta = fechaReceta;
		this.fechaPago = fechaPago;
		this.notaVenta = notaVenta;
		this.proforma = proforma;
		this.nomPreparado = nomPreparado;
		this.codigoCliente = codigoCliente;
		this.cliente = cliente;
		this.codigoMedico = codigoMedico;
		this.medico = medico;
		this.costoPreparado = costoPreparado;
		this.valorDescuento = valorDescuento;
		this.valorPreparado = valorPreparado;
		this.moneda = moneda;
		this.estadoVenta = estadoVenta;
		this.precioPublico = precioPublico;
		this.cantidadEnvase = cantidadEnvase;
		this.precioUnitPreElab = precioUnitPreElab;
	}
	
	public EstructuraDetallePedido(int id, String fechaPago, String notaVenta,
			String proforma, String codigoCliente, String cliente,
			double montoTotal) {
		super();
		this.id = id;
		this.fechaPago = fechaPago;
		this.notaVenta = notaVenta;
		this.proforma = proforma;
		this.codigoCliente = codigoCliente;
		this.cliente = cliente;
		this.montoTotal = montoTotal;
	}
	
	//constructor
		public EstructuraDetallePedido(String codReceta, String codSubPrep,
				String fechaReceta, String fechaPago, String notaVenta,
				String proforma, String nomPreparado, String codigoCliente,
				String cliente, String codigoMedico, String medico,
				double costoPreparado, double valorDescuento, double valorPreparado, String moneda,
				String estadoVenta, double precioPublico, double cantidadEnvase,
				double precioUnitPreElab, boolean selected) {
			super();
			this.codReceta = codReceta;
			this.codSubPrep = codSubPrep;
			this.fechaReceta = fechaReceta;
			this.fechaPago = fechaPago;
			this.notaVenta = notaVenta;
			this.proforma = proforma;
			this.nomPreparado = nomPreparado;
			this.codigoCliente = codigoCliente;
			this.cliente = cliente;
			this.codigoMedico = codigoMedico;
			this.medico = medico;
			this.costoPreparado = costoPreparado;
			this.valorDescuento = valorDescuento;
			this.valorPreparado = valorPreparado;
			this.moneda = moneda;
			this.estadoVenta = estadoVenta;
			this.precioPublico = precioPublico;
			this.cantidadEnvase = cantidadEnvase;
			this.precioUnitPreElab = precioUnitPreElab;
			this.selected = selected;
		}
	
	//get and set
	public String getCodReceta() {
		return codReceta;
	}
	public void setCodReceta(String codReceta) {
		this.codReceta = codReceta;
	}
	public String getCodSubPrep() {
		return codSubPrep;
	}
	public void setCodSubPrep(String codSubPrep) {
		this.codSubPrep = codSubPrep;
	}
	public String getFechaReceta() {
		return fechaReceta;
	}
	public void setFechaReceta(String fechaReceta) {
		this.fechaReceta = fechaReceta;
	}
	public String getFechaPago() {
		return fechaPago;
	}
	public void setFechaPago(String fechaPago) {
		this.fechaPago = fechaPago;
	}
	public String getNotaVenta() {
		return notaVenta;
	}
	public void setNotaVenta(String notaVenta) {
		this.notaVenta = notaVenta;
	}
	public String getProforma() {
		return proforma;
	}
	public void setProforma(String proforma) {
		this.proforma = proforma;
	}
	public String getNomPreparado() {
		return nomPreparado;
	}
	public void setNomPreparado(String nomPreparado) {
		this.nomPreparado = nomPreparado;
	}
	public String getCodigoCliente() {
		return codigoCliente;
	}
	public void setCodigoCliente(String codigoCliente) {
		this.codigoCliente = codigoCliente;
	}
	public String getCliente() {
		return cliente;
	}
	public void setCliente(String cliente) {
		this.cliente = cliente;
	}
	public String getCodigoMedico() {
		return codigoMedico;
	}
	public void setCodigoMedico(String codigoMedico) {
		this.codigoMedico = codigoMedico;
	}
	public String getMedico() {
		return medico;
	}
	public void setMedico(String medico) {
		this.medico = medico;
	}
	public double getCostoPreparado() {
		try {
			costoPreparado = round(valorPreparado, 2);
			return costoPreparado;
		} catch (Exception e) {
			// TODO: handle exception
			System.out.println("Error en getCostoPreparado : " + e.getMessage());
			return 0;
		}
	}
	public void setCostoPreparado(double costoPreparado) {
		this.costoPreparado = costoPreparado;
	}
	public double getValorPreparado() {
		try {
			valorPreparado = round(valorPreparado, 2);
			return valorPreparado;
		} catch (Exception e) {
			// TODO: handle exception
			System.out.println("Error en getValorPreparado : " + e.getMessage());
			return 0;
		}
	}
	public void setValorPreparado(double valorPreparado) {
		this.valorPreparado = valorPreparado;
	}
	public String getMoneda() {
		return moneda;
	}
	public void setMoneda(String moneda) {
		this.moneda = moneda;
	}
	public String getEstadoVenta() {
		return estadoVenta;
	}
	public void setEstadoVenta(String estadoVenta) {
		this.estadoVenta = estadoVenta;
	}
	public double getPrecioPublico() {
		return precioPublico;
	}
	public void setPrecioPublico(double precioPublico) {
		this.precioPublico = precioPublico;
	}
	public double getCantidadEnvase() {
		return cantidadEnvase;
	}
	public void setCantidadEnvase(double cantidadEnvase) {
		this.cantidadEnvase = cantidadEnvase;
	}
	public double getPrecioUnitPreElab() {
		return precioUnitPreElab;
	}
	public void setPrecioUnitPreElab(double precioUnitPreElab) {
		this.precioUnitPreElab = precioUnitPreElab;
	}

	public boolean isSelected() {
		return selected;
	}

	public void setSelected(boolean selected) {
		this.selected = selected;
	}

	public double getValorDescuento() {
		try {
			valorDescuento = round(valorDescuento, 2);
			return valorDescuento;
		} catch (Exception e) {
			// TODO: handle exception
			System.out.println("Error en getValorDescuento : " + e.getMessage());
			return 0;
		}
	}

	public void setValorDescuento(double valorDescuento) {
		this.valorDescuento = valorDescuento;
	}

	public double getMontoTotal() {
		try {
			montoTotal = round(montoTotal, 2);
			return montoTotal;
		} catch (Exception e) {
			// TODO: handle exception
			System.out.println("Error en getMontoTotal : " + e.getMessage());
			return 0;
		}
	}

	public void setMontoTotal(double montoTotal) {
		this.montoTotal = montoTotal;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}
	
	public double round(double value, int places) {
	    if (places < 0) throw new IllegalArgumentException();

	    long factor = (long) Math.pow(10, places);
	    value = value * factor;
	    long tmp = Math.round(value);
	    return (double) tmp / factor;
	}
	
}
