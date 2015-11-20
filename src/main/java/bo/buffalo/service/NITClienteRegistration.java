package bo.buffalo.service;

import java.util.logging.Logger;

import javax.ejb.Stateless;
import javax.enterprise.event.Event;
import javax.inject.Inject;
import javax.persistence.EntityManager;

import bo.buffalo.model.NitCliente;

//The @Stateless annotation eliminates the need for manual transaction demarcation
@Stateless
public class NITClienteRegistration {
	
	@Inject
    private Logger log;

    @Inject
    private EntityManager em;

    @Inject
    private Event<NitCliente> nitClienteEventSrc;
    
    public void register(NitCliente nitCliente) throws Exception {
        log.info("Registering " + nitCliente.getNombreFactura() + " NIT: "+nitCliente.getNitCi());
        em.persist(nitCliente);
        nitClienteEventSrc.fire(nitCliente);
    }
    
    public void updated(NitCliente nitCliente) throws Exception {
    	log.info("Updated " + nitCliente.getNombreFactura() + " NIT: "+nitCliente.getNitCi());
        em.merge(nitCliente);
        nitClienteEventSrc.fire(nitCliente);
    }
	
}
