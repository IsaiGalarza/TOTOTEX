package bo.buffalo.service;

import java.util.logging.Logger;

import javax.ejb.Stateless;
import javax.enterprise.event.Event;
import javax.inject.Inject;
import javax.persistence.EntityManager;

import bo.buffalo.model.Presentacion;

//The @Stateless annotation eliminates the need for manual transaction demarcation
@Stateless
public class PresentacionRegistration {
	
	@Inject
    private Logger log;

    @Inject
    private EntityManager em;

    @Inject
    private Event<Presentacion> presentacionEventSrc;
    
    public void register(Presentacion presentacion) throws Exception {
        log.info("Registering Presentacion: " + presentacion.getDescripcion());
        em.persist(presentacion);
        presentacionEventSrc.fire(presentacion);
    }
    
    public void updated(Presentacion presentacion) throws Exception {
    	log.info("Updated Presentacion: " + presentacion.getDescripcion());
        em.merge(presentacion);
        presentacionEventSrc.fire(presentacion);
    }
    
    public void remover(Presentacion presentacion){
    	log.info("Remover Presentacion: " + presentacion.getDescripcion());
    	presentacion.setEstado("RM");
        em.merge(presentacion);
        presentacionEventSrc.fire(presentacion);
    }
	
}
