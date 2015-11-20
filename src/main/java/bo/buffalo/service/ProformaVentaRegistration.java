package bo.buffalo.service;

import java.util.logging.Logger;

import javax.ejb.Stateless;
import javax.enterprise.event.Event;
import javax.inject.Inject;
import javax.persistence.EntityManager;

import bo.buffalo.model.ProformaVenta;

//The @Stateless annotation eliminates the need for manual transaction demarcation
@Stateless
public class ProformaVentaRegistration {
	
	@Inject
    private Logger log;

    @Inject
    private EntityManager em;

    @Inject
    private Event<ProformaVenta> proformaVentaEventSrc;
    
    public void register(ProformaVenta proformaVenta) throws Exception {
        log.info("Registering Proforma ID: " + proformaVenta.getProforma().getId());
        em.persist(proformaVenta);
        proformaVentaEventSrc.fire(proformaVenta);
    }
    
    public void updated(ProformaVenta proformaVenta) throws Exception {
        log.info("Updated Proforma ID: " + proformaVenta.getProforma().getId());
        em.merge(proformaVenta);
        proformaVentaEventSrc.fire(proformaVenta);
    }
    public void remover(ProformaVenta proformaVenta){
    	log.info("Remover Proforma ID: " + proformaVenta.getProforma().getId());
    	proformaVenta.setEstado("RM");
        em.merge(proformaVenta);
        proformaVentaEventSrc.fire(proformaVenta);
    }
	
}
