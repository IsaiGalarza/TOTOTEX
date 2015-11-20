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

import bo.buffalo.model.Roles;

@ApplicationScoped
public class RolesRepository {
	
	@Inject
    private EntityManager em;

    public Roles findById(int id) {
        return em.find(Roles.class, id);
    }

    public List<Roles> findAllOrderedByName() {
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<Roles> criteria = cb.createQuery(Roles.class);
        Root<Roles> rol = criteria.from(Roles.class);
        criteria.select(rol).orderBy(cb.desc(rol.get("name")));
        return em.createQuery(criteria).getResultList();
    }
    
}
