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

import bo.buffalo.model.LineasProveedor;

@ApplicationScoped
public class LineaProveedorRepository {
	
	@Inject
    private EntityManager em;

    public LineasProveedor findById(int id) {
        return em.find(LineasProveedor.class, id);
    }

    public List<LineasProveedor> findAllOrderedByDescripcion() {
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<LineasProveedor> criteria = cb.createQuery(LineasProveedor.class);
        Root<LineasProveedor> LineasProveedor = criteria.from(LineasProveedor.class);
        criteria.select(LineasProveedor).orderBy(cb.desc(LineasProveedor.get("nombre")));
        return em.createQuery(criteria).getResultList();
    }
    
    public List<LineasProveedor> findAllOrderedByID() {
    	String query = "select ser from LineasProveedor ser where ser.estado='AC' or ser.estado='IN' order by ser.id desc";
    	System.out.println("Query LineasProveedor: "+query);
    	return em.createQuery(query).getResultList();
    }
    
    public List<LineasProveedor> findAllOrderedByFechaRegistro() {
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<LineasProveedor> criteria = cb.createQuery(LineasProveedor.class);
        Root<LineasProveedor> LineasProveedor = criteria.from(LineasProveedor.class);
        criteria.select(LineasProveedor).orderBy(cb.desc(LineasProveedor.get("fechaRegistro")));
        return em.createQuery(criteria).getResultList();
    }
    
    
    public List<LineasProveedor> findAllLineasProveedoresForDescription(String criterio) {
        try {
        	String query = "select ser from LineasProveedor ser where ser.nombre like '%"+criterio+"%'";
        	System.out.println("Consulta: "+query);
            List<LineasProveedor> listaProvedores = em.createQuery(query).getResultList();
            return listaProvedores;        	
		} catch (Exception e) {
			// TODO: handle exception
			System.out.println("Error en findAllLineasProveedorForDescription: "+e.getMessage());
			return null;
		}
    }
    
    public List<LineasProveedor> traerLineasProveedor(int proveedorID) {
        try {
        	System.out.println("Ingreso a traerLineasProveedor: "+proveedorID);
        	String query = "select ser from LineasProveedor ser where ser.estado='AC' and ser.proveedor.id="+proveedorID+" order by ser.id desc";
        	System.out.println("Consulta traerLineasProveedor: "+query);
            return em.createQuery(query).getResultList();
		} catch (Exception e) {
			// TODO: handle exception
			System.out.println("Error en traerLineasProveedor: "+e.getMessage());
			return null;
		}
    }
    
    public List<LineasProveedor> traerLineasProveedoresActivas() {
        try {
        	String query = "select ser from LineasProveedor ser where ser.estado='AC' order by ser.id desc";
        	System.out.println("Consulta traerLineasProveedoresActivas: "+query);
            List<LineasProveedor> listaProvedores = em.createQuery(query).getResultList();
            return listaProvedores;        	
		} catch (Exception e) {
			// TODO: handle exception
			System.out.println("Error en traerLineasProveedorActivas: "+e.getMessage());
			return null;
		}
    }
    
    public List<LineasProveedor> findAll100UltimosLineasProveedores() {
        try {
        	String query = "select ser from LineasProveedor ser order by ser.fechaRegistro desc";
        	System.out.println("Consulta: "+query);
            List<LineasProveedor> listaProvedores = em.createQuery(query).setMaxResults(100).getResultList();
            return listaProvedores;        	
		} catch (Exception e) {
			// TODO: handle exception
			System.out.println("Error en findAll100UltimosLineasProveedor: "+e.getMessage());
			return null;
		}
    }
    
    
	
}
