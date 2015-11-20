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

@ApplicationScoped
public class ClienteRepository {
	
	@Inject
    private EntityManager em;

    public Cliente findById(String id) {
        try {
        	return em.find(Cliente.class, id);
		} catch (Exception e) {
			// TODO: handle exception
			return null;
		}
    }
    
    public Cliente buscarClienteNIT(String nitCliente) {
    	String query = "select ser from Cliente ser where ser.estado='AC' and ser.nit='"+nitCliente+"' order by ser.id desc";
    	System.out.println("Query Cliente: "+query);
    	return (Cliente) em.createQuery(query).getSingleResult();
    }
    
    public List<Cliente> findAllOrderedByID() {
    	String query = "select ser from Cliente ser where ser.estado='AC' or ser.estado='IN' order by ser.id desc";
    	System.out.println("Query Cliente: "+query);
    	return em.createQuery(query).getResultList();
    }
    
    public List<Cliente> findAllOrderedByFechaReg() {
    	String query = "select cli from Cliente cli where cli.estado='AC' or cli.estado='IN' order by cli.fechaRegistro desc";
    	System.out.println("Query Cliente: "+query);
    	return em.createQuery(query).getResultList();
    }
    
    public List<Cliente> traerClientesActivos() {
    	String query = "select ser from Cliente ser where ser.estado='AC' order by ser.nombreCompleto asc";
    	System.out.println("Query Cliente: "+query);
    	return em.createQuery(query).getResultList();
    }

    
    @SuppressWarnings("unchecked")
   	public List<Cliente> traerClienteNinguno() {
           try {
           	String query = "select med from Cliente med where med.estado='AC' and med.nombreCompleto='NINGUNO' order by med.id desc";
           	System.out.println("Consulta traerMedicoActivos: "+query);
               return em.createQuery(query).getResultList();
   		} catch (Exception e) {
   			// TODO: handle exception
   			System.out.println("Error en traerMedicoActivos: "+e.getMessage());
   			return null;
   		}
       }
    
    public List<Cliente> findAllOrderedByName() {
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<Cliente> criteria = cb.createQuery(Cliente.class);
        Root<Cliente> cliente = criteria.from(Cliente.class);
        criteria.select(cliente).orderBy(cb.asc(cliente.get("nombreCompleto")));
        return em.createQuery(criteria).getResultList();
    }
    
    public List<Cliente> findAllOrderedByFechaRegistro() {
    	CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<Cliente> criteria = cb.createQuery(Cliente.class);
        Root<Cliente> cliente = criteria.from(Cliente.class);
        criteria.select(cliente).orderBy(cb.desc(cliente.get("fechaRegistro")));
        return em.createQuery(criteria).getResultList();
    }
    
    
    public List<Cliente> findAllClientsForName(String criterio) {
        try {
        	String query = "select ser from Cliente ser where upper(ser.nombreCompleto) like '%"+criterio.toUpperCase()+"%'";
        	System.out.println("Consulta: "+query);
            List<Cliente> listaClientes = em.createQuery(query).getResultList();
            System.out.println("Resultado: "+listaClientes.size());
            return listaClientes;        	
		} catch (Exception e) {
			// TODO: handle exception
			System.out.println("Error en findAllClientsForName: "+e.getMessage());
			return null;
		}
    }
    
    
	
}
