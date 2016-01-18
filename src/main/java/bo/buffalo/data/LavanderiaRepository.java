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

import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.persistence.EntityManager;

import bo.buffalo.model.Lavanderia;

@Stateless
public class LavanderiaRepository {
	
	@Inject
    private EntityManager em;

    public Lavanderia findById(int id) {
        return em.find(Lavanderia.class, id);
    }

    @SuppressWarnings("unchecked")
	public List<Lavanderia> findAllOrderedByNombre() {
    	String query = "select em from Lavanderia em where em.estado='AC' order by em.nombre desc";
    	System.out.println("Query Lavanderia: "+query);
    	return em.createQuery(query).getResultList();
    }
    
    @SuppressWarnings("unchecked")
	public List<Lavanderia> findActivosOrderedByFechaRegistro() {
    	String query = "select em from Lavanderia em where em.estado='AC' order by em.fechaRegistro desc";
    	System.out.println("Query Lavanderia: "+query);
    	return em.createQuery(query).getResultList();
    }
    
    @SuppressWarnings("unchecked")
	public List<Lavanderia> findAllOrderedByFechaRegistro() {
    	String query = "select em from Lavanderia em where em.estado='AC' or em.estado='IN' order by em.fechaRegistro desc";
    	System.out.println("Query Lavanderia: "+query);
    	return em.createQuery(query).getResultList();
    }
    
	
}
