/*
* CantidadUnidadPresentacionRepository.java	1.0 2014/09/19
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

import bo.buffalo.model.CantidadUnidadPresentacion;

@ApplicationScoped
public class CantUnitPresRepository {
	
	@Inject
    private EntityManager em;

    public CantidadUnidadPresentacion findById(int id) {
        try {
        	return em.find(CantidadUnidadPresentacion.class, id);
		} catch (Exception e) {
			// TODO: handle exception
			return null;
		}
    }
    
    public List<CantidadUnidadPresentacion> findAllOrderedByID() {
    	String query = "select ser from CantidadUnidadPresentacion ser where ser.estado='AC' or ser.estado='IN' order by ser.id desc";
    	System.out.println("Query CantidadUnidadPresentacion: "+query);
    	return em.createQuery(query).getResultList();
    }

    
   
    
    public List<CantidadUnidadPresentacion> findAllOrderedByName() {
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<CantidadUnidadPresentacion> criteria = cb.createQuery(CantidadUnidadPresentacion.class);
        Root<CantidadUnidadPresentacion> cCantidadUnidadPresentacion = criteria.from(CantidadUnidadPresentacion.class);
        criteria.select(cCantidadUnidadPresentacion).orderBy(cb.asc(cCantidadUnidadPresentacion.get("nombreCompleto")));
        return em.createQuery(criteria).getResultList();
    }
    
    public List<CantidadUnidadPresentacion> findAllOrderedByFechaRegistro() {
    	CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<CantidadUnidadPresentacion> criteria = cb.createQuery(CantidadUnidadPresentacion.class);
        Root<CantidadUnidadPresentacion> cCantidadUnidadPresentacion = criteria.from(CantidadUnidadPresentacion.class);
        criteria.select(cCantidadUnidadPresentacion).orderBy(cb.desc(cCantidadUnidadPresentacion.get("fechaRegistro")));
        return em.createQuery(criteria).getResultList();
    }
    
    
    public List<CantidadUnidadPresentacion> findAllClientsForName(String criterio) {
        try {
        	String query = "select ser from CantidadUnidadPresentacion ser where ser.nombreCompleto like '%"+criterio+"%'";
        	System.out.println("Consulta: "+query);
            List<CantidadUnidadPresentacion> listaCantidadUnidadPresentacions = em.createQuery(query).getResultList();
            System.out.println("Resultado: "+listaCantidadUnidadPresentacions.size());
            return listaCantidadUnidadPresentacions;        	
		} catch (Exception e) {
			// TODO: handle exception
			System.out.println("Error en findAllClientsForName: "+e.getMessage());
			return null;
		}
    }
    
    
	
}
