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

import bo.buffalo.model.Confeccionista;

@ApplicationScoped
public class ConfeccionistaRepository {
	
	@Inject
    private EntityManager em;

    public Confeccionista findById(int id) {
        return em.find(Confeccionista.class, id);
    }

    public List<Confeccionista> findAllOrderedByNombre() {
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<Confeccionista> criteria = cb.createQuery(Confeccionista.class);
        Root<Confeccionista> cargo = criteria.from(Confeccionista.class);
        criteria.select(cargo).orderBy(cb.desc(cargo.get("nombre")));
        return em.createQuery(criteria).getResultList();
    }
    
    @SuppressWarnings("unchecked")
	public List<Confeccionista> findActivosOrderedByFechaRegistro() {
    	String query = "select car from Confeccionista car where car.estado='AC' order by car.fechaRegistro desc";
    	System.out.println("Query Servicios: "+query);
    	return em.createQuery(query).getResultList();
    }
    
    @SuppressWarnings("unchecked")
	public List<Confeccionista> findAllOrderedByFechaRegistro() {
    	String query = "select car from Confeccionista car where car.estado='AC' or car.estado='IN' order by car.fechaRegistro desc";
    	System.out.println("Query Servicios: "+query);
    	return em.createQuery(query).getResultList();
    }
    
	
}
