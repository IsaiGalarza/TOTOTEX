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

import bo.buffalo.model.DetalleOrdenCompra;
import bo.buffalo.model.OrdenCompra;


@ApplicationScoped
public class DetalleOrdenCompraRepository {
	
	@Inject
    private EntityManager em;

    public DetalleOrdenCompra findById(int id) {
        return em.find(DetalleOrdenCompra.class, id);
    }

    public List<DetalleOrdenCompra> findAllOrderedByDescripcion() {
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<DetalleOrdenCompra> criteria = cb.createQuery(DetalleOrdenCompra.class);
        Root<DetalleOrdenCompra> presentacion = criteria.from(DetalleOrdenCompra.class);
        criteria.select(presentacion).orderBy(cb.desc(presentacion.get("nombre")));
        return em.createQuery(criteria).getResultList();
    }
    
    public List<DetalleOrdenCompra> findAllOrderedByID(OrdenCompra pedido ) {
    	String query = "select ser from DetalleOrdenCompra ser where ser.ordenCompra.id="+pedido.getId()+"  order by ser.id asc";
    	System.out.println("[findAllOrderedByID]Query DetalleOrdenCompra: "+query);
    	return em.createQuery(query).getResultList();
    }
    
    public List<DetalleOrdenCompra> findAllOrderedByFechaRegistro() {
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<DetalleOrdenCompra> criteria = cb.createQuery(DetalleOrdenCompra.class);
        Root<DetalleOrdenCompra> presentacion = criteria.from(DetalleOrdenCompra.class);
        criteria.select(presentacion).orderBy(cb.desc(presentacion.get("fechaRegistro")));
        return em.createQuery(criteria).getResultList();
    }
    

    public List<DetalleOrdenCompra> traerDetalleOrdenCompraActivas(OrdenCompra pedido) {
        try {
        	String query = "select ser from DetalleOrdenCompra ser where ser.estado='AC' and ser.ordenCompra.id="+pedido.getId()+" order by ser.id desc";
        	System.out.println("Consulta traerDetalleOrdenCompraActivas: "+query);
            List<DetalleOrdenCompra> listaDetalleOrdenCompra = em.createQuery(query).getResultList();
            return listaDetalleOrdenCompra;        	
		} catch (Exception e) {
			// TODO: handle exception
			System.out.println("Error en traerDetalleOrdenCompraActivas: "+e.getMessage());
			return null;
		}
    }
    
    public List<DetalleOrdenCompra> findAll100UltimosDetalleOrdenCompra() {
        try {
        	String query = "select ser from DetalleOrdenCompra ser order by ser.fechaRegistro desc";
        	System.out.println("Consulta: "+query);
            List<DetalleOrdenCompra> listaDetalleOrdenCompra = em.createQuery(query).setMaxResults(100).getResultList();
            return listaDetalleOrdenCompra;        	
		} catch (Exception e) {
			// TODO: handle exception
			System.out.println("Error en findAll100UltimosDetalleOrdenCompra: "+e.getMessage());
			return null;
		}
    }
    
    
	
}
