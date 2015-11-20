package bo.buffalo.service;

import java.util.logging.Logger;

import javax.ejb.Stateless;
import javax.enterprise.event.Event;
import javax.inject.Inject;
import javax.persistence.EntityManager;

import bo.buffalo.model.HistoriaMargenUtilidad;


//The @Stateless annotation eliminates the need for manual transaction demarcation
@Stateless
public class MargenUtilidadRegistration {
	
	@Inject
    private Logger log;

    @Inject
    private EntityManager em;

    @Inject
    private Event<HistoriaMargenUtilidad> margenUtilEventSrc;
    
    public void register(HistoriaMargenUtilidad margenUtil) throws Exception {
        log.info("Registering HistoriaMargenUtilidad: " + margenUtil.getId());
        em.persist(margenUtil);
        margenUtilEventSrc.fire(margenUtil);
    }
    
    public void updated(HistoriaMargenUtilidad margenUtil) throws Exception {
    	log.info("Updated HistoriaMargenUtilidad: " + margenUtil.getId());
        em.merge(margenUtil);
        margenUtilEventSrc.fire(margenUtil);
    }
    
    public void enable(HistoriaMargenUtilidad margenUtil) throws Exception {
    	log.info("Updated HistoriaMargenUtilidad: " + margenUtil.getId());
    	margenUtil.setEstado("IN");
        em.merge(margenUtil);
        margenUtilEventSrc.fire(margenUtil);
    }
    public void remover(HistoriaMargenUtilidad margenUtil){
    	log.info("Remover HistoriaMargenUtilidad: " + margenUtil.getId());
    	margenUtil.setEstado("RM");
        em.merge(margenUtil);
        margenUtilEventSrc.fire(margenUtil);
    }
	
}
