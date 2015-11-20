/*
* EstructuraConcepto.java	1.0 2014/09/19
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

public class EstructuraConcepto implements Serializable{

	private static final long serialVersionUID = 6438939962626559371L;
		
	private int id;
	private String codigo;
	private String concepto;
	private double cantidad;
	private double precioUnitario;
	private double precioTotal;
	private double descuento;
	private String tipo;
	
	//constructor
	public EstructuraConcepto(){
		
	}
	
	public EstructuraConcepto(EstructuraConcepto cooncepto){
		super();
		this.id = cooncepto.getId();
		this.codigo = cooncepto.getCodigo();
		this.concepto = cooncepto.getConcepto();
		this.cantidad = cooncepto.getCantidad();
		this.precioUnitario = cooncepto.getPrecioUnitario();
		this.precioTotal = cooncepto.getPrecioTotal();
		this.tipo = cooncepto.getTipo();
	}
	
	public EstructuraConcepto(int id, String codigo, String concepto, double cantidad,
			double precioUnitario, double precioTotal, double descuento, String tipo) {
		super();
		this.id = id;
		this.codigo = codigo;
		this.concepto = concepto;
		this.cantidad = cantidad;
		this.precioUnitario = precioUnitario;
		this.precioTotal = precioTotal;
		this.descuento = descuento;
		this.tipo = tipo;
	}
	
	public EstructuraConcepto(int id, String codigo, String concepto, double cantidad,
			double precioUnitario, double precioTotal, String tipo) {
		super();
		this.id = id;
		this.codigo = codigo;
		this.concepto = concepto;
		this.cantidad = cantidad;
		this.precioUnitario = precioUnitario;
		this.precioTotal = precioTotal;
		this.tipo = tipo;
	}
	
	public double round(double value, int places) {
	    if (places < 0) throw new IllegalArgumentException();

	    long factor = (long) Math.pow(10, places);
	    value = value * factor;
	    long tmp = Math.round(value);
	    return (double) tmp / factor;
	}
	
	//get and set
	public String getCodigo() {
		return codigo;
	}
	public void setCodigo(String codigo) {
		this.codigo = codigo;
	}
	public String getConcepto() {
		return concepto;
	}
	public void setConcepto(String concepto) {
		this.concepto = concepto;
	}
	public double getCantidad() {
		return cantidad;
	}
	public void setCantidad(double cantidad) {
		this.cantidad = cantidad;
	}
	public double getPrecioUnitario() {
		try {
			precioUnitario = round(precioUnitario, 2);
			return precioUnitario;
		} catch (Exception e) {
			// TODO: handle exception
			System.out.println("Error en getPrecioUnitario : " + e.getMessage());
			return 0;
		}
	}
	public void setPrecioUnitario(double precioUnitario) {
		this.precioUnitario = precioUnitario;
	}
	public double getPrecioTotal() {
		try {
			precioTotal = round(precioTotal, 2);
			return precioTotal;
		} catch (Exception e) {
			// TODO: handle exception
			System.out.println("Error en getPrecioTotal : " + e.getMessage());
			return 0;
		}
	}
	public void setPrecioTotal(double precioTotal) {
		this.precioTotal = precioTotal;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getTipo() {
		return tipo;
	}

	public void setTipo(String tipo) {
		this.tipo = tipo;
	}

	public double getDescuento() {
		return descuento;
	}

	public void setDescuento(double descuento) {
		this.descuento = descuento;
	}
	
}
