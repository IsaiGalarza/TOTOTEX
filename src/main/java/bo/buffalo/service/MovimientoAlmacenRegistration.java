package bo.buffalo.service;

import java.util.logging.Logger;

import javax.ejb.Stateless;
import javax.enterprise.event.Event;
import javax.inject.Inject;
import javax.persistence.EntityManager;

import bo.buffalo.model.MovimientoAlmacen;

//The @Stateless annotation eliminates the need for manual transaction demarcation
@Stateless
public class MovimientoAlmacenRegistration {
	
	@Inject
    private Logger log;

    @Inject
    private EntityManager em;

    @Inject
    private Event<MovimientoAlmacen> movimientoEventSrc;
    
    public void register(MovimientoAlmacen movimiento) throws Exception {
        log.info("Registering Movimiento: " + movimiento.getTipoMovimiento());
        em.persist(movimiento);
        movimientoEventSrc.fire(movimiento);
    }
    
    public void updated(MovimientoAlmacen movimiento) throws Exception {
    	log.info("Updated Movimiento: " + movimiento.getTipoMovimiento());
        em.merge(movimiento);
        movimientoEventSrc.fire(movimiento);
    }
    
    public void remover(MovimientoAlmacen movimiento){
    	log.info("Remover Movimiento: " + movimiento.getTipoMovimiento());
    	movimiento.setEstado("RM");
        em.merge(movimiento);
        movimientoEventSrc.fire(movimiento);
    }
	
}
