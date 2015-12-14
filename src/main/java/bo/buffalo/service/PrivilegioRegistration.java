package bo.buffalo.service;

import java.util.logging.Logger;

import javax.ejb.Stateless;
import javax.enterprise.event.Event;
import javax.inject.Inject;
import javax.persistence.EntityManager;

import bo.buffalo.model.Privilegio;

//The @Stateless annotation eliminates the need for manual transaction demarcation
@Stateless
public class PrivilegioRegistration {
	
	@Inject
    private Logger log;

    @Inject
    private EntityManager em;

    @Inject
    private Event<Privilegio> historialCostosEventSrc;
    
    public void register(Privilegio privilegio) throws Exception {
        log.info("Registering Privilegio ");
        em.persist(privilegio);
        historialCostosEventSrc.fire(privilegio);
    }
    
    public void updated(Privilegio privilegio) throws Exception {
    	log.info("Updated Privilegio ");
        em.merge(privilegio);
        historialCostosEventSrc.fire(privilegio);
    }
    
    public void remover(Privilegio privilegio){
    	log.info("Remover Privilegio ");
    	privilegio.setEstado("RM");
        em.merge(privilegio);
        historialCostosEventSrc.fire(privilegio);
    }
	
}
