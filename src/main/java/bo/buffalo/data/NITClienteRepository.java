/*
* ClienteRepository.java	1.0 2014/09/19
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

import bo.buffalo.model.Cliente;
import bo.buffalo.model.NitCliente;

@ApplicationScoped
public class NITClienteRepository {
	
	@Inject
    private EntityManager em;

    public NitCliente findById(int id) {
        try {
        	return em.find(NitCliente.class, id);
		} catch (Exception e) {
			// TODO: handle exception
			return null;
		}
    }

    public List<NitCliente> findAllOrderedByID() {
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<NitCliente> criteria = cb.createQuery(NitCliente.class);
        Root<NitCliente> cliente = criteria.from(NitCliente.class);
        criteria.select(cliente).orderBy(cb.asc(cliente.get("id")));
        return em.createQuery(criteria).getResultList();
    }
    
    public List<NitCliente> findAllOrderedByFechaRegistro() {
    	CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<NitCliente> criteria = cb.createQuery(NitCliente.class);
        Root<NitCliente> cliente = criteria.from(NitCliente.class);
        criteria.select(cliente).orderBy(cb.asc(cliente.get("fechaRegistro")));
        return em.createQuery(criteria).getResultList();
    }
    
    
    public List<NitCliente> findNitCliente(int clienteID) {
        try {
        	String query = "select nc from NitCliente nc where nc.cliente.id="+clienteID+" order by nc.fechaRegistro desc";
        	System.out.println("Consulta Cliente: "+query);
        	List<NitCliente> lista = em.createQuery(query).getResultList();
            return lista;        	
		} catch (Exception e) {
			// TODO: handle exception
			System.out.println("Error en findNitCliente: "+e.getMessage());
			return null;
		}
    }
    
    public NitCliente findNit(String nit) {
        try {
        	String query = "select nc from NitCliente nc where nc.nitCi='"+nit+"'";
        	System.out.println("Consulta NIT: "+query);
        	NitCliente nitCliente = (NitCliente) em.createQuery(query).getSingleResult();
            return nitCliente;        	
		} catch (Exception e) {
			// TODO: handle exception
			System.out.println("Error en findNit: "+e.getMessage());
			return null;
		}
    }
    
    
	
}
