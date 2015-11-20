package bo.buffalo.service;

import java.util.logging.Logger;

import javax.ejb.Stateless;
import javax.enterprise.event.Event;
import javax.inject.Inject;
import javax.persistence.EntityManager;

import bo.buffalo.model.Gastos;

//The @Stateless annotation eliminates the need for manual transaction demarcation
@Stateless
public class GastosRegistration {
	
	@Inject
    private Logger log;

    @Inject
    private EntityManager em;

    @Inject
    private Event<Gastos> gastosEventSrc;
    
    public void register(Gastos gastos) throws Exception {
        log.info("Registering Gastos: " + gastos.getDescripcion());
        em.persist(gastos);
        gastosEventSrc.fire(gastos);
    }
    
    public void updated(Gastos gastos) throws Exception {
    	log.info("Update Gastos: " + gastos.getDescripcion());
        em.merge(gastos);
        gastosEventSrc.fire(gastos);
    }
    
    public void remover(Gastos gastos){
    	log.info("Remove Gastos: "  + gastos.getDescripcion());
    	gastos.setEstado("RM");
        em.merge(gastos);
        gastosEventSrc.fire(gastos);
    }
	
}
