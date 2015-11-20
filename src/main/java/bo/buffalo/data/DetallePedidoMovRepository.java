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

import bo.buffalo.model.DetallePedidoMov;
import bo.buffalo.model.PedidoMov;

@ApplicationScoped
public class DetallePedidoMovRepository {
	
	@Inject
    private EntityManager em;

    public DetallePedidoMov findById(int id) {
        return em.find(DetallePedidoMov.class, id);
    }

    public List<DetallePedidoMov> findAllOrderedByDescripcion() {
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<DetallePedidoMov> criteria = cb.createQuery(DetallePedidoMov.class);
        Root<DetallePedidoMov> presentacion = criteria.from(DetallePedidoMov.class);
        criteria.select(presentacion).orderBy(cb.desc(presentacion.get("nombre")));
        return em.createQuery(criteria).getResultList();
    }
    
    public List<DetallePedidoMov> findAllOrderedByID(PedidoMov pedido ) {
    	String query = "select ser from DetallePedidoMov ser where ser.pedidoMov.id="+pedido.getId()+"  order by ser.id asc";
    	System.out.println("Query DetallePedidoMov: "+query);
    	return em.createQuery(query).getResultList();
    }
    
    public List<DetallePedidoMov> findAllOrderedByFechaRegistro() {
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<DetallePedidoMov> criteria = cb.createQuery(DetallePedidoMov.class);
        Root<DetallePedidoMov> presentacion = criteria.from(DetallePedidoMov.class);
        criteria.select(presentacion).orderBy(cb.desc(presentacion.get("fechaRegistro")));
        return em.createQuery(criteria).getResultList();
    }
    

    public List<DetallePedidoMov> traerDetallePedidoMovActivas(PedidoMov pedido) {
        try {
        	String query = "select ser from DetallePedidoMov ser where ser.estado='AC' and ser.pedidoMov.id="+pedido.getId()+" order by ser.id desc";
        	System.out.println("Consulta traerDetallePedidoMovActivas: "+query);
            List<DetallePedidoMov> listaDetallePedidoMov = em.createQuery(query).getResultList();
            return listaDetallePedidoMov;        	
		} catch (Exception e) {
			// TODO: handle exception
			System.out.println("Error en traerDetallePedidoMovActivas: "+e.getMessage());
			return null;
		}
    }
    
    public List<DetallePedidoMov> findAll100UltimosDetallePedidoMov() {
        try {
        	String query = "select ser from DetallePedidoMov ser order by ser.fechaRegistro desc";
        	System.out.println("Consulta: "+query);
            List<DetallePedidoMov> listaDetallePedidoMov = em.createQuery(query).setMaxResults(100).getResultList();
            return listaDetallePedidoMov;        	
		} catch (Exception e) {
			// TODO: handle exception
			System.out.println("Error en findAll100UltimosDetallePedidoMov: "+e.getMessage());
			return null;
		}
    }
    
    
	
}
