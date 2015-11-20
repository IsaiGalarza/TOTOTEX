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

import bo.buffalo.model.Ciudad;

@ApplicationScoped
public class CiudadRepository {
	
	@Inject
    private EntityManager em;

    public Ciudad findById(int id) {
        return em.find(Ciudad.class, id);
    }

    public List<Ciudad> findAllOrderedByDescripcion() {
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<Ciudad> criteria = cb.createQuery(Ciudad.class);
        Root<Ciudad> tipoProducto = criteria.from(Ciudad.class);
        criteria.select(tipoProducto).orderBy(cb.desc(tipoProducto.get("descripcion")));
        return em.createQuery(criteria).getResultList();
    }
    
    public List<Ciudad> findAllOrderedByID() {
    	String query = "select ser from Ciudad ser where ser.estado='AC' or ser.estado='IN' order by ser.id desc";
    	System.out.println("Query Servicios: "+query);
    	return em.createQuery(query).getResultList();
    }
    
    public List<Ciudad> findAllOrderedByFechaRegistro() {
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<Ciudad> criteria = cb.createQuery(Ciudad.class);
        Root<Ciudad> tipoProducto = criteria.from(Ciudad.class);
        criteria.select(tipoProducto).orderBy(cb.desc(tipoProducto.get("fechaRegistro")));
        return em.createQuery(criteria).getResultList();
    }
    
    
    public List<Ciudad> findAllServicesForDescription(String criterio) {
        try {
        	String query = "select ser from Ciudad ser where ser.descripcion like '%"+criterio+"%'";
        	System.out.println("Consulta: "+query);
            List<Ciudad> listaServicio = em.createQuery(query).getResultList();
            return listaServicio;        	
		} catch (Exception e) {
			// TODO: handle exception
			System.out.println("Error en findAllServicesForDescription: "+e.getMessage());
			return null;
		}
    }
    
    public List<Ciudad> traerCiudad() {
        try {
        	String query = "select ser from Ciudad ser where ser.estado='AC' order by ser.id desc";
        	System.out.println("Consulta traerCiudad: "+query);
            List<Ciudad> listaCiudad = em.createQuery(query).getResultList();
            return listaCiudad;        	
		} catch (Exception e) {
			// TODO: handle exception
			System.out.println("Error en traerCiudad: "+e.getMessage());
			return null;
		}
    }
    
    public List<Ciudad> findAll100UltimosCiudad() {
        try {
        	String query = "select ser from Ciudad ser order by ser.fechaRegistro desc";
        	System.out.println("Consulta: "+query);
            List<Ciudad> listaCiudad = em.createQuery(query).setMaxResults(100).getResultList();
            return listaCiudad;        	
		} catch (Exception e) {
			// TODO: handle exception
			System.out.println("Error en findAll100UltimosCiudad: "+e.getMessage());
			return null;
		}
    }
    
    
	
}
