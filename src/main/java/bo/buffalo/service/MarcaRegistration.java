package bo.buffalo.service;

import java.util.logging.Logger;

import javax.ejb.Stateless;
import javax.enterprise.event.Event;
import javax.inject.Inject;
import javax.persistence.EntityManager;

import bo.buffalo.model.Marca;

//The @Stateless annotation eliminates the need for manual transaction demarcation
@Stateless
public class MarcaRegistration {
	
	@Inject
    private Logger log;

    @Inject
    private EntityManager em;

    @Inject
    private Event<Marca> MarcaEventSrc;
    
    public void register(Marca marca) throws Exception {
        log.info("Registering Marca: " + marca.getDescripcion());
        em.persist(marca);
        MarcaEventSrc.fire(marca);
    }
    
	public void updated(Marca marca) throws Exception {
    	log.info("Updated Marca: " + marca.getDescripcion());
        em.merge(marca);
        MarcaEventSrc.fire(marca);
    }
    
    public void remover(Marca marca){
    	log.info("Remover Marca: " + marca.getDescripcion());
    	marca.setEstado("RM");
        em.merge(marca);
        MarcaEventSrc.fire(marca);
    }
	
}
