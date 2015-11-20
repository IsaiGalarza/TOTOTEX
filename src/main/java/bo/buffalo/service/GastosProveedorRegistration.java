package bo.buffalo.service;

import java.util.logging.Logger;

import javax.ejb.Stateless;
import javax.enterprise.event.Event;
import javax.inject.Inject;
import javax.persistence.EntityManager;



import javax.persistence.Query;

import bo.buffalo.model.GastosProveedor;
import bo.buffalo.model.Proveedor;

//The @Stateless annotation eliminates the need for manual transaction demarcation
@Stateless
public class GastosProveedorRegistration {
	
	@Inject
    private Logger log;

    @Inject
    private EntityManager em;

    @Inject
    private Event<GastosProveedor> gastosProveedorEventSrc;
    
    public void register(GastosProveedor gastosProveedor) throws Exception {
        log.info("Registering GastosProveedor: " + gastosProveedor.getDescripcion());
        em.persist(gastosProveedor);
        gastosProveedorEventSrc.fire(gastosProveedor);
    }
    
    public void updated(GastosProveedor gastosProveedor) throws Exception {
    	log.info("Update GastosProveedor: " + gastosProveedor.getDescripcion());
        em.merge(gastosProveedor);
        gastosProveedorEventSrc.fire(gastosProveedor);
    }
    
    public void deleteAll(Proveedor proveedor){
    	log.info("Update GastosProveedor: " + proveedor.getNombre());
    	String query ="delete from GastosProveedor gp where gp.proveedor.id="+proveedor.getId();
    	Query queries= (Query) em.createQuery(query);
    	int result = queries.executeUpdate();
    }
    
    public void remover(GastosProveedor gastosProveedor){
    	log.info("Remove GastosProveedor: "  + gastosProveedor.getDescripcion());
    	gastosProveedor.setEstado("RM");
        em.merge(gastosProveedor);
        gastosProveedorEventSrc.fire(gastosProveedor);
    }
	
}
