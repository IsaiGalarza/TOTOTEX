package bo.buffalo.service;

import java.util.logging.Logger;

import javax.ejb.Stateless;
import javax.enterprise.event.Event;
import javax.inject.Inject;
import javax.persistence.EntityManager;

import bo.buffalo.model.HistorialCostos;

//The @Stateless annotation eliminates the need for manual transaction demarcation
@Stateless
public class HistorialCostosRegistration {
	
	@Inject
    private Logger log;

    @Inject
    private EntityManager em;

    @Inject
    private Event<HistorialCostos> historialCostosEventSrc;
    
    public void register(HistorialCostos historialCostos) throws Exception {
        log.info("Registering historialCostos: " + historialCostos.getObservacion());
        em.persist(historialCostos);
        historialCostosEventSrc.fire(historialCostos);
    }
    
    public void updated(HistorialCostos historialCostos) throws Exception {
    	log.info("Updated historialCostos: " + historialCostos.getObservacion());
        em.merge(historialCostos);
        historialCostosEventSrc.fire(historialCostos);
    }
    
    public void remover(HistorialCostos historialCostos){
    	log.info("Remover GrupoProducto: " + historialCostos.getObservacion());
    	historialCostos.setEstado("RM");
        em.merge(historialCostos);
        historialCostosEventSrc.fire(historialCostos);
    }
	
}
