package bo.buffalo.service;

import java.util.logging.Logger;

import javax.ejb.Stateless;
import javax.enterprise.event.Event;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import bo.buffalo.model.Pedido;

//The @Stateless annotation eliminates the need for manual transaction demarcation
@Stateless
public class PedidoRegistration {
	
	@Inject
    private Logger log;

    @Inject
    private EntityManager em;

    @Inject
    private Event<Pedido> pedidoEventSrc;
    
    public void register(Pedido pedido) throws Exception {
        log.info("Registering " + pedido.getObservacion());
        em.persist(pedido);
        pedidoEventSrc.fire(pedido);
    }
    
    public void updated(Pedido pedido) throws Exception {
        log.info("Update Pedido: " + pedido.getObservacion());
        em.merge(pedido);
        pedidoEventSrc.fire(pedido);
    }
    public void remover(Pedido pedido){
    	log.info("Remover Pedido: " + pedido.getObservacion());
    	pedido.setEstado("RM");
        em.merge(pedido);
        pedidoEventSrc.fire(pedido);
    }
	
}
