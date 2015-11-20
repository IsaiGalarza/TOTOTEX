package bo.buffalo.service;

import java.util.Date;
import java.util.List;
import java.util.logging.Logger;

import javax.ejb.Stateless;
import javax.enterprise.event.Event;
import javax.inject.Inject;
import javax.persistence.EntityManager;

import bo.buffalo.model.Dosificacion;
import bo.buffalo.model.Sucursal;

@Stateless
public class SucursalRegistration {
	
	@Inject
    private Logger log;

    @Inject
    private EntityManager em;

    @Inject
    private Event<Sucursal> sucursalEventSrc;
    
    public void register(Sucursal sucursal,List<Dosificacion> dosificaciones) throws Exception {
        log.info("Registering " + sucursal.getNombre());
        em.persist(sucursal);
        sucursalEventSrc.fire(sucursal);
        for (Dosificacion dosificacion : dosificaciones) {
			dosificacion.setSucursal(sucursal);
			dosificacion.setFechaRegistro(new Date());
			dosificacion.setUsuarioRegistro(sucursal.getUsuarioRegistro());
			em.persist(dosificacion);
		}
    }
    
    public void updated(Sucursal sucursal,List<Dosificacion> dosificaciones) throws Exception {
        log.info("Update Sucursal: " + sucursal.getNombre());
        em.merge(sucursal);
        sucursalEventSrc.fire(sucursal);
        for (Dosificacion dosificacion : dosificaciones) {
			if(dosificacion.getId()==null){
				dosificacion.setSucursal(sucursal);
				dosificacion.setFechaRegistro(new Date());
				dosificacion.setUsuarioRegistro(sucursal.getUsuarioRegistro());
				em.persist(dosificacion);
			}else{
				em.merge(dosificacion);
			}
		}
    }
    
    public void remove(Sucursal sucursal,List<Dosificacion> dosificaciones) throws Exception {
        log.info("Eliminar Sucursal: " + sucursal.getNombre());
        sucursal.setEstado("RM");
        em.merge(sucursal);
        sucursalEventSrc.fire(sucursal);
        for (Dosificacion dosificacion : dosificaciones) {
			dosificacion.setBaja(true);
			dosificacion.setActivo(false);
			em.merge(dosificacion);
		}
    }
	
}
