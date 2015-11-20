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

import java.util.List;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;

import bo.buffalo.model.Compra;
import bo.buffalo.model.Factura;

@ApplicationScoped
public class CompraRepository {
	
	@Inject
    private EntityManager em;

    public Compra findById(int id) {
        return em.find(Compra.class, id);
    }

    public List<Compra> findAllOrderedByFechaRegistro() {
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<Compra> criteria = cb.createQuery(Compra.class);
        Root<Compra> compra = criteria.from(Compra.class);
        criteria.select(compra).orderBy(cb.desc(compra.get("fechaRegistro")));
        return em.createQuery(criteria).getResultList();
    }
    
    @SuppressWarnings("unchecked")
	public List<Compra> findAllOrderedByID() {
    	String query = "select com from Compra com where com.estado='AC' or com.estado='IN' order by com.id desc";
    	System.out.println("Query Compras: "+query);
    	return em.createQuery(query).getResultList();
    }

    @SuppressWarnings("unchecked")
	public List<Compra> findAllServicesForRazonSocial(String criterio) {
        try {
        	String query = "select com from Compra com where upper(com.razonSocial) like '%"+criterio.toUpperCase()+"%' and com.estado='AC'";
        	System.out.println("Consulta por Criterio: "+query);
            List<Compra> listaCompra = em.createQuery(query).getResultList();
            System.out.println("Resultado: "+listaCompra.size());
            return listaCompra;        	
		} catch (Exception e) {
			// TODO: handle exception
			System.out.println("Error en findAllServicesForRazonSocial: "+e.getMessage());
			return null;
		}
    }
    
    @SuppressWarnings("unchecked")
	public List<Compra> traerCompras() {
        try {
        	String query = "select com from Compra com where com.estado='AC' order by com.id desc";
        	System.out.println("Consulta traerCompras: "+query);
            List<Compra> listaCompra = em.createQuery(query).getResultList();
            System.out.println("Resultado: "+listaCompra.size());
            return listaCompra;        	
		} catch (Exception e) {
			// TODO: handle exception
			System.out.println("Error en traerCompras: "+e.getMessage());
			return null;
		}
    }
    
    @SuppressWarnings("unchecked")
	public List<Compra> traerUltimas10Compras() {
        try {
        	String query = "select com from Compra com where com.estado='AC' order by com.id desc";
        	System.out.println("Consulta traerServiciosUltimas10Compras: "+query);
            List<Compra> listaCompra = em.createQuery(query).setMaxResults(10).getResultList();
            System.out.println("Resultado: "+listaCompra.size());
            return listaCompra;        	
		} catch (Exception e) {
			// TODO: handle exception
			System.out.println("Error en traerServiciosUltimas10Compras: "+e.getMessage());
			return null;
		}
    }
    
    @SuppressWarnings("unchecked")
	public List<Compra> traerComprasGestionFiscal(String mes, String gestion) {
        try {
        	String query = "select com from Compra com where com.estado='AC' and com.mes='"+mes+"' and com.gestion='"+gestion+"' order by com.id desc";
        	System.out.println("Consulta traerComprasGestionFiscal: "+query);
            List<Compra> listaCompra = em.createQuery(query).getResultList();
            System.out.println("Resultado: "+listaCompra.size());
            return listaCompra;        	
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
			System.out.println("Error en traerServiciosUltimas10Compras: "+e.getMessage());
			return null;
		}
    }
    
    @SuppressWarnings("unchecked")
	public List<Compra> findAll100UltimosCompras() {
        try {
        	String query = "select com from Compra com where com.estado='AC' order by com.id desc";
        	System.out.println("Consulta findAll100UltimosCompras: "+query);
            List<Compra> listaCompra = em.createQuery(query).setMaxResults(100).getResultList();
            System.out.println("Resultado: "+listaCompra.size());
            return listaCompra;        	
		} catch (Exception e) {
			// TODO: handle exception
			System.out.println("Error en findAll100UltimosCompras: "+e.getMessage());
			return null;
		}
    }
    
    
    public List<Compra> traerComprasPeriodoFiscal(String gestion, String mes){
    	try {
    		System.out.println("Ingreso a traerComprasPeriodoFiscal");
			String query = "select comp from Compra comp where comp.gestion='"+gestion+"' and comp.mes='"+mes+"'"
					+ " and comp.estado='AC' order by comp.fechaFactura asc"; 
			return em.createQuery(query).getResultList();
			
		} catch (Exception e) {
			// TODO: handle exception
			System.out.println("Error en traerComprasPeriodoFiscal: "+e.getMessage());
			return null;
		}
    }
    
	
}
