package bo.buffalo.service;

import java.util.logging.Logger;

import javax.ejb.Stateless;
import javax.enterprise.event.Event;
import javax.inject.Inject;
import javax.persistence.EntityManager;

import bo.buffalo.model.Proforma;

//The @Stateless annotation eliminates the need for manual transaction demarcation
@Stateless
public class ProformaRegistration {
	
	@Inject
    private Logger log;

    @Inject
    private EntityManager em;

    @Inject
    private Event<Proforma> proformaEventSrc;
    
    public void register(Proforma proforma) throws Exception {
        log.info("Registering " + proforma.getObservacion());
        em.persist(proforma);
        proformaEventSrc.fire(proforma);
    }
    
    public void updated(Proforma cliente) throws Exception {
        log.info("Update Proforma: " + cliente.getObservacion());
        em.merge(cliente);
        proformaEventSrc.fire(cliente);
    }
    public void remover(Proforma proveedor){
    	log.info("Remover Proforma: " + proveedor.getObservacion());
    	proveedor.setEstado("RM");
        em.merge(proveedor);
        proformaEventSrc.fire(proveedor);
    }
	
}
