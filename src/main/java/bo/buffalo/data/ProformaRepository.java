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

import bo.buffalo.model.Proforma;

@ApplicationScoped
public class ProformaRepository {
	
	@Inject
    private EntityManager em;

    public Proforma findById(int id) {
        return em.find(Proforma.class, id);
    }

    public List<Proforma> findAllOrderedByName() {
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<Proforma> criteria = cb.createQuery(Proforma.class);
        Root<Proforma> empresa = criteria.from(Proforma.class);
        criteria.select(empresa).orderBy(cb.desc(empresa.get("nombre")));
        return em.createQuery(criteria).getResultList();
    }
    
    @SuppressWarnings("unchecked")
	public List<Proforma> findAllOrderedByID() {
    	String query = "select emp from Proforma emp where emp.estado='AC' or emp.estado='IN' order by emp.id desc";
    	System.out.println("Query Proforma: "+query);
    	return em.createQuery(query).getResultList();
    }
    
    
    @SuppressWarnings("unchecked")
   	public List<Proforma> findAllOrderedByIDFarmaco() {
       	String query = "select emp from Proforma emp where (emp.estado='AC' or emp.estado='IN' or emp.estado='PR' or emp.estado='FA') and emp.tipoProforma='REPUESTO' order by emp.id desc";
       	System.out.println("Query Proforma: "+query);
       	return em.createQuery(query).getResultList();
    }
    
    @SuppressWarnings("unchecked")
   	public List<Proforma> findAllOrderedByIDServicio() {
       	try {
       		String query = "select emp from Proforma emp where (emp.estado='AC' or emp.estado='IN' or emp.estado='PR') and emp.tipoProforma='SERVICIO' order by emp.id desc";
           	System.out.println("Query Proforma: "+query);
           	return em.createQuery(query).getResultList();
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
			return null;
		}
    }
    
    @SuppressWarnings("unchecked")
   	public List<Proforma> findAllOrderedFechaRegistro() {
       	try {
       		String query = "select emp from Proforma emp where (emp.estado='AC' or emp.estado='IN' or emp.estado='PR' or emp.estado='FA') and emp.tipoProforma='SERVICIO' order by emp.fechaRegistro desc";
           	System.out.println("Query Proforma: "+query);
           	return em.createQuery(query).getResultList();
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
			return null;
		}
    }
    
    @SuppressWarnings("unchecked")
   	public List<Proforma> findAllOrderedByIDPreparado() {
       	String query = "select emp from Proforma emp where (emp.estado='AC' or emp.estado='IN' or emp.estado='PR') and emp.tipoProforma='PREPARADO' order by emp.fechaRegistro desc";
       	System.out.println("Query Proforma: "+query);
       	return em.createQuery(query).getResultList();
       }
    
    
	public List<Proforma> findAllOrderedByIDPreparadoProcess() {
       	String query = "select emp from Proforma emp where  emp.estado='PR' and emp.tipoProforma='PREPARADO' order by emp.id desc";
       	System.out.println("Query Proforma: "+query);
       	return em.createQuery(query).getResultList();
       }
	
    public List<Proforma> findAllOrderedByFechaRegistro() {
    	CriteriaBuilder cb = em.getCriteriaBuilder();
    	CriteriaQuery<Proforma> criteria = cb.createQuery(Proforma.class);
        Root<Proforma> empresa = criteria.from(Proforma.class);
        criteria.select(empresa).orderBy(cb.desc(empresa.get("fechaRegistro")));
        return em.createQuery(criteria).getResultList();
    }
    
    
    @SuppressWarnings("unchecked")
	public List<Proforma> findAllProformaForNombre(String criterio) {
        try {
        	String query = "select emp from Proforma emp where upper(emp.nombre) like '%"+criterio+"%' or upper(emp.especialidad) like '%"+criterio+"%' and emp.estado='AC' ";
        	System.out.println("Consulta findAllProformaForNombre: "+query);
            return em.createQuery(query).getResultList();
		} catch (Exception e) {
			// TODO: handle exception
			System.out.println("Error en findAllProformaForNombre: "+e.getMessage());
			return null;
		}
    }
    
    @SuppressWarnings("unchecked")
	public List<Proforma> buscarProformaCliente(String nombreCliente) {
        try {
        	String query = "select pro from Proforma pro where upper(pro.cliente.nombreCompleto) like '%"+nombreCliente+"%' and pro.estado='PR' order by pro.fechaRegistro desc";
        	System.out.println("Consulta buscarProformaCliente: "+query);
            return em.createQuery(query).getResultList();
		} catch (Exception e) {
			// TODO: handle exception
			System.out.println("Error en buscarProformaCliente: "+e.getMessage());
			return null;
		}
    }
    
    @SuppressWarnings("unchecked")
	public List<Proforma> traerProformasProcesadas() {
        try {
        	String query = "select pro from Proforma pro where pro.estado='PR' order by pro.fechaRegistro desc";
        	System.out.println("Consulta buscarProformaCliente: "+query);
            return em.createQuery(query).getResultList();
		} catch (Exception e) {
			// TODO: handle exception
			System.out.println("Error en buscarProformaCliente: "+e.getMessage());
			return null;
		}
    }
    
    @SuppressWarnings("unchecked")
	public List<Proforma> traerProformasActivas() {
        try {
        	String query = "select emp from Proforma emp where emp.estado='AC' order by emp.id desc";
        	System.out.println("Consulta traerProformasActivas: "+query);
            return em.createQuery(query).getResultList();
		} catch (Exception e) {
			// TODO: handle exception
			System.out.println("Error en traerProformasActivas: "+e.getMessage());
			return null;
		}
    }
    
    @SuppressWarnings("unchecked")
	public List<Proforma> traerProformasDeVentas() {
        try {
        	String query = "select emp from Proforma emp where emp.estado='PR' order by emp.fechaRegistro desc";
        	System.out.println("Consulta traerProformasDeVentas: "+query);
            return em.createQuery(query).getResultList();
		} catch (Exception e) {
			// TODO: handle exception
			System.out.println("Error en traerProformasDeVentas: "+e.getMessage());
			return null;
		}
    }
    
    
    @SuppressWarnings("unchecked")
   	public List<Proforma> traerProformaActivosPrimeros10() {
           try {
           	String query = "select emp from Proforma emp where emp.estado='AC' order by emp.id desc";
           	System.out.println("Consulta traerProformaActivosPrimeros10: "+query);
               return em.createQuery(query).setMaxResults(10).getResultList();
   		} catch (Exception e) {
   			// TODO: handle exception
   			System.out.println("Error en traerProformaActivosPrimeros10: "+e.getMessage());
   			return null;
   		}
       }
    
    
    
	
}
