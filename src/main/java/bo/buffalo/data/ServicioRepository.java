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

import bo.buffalo.model.Servicio;

@ApplicationScoped
public class ServicioRepository {
	
	@Inject
    private EntityManager em;

    public Servicio findById(int id) {
        return em.find(Servicio.class, id);
    }

    public List<Servicio> findAllOrderedByName() {
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<Servicio> criteria = cb.createQuery(Servicio.class);
        Root<Servicio> servicio = criteria.from(Servicio.class);
        criteria.select(servicio).orderBy(cb.desc(servicio.get("nombreServicio")));
        return em.createQuery(criteria).getResultList();
    }
    
    @SuppressWarnings("unchecked")
	public List<Servicio> findAllOrderedByID() {
    	String query = "select ser from Servicio ser where ser.estado='AC' or ser.estado='IN' order by ser.id desc";
    	System.out.println("Query Servicio: "+query);
    	return em.createQuery(query).getResultList();
    }
    
    public List<Servicio> findAllOrderedByFechaRegistro() {
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<Servicio> criteria = cb.createQuery(Servicio.class);
        Root<Servicio> servicio = criteria.from(Servicio.class);
        criteria.select(servicio).orderBy(cb.desc(servicio.get("fechaRegistro")));
        return em.createQuery(criteria).getResultList();
    }
    
    
    @SuppressWarnings("unchecked")
	public List<Servicio> findAllServicesForDescription(String criterio) {
        try {
        	String query = "select ser from Servicio ser where upper(ser.nombreServicio) like '%"+criterio.toUpperCase()+"%' and ser.estado='AC'";
        	System.out.println("Consulta: "+query);
            List<Servicio> listaServicio = em.createQuery(query).setMaxResults(10).getResultList();
            System.out.println("Resultado: "+listaServicio.size());
            return listaServicio;        	
		} catch (Exception e) {
			// TODO: handle exception
			System.out.println("Error en findAllServicesForDescription: "+e.getMessage());
			return null;
		}
    }
    
    @SuppressWarnings("unchecked")
	public List<Servicio> traerServicios() {
        try {
        	String query = "select ser from Servicio ser where ser.estado='AC' order by ser.id desc";
        	System.out.println("Consulta traerServicios: "+query);
            List<Servicio> listaServicio = em.createQuery(query).getResultList();
            System.out.println("Resultado: "+listaServicio.size());
            return listaServicio;        	
		} catch (Exception e) {
			// TODO: handle exception
			System.out.println("Error en traerServicios: "+e.getMessage());
			return null;
		}
    }
    
    @SuppressWarnings("unchecked")
	public List<Servicio> traerServiciosUltimos10Servicios() {
        try {
        	String query = "select ser from Servicio ser where ser.estado='AC' order by ser.id desc";
        	System.out.println("Consulta traerServicios: "+query);
            List<Servicio> listaServicio = em.createQuery(query).setMaxResults(10).getResultList();
            System.out.println("Resultado: "+listaServicio.size());
            return listaServicio;        	
		} catch (Exception e) {
			// TODO: handle exception
			System.out.println("Error en traerServicios: "+e.getMessage());
			return null;
		}
    }
    
    @SuppressWarnings("unchecked")
	public List<Servicio> findAll100UltimosServicios() {
        try {
        	String query = "select ser from Servicio ser order by ser.fechaRegistro desc";
        	System.out.println("Consulta: "+query);
            List<Servicio> listaServicio = em.createQuery(query).setMaxResults(100).getResultList();
            System.out.println("Resultado: "+listaServicio.size());
            return listaServicio;        	
		} catch (Exception e) {
			// TODO: handle exception
			System.out.println("Error en findAllServicesForDescription: "+e.getMessage());
			return null;
		}
    }
    
    
	
}
