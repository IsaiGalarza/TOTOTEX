package bo.buffalo.service;

import java.util.logging.Logger;

import javax.ejb.Stateless;
import javax.enterprise.event.Event;
import javax.inject.Inject;
import javax.persistence.EntityManager;

import bo.buffalo.model.CantidadUnidadPresentacion;
import bo.buffalo.model.Proveedor;

//The @Stateless annotation eliminates the need for manual transaction demarcation
@Stateless
public class CantUnitPresRegistration {
	
	@Inject
    private Logger log;

    @Inject
    private EntityManager em;

    @Inject
    private Event<CantidadUnidadPresentacion> CantUnitPresEventSrc;
    
    public void register(CantidadUnidadPresentacion cliente) throws Exception {
        log.info("Registering " + cliente.getCantidad());
        em.merge(cliente);
        CantUnitPresEventSrc.fire(cliente);
    }
    
    public void updated(CantidadUnidadPresentacion cliente) throws Exception {
        log.info("Update CantidadUnidadPresentacion: " + cliente.getCantidad());
        em.merge(cliente);
        CantUnitPresEventSrc.fire(cliente);
    }
    public void remover(CantidadUnidadPresentacion proveedor){
    	log.info("Remover CantidadUnidadPresentacion: " + proveedor.getCantidad());
    	proveedor.setEstado("RM");
        em.merge(proveedor);
        CantUnitPresEventSrc.fire(proveedor);
    }
	
}
