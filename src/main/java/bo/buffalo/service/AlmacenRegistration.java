package bo.buffalo.service;

import java.util.logging.Logger;

import javax.ejb.Stateless;
import javax.enterprise.event.Event;
import javax.inject.Inject;
import javax.persistence.EntityManager;

import bo.buffalo.model.Almacen;

//The @Stateless annotation eliminates the need for manual transaction demarcation
@Stateless
public class AlmacenRegistration {
	
	@Inject
    private Logger log;

    @Inject
    private EntityManager em;

    @Inject
    private Event<Almacen> almacenEventSrc;
    
    public void register(Almacen almacen) throws Exception {
        log.info("Registering Almacen: " + almacen.getNombre());
        em.persist(almacen);
        almacenEventSrc.fire(almacen);
    }
    
    public void updated(Almacen almacen) throws Exception {
    	log.info("Updated Almacen: " + almacen.getNombre());
        em.merge(almacen);
        almacenEventSrc.fire(almacen);
    }
    
    public void remover(Almacen almacen){
    	log.info("Remover Almacen: " + almacen.getNombre());
    	almacen.setEstado("RM");
        em.merge(almacen);
        almacenEventSrc.fire(almacen);
    }
	
}
