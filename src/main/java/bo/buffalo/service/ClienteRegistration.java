package bo.buffalo.service;

import java.util.logging.Logger;

import javax.ejb.Stateless;
import javax.enterprise.event.Event;
import javax.inject.Inject;
import javax.persistence.EntityManager;

import bo.buffalo.model.Cliente;
import bo.buffalo.model.Proveedor;

//The @Stateless annotation eliminates the need for manual transaction demarcation
@Stateless
public class ClienteRegistration {
	
	@Inject
    private Logger log;

    @Inject
    private EntityManager em;

    @Inject
    private Event<Cliente> sucursalEventSrc;
    
    public void register(Cliente cliente) throws Exception {
        log.info("Registering " + cliente.getNombreCompleto());
        em.persist(cliente);
        sucursalEventSrc.fire(cliente);
    }
    
    public void updated(Cliente cliente) throws Exception {
        log.info("Update Cliente: " + cliente.getNombreCompleto());
        em.merge(cliente);
        sucursalEventSrc.fire(cliente);
    }
    public void remover(Cliente proveedor){
    	log.info("Remover Cliente: " + proveedor.getNombreCompleto());
    	proveedor.setEstado("RM");
        em.merge(proveedor);
        sucursalEventSrc.fire(proveedor);
    }
	
}
