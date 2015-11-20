package bo.buffalo.service;

import java.util.logging.Logger;

import javax.ejb.Stateless;
import javax.enterprise.event.Event;
import javax.inject.Inject;
import javax.persistence.EntityManager;

import bo.buffalo.model.Servicio;
import bo.buffalo.model.Sucursal;

//The @Stateless annotation eliminates the need for manual transaction demarcation
@Stateless
public class ServicioRegistration {
	
	@Inject
    private Logger log;

    @Inject
    private EntityManager em;

    @Inject
    private Event<Servicio> servicioEventSrc;
    
    public void register(Servicio servicio) throws Exception {
        log.info("Registering Servicio: " + servicio.getNombreServicio());
        em.persist(servicio);
        servicioEventSrc.fire(servicio);
    }
    
    public void updated(Servicio servicio) throws Exception {
        log.info("Update Servicio: " + servicio.getNombreServicio());
        em.merge(servicio);
        servicioEventSrc.fire(servicio);
    }
    
    public void remover(Servicio servicio){
    	log.info("Remove Servicio: " + servicio.getNombreServicio());
    	servicio.setEstado("RM");
        em.merge(servicio);
        servicioEventSrc.fire(servicio);
    }
	
}
