package bo.buffalo.service;

import java.util.logging.Logger;

import javax.ejb.Stateless;
import javax.enterprise.event.Event;
import javax.inject.Inject;
import javax.persistence.EntityManager;

import bo.buffalo.model.Cargo;

//The @Stateless annotation eliminates the need for manual transaction demarcation
@Stateless
public class CargoRegistration {
	
	@Inject
    private Logger log;

    @Inject
    private EntityManager em;

    @Inject
    private Event<Cargo> gastosEventSrc;
    
    public void register(Cargo cargo) throws Exception {
        log.info("Registering Cargo: " + cargo.getNombre());
        em.persist(cargo);
        gastosEventSrc.fire(cargo);
    }
    
    public void updated(Cargo cargo) throws Exception {
    	log.info("Update Cargo: " + cargo.getNombre());
        em.merge(cargo);
        gastosEventSrc.fire(cargo);
    }
    
    public void remover(Cargo cargo){
    	log.info("Remove Cargo: "  + cargo.getNombre());
    	cargo.setEstado("RM");
        em.merge(cargo);
        gastosEventSrc.fire(cargo);
    }
	
}
