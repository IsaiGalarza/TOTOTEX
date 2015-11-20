package bo.buffalo.service;

import java.util.logging.Logger;

import javax.ejb.Stateless;
import javax.enterprise.event.Event;
import javax.inject.Inject;
import javax.persistence.EntityManager;

import bo.buffalo.model.DetalleOrdenCompra;
import bo.buffalo.model.OrdenCompra;




//The @Stateless annotation eliminates the need for manual transaction demarcation
@Stateless
public class DetalleOrdenCompraRegistration {
	
	@Inject
    private Logger log;

    @Inject
    private EntityManager em;

    @Inject
    private Event<DetalleOrdenCompra> presentacionEventSrc;
    
    public void register(DetalleOrdenCompra presentacion) throws Exception {
        log.info("Registering Pedido: " );
        em.persist(presentacion);
        presentacionEventSrc.fire(presentacion);
    }
    
    public void updated(DetalleOrdenCompra presentacion) throws Exception {
    	log.info("Updated Pedido: " );
        em.merge(presentacion);
        presentacionEventSrc.fire(presentacion);
    }
    
    public void remover(DetalleOrdenCompra presentacion){
    	log.info("Remover Pedido: " );
    	presentacion.setEstado("RM");
        em.merge(presentacion);
        presentacionEventSrc.fire(presentacion);
    }
    
    public void removerForPedido(OrdenCompra pedido){
    	log.info("Remover DetallePreparado: " + pedido.getId()); 
    	String  query = "delete from DetalleOrdenCompra  pro where  pro.ordenCompra.id="+pedido.getId();
    	System.out.println("Query removerForPedido: "+query);
    	 em.createQuery(query).executeUpdate();
    	 
    }
	
}
