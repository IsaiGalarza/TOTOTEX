package bo.buffalo.service;

import java.util.logging.Logger;

import javax.ejb.Stateless;
import javax.enterprise.event.Event;
import javax.inject.Inject;
import javax.persistence.EntityManager;

import bo.buffalo.model.DetallePedidoMov;
import bo.buffalo.model.PedidoMov;
import bo.buffalo.model.Proforma;

//The @Stateless annotation eliminates the need for manual transaction demarcation
@Stateless
public class DetallePedidoMovRegistration {
	
	@Inject
    private Logger log;

    @Inject
    private EntityManager em;

    @Inject
    private Event<DetallePedidoMov> presentacionEventSrc;
    
    public void register(DetallePedidoMov presentacion) throws Exception {
        log.info("Registering Pedido: " );
        em.persist(presentacion);
        presentacionEventSrc.fire(presentacion);
    }
    
    public void updated(DetallePedidoMov presentacion) throws Exception {
    	log.info("Updated Pedido: " );
        em.merge(presentacion);
        presentacionEventSrc.fire(presentacion);
    }
    
    public void remover(DetallePedidoMov presentacion){
    	log.info("Remover Pedido: " );
    	presentacion.setEstado("RM");
        em.merge(presentacion);
        presentacionEventSrc.fire(presentacion);
    }
    
    public void removerForPedido(PedidoMov pedido){
    	log.info("Remover DetallePreparado: " + pedido.getId());
    	String  query = "delete from DetallePedidoMov  pro where  pro.pedidoMov.id="+pedido.getId();
    	System.out.println("Query removerForPedido: "+query);
    	 em.createQuery(query).executeUpdate();
    	 
    }
	
}
