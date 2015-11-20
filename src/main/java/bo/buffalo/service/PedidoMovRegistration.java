package bo.buffalo.service;

import java.util.logging.Logger;

import javax.ejb.Stateless;
import javax.enterprise.event.Event;
import javax.inject.Inject;
import javax.persistence.EntityManager;

import bo.buffalo.model.PedidoMov;

//The @Stateless annotation eliminates the need for manual transaction demarcation
@Stateless
public class PedidoMovRegistration {
	
	@Inject
    private Logger log;

    @Inject
    private EntityManager em;

    @Inject
    private Event<PedidoMov> pedidoEventSrc;
    
    public void register(PedidoMov pedido) throws Exception {
        log.info("Registering Pedido: " + pedido.getObservacion());
        em.persist(pedido);
        pedidoEventSrc.fire(pedido);
    }
    
    public void updated(PedidoMov pedido) throws Exception {
    	log.info("Updated Pedido: " + pedido.getObservacion());
        em.merge(pedido);
        pedidoEventSrc.fire(pedido);
    }
    
    public void remover(PedidoMov pedido){
    	log.info("Remover Pedido: " + pedido.getObservacion());
    	pedido.setEstado("RM");
        em.merge(pedido);
        pedidoEventSrc.fire(pedido);
    }
	
}
