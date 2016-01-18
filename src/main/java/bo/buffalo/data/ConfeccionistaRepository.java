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
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.persistence.EntityManager;

import bo.buffalo.model.Confeccionista;

@Stateless
public class ConfeccionistaRepository {
	
	@Inject
    private EntityManager em;

    public Confeccionista findById(int id) {
        return em.find(Confeccionista.class, id);
    }

    @SuppressWarnings("unchecked")
	public List<Confeccionista> findAllOrderedByNombre() {
    	String query = "select em from Confeccionista em where em.estado='AC' order by em.nombre desc";
    	System.out.println("Query Confeccionista: "+query);
    	return em.createQuery(query).getResultList();
    }
    
    @SuppressWarnings("unchecked")
	public List<Confeccionista> findActivosOrderedByFechaRegistro() {
    	String query = "select em from Confeccionista em where em.estado='AC' order by em.fechaRegistro desc";
    	System.out.println("Query Confeccionista: "+query);
    	return em.createQuery(query).getResultList();
    }
    
    @SuppressWarnings("unchecked")
	public List<Confeccionista> findAllOrderedByFechaRegistro() {
    	String query = "select em from Confeccionista em where em.estado='AC' or em.estado='IN' order by em.fechaRegistro desc";
    	System.out.println("Query Confeccionista: "+query);
    	return em.createQuery(query).getResultList();
    }
    
	
}
