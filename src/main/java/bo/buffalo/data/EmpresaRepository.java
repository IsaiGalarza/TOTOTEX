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

import bo.buffalo.model.Empresa;

@ApplicationScoped
public class EmpresaRepository {
	
	@Inject
    private EntityManager em;

    public Empresa findById(int id) {
        return em.find(Empresa.class, id);
    }

    public List<Empresa> findAllOrderedByName() {
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<Empresa> criteria = cb.createQuery(Empresa.class);
        Root<Empresa> empresa = criteria.from(Empresa.class);
        criteria.select(empresa).orderBy(cb.desc(empresa.get("nombre")));
        return em.createQuery(criteria).getResultList();
    }
    
    @SuppressWarnings("unchecked")
	public List<Empresa> findAllOrderedByID() {
    	String query = "select emp from Empresa emp where emp.estado='AC' or emp.estado='IN' order by emp.id desc";
    	System.out.println("Query Empresa: "+query);
    	return em.createQuery(query).getResultList();
    }
    
    public List<Empresa> findAllOrderedByFechaRegistro() {
    	CriteriaBuilder cb = em.getCriteriaBuilder();
    	CriteriaQuery<Empresa> criteria = cb.createQuery(Empresa.class);
        Root<Empresa> empresa = criteria.from(Empresa.class);
        criteria.select(empresa).orderBy(cb.desc(empresa.get("fechaRegistro")));
        return em.createQuery(criteria).getResultList();
    }
    
    
    @SuppressWarnings("unchecked")
	public List<Empresa> findAllEmpresaForNombre(String criterio) {
        try {
        	String query = "select emp from Empresa emp where upper(emp.nombre) like '%"+criterio+"%' or upper(emp.especialidad) like '%"+criterio+"%' and emp.estado='AC' ";
        	System.out.println("Consulta findAllEmpresaForNombre: "+query);
            return em.createQuery(query).getResultList();
		} catch (Exception e) {
			// TODO: handle exception
			System.out.println("Error en findAllEmpresaForNombre: "+e.getMessage());
			return null;
		}
    }
    
    @SuppressWarnings("unchecked")
	public List<Empresa> traerEmpresasActivas() {
        try {
        	String query = "select emp from Empresa emp where emp.estado='AC' order by emp.id desc";
        	System.out.println("Consulta traerEmpresasActivas: "+query);
            return em.createQuery(query).getResultList();
		} catch (Exception e) {
			// TODO: handle exception
			System.out.println("Error en traerEmpresasActivas: "+e.getMessage());
			return null;
		}
    }
    
    
    @SuppressWarnings("unchecked")
   	public List<Empresa> traerEmpresaActivosPrimeros10() {
           try {
           	String query = "select emp from Empresa emp where emp.estado='AC' order by emp.id desc";
           	System.out.println("Consulta traerEmpresaActivosPrimeros10: "+query);
               return em.createQuery(query).setMaxResults(10).getResultList();
   		} catch (Exception e) {
   			// TODO: handle exception
   			System.out.println("Error en traerEmpresaActivosPrimeros10: "+e.getMessage());
   			return null;
   		}
       }
    
    
    
	
}
