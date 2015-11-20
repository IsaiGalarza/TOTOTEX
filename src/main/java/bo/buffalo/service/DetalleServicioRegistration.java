package bo.buffalo.service;

import java.util.logging.Logger;

import javax.ejb.Stateless;
import javax.enterprise.event.Event;
import javax.inject.Inject;
import javax.persistence.EntityManager;

import bo.buffalo.model.DetalleServicio;
import bo.buffalo.model.Proforma;

//The @Stateless annotation eliminates the need for manual transaction demarcation
@Stateless
public class DetalleServicioRegistration {
	
	@Inject
    private Logger log;

    @Inject
    private EntityManager em;

    @Inject
    private Event<DetalleServicio> detalleCostosVentaEventSrc;
    
    public void register(DetalleServicio detalleCostosVenta) throws Exception {
        log.info("Registering DetalleServicio: " );
        em.persist(detalleCostosVenta);
        detalleCostosVentaEventSrc.fire(detalleCostosVenta);
    }
    
    public void updated(DetalleServicio detalleCostosVenta) throws Exception {
    	log.info("Updated DetalleServicio: " + detalleCostosVenta.getId());
        em.merge(detalleCostosVenta);
        detalleCostosVentaEventSrc.fire(detalleCostosVenta);
    }
    
    public void remover(DetalleServicio detalleCostosVenta){
    	log.info("Remover DetalleServicio: " + detalleCostosVenta.getId());
    	detalleCostosVenta.setEstado("RM");
        em.merge(detalleCostosVenta);
        detalleCostosVentaEventSrc.fire(detalleCostosVenta);
    }
    
    
    public void removerForProforma(Proforma proforma){
    	log.info("Remover DetalleServicio: " + proforma.getId());
    	String  query = "delete from DetalleServicio pro where  pro.proforma.id="+proforma.getId();
    	System.out.println("Query removerForProforma: "+query);
    	 em.createQuery(query).executeUpdate();
    	 
    }
    
   
	
}
