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

import bo.buffalo.model.GastosProveedor;
import bo.buffalo.model.Proveedor;

@ApplicationScoped
public class GastosProveedorRepository {
	
	@Inject
    private EntityManager em;

    public GastosProveedor findById(int id) {
        return em.find(GastosProveedor.class, id);
    }

    public List<GastosProveedor> findAllOrderedByDescripcion() {
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<GastosProveedor> criteria = cb.createQuery(GastosProveedor.class);
        Root<GastosProveedor> gastosProveedor = criteria.from(GastosProveedor.class);
        criteria.select(gastosProveedor).orderBy(cb.desc(gastosProveedor.get("descripcion")));
        return em.createQuery(criteria).getResultList();
    }
    
    public List<GastosProveedor> findAllOrderedByID() {
    	String query = "select ser from GastosProveedor ser where ser.estado='AC' or ser.estado='IN' order by ser.id desc";
    	System.out.println("Query Servicios: "+query);
    	return em.createQuery(query).getResultList();
    }
    
    public List<GastosProveedor> findAllOrderedByFechaRegistro() {
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<GastosProveedor> criteria = cb.createQuery(GastosProveedor.class);
        Root<GastosProveedor> gastosProveedor = criteria.from(GastosProveedor.class);
        criteria.select(gastosProveedor).orderBy(cb.desc(gastosProveedor.get("fechaRegistro")));
        return em.createQuery(criteria).getResultList();
    }
    
    
    public List<GastosProveedor> findAllServicesForDescription(String criterio) {
        try {
        	String query = "select ser from GastosProveedor ser where ser.descripcion like '%"+criterio+"%'";
        	System.out.println("Consulta: "+query);
            List<GastosProveedor> listaServicio = em.createQuery(query).getResultList();
            return listaServicio;        	
		} catch (Exception e) {
			// TODO: handle exception
			System.out.println("Error en findAllServicesForDescription: "+e.getMessage());
			return null;
		}
    }
    
    public List<GastosProveedor> traerGastosProveedor() {
        try {
        	String query = "select ser from GastosProveedor ser where ser.estado='AC' order by ser.id desc";
        	System.out.println("Consulta traerGastosProveedor: "+query);
            List<GastosProveedor> listaGastosProveedor = em.createQuery(query).getResultList();
            return listaGastosProveedor;        	
		} catch (Exception e) {
			// TODO: handle exception
			System.out.println("Error en traerGastosProveedor: "+e.getMessage());
			return null;
		}
    }
    
    public List<GastosProveedor> traerGastosProveedor(Proveedor proveedor) {
        try {
        	String query = "select ser from GastosProveedor ser where ser.estado='AC' and ser.proveedor.id="+proveedor.getId()+" order by ser.id desc";
        	System.out.println("Consulta traerGastosProveedor: "+query);
            List<GastosProveedor> listaGastosProveedor = em.createQuery(query).getResultList();
            return listaGastosProveedor;        	
		} catch (Exception e) {
			// TODO: handle exception
			System.out.println("Error en traerGastosProveedor: "+e.getMessage());
			return null;
		}
    }
    
    public List<GastosProveedor> findAll100UltimosGastosProveedor() {
        try {
        	String query = "select ser from GastosProveedor ser order by ser.fechaRegistro desc";
        	System.out.println("Consulta: "+query);
            List<GastosProveedor> listaGastosProveedor = em.createQuery(query).setMaxResults(100).getResultList();
            return listaGastosProveedor;        	
		} catch (Exception e) {
			// TODO: handle exception
			System.out.println("Error en findAll100UltimosGastosProveedor: "+e.getMessage());
			return null;
		}
    }
    
    
	
}
