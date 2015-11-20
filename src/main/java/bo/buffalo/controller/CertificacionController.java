/*
 * JBoss, Home of Professional Open Source
 * Copyright 2014, Red Hat, Inc. and/or its affiliates, and individual
 * contributors by the @authors tag. See the copyright.txt in the
 * distribution for a full listing of individual contributors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * http://www.apache.org/licenses/LICENSE-2.0
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package bo.buffalo.controller;

import java.text.SimpleDateFormat;
import java.util.Date;

import javax.annotation.PostConstruct;
import javax.enterprise.event.Event;
import javax.enterprise.inject.Model;
import javax.inject.Inject;

import org.richfaces.cdi.push.Push;

import bo.buffalo.util.CodigoControl7;

// The @Model stereotype is a convenience mechanism to make this a request-scoped bean that has an
// EL name
// Read more about the @Model stereotype in this FAQ:
// http://sfwk.org/Documentation/WhatIsThePurposeOfTheModelAnnotation
@Model
public class CertificacionController {

    public static final String PUSH_CDI_TOPIC = "pushCdi";

    @Inject
    @Push(topic = PUSH_CDI_TOPIC)
    Event<String> pushEvent;
    
    private String llaveDosificacion;
    private String numeroAutorizacion;
    private int numeroFactura;
    private String nitCI;
    private String fechaTransaccion;
    private int monto;
    
    private String codigoControl;
    
    @PostConstruct
    public void initCertificacion() {
    	
    }
    
    public static void main(String[] args){
    	try {
    		String formatoFecha = "11/30/2014";
        	SimpleDateFormat sdf = new SimpleDateFormat("MM/dd/yyyy");
        	Date date;
    		date = sdf.parse(formatoFecha);
    		System.out.println("Fecha: "+date);
    		
    		//Fecha: Sat Jun 11 00:00:00 BOT 2016
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}
    }
    
    
    public void generarCodigoControlV7(){
    	try {
			System.out.println("Certificar Codigo Control... ");
			CodigoControl7 cc = new CodigoControl7();
	    	cc.setNumeroAutorizacion(this.getNumeroAutorizacion());
	    	cc.setNumeroFactura(this.getNumeroFactura());
	    	cc.setNitci(this.getNitCI());
	    	
	    	SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
	    	Date date;
			date = sdf.parse(this.getFechaTransaccion());
	    	cc.setFechaTransaccion(date);
	    	cc.setMonto(this.getMonto());
	    	cc.setLlaveDosificacion(this.getLlaveDosificacion());
			this.setCodigoControl(cc.obtener());
			System.out.println("Codigo Control V7: "+this.getCodigoControl());
		} catch (Exception e) {
			// TODO: handle exception
			System.out.println("Error al generarCodigoControlV7: "+e.getMessage());
			limpiarCampos();
		}
    }
    
    public void limpiarCampos(){
    	llaveDosificacion = null;
        numeroAutorizacion = null;
        numeroFactura = 0;
        nitCI = null;
        fechaTransaccion = null;
        monto = 0;
        
        codigoControl = null;
    }
    

	public String getLlaveDosificacion() {
		return llaveDosificacion;
	}

	public void setLlaveDosificacion(String llaveDosificacion) {
		this.llaveDosificacion = llaveDosificacion;
	}

	public String getNumeroAutorizacion() {
		return numeroAutorizacion;
	}

	public void setNumeroAutorizacion(String numeroAutorizacion) {
		this.numeroAutorizacion = numeroAutorizacion;
	}

	public int getNumeroFactura() {
		return numeroFactura;
	}

	public void setNumeroFactura(int numeroFactura) {
		this.numeroFactura = numeroFactura;
	}

	public String getNitCI() {
		return nitCI;
	}

	public void setNitCI(String nitCI) {
		this.nitCI = nitCI;
	}

	public String getFechaTransaccion() {
		return fechaTransaccion;
	}

	public void setFechaTransaccion(String fechaTransaccion) {
		this.fechaTransaccion = fechaTransaccion;
	}

	public int getMonto() {
		return monto;
	}

	public void setMonto(int monto) {
		this.monto = monto;
	}

	public String getCodigoControl() {
		return codigoControl;
	}

	public void setCodigoControl(String codigoControl) {
		this.codigoControl = codigoControl;
	}

}
