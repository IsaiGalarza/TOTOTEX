package bo.buffalo.service;

import java.util.logging.Logger;

import javax.ejb.Stateless;
import javax.enterprise.event.Event;
import javax.inject.Inject;
import javax.persistence.EntityManager;

import bo.buffalo.model.Confeccionista;

//The @Stateless annotation eliminates the need for manual transaction demarcation
@Stateless
public class ConfeccionistaRegistration {
	
	@Inject
    private Logger log;

    @Inject
    private EntityManager em;

    @Inject
    private Event<Confeccionista> gastosEventSrc;
    
    public void register(Confeccionista cargo) throws Exception {
        log.info("Registering Confeccionista: " + cargo.getNombre());
        em.persist(cargo);
        gastosEventSrc.fire(cargo);
    }
    
    public void updated(Confeccionista cargo) throws Exception {
    	log.info("Update Confeccionista: " + cargo.getNombre());
        em.merge(cargo);
        gastosEventSrc.fire(cargo);
    }
    
    public void remover(Confeccionista cargo){
    	log.info("Remove Confeccionista: "  + cargo.getNombre());
    	cargo.setEstado("RM");
        em.merge(cargo);
        gastosEventSrc.fire(cargo);
    }
	
}
