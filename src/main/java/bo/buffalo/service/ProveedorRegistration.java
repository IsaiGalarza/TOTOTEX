package bo.buffalo.service;

import java.util.logging.Logger;

import javax.ejb.Stateless;
import javax.enterprise.event.Event;
import javax.inject.Inject;
import javax.persistence.EntityManager;

import bo.buffalo.model.Proveedor;

//The @Stateless annotation eliminates the need for manual transaction demarcation
@Stateless
public class ProveedorRegistration {
	
	@Inject
    private Logger log;

    @Inject
    private EntityManager em;

    @Inject
    private Event<Proveedor> proveedorEventSrc;
    
    public void register(Proveedor proveedor) throws Exception {
        log.info("Registering proveedor: " + proveedor.getNombre());
        em.persist(proveedor);
        proveedorEventSrc.fire(proveedor);
    }
    
    public void updated(Proveedor proveedor) throws Exception {
    	log.info("Updated proveedor: " + proveedor.getNombre());
        em.merge(proveedor);
        proveedorEventSrc.fire(proveedor);
    }
    
    public void remover(Proveedor proveedor){
    	log.info("Remover GrupoProducto: " + proveedor.getNombre());
    	proveedor.setEstado("RM");
        em.merge(proveedor);
        proveedorEventSrc.fire(proveedor);
    }
	
}
