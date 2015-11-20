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

import bo.buffalo.model.UnidadMedida;

@ApplicationScoped
public class UnidadMedidaRepository {
	
	@Inject
    private EntityManager em;

    public UnidadMedida findById(int id) {
        return em.find(UnidadMedida.class, id);
    }

    public List<UnidadMedida> findAllOrderedByDescripcion() {
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<UnidadMedida> criteria = cb.createQuery(UnidadMedida.class);
        Root<UnidadMedida> unidadMedida = criteria.from(UnidadMedida.class);
        criteria.select(unidadMedida).orderBy(cb.desc(unidadMedida.get("descripcion")));
        return em.createQuery(criteria).getResultList();
    }
    
    public List<UnidadMedida> findAllOrderedByID() {
    	String query = "select ser from UnidadMedida ser where ser.estado='AC' or ser.estado='IN' order by ser.id desc";
    	System.out.println("Query Servicios: "+query);
    	return em.createQuery(query).getResultList();
    }
    
    public List<UnidadMedida> findAllOrderedByFechaRegistro() {
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<UnidadMedida> criteria = cb.createQuery(UnidadMedida.class);
        Root<UnidadMedida> unidadMedida = criteria.from(UnidadMedida.class);
        criteria.select(unidadMedida).orderBy(cb.desc(unidadMedida.get("fechaRegistro")));
        return em.createQuery(criteria).getResultList();
    }
    
    
    public List<UnidadMedida> findAllUnidadMedidaForDescription(String criterio) {
        try {
        	String query = "select ser from UnidadMedida ser where ser.descripcion like '%"+criterio+"%'";
        	System.out.println("Consulta: "+query);
            List<UnidadMedida> listaUnidadMedida = em.createQuery(query).getResultList();
            return listaUnidadMedida;        	
		} catch (Exception e) {
			// TODO: handle exception
			System.out.println("Error en findAllUnidadMedidaForDescription: "+e.getMessage());
			return null;
		}
    }
    
    public List<UnidadMedida> traerUnidadMedidaActivas() {
        try {
        	String query = "select ser from UnidadMedida ser where ser.estado='AC' order by ser.id desc";
        	System.out.println("Consulta traerTipoProducto: "+query);
            List<UnidadMedida> listaUnidadMedida = em.createQuery(query).getResultList();
            return listaUnidadMedida;        	
		} catch (Exception e) {
			// TODO: handle exception
			System.out.println("Error en traerUnidadMedidaActivas: "+e.getMessage());
			return null;
		}
    }
    
    
    public List<UnidadMedida> traerUnidadMedidaActivas(UnidadMedida unidadMedida) {
        try {
        	String query = "select ser from UnidadMedida ser where ser.estado='AC' and ser.id<>"+unidadMedida.getId()
        			+" and ser.id not in (select con.conversion.id from Conversiones con where  con.unidadMedida.id="+unidadMedida.getId()+") order by ser.id desc";
        	System.out.println("Consulta traerTipoProducto: "+query);
            List<UnidadMedida> listaUnidadMedida = em.createQuery(query).getResultList();
            return listaUnidadMedida;        	
		} catch (Exception e) {
			// TODO: handle exception
			System.out.println("Error en traerUnidadMedidaActivas: "+e.getMessage());
			return null;
		}
    }
    
    public List<UnidadMedida> findAll100UltimosUnidadMedida() {
        try {
        	String query = "select ser from UnidadMedida ser order by ser.fechaRegistro desc";
        	System.out.println("Consulta: "+query);
            List<UnidadMedida> listaUnidadMedida = em.createQuery(query).setMaxResults(100).getResultList();
            return listaUnidadMedida;        	
		} catch (Exception e) {
			// TODO: handle exception
			System.out.println("Error en findAll100UltimosUnidadMedida: "+e.getMessage());
			return null;
		}
    }
    
    
	
}
