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

import bo.buffalo.model.Cargo;

@ApplicationScoped
public class CargoRepository {
	
	@Inject
    private EntityManager em;

    public Cargo findById(int id) {
        return em.find(Cargo.class, id);
    }

    public List<Cargo> findAllOrderedByDescripcion() {
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<Cargo> criteria = cb.createQuery(Cargo.class);
        Root<Cargo> cargo = criteria.from(Cargo.class);
        criteria.select(cargo).orderBy(cb.desc(cargo.get("nombre")));
        return em.createQuery(criteria).getResultList();
    }
    
    @SuppressWarnings("unchecked")
	public List<Cargo> findActivosOrderedByFechaRegistro() {
    	String query = "select car from Cargo car where car.estado='AC' or car.estado='IN' order by car.fechaRegistro desc";
    	System.out.println("Query Servicios: "+query);
    	return em.createQuery(query).getResultList();
    }
    
    @SuppressWarnings("unchecked")
	public List<Cargo> findAllOrderedByFechaRegistro() {
    	String query = "select car from Cargo car where car.estado='AC' or car.estado='IN' order by car.fechaRegistro desc";
    	System.out.println("Query Servicios: "+query);
    	return em.createQuery(query).getResultList();
    }
    
	
}
