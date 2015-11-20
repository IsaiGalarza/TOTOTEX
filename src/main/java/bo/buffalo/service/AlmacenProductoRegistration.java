package bo.buffalo.service;

import java.util.logging.Logger;

import javax.ejb.Stateless;
import javax.enterprise.event.Event;
import javax.inject.Inject;
import javax.persistence.EntityManager;

import bo.buffalo.model.AlmProducto;

//The @Stateless annotation eliminates the need for manual transaction demarcation
@Stateless
public class AlmacenProductoRegistration {
	
	@Inject
    private Logger log;

    @Inject
    private EntityManager em;

    @Inject
    private Event<AlmProducto> almProductoEventSrc;
    
    public void register(AlmProducto almProducto) throws Exception {
        log.info("Registering AlmacenProducto: " + almProducto.getProducto().getNombreProducto());
        em.persist(almProducto);
        almProductoEventSrc.fire(almProducto);
    }
    
    public void updated(AlmProducto almProducto) throws Exception {
    	log.info("Updated AlmacenProducto: " + almProducto.getProducto().getNombreProducto());
        em.merge(almProducto);
        almProductoEventSrc.fire(almProducto);
    }
    
    public void remover(AlmProducto almProducto){
    	log.info("Remover AlmacenProducto: " + almProducto.getProducto().getNombreProducto());
    	almProducto.setEstado("RM");
        em.merge(almProducto);
        almProductoEventSrc.fire(almProducto);
    }
	
}
