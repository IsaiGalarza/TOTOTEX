package bo.buffalo.service;

import java.util.logging.Logger;

import javax.ejb.Stateless;
import javax.enterprise.event.Event;
import javax.inject.Inject;
import javax.persistence.EntityManager;

import bo.buffalo.model.LineasProveedor;
import bo.buffalo.model.Proveedor;

//The @Stateless annotation eliminates the need for manual transaction demarcation
@Stateless
public class LineaProveedorRegistration {
	
	@Inject
    private Logger log;

    @Inject
    private EntityManager em;

    @Inject
    private Event<LineasProveedor> proveedorEventSrc;
    
    public void register(LineasProveedor proveedor) throws Exception {
        log.info("Registering LineasProveedor: " + proveedor.getNombre());
        em.persist(proveedor);
        proveedorEventSrc.fire(proveedor);
    }
    
    public void updated(LineasProveedor proveedor) throws Exception {
    	log.info("Updated LineasProveedor: " + proveedor.getNombre());
        em.merge(proveedor);
        proveedorEventSrc.fire(proveedor);
    }
    
    public void remover(LineasProveedor proveedor){
    	log.info("Remover LineasProveedor: " + proveedor.getNombre());
    	proveedor.setEstado("RM");
        em.merge(proveedor);
        proveedorEventSrc.fire(proveedor);
    }
	
}
