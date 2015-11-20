package bo.buffalo.service;

import java.util.logging.Logger;

import javax.ejb.Stateless;
import javax.enterprise.event.Event;
import javax.inject.Inject;
import javax.persistence.EntityManager;

import bo.buffalo.model.Compra;

//The @Stateless annotation eliminates the need for manual transaction demarcation
@Stateless
public class CompraRegistration {
	
	@Inject
    private Logger log;

    @Inject
    private EntityManager em;

    @Inject
    private Event<Compra> compraEventSrc;
    
    public void register(Compra compra) throws Exception {
        log.info("Registering Compra: " + compra.getImporteTotal()+", mes: "+compra.getMes()+", gestion: "+compra.getGestion());
        em.persist(compra);
        compraEventSrc.fire(compra);
    }
    
    public void updated(Compra compra) throws Exception {
    	log.info("Updated Compra: " + compra.getImporteTotal()+", mes: "+compra.getMes()+", gestion: "+compra.getGestion());
        em.merge(compra);
        compraEventSrc.fire(compra);
    }
    
    public void remover(Compra compra) throws Exception {
    	log.info("Remove Compra: " + compra.getImporteTotal()+", mes: "+compra.getMes()+", gestion: "+compra.getGestion());
    	compra.setEstado("RM");
        em.merge(compra);
        compraEventSrc.fire(compra);
    }
	
}
