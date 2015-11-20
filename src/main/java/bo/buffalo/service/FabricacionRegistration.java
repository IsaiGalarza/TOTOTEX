package bo.buffalo.service;

import java.util.logging.Logger;

import javax.ejb.Stateless;
import javax.enterprise.event.Event;
import javax.inject.Inject;
import javax.persistence.EntityManager;

import bo.buffalo.model.Fabricante;

//The @Stateless annotation eliminates the need for manual transaction demarcation
@Stateless
public class FabricacionRegistration {
	
	@Inject
    private Logger log;

    @Inject
    private EntityManager em;

    @Inject
    private Event<Fabricante> FabricacionEventSrc;
    
    public void register(Fabricante fabricacion) throws Exception {
        log.info("Registering Fabricacion: " + fabricacion.getDescripcion());
        em.persist(fabricacion);
        FabricacionEventSrc.fire(fabricacion);
    }
    
    public void updated(Fabricante fabricacion) throws Exception {
    	log.info("Updated Fabricacion: " + fabricacion.getDescripcion());
        em.merge(fabricacion);
        FabricacionEventSrc.fire(fabricacion);
    }
    
    public void remover(Fabricante fabricacion){
    	log.info("Remover Fabricacion: " + fabricacion.getDescripcion());
    	fabricacion.setEstado("RM");
        em.merge(fabricacion);
        FabricacionEventSrc.fire(fabricacion);
    }
	
}
