package bo.buffalo.service;

import java.util.logging.Logger;

import javax.ejb.Stateless;
import javax.enterprise.event.Event;
import javax.inject.Inject;
import javax.persistence.EntityManager;

import bo.buffalo.model.NotaVenta;

//The @Stateless annotation eliminates the need for manual transaction demarcation
@Stateless
public class NotaVentaRegistration {
	
	@Inject
    private Logger log;

    @Inject
    private EntityManager em;

    @Inject
    private Event<NotaVenta> notaVentaEventSrc;
    
    public void register(NotaVenta notaVenta) throws Exception {
        log.info("Registering notaVenta ID: " + notaVenta.getId());
        em.persist(notaVenta);
        notaVentaEventSrc.fire(notaVenta);
    }
    
    public void updated(NotaVenta notaVenta) throws Exception {
        log.info("Updated notaVenta ID: " + notaVenta.getId());
        em.merge(notaVenta);
        notaVentaEventSrc.fire(notaVenta);
    }
    public void remover(NotaVenta notaVenta){
    	log.info("Remover notaVenta ID: " + notaVenta.getId());
    	notaVenta.setEstado("RM");
        em.merge(notaVenta);
        notaVentaEventSrc.fire(notaVenta);
    }
	
}
