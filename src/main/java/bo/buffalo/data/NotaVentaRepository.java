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

import bo.buffalo.model.Factura;
import bo.buffalo.model.NotaVenta;
import bo.buffalo.model.ProformaVenta;

@ApplicationScoped
public class NotaVentaRepository {
	
	@Inject
    private EntityManager em;

    public NotaVenta findById(int id) {
        return em.find(NotaVenta.class, id);
    }

    public List<NotaVenta> findAllOrderedByFechaRegistro() {
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<NotaVenta> criteria = cb.createQuery(NotaVenta.class);
        Root<NotaVenta> notaVenta = criteria.from(NotaVenta.class);
        criteria.select(notaVenta).orderBy(cb.desc(notaVenta.get("fechaRegistro")));
        return em.createQuery(criteria).getResultList();
    }
    
   
    @SuppressWarnings("unchecked")
	public List<NotaVenta> findAllOrderedByIDForProforma(ProformaVenta proforma) {
    	String query = "select pro from NotaVenta pro where pro.estado='AC'  and pro.proformaVenta.id="+proforma.getId()+" order by pro.id desc";
    	System.out.println("Query Nota Venta: "+query);
    	return em.createQuery(query).getResultList();
    }
    
    
    @SuppressWarnings("unchecked")
	public NotaVenta findAllOrderedByIDForFactura(Factura factura) {
    	String query = "select pro from NotaVenta pro where pro.estado='AC'  and pro.factura.id="+factura.getId()+" order by pro.id desc";
    	System.out.println("Query Nota Venta: "+query);
    	return (NotaVenta) em.createQuery(query).getSingleResult();
    }
    
	
}
