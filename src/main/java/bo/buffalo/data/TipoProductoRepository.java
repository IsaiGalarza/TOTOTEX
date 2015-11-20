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

import bo.buffalo.model.TipoProducto;

@ApplicationScoped
public class TipoProductoRepository {
	
	@Inject
    private EntityManager em;

    public TipoProducto findById(int id) {
    	try {
    		return em.find(TipoProducto.class, id);
		} catch (Exception e) {
			return null;
		}
        
    }
    
    public TipoProducto findByParameterObject(String parameter,Object valor) {
    	try {
    		CriteriaBuilder cb = em.getCriteriaBuilder();
            CriteriaQuery<TipoProducto> criteria = cb.createQuery(TipoProducto.class);
            Root<TipoProducto> object = criteria.from(TipoProducto.class);
            criteria.select(object).where(cb.equal(object.get(parameter), valor));
            return em.createQuery(criteria).getSingleResult();
		} catch (Exception e) {
			return null;
		}
        
    }
    

    public List<TipoProducto> findAllOrderedByDescripcion() {
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<TipoProducto> criteria = cb.createQuery(TipoProducto.class);
        Root<TipoProducto> tipoProducto = criteria.from(TipoProducto.class);
        criteria.select(tipoProducto).orderBy(cb.desc(tipoProducto.get("descripcion")));
        return em.createQuery(criteria).getResultList();
    }
    
    public List<TipoProducto> findAllOrderedByID() {
    	String query = "select ser from TipoProducto ser where ser.estado='AC' or ser.estado='IN' order by ser.id desc";
    	System.out.println("Query Servicios: "+query);
    	return em.createQuery(query).getResultList();
    }
    
    public List<TipoProducto> findAllOrderedByFechaRegistro() {
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<TipoProducto> criteria = cb.createQuery(TipoProducto.class);
        Root<TipoProducto> tipoProducto = criteria.from(TipoProducto.class);
        criteria.select(tipoProducto).orderBy(cb.desc(tipoProducto.get("fechaRegistro")));
        return em.createQuery(criteria).getResultList();
    }
    
    
    public List<TipoProducto> findAllServicesForDescription(String criterio) {
        try {
        	String query = "select ser from TipoProducto ser where ser.descripcion like '%"+criterio+"%'";
        	System.out.println("Consulta: "+query);
            List<TipoProducto> listaServicio = em.createQuery(query).getResultList();
            return listaServicio;        	
		} catch (Exception e) {
			// TODO: handle exception
			System.out.println("Error en findAllServicesForDescription: "+e.getMessage());
			return null;
		}
    }
    
    public List<TipoProducto> traerTipoProducto() {
        try {
        	String query = "select ser from TipoProducto ser where ser.estado='AC' order by ser.id desc";
        	System.out.println("Consulta traerTipoProducto: "+query);
            List<TipoProducto> listaTipoProducto = em.createQuery(query).getResultList();
            return listaTipoProducto;        	
		} catch (Exception e) {
			// TODO: handle exception
			System.out.println("Error en traerTipoProducto: "+e.getMessage());
			return null;
		}
    }
    
    
    public List<TipoProducto> findAllByParameterObject(String parameter,Object valor) {
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<TipoProducto> criteria = cb.createQuery(TipoProducto.class);
        Root<TipoProducto> object = criteria.from(TipoProducto.class);
        criteria.select(object).where(cb.equal(object.get(parameter), valor)).orderBy(cb.desc(object.get("id")));
        return em.createQuery(criteria).getResultList();
    }
    
    public List<TipoProducto> findAllByParameterIsNull(String parameter) {
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<TipoProducto> criteria = cb.createQuery(TipoProducto.class);
        Root<TipoProducto> object = criteria.from(TipoProducto.class);
        criteria.select(object).where(cb.isNull(object.get(parameter))).orderBy(cb.desc(object.get("id")));
        return em.createQuery(criteria).getResultList();
    }
    

    
    public List<TipoProducto> findAll100UltimosTipoProducto() {
        try {
        	String query = "select ser from TipoProducto ser order by ser.fechaRegistro desc";
        	System.out.println("Consulta: "+query);
            List<TipoProducto> listaTipoProducto = em.createQuery(query).setMaxResults(100).getResultList();
            return listaTipoProducto;        	
		} catch (Exception e) {
			// TODO: handle exception
			System.out.println("Error en findAll100UltimosTipoProducto: "+e.getMessage());
			return null;
		}
    }
    
    
	
}
