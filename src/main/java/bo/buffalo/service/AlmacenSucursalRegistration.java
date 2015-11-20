package bo.buffalo.service;

import java.util.logging.Logger;

import javax.ejb.Stateless;
import javax.enterprise.event.Event;
import javax.inject.Inject;
import javax.persistence.EntityManager;

import bo.buffalo.model.AlmacenSucursal;

//The @Stateless annotation eliminates the need for manual transaction demarcation
@Stateless
public class AlmacenSucursalRegistration {
	
	@Inject
    private Logger log;

    @Inject
    private EntityManager em;

    @Inject
    private Event<AlmacenSucursal> almacenSucursalEventSrc;
    
    public void register(AlmacenSucursal almacenSucursal) throws Exception {
        log.info("Registering AlmacenSucursal: " + almacenSucursal.getId());
        em.persist(almacenSucursal);
        almacenSucursalEventSrc.fire(almacenSucursal);
    }
    
    public void updated(AlmacenSucursal almacenSucursal) throws Exception {
    	log.info("Updated AlmacenSucursal: " + almacenSucursal.getId());
        em.merge(almacenSucursal);
        almacenSucursalEventSrc.fire(almacenSucursal);
    }
    
    public void remover(AlmacenSucursal almacenSucursal){
    	log.info("Remover AlmacenSucursal: " + almacenSucursal.getId());
    	almacenSucursal.setEstado("RM");
        em.merge(almacenSucursal);
        almacenSucursalEventSrc.fire(almacenSucursal);
    }
	
}
