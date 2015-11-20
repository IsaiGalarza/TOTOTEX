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

import bo.buffalo.model.Presentacion;
import bo.buffalo.model.UnidadMedida;

@ApplicationScoped
public class PresentacionRepository {
	
	@Inject
    private EntityManager em;

    public Presentacion findById(int id) {
        return em.find(Presentacion.class, id);
    }

    public List<Presentacion> findAllOrderedByDescripcion() {
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<Presentacion> criteria = cb.createQuery(Presentacion.class);
        Root<Presentacion> presentacion = criteria.from(Presentacion.class);
        criteria.select(presentacion).orderBy(cb.desc(presentacion.get("descripcion")));
        return em.createQuery(criteria).getResultList();
    }
    
    public List<Presentacion> findAllOrderedByID() {
    	String query = "select ser from Presentacion ser where ser.estado='AC' or ser.estado='IN' order by ser.id desc";
    	System.out.println("Query Presentacion: "+query);
    	return em.createQuery(query).getResultList();
    }
    
    public List<Presentacion> findAllOrderedByFechaRegistro() {
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<Presentacion> criteria = cb.createQuery(Presentacion.class);
        Root<Presentacion> presentacion = criteria.from(Presentacion.class);
        criteria.select(presentacion).orderBy(cb.desc(presentacion.get("fechaRegistro")));
        return em.createQuery(criteria).getResultList();
    }
    
    
    public List<Presentacion> findAllPresentacionForDescription(String criterio) {
        try {
        	String query = "select ser from Presentacion ser where ser.descripcion like '%"+criterio+"%'";
        	System.out.println("Consulta: "+query);
            List<Presentacion> listaPresentacion = em.createQuery(query).getResultList();
            return listaPresentacion;        	
		} catch (Exception e) {
			// TODO: handle exception
			System.out.println("Error en findAllPresentacionForDescription: "+e.getMessage());
			return null;
		}
    }
    
    public List<Presentacion> traerPresentacionActivas() {
        try {
        	String query = "select ser from Presentacion ser where ser.estado='AC' order by ser.id desc";
        	System.out.println("Consulta traerPresentacionActivas: "+query);
            List<Presentacion> listaPresentacion = em.createQuery(query).getResultList();
            return listaPresentacion;        	
		} catch (Exception e) {
			// TODO: handle exception
			System.out.println("Error en traerPresentacionActivas: "+e.getMessage());
			return null;
		}
    }
    
    public List<Presentacion> findAll100UltimosPresentacion() {
        try {
        	String query = "select ser from Presentacion ser order by ser.fechaRegistro desc";
        	System.out.println("Consulta: "+query);
            List<Presentacion> listaPresentacion = em.createQuery(query).setMaxResults(100).getResultList();
            return listaPresentacion;        	
		} catch (Exception e) {
			// TODO: handle exception
			System.out.println("Error en findAll100UltimosPresentacion: "+e.getMessage());
			return null;
		}
    }
    
    
	
}
