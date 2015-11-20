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

import bo.buffalo.model.ProductoProveedor;

public class EstructuraCentralCostos implements Serializable{

	private static final long serialVersionUID = 6438939962626559371L;
		
	private int id;
	private ProductoProveedor productoProveedor;
	private double costo;
	private double utilMax;
	private double precioVenta1;
	private double precioFinal;
	private double precioEstablecido;
	
	
	
	
	
	public EstructuraCentralCostos() {
		super();
	}

	public EstructuraCentralCostos(Integer id,ProductoProveedor productoProveedor,
			double costo, double utilMax, double precioVenta1,
			double precioFinal, double precioEstablecido) {
		super();
		this.id=id;
		this.productoProveedor = productoProveedor;
		this.costo = costo;
		this.utilMax = utilMax;
		this.precioVenta1 = precioVenta1;
		this.precioFinal = precioFinal;
		this.precioEstablecido = precioEstablecido;
	}

	public ProductoProveedor getProductoProveedor() {
		return productoProveedor;
	}

	public void setProductoProveedor(ProductoProveedor productoProveedor) {
		this.productoProveedor = productoProveedor;
	}

	public double getCosto() {
		return costo;
	}

	public void setCosto(double costo) {
		this.costo = costo;
	}

	public double getUtilMax() {
		return utilMax;
	}

	public void setUtilMax(double utilMax) {
		this.utilMax = utilMax;
	}

	public double getPrecioVenta1() {
		return precioVenta1;
	}

	public void setPrecioVenta1(double precioVenta1) {
		this.precioVenta1 = precioVenta1;
	}

	public double getPrecioFinal() {
		return precioFinal;
	}

	public void setPrecioFinal(double precioFinal) {
		this.precioFinal = precioFinal;
	}

	public double getPrecioEstablecido() {
		return precioEstablecido;
	}

	public void setPrecioEstablecido(double precioEstablecido) {
		this.precioEstablecido = precioEstablecido;
	}

	public double round(double value, int places) {
	    if (places < 0) throw new IllegalArgumentException();

	    long factor = (long) Math.pow(10, places);
	    value = value * factor;
	    long tmp = Math.round(value);
	    return (double) tmp / factor;
	}
	
	//get and set
	

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	
	
}
