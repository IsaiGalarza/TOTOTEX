/*
* ServicioRepository.java	1.0 2014/09/19
*
* Copyright (c) 2014 by QBIT SRL, Inc. All Rights Reserved.
* 
* QBIT SRL grants you ("Licensee") a non-exclusive, royalty free, license to use,
* modify and redistribute this software in source and binary code form,
* depvided that i) this copyright notice and license appear on all copies of
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
import bo.buffalo.model.DetallePedido;
import bo.buffalo.model.Pedido;

@ApplicationScoped
public class DetallePedidoRepository {
	
	@Inject
    private EntityManager em;

    public DetallePedido findById(int id) {
        return em.find(DetallePedido.class, id);
    }

    @SuppressWarnings("unchecked")
	public List<DetallePedido> findAllOrderedByIDForPedido(Pedido pedido) {
    	String query = "select dep from DetallePedido dep where (dep.estado='AC' or dep.estado='IN')  and dep.pedido.id="+pedido.getId()+" order by dep.id desc";
    	System.out.println("Query DetallePedido: "+query);
    	return em.createQuery(query).getResultList();
    }
    
    @SuppressWarnings("unchecked")
	public List<DetallePedido> traerDetalleProductos(Pedido pedido) {
    	String query = "select dep from DetallePedido dep where dep.estado='AC' and dep.pedido.id="+pedido.getId()+" order by dep.id desc";
    	System.out.println("Query DetallePedido Productos: "+query);
    	return em.createQuery(query).getResultList();
    }
    
	
}
