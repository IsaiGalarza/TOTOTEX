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
package bo.buffalo.service;

import javax.ejb.Stateless;
import javax.enterprise.event.Event;
import javax.inject.Inject;
import javax.persistence.EntityManager;

import bo.buffalo.model.DetalleFactura;
import bo.buffalo.model.Factura;
import bo.buffalo.model.Sucursal;
import bo.buffalo.util.EstructuraConcepto;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.logging.Logger;

// The @Stateless annotation eliminates the need for manual transaction demarcation
@Stateless
public class FacturaRegistration {

    @Inject
    private Logger log;

    @Inject
    private EntityManager em;

    @Inject
    private Event<Factura> facturaEventSrc;
    
    @Inject
    private Event<DetalleFactura> detalleFacturaEventSrc;

    public void register(Factura factura) throws Exception {
        log.info("Registering " + factura.getNumeroFactura() + " NIT: " + factura.getNitCi() + " Cliente: " + factura.getNombreFactura());
        em.persist(factura);
        facturaEventSrc.fire(factura);
    }
    
    public void updated(Factura factura) throws Exception {
        log.info("Updated Factura " + factura.getNumeroFactura() + " NIT: " + factura.getNitCi() + " Cliente: " + factura.getNombreFactura());
        em.merge(factura);
        facturaEventSrc.fire(factura);
    }
    
    public void register(Factura factura, List<EstructuraConcepto> listaConcepto) throws Exception {
        log.info("Registering " + factura.getNumeroFactura() + " NIT: " + factura.getNitCi() + " Cliente: " + factura.getNombreFactura());
        
        Sucursal sucursal = em.find(Sucursal.class, factura.getIdSucursal());
        
        //actualizar codigo de respuesta rapida
        String codigoRespuestaRapida = sucursal.getNit()+"|"+factura.getNumeroFactura()+"|"+factura.getNumeroAutorizacion()+"|"+formatearFecha(factura.getFechaFactura())+"|"+
        		round(factura.getTotalFacturado())+"|"+round(factura.getImporteBaseDebitoFiscal())+"|"+factura.getCodigoControl()+"|"+factura.getNitCi()+"|0|0|0|0";
        
        factura.setCodigoRespuestaRapida(codigoRespuestaRapida);
        
        em.persist(factura);
        facturaEventSrc.fire(factura);
        
        for(EstructuraConcepto concepto : listaConcepto){
        	DetalleFactura detalle = new DetalleFactura();
        	detalle.setFactura(factura);
        	detalle.setCodigoProducto(concepto.getCodigo());
        	detalle.setConcepto(concepto.getConcepto());
        	detalle.setCantidad(concepto.getCantidad());
        	detalle.setPrecioUnitario(concepto.getPrecioUnitario());
        	detalle.setPrecioTotal(concepto.getPrecioTotal());
        	detalle.setDescuentos(concepto.getDescuento());
        	detalle.setEstado("AC");
        	detalle.setFechaRegistro(new Date());
        	detalle.setUsuarioRegistro(factura.getUsuarioRegistro());
        	detalle.setOrigen(concepto.getTipo());

        	//registrar detalle
        	registerDetalleFactura(detalle);
        }
 
    }
    
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
	    	return df.format(value);
		} catch (Exception e) {
			// TODO: handle exception
			System.out.println("Error en round: "+e.getMessage());
			return "0.00";
		}
	}
    
    public void validaFactura(Factura factura) throws Exception {
        log.info("Ingreso --> validaFactura " + factura.getNumeroFactura() + " NIT: " + factura.getNitCi() + " Cliente: " + factura.getNombreFactura());
        factura.setEstado("V");
        em.merge(factura);
        facturaEventSrc.fire(factura);
    }
    
    public void anularFactura(Factura factura) throws Exception {
        log.info("Ingreso --> AnularFactura " + factura.getNumeroFactura() + " NIT: " + factura.getNitCi() + " Cliente: " + factura.getNombreFactura());
        factura.setEstado("A");
        factura.setNombreFactura("ANULADA");
        factura.setNitCi("0");
        factura.setTotalLiteral("ANULADA");
        factura.setTotalFacturado(0);
        factura.setTotalEfectivo(0);
        factura.setTotalPagoDebito(0);
        factura.setTotalEfectivo(0);
        factura.setTotalEfectivoDolares(0);
        factura.setTotalPagar(0);
        factura.setCambio(0);
        
        //libro venta
        factura.setImporteICE(0);
        factura.setImporteExportaciones(0);
        factura.setImporteVentasGrabadasTasaCero(0);
        factura.setImporteSubTotal(0);
        factura.setImporteDescuentosBonificaciones(0);
        factura.setImporteBaseDebitoFiscal(0);
        factura.setDebitoFiscal(0);
        
        Sucursal sucursal = em.find(Sucursal.class, factura.getIdSucursal());
        
        //actualizar codigo de respuesta rapida
        String codigoRespuestaRapida = sucursal.getNit()+"|"+factura.getNumeroFactura()+"|"+factura.getNumeroAutorizacion()+"|"+formatearFecha(factura.getFechaFactura())+"|"+
        		round(factura.getTotalFacturado())+"|"+round(factura.getImporteBaseDebitoFiscal())+"|"+factura.getCodigoControl()+"|"+factura.getNitCi()+"|0|0|0|0";
        
        factura.setCodigoRespuestaRapida(codigoRespuestaRapida);
        
        //actualizar codigo de control
        
        em.merge(factura);
        facturaEventSrc.fire(factura);
    }
    
    public void extravidaFactura(Factura factura) throws Exception {
        log.info("Ingreso --> extraviarFactura " + factura.getNumeroFactura() + " NIT: " + factura.getNitCi() + " Cliente: " + factura.getNombreFactura());
        factura.setEstado("E");
        em.merge(factura);
        facturaEventSrc.fire(factura);
    }
    
    public void noUtilizadaFactura(Factura factura) throws Exception {
        log.info("Ingreso --> noUtilizadaFactura " + factura.getNumeroFactura() + " NIT: " + factura.getNitCi() + " Cliente: " + factura.getNombreFactura());
        factura.setEstado("N"); //NO UTILIZADA
        em.merge(factura);
        facturaEventSrc.fire(factura);
    }
    
    public void emitidaEnContingenciaFactura(Factura factura) throws Exception {
        log.info("Ingreso --> emitidaEnContingenciaFactura " + factura.getNumeroFactura() + " NIT: " + factura.getNitCi() + " Cliente: " + factura.getNombreFactura());
        factura.setEstado("C"); //EMITIDA EN CONTINGENCIA
        em.merge(factura);
        facturaEventSrc.fire(factura);
    }
    
    public void registerDetalleFactura(DetalleFactura detalleFactura) throws Exception {
        log.info("DetalleFactura Registrado: " + detalleFactura.getConcepto() + ", Factura: "+detalleFactura.getFactura().getNumeroFactura());
        em.persist(detalleFactura);
        detalleFacturaEventSrc.fire(detalleFactura);
    }
}
