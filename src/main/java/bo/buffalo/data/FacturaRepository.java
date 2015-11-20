/*
* ServicioRepository.java	1.0 2014/09/19
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
package bo.buffalo.data;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;

import org.exolab.castor.types.DateTime;

import bo.buffalo.model.Cliente;
import bo.buffalo.model.Factura;
import bo.buffalo.model.Usuario;

import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

@ApplicationScoped
public class FacturaRepository {

    @Inject
    private EntityManager em;

    public Factura findById(int id) {
        return em.find(Factura.class, id);
    }

    public Factura findByID(int facturaID) {
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<Factura> criteria = cb.createQuery(Factura.class);
        Root<Factura> factura = criteria.from(Factura.class);
        criteria.select(factura).where(cb.equal(factura.get("id"), facturaID));
        return em.createQuery(criteria).getSingleResult();
    }
    
    public Factura findByNumeroFactura(String numeroFactura) {
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<Factura> criteria = cb.createQuery(Factura.class);
        Root<Factura> factura = criteria.from(Factura.class);
        criteria.select(factura).where(cb.equal(factura.get("numeroFactura"), numeroFactura));
        return em.createQuery(criteria).getSingleResult();
    }

    public List<Factura> findAllOrderedByFechaRegistro() {
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<Factura> criteria = cb.createQuery(Factura.class);
        Root<Factura> factura = criteria.from(Factura.class);
        criteria.select(factura).orderBy(cb.desc(factura.get("fechaRegistro")));
        return em.createQuery(criteria).getResultList();
    }
    
    public List<Factura> findUltimas100Facturas() {
        try {
        	String query = "select fac from Factura fac order by fac.fechaRegistro desc";
        	System.out.println("Consulta Ultimas Facturas: "+query);
            List<Factura> listaFacturas = em.createQuery(query).setMaxResults(100).getResultList();
            return listaFacturas;        	
		} catch (Exception e) {
			// TODO: handle exception
			System.out.println("Error en findUltimas100Facturas: "+e.getMessage());
			return null;
		}
    }
    
    public List<Factura> buscarFacturas(String criterio) {
        try {
        	String query = "select fac from Factura fac"
        			+ " where fac.nitCi='"+criterio+"' or (upper(fac.nombreFactura) like '%"+criterio+"%')";
        	System.out.println("Consulta Ultimas Facturas: "+query);
            List<Factura> listaFacturas = em.createQuery(query).setMaxResults(100).getResultList();
            return listaFacturas;        	
		} catch (Exception e) {
			// TODO: handle exception
			System.out.println("Error en buscarFacturas: "+e.getMessage());
			return null;
		}
    }
    
    public List<Factura> findAllOrderedByID() {
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<Factura> criteria = cb.createQuery(Factura.class);
        Root<Factura> factura = criteria.from(Factura.class);
        criteria.select(factura).orderBy(cb.desc(factura.get("id")));
        return em.createQuery(criteria).getResultList();
    }
    
    
    @SuppressWarnings("unchecked")
	public List<Factura> buscarFacturasReporteVentas(Date fechaIni, Date fechaFin, String estado, int sucursalID) {
        try {
        	//DateTime dateTime = new DateTime(date);
//        	String DATE_FORMAT = "yyyyMMdd";
//    	    SimpleDateFormat sdf = new SimpleDateFormat(DATE_FORMAT);
    	    DateTime frmDate = new DateTime(fechaIni);
    	    DateTime enDate = new DateTime(fechaFin);
    	    
    	    Timestamp pFechaInicial = new Timestamp(fechaIni.getTime());
    	    Timestamp pFechaFinal = new Timestamp(fechaFin.getTime());
    	    																														//like '%"+criterio+"%'
//    	    return em.createQuery("select fac FROM Factura fac WHERE fac.fechaFactura BETWEEN :stDate AND :edDate "
//    	    		+ "and fac.estado like '%"+estado+"%' and fac.idSucursal=:pSucursal order by fac.fechaRegistro desc")
//    	    .setParameter("stDate", fechaIni)
//    	    .setParameter("edDate", fechaFin)
//    	    .setParameter("pSucursal", sucursalID)
//    	    .getResultList();
    	    
    	    return em.createQuery("select fac FROM Factura fac WHERE fac.fechaFactura>=:stDate and fac.fechaFactura<=:edDate "
    	    		+ "and fac.estado like '%"+estado+"%' and fac.idSucursal=:pSucursal order by fac.fechaRegistro desc")
    	    .setParameter("stDate", fechaIni)
    	    .setParameter("edDate", fechaFin)
    	    .setParameter("pSucursal", sucursalID)
    	    .getResultList();
    	    
 
    	    
        	
//    	    String query = "select fac from Factura fac where to_number(to_char(fac.fechaFactura,'YYYYMMDD')) >="+sdf.format(fechaIni.getTime()) +
//    	    		 " and to_number(to_char(fac.fechaFactura,'YYYYMMDD')) <="+sdf.format(fechaFin.getTime()) +
//        			 " and fac.estado='"+estado+"' order by fac.fechaRegistro desc";
    	    
//    	    String query = "select fac from Factura fac where fac.fechaFactura between "+fechaIni+" and "+fechaFin+"  order by fac.fechaRegistro desc";
//    	    
//        	System.out.println("Consulta Reporte Ventas: "+query);
//            List<Factura> listaFacturas = em.createQuery(query).setMaxResults(100).getResultList();
//            return listaFacturas;     
    	    
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
			System.out.println("Error en buscarFacturasReporteVentas: "+e.getMessage());
			return null;
		}
    }
    
    
    @SuppressWarnings("unchecked")
	public List<Factura> buscarFacturasSucursal(Date fechaIni, Date fechaFin, String estado, int sucursalID) {
        try {
        	System.out.println("Ingreso a buscarFacturasSucursal....");
    	    
    	    return em.createQuery("select fac FROM Factura fac WHERE fac.fechaFactura>=:stDate and fac.fechaFactura<=:edDate "
    	    		+ "and fac.estado like '%"+estado+"%' and fac.idSucursal=:pSucursal order by fac.fechaRegistro desc")
    	    .setParameter("stDate", fechaIni)
    	    .setParameter("edDate", fechaFin)
    	    .setParameter("pSucursal", sucursalID)
    	    .getResultList();
    	    
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
			System.out.println("Error en buscarFacturasSucursal: "+e.getMessage());
			return null;
		}
    }
    
    @SuppressWarnings("unchecked")
	public List<String> traerGestionesFacturadas(){
    	try {
    		System.out.println("Ingreso a traerGestionesFacturadas");
			String query = "select distinct fac.gestion from Factura fac";
			return em.createQuery(query).getResultList();
			
		} catch (Exception e) {
			// TODO: handle exception
			System.out.println("Error en traerGestionesFacturadas: "+e.getMessage());
			return null;
		}
    }
    
	public List<Factura> traerFacturasPeriodoFiscal(String gestion, String mes){
    	try {
    		System.out.println("Ingreso a traerFacturasPeriodoFiscal");
			String query = "select fac from Factura fac where fac.gestion='"+gestion+"' and fac.mes='"+mes+"'"
					+ " order by fac.fechaFactura, fac.numeroAutorizacion asc";
			return em.createQuery(query).getResultList();
			
		} catch (Exception e) {
			// TODO: handle exception
			System.out.println("Error en traerFacturasPeriodoFiscal: "+e.getMessage());
			return null;
		}
    }
    
}
