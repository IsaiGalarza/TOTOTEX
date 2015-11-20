package bo.buffalo.service;

import java.util.logging.Logger;

import javax.ejb.Stateless;
import javax.enterprise.event.Event;
import javax.inject.Inject;
import javax.persistence.EntityManager;

import bo.buffalo.model.Conversiones;

//The @Stateless annotation eliminates the need for manual transaction demarcation
@Stateless
public class ConversionesRegistration {
	
	@Inject
    private Logger log;

    @Inject
    private EntityManager em;

    @Inject
    private Event<Conversiones> conversionesEventSrc;
    
    public void register(Conversiones conversiones) throws Exception {
        log.info("Registering Conversiones: " + conversiones.getEquivalente());
        em.persist(conversiones);
        conversionesEventSrc.fire(conversiones);
    }
    
    public void updated(Conversiones conversiones) throws Exception {
    	log.info("Updated Conversiones: " + conversiones.getEquivalente());
        em.merge(conversiones);
        conversionesEventSrc.fire(conversiones);
    }
    
    public void remover(Conversiones conversiones){
    	log.info("Remover Conversiones: " + conversiones.getEquivalente());
    	conversiones.setEstado("RM");
        em.merge(conversiones);
        conversionesEventSrc.fire(conversiones);
    }
	
}
