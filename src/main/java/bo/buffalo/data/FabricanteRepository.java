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

import bo.buffalo.model.Fabricante;

@ApplicationScoped
public class FabricanteRepository {
	
	@Inject
    private EntityManager em;

    public Fabricante findById(int id) {
        return em.find(Fabricante.class, id);
    }

    public List<Fabricante> findAllOrderedByDescripcion() {
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<Fabricante> criteria = cb.createQuery(Fabricante.class);
        Root<Fabricante> Fabricante = criteria.from(Fabricante.class);
        criteria.select(Fabricante).orderBy(cb.desc(Fabricante.get("descripcion")));
        return em.createQuery(criteria).getResultList();
    }
    
    public List<Fabricante> findAllOrderedByID() {
    	String query = "select ser from Fabricante ser where ser.estado='AC' or ser.estado='IN' order by ser.fechaRegistro desc";
    	System.out.println("Query Fabricante: "+query);
    	return em.createQuery(query).getResultList();
    }
    
    public List<Fabricante> findAllOrderedByFechaRegistro() {
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<Fabricante> criteria = cb.createQuery(Fabricante.class);
        Root<Fabricante> Fabricante = criteria.from(Fabricante.class);
        criteria.select(Fabricante).orderBy(cb.desc(Fabricante.get("fechaRegistro")));
        return em.createQuery(criteria).getResultList();
    }
    
    
    public List<Fabricante> findAllFabricanteForDescription(String criterio) {
        try {
        	String query = "select ser from Fabricante ser where ser.descripcion like '%"+criterio+"%'";
        	System.out.println("Consulta: "+query);
            List<Fabricante> listaFabricante = em.createQuery(query).getResultList();
            return listaFabricante;        	
		} catch (Exception e) {
			// TODO: handle exception
			System.out.println("Error en findAllFabricanteForDescription: "+e.getMessage());
			return null;
		}
    }
    
    public List<Fabricante> traerFabricanteActivas() {
        try {
        	String query = "select ser from Fabricante ser where ser.estado='AC' order by ser.fechaRegistro desc";
        	System.out.println("Consulta traerFabricanteActivas: "+query);
            List<Fabricante> listaFabricante = em.createQuery(query).getResultList();
            return listaFabricante;        	
		} catch (Exception e) {
			// TODO: handle exception
			System.out.println("Error en traerFabricanteActivas: "+e.getMessage());
			return null;
		}
    }
    
    public List<Fabricante> findAll100UltimosFabricante() {
        try {
        	String query = "select ser from Fabricante ser order by ser.fechaRegistro desc";
        	System.out.println("Consulta: "+query);
            List<Fabricante> listaFabricante = em.createQuery(query).setMaxResults(100).getResultList();
            return listaFabricante;        	
		} catch (Exception e) {
			// TODO: handle exception
			System.out.println("Error en findAll100UltimosFabricante: "+e.getMessage());
			return null;
		}
    }
    
    
	
}
