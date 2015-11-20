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

import bo.buffalo.model.Marca;

@ApplicationScoped
public class MarcaRepository {
	
	@Inject
    private EntityManager em;

    public Marca findById(int id) {
        return em.find(Marca.class, id);
    }

    public List<Marca> findAllOrderedByDescripcion() {
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<Marca> criteria = cb.createQuery(Marca.class);
        Root<Marca> Marca = criteria.from(Marca.class);
        criteria.select(Marca).orderBy(cb.desc(Marca.get("descripcion")));
        return em.createQuery(criteria).getResultList();
    }
    
    public List<Marca> findAllOrderedByID() {
    	String query = "select ser from Marca ser where ser.estado='AC' or ser.estado='IN' order by ser.fechaRegistro desc";
    	System.out.println("Query Marca: "+query);
    	return em.createQuery(query).getResultList();
    }
    
    public List<Marca> findAllOrderedByFechaRegistro() {
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<Marca> criteria = cb.createQuery(Marca.class);
        Root<Marca> Marca = criteria.from(Marca.class);
        criteria.select(Marca).orderBy(cb.desc(Marca.get("fechaRegistro")));
        return em.createQuery(criteria).getResultList();
    }
    
    
    public List<Marca> findAllMarcaForDescription(String criterio) {
        try {
        	String query = "select ser from Marca ser where ser.descripcion like '%"+criterio+"%'";
        	System.out.println("Consulta: "+query);
            List<Marca> listaMarca = em.createQuery(query).getResultList();
            return listaMarca;        	
		} catch (Exception e) {
			// TODO: handle exception
			System.out.println("Error en findAllMarcaForDescription: "+e.getMessage());
			return null;
		}
    }
    
    public List<Marca> traerMarcaActivas() {
        try {
        	String query = "select ser from Marca ser where ser.estado='AC' order by ser.fechaRegistro desc";
        	System.out.println("Consulta traerMarcaActivas: "+query);
            List<Marca> listaMarca = em.createQuery(query).getResultList();
            return listaMarca;        	
		} catch (Exception e) {
			// TODO: handle exception
			System.out.println("Error en traerMarcaActivas: "+e.getMessage());
			return null;
		}
    }
    
    public List<Marca> findAll100UltimosMarca() {
        try {
        	String query = "select ser from Marca ser order by ser.fechaRegistro desc";
        	System.out.println("Consulta: "+query);
            List<Marca> listaMarca = em.createQuery(query).setMaxResults(100).getResultList();
            return listaMarca;        	
		} catch (Exception e) {
			// TODO: handle exception
			System.out.println("Error en findAll100UltimosMarca: "+e.getMessage());
			return null;
		}
    }
    
    
	
}
