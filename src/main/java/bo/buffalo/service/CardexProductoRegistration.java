package bo.buffalo.service;

import java.util.logging.Logger;

import javax.ejb.Stateless;
import javax.enterprise.event.Event;
import javax.inject.Inject;
import javax.persistence.EntityManager;

import bo.buffalo.model.CardexProducto;

//The @Stateless annotation eliminates the need for manual transaction demarcation
@Stateless
public class CardexProductoRegistration {
	
	@Inject
    private Logger log;

    @Inject
    private EntityManager em;

    @Inject
    private Event<CardexProducto> cardexEventSrc;
    
    public void register(CardexProducto cardex) throws Exception {
        log.info("Registering Movimiento: " + cardex.getTipoMovimiento());
        em.persist(cardex);
        cardexEventSrc.fire(cardex);
    }
    
    public void updated(CardexProducto cardex) throws Exception {
    	log.info("Updated Movimiento: " + cardex.getTipoMovimiento());
        em.merge(cardex);
        cardexEventSrc.fire(cardex);
    }
    
    public void remover(CardexProducto cardex){
    	log.info("Remover Movimiento: " + cardex.getTipoMovimiento());
    	cardex.setEstado("RM");
        em.merge(cardex);
        cardexEventSrc.fire(cardex);
    }
	
}
