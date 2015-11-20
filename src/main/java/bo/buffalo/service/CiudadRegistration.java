package bo.buffalo.service;

import java.util.logging.Logger;

import javax.ejb.Stateless;
import javax.enterprise.event.Event;
import javax.inject.Inject;
import javax.persistence.EntityManager;

import bo.buffalo.model.Ciudad;

//The @Stateless annotation eliminates the need for manual transaction demarcation
@Stateless
public class CiudadRegistration {
	
	@Inject
    private Logger log;

    @Inject
    private EntityManager em;

    @Inject
    private Event<Ciudad> ciudadEventSrc;
    
    public void register(Ciudad ciudad) throws Exception {
        log.info("Registering Ciudad: " + ciudad.getNombre());
        em.persist(ciudad);
        ciudadEventSrc.fire(ciudad);
    }
    
    public void updated(Ciudad ciudad) throws Exception {
    	log.info("Update Ciudad: " + ciudad.getNombre());
        em.merge(ciudad);
        ciudadEventSrc.fire(ciudad);
    }
    
    public void remover(Ciudad ciudad){
    	log.info("Remove Ciudad: "  + ciudad.getNombre());
    	ciudad.setEstado("RM");
        em.merge(ciudad);
        ciudadEventSrc.fire(ciudad);
    }
	
}
