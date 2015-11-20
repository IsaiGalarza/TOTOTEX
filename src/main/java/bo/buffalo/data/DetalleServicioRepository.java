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

import bo.buffalo.model.DetalleServicio;
import bo.buffalo.model.Proforma;

@ApplicationScoped
public class DetalleServicioRepository {
	
	@Inject
    private EntityManager em;

    public DetalleServicio findById(int id) {
        return em.find(DetalleServicio.class, id);
    }

    public List<DetalleServicio> findAllOrderedByProforma() {
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<DetalleServicio> criteria = cb.createQuery(DetalleServicio.class);
        Root<DetalleServicio> producto = criteria.from(DetalleServicio.class);
        criteria.select(producto).orderBy(cb.desc(producto.get("nombreDetalleFarmaco")));
        return em.createQuery(criteria).getResultList();
    }
    
    @SuppressWarnings("unchecked")
	public List<DetalleServicio> findAllOrderedByIDForProforma(Proforma proforma) {
    	String query = "select pro from DetalleServicio pro where (pro.estado='AC' or pro.estado='IN')  and pro.proforma.id="+proforma.getId()+" order by pro.id desc";
    	System.out.println("Query Servicios: "+query);
    	return em.createQuery(query).getResultList();
    }
    
    @SuppressWarnings("unchecked")
	public List<DetalleServicio> traerDetalleServicio(Proforma proforma) {
    	String query = "select pro from DetalleServicio pro where pro.estado='AC' and pro.proforma.id="+proforma.getId()+" order by pro.id desc";
    	System.out.println("Query traerDetalleServicio: "+query);
    	return em.createQuery(query).getResultList();
    }
    
    public List<DetalleServicio> findAllOrderedByFechaRegistro() {
    	CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<DetalleServicio> criteria = cb.createQuery(DetalleServicio.class);
        Root<DetalleServicio> producto = criteria.from(DetalleServicio.class);
        criteria.select(producto).orderBy(cb.desc(producto.get("fechaRegistro")));
        return em.createQuery(criteria).getResultList();
    }
    
    
    @SuppressWarnings("unchecked")
	public List<DetalleServicio> findAllDetalleServicioForDescription(String criterio) {
        try {
        	String query = "select pro from DetalleServicio pro where pro.nombreDetalleFarmaco like '%"+criterio+"%'";
        	System.out.println("Consulta: "+query);
            return em.createQuery(query).getResultList();
		} catch (Exception e) {
			// TODO: handle exception
			System.out.println("Error en findAllDetalleFarmacosForDescription: "+e.getMessage());
			return null;
		}
    }
    
    @SuppressWarnings("unchecked")
	public List<DetalleServicio> traerDetalleServicio() {
        try {
        	String query = "select pro from DetalleServicio pro where pro.estado='AC' order by pro.id desc";
        	System.out.println("Consulta traerServicios: "+query);
            return em.createQuery(query).getResultList();
		} catch (Exception e) {
			// TODO: handle exception
			System.out.println("Error en traerDetalleServicio: "+e.getMessage());
			return null;
		}
    }
    
    
    
	
}
