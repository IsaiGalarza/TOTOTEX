package bo.buffalo.service;

import java.util.logging.Logger;

import javax.ejb.Stateless;
import javax.enterprise.event.Event;
import javax.inject.Inject;
import javax.persistence.EntityManager;

import bo.buffalo.model.OrdenCompra;


//The @Stateless annotation eliminates the need for manual transaction demarcation
@Stateless
public class OrdenCompraRegistration {
	
	@Inject
    private Logger log;

    @Inject
    private EntityManager em;

    @Inject
    private Event<OrdenCompra> pedidoEventSrc;
    
    public void register(OrdenCompra pedido) throws Exception {
        log.info("Registering Pedido: " + pedido.getObservacion());
        em.persist(pedido);
        pedidoEventSrc.fire(pedido);
    }
    
    public void updated(OrdenCompra pedido) throws Exception {
    	log.info("Updated Pedido: " + pedido.getObservacion());
        em.merge(pedido);
        pedidoEventSrc.fire(pedido);
    }
    
    public void remover(OrdenCompra pedido){
    	log.info("Remover Pedido: " + pedido.getObservacion());
    	pedido.setEstado("RM");
        em.merge(pedido);
        pedidoEventSrc.fire(pedido);
    }
	
}
