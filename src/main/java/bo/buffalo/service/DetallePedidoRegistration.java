package bo.buffalo.service;

import java.util.logging.Logger;

import javax.ejb.Stateless;
import javax.enterprise.event.Event;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import bo.buffalo.model.DetallePedido;

//The @Stateless annotation eliminates the need for manual transaction demarcation
@Stateless
public class DetallePedidoRegistration {
	
	@Inject
    private Logger log;

    @Inject
    private EntityManager em;

    @Inject
    private Event<DetallePedido> detallePedidoEventSrc;
    
    public void register(DetallePedido detallePedido) throws Exception {
        log.info("Registering detallePedido: " );
        em.persist(detallePedido);
        detallePedidoEventSrc.fire(detallePedido);
    }
    
    public void updated(DetallePedido detallePedido) throws Exception {
    	log.info("Updated DetallePedido: " + detallePedido.getId());
        em.merge(detallePedido);
        detallePedidoEventSrc.fire(detallePedido);
    }
    
    public void remover(DetallePedido detallePedido){
    	log.info("Remover detallePedido: " + detallePedido.getId());
    	detallePedido.setEstado("RM");
        em.merge(detallePedido);
        detallePedidoEventSrc.fire(detallePedido);
    }
    
}
