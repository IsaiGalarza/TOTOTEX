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

import bo.buffalo.model.Almacen;
import bo.buffalo.model.Sucursal;
import bo.buffalo.model.Usuario;

@ApplicationScoped
public class AlmacenRepository {
	
	@Inject
    private EntityManager em;

    public Almacen findById(int id) {
        return em.find(Almacen.class, id);
    }

    public List<Almacen> findAllOrderedByDescripcion() {
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<Almacen> criteria = cb.createQuery(Almacen.class);
        Root<Almacen> presentacion = criteria.from(Almacen.class);
        criteria.select(presentacion).orderBy(cb.desc(presentacion.get("descripcion")));
        return em.createQuery(criteria).getResultList();
    }
    
    public List<Almacen> findAllOrderedByID() {
    	String query = "select ser from Almacen ser where ser.estado='AC' or ser.estado='IN' order by ser.id desc";
    	System.out.println("Query Almacen: "+query);
    	return em.createQuery(query).getResultList();
    }
    
    public Almacen findAlmacenForUser(Usuario user) {
    	String query = "select ser from Almacen ser where (ser.estado='AC' or ser.estado='IN') and ser.encargado.id="+user.getId()+" order by ser.id desc";
    	System.out.println("Query Almacen: "+query);
    	List<Almacen> listAlmacen=em.createQuery(query).getResultList();
    	if (listAlmacen.size()>0) {
			return listAlmacen.get(0);
		}else{
			Almacen almacen= new Almacen();
			almacen.setId(-1);
			return almacen;
		}
    }
    
    public List<Almacen> findAlmacenForSucursal(Sucursal suc) {
    	String query = "select ser.almacen from AlmacenSucursal ser  where ser.sucursal.id="+suc.getId()+" and ser.almacen.estado='AC' order by ser.almacen.nombre asc";
    	System.out.println("Query findAlmacenForSucursal: "+query);
    	return em.createQuery(query).getResultList();
    }
    
    public List<Almacen> findAlmacensDiferentsUser(Usuario user) {
    	String query = "select ser from Almacen ser where (ser.estado='AC' or ser.estado='IN') and ser.encargado.id<>"+user.getId()+" order by ser.id desc";
    	System.out.println("Query Almacen: "+query);
    	return em.createQuery(query).getResultList();
    }
    
    public List<Almacen> findAllOrderedByFechaRegistro() {
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<Almacen> criteria = cb.createQuery(Almacen.class);
        Root<Almacen> presentacion = criteria.from(Almacen.class);
        criteria.select(presentacion).orderBy(cb.desc(presentacion.get("fechaRegistro")));
        return em.createQuery(criteria).getResultList();
    }
    
    
    public List<Almacen> findAllAlmacenForDescription(String criterio) {
        try {
        	String query = "select ser from Almacen ser where ser.nombre like '%"+criterio+"%'";
        	System.out.println("Consulta: "+query);
            List<Almacen> listaAlmacen = em.createQuery(query).getResultList();
            return listaAlmacen;        	
		} catch (Exception e) {
			// TODO: handle exception
			System.out.println("Error en findAllAlmacenForDescription: "+e.getMessage());
			return null;
		}
    }
    
    public List<Almacen> traerAlmacenActivas() {
        try {
        	String query = "select ser from Almacen ser where ser.estado='AC' order by ser.id desc";
        	System.out.println("Consulta traerAlmacenActivas: "+query);
            List<Almacen> listaAlmacen = em.createQuery(query).getResultList();
            return listaAlmacen;        	
		} catch (Exception e) {
			// TODO: handle exception
			System.out.println("Error en traerAlmacenActivas: "+e.getMessage());
			return null;
		}
    }
    
    public List<Almacen> findAll100UltimosAlmacen() {
        try {
        	String query = "select ser from Almacen ser order by ser.fechaRegistro desc";
        	System.out.println("Consulta: "+query);
            List<Almacen> listaAlmacen = em.createQuery(query).setMaxResults(100).getResultList();
            return listaAlmacen;        	
		} catch (Exception e) {
			// TODO: handle exception
			System.out.println("Error en findAll100UltimosAlmacen: "+e.getMessage());
			return null;
		}
    }
    
    
	
}
