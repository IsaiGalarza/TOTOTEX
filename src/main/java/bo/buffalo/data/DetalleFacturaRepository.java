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

import bo.buffalo.model.DetalleFactura;
import bo.buffalo.model.Factura;

import java.util.List;

@ApplicationScoped
public class DetalleFacturaRepository {

    @Inject
    private EntityManager em;

    public Factura findById(int id) {
        return em.find(Factura.class, id);
    }

    public DetalleFactura findByID(int facturaID) {
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<DetalleFactura> criteria = cb.createQuery(DetalleFactura.class);
        Root<DetalleFactura> detalleFactura = criteria.from(DetalleFactura.class);
        criteria.select(detalleFactura).where(cb.equal(detalleFactura.get("id"), facturaID));
        return em.createQuery(criteria).getSingleResult();
    }

    public List<DetalleFactura> findAllOrderedByFechaRegistro() {
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<DetalleFactura> criteria = cb.createQuery(DetalleFactura.class);
        Root<DetalleFactura> detalleFactura = criteria.from(DetalleFactura.class);
        criteria.select(detalleFactura).orderBy(cb.desc(detalleFactura.get("fechaRegistro")));
        return em.createQuery(criteria).getResultList();
    }
    
    public List<DetalleFactura> findDetalleFactura(int facturaID) {
        try {
        	String query = "select deta from DetalleFactura deta where deta.factura.id="+facturaID;
        	System.out.println("Consulta Detalle Facturas: "+query);
            List<DetalleFactura> listaFacturas = em.createQuery(query).getResultList();
            return listaFacturas;        	
		} catch (Exception e) {
			// TODO: handle exception
			System.out.println("Error en findDetalleFactura: "+e.getMessage());
			return null;
		}
    }
}
