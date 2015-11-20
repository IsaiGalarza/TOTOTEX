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
import bo.buffalo.model.Cliente;
import bo.buffalo.model.Pedido;

@ApplicationScoped
public class PedidoRepository {
	
	@Inject
    private EntityManager em;

    public Pedido findById(int id) {
        return em.find(Pedido.class, id);
    }
    
    @SuppressWarnings("unchecked")
	public List<Pedido> findAllOrderedByID() {
    	String query = "select ped from Pedido ped where ped.estado<>'RM' order by ped.id desc";
    	System.out.println("Query Pedido: "+query);
    	return em.createQuery(query).getResultList();
    }
    
    @SuppressWarnings("unchecked")
	public List<Pedido> findAllOrderedByRegistro() {
    	String query = "select ped from Pedido ped where ped.estado<>'RM' order by ped.fechaRegistro desc";
    	System.out.println("Query Pedido: "+query);
    	return em.createQuery(query).getResultList();
    }
    
    @SuppressWarnings("unchecked")
   	public List<Pedido> findAllOrderedByCliente(Cliente cliente) {
       	String query = "select ped from Pedido ped where ped.estado<>'RM' and ped.cliente.id="+cliente.getId()+" order by ped.id desc";
       	System.out.println("Query Pedido: "+query);
       	return em.createQuery(query).getResultList();
    }
    
}
