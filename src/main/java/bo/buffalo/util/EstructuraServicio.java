/*
* EstructuraServicio.java	1.0 2014/09/19
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

import bo.buffalo.model.Servicio;

public class EstructuraServicio implements Serializable{
	
	private static final long serialVersionUID = -5744370483095747055L;
	
	private Servicio servicio;
	private double cantidad;
	private boolean selected;
	private double subTotal;
	
	//constructor
	public EstructuraServicio(){
		
	}
	
	public EstructuraServicio(Servicio servicio, double cantidad,
			boolean selected) {
		super();
		this.servicio = servicio;
		this.cantidad = cantidad;
		this.selected = selected;
	}
	
	//get and set
	public Servicio getServicio() {
		return servicio;
	}
	public void setServicio(Servicio servicio) {
		this.servicio = servicio;
	}
	public double getCantidad() {
		return cantidad;
	}
	public void setCantidad(double cantidad) {
		this.cantidad = cantidad;
	}
	public boolean isSelected() {
		return selected;
	}
	public void setSelected(boolean selected) {
		this.selected = selected;
	}

	public double getSubTotal() {
		try {
			subTotal = servicio.getPrecioUnitarioVenta()*cantidad;
			return subTotal;
		} catch (Exception e) {
			// TODO: handle exception
			System.out.println("Error en getSubTotal: " + e.getMessage());
			return 0;
		}
	}

	public void setSubTotal(double subTotal) {
		this.subTotal = subTotal;
	}
}
