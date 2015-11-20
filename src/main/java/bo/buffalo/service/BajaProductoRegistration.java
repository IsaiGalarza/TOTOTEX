package bo.buffalo.service;

import java.util.logging.Logger;

import javax.ejb.Stateless;
import javax.enterprise.event.Event;
import javax.inject.Inject;
import javax.persistence.EntityManager;

import bo.buffalo.model.BajaProductos;


//The @Stateless annotation eliminates the need for manual transaction demarcation
@Stateless
public class BajaProductoRegistration {
	
	@Inject
    private Logger log;

    @Inject
    private EntityManager em;

    @Inject
    private Event<BajaProductos> margenUtilEventSrc;
    
    public void register(BajaProductos margenUtil) throws Exception {
        log.info("Registering BajaProductos: " + margenUtil.getId());
        em.persist(margenUtil);
        margenUtilEventSrc.fire(margenUtil);
    }
    
    public void updated(BajaProductos margenUtil) throws Exception {
    	log.info("Updated BajaProductos: " + margenUtil.getId());
        em.merge(margenUtil);
        margenUtilEventSrc.fire(margenUtil);
    }
    
    public void enable(BajaProductos margenUtil) throws Exception {
    	log.info("Updated BajaProductos: " + margenUtil.getId());
    	margenUtil.setEstado("IN");
        em.merge(margenUtil);
        margenUtilEventSrc.fire(margenUtil);
    }
    public void remover(BajaProductos margenUtil){
    	log.info("Remover BajaProductos: " + margenUtil.getId());
    	margenUtil.setEstado("RM");
        em.merge(margenUtil);
        margenUtilEventSrc.fire(margenUtil);
    }
	
}
