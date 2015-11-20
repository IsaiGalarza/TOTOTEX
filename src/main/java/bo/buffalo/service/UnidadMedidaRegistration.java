package bo.buffalo.service;

import java.util.logging.Logger;

import javax.ejb.Stateless;
import javax.enterprise.event.Event;
import javax.inject.Inject;
import javax.persistence.EntityManager;

import bo.buffalo.model.UnidadMedida;

//The @Stateless annotation eliminates the need for manual transaction demarcation
@Stateless
public class UnidadMedidaRegistration {
	
	@Inject
    private Logger log;

    @Inject
    private EntityManager em;

    @Inject
    private Event<UnidadMedida> unidadMedidaEventSrc;
    
    public void register(UnidadMedida unidadMedida) throws Exception {
        log.info("Registering UnidadMedida: " + unidadMedida.getDescripcion());
        em.persist(unidadMedida);
        unidadMedidaEventSrc.fire(unidadMedida);
    }
    
    public void updated(UnidadMedida unidadMedida) throws Exception {
    	log.info("Updated UnidadMedida: " + unidadMedida.getDescripcion());
        em.merge(unidadMedida);
        unidadMedidaEventSrc.fire(unidadMedida);
    }
    
    public void remover(UnidadMedida unidadMedida){
    	log.info("Remover UnidadMedida: " + unidadMedida.getDescripcion());
    	unidadMedida.setEstado("RM");
        em.merge(unidadMedida);
        unidadMedidaEventSrc.fire(unidadMedida);
    }
	
}
