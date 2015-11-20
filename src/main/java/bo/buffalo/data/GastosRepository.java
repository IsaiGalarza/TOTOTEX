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

import bo.buffalo.model.Gastos;
import bo.buffalo.model.Proveedor;

@ApplicationScoped
public class GastosRepository {
	
	@Inject
    private EntityManager em;

    public Gastos findById(int id) {
        return em.find(Gastos.class, id);
    }

    public List<Gastos> findAllOrderedByDescripcion() {
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<Gastos> criteria = cb.createQuery(Gastos.class);
        Root<Gastos> gastos = criteria.from(Gastos.class);
        criteria.select(gastos).orderBy(cb.desc(gastos.get("descripcion")));
        return em.createQuery(criteria).getResultList();
    }
    
    public List<Gastos> findAllOrderedByID() {
    	String query = "select ser from Gastos ser where ser.estado='AC' or ser.estado='IN' order by ser.id desc";
    	System.out.println("Query Servicios: "+query);
    	return em.createQuery(query).getResultList();
    }
    
    public List<Gastos> findAllOrderedByFechaRegistro() {
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<Gastos> criteria = cb.createQuery(Gastos.class);
        Root<Gastos> gastos = criteria.from(Gastos.class);
        criteria.select(gastos).orderBy(cb.desc(gastos.get("fechaRegistro")));
        return em.createQuery(criteria).getResultList();
    }
    
    
    public List<Gastos> findAllServicesForDescription(String criterio) {
        try {
        	String query = "select ser from Gastos ser where ser.descripcion like '%"+criterio+"%'";
        	System.out.println("Consulta: "+query);
            List<Gastos> listaServicio = em.createQuery(query).getResultList();
            return listaServicio;        	
		} catch (Exception e) {
			// TODO: handle exception
			System.out.println("Error en findAllServicesForDescription: "+e.getMessage());
			return null;
		}
    }
    
    public List<Gastos> traerGastos(Proveedor proveedor,boolean sw) {
        try {
        	String query = "select ser from Gastos ser where ser.estado='AC' order by ser.descripcion asc";
        	if (!sw) {
				query="select ser from Gastos ser where ser.estado='AC' and ser.id not in(select gas.gastos.id from GastosProveedor gas where gas.proveedor.id="+proveedor.getId()+") order by ser.descripcion asc";
			}
        	System.out.println("Consulta traerGastos: "+query);
            List<Gastos> listaGastos = em.createQuery(query).getResultList();
            return listaGastos;        	
		} catch (Exception e) {
			// TODO: handle exception
			System.out.println("Error en traerGastos: "+e.getMessage());
			return null;
		}
    }
    
    public List<Gastos> findAll100UltimosGastos() {
        try {
        	String query = "select ser from Gastos ser order by ser.fechaRegistro desc";
        	System.out.println("Consulta: "+query);
            List<Gastos> listaGastos = em.createQuery(query).setMaxResults(100).getResultList();
            return listaGastos;        	
		} catch (Exception e) {
			// TODO: handle exception
			System.out.println("Error en findAll100UltimosGastos: "+e.getMessage());
			return null;
		}
    }
    
    
	
}
