package bo.buffalo.service;

import java.util.logging.Logger;

import javax.ejb.Stateless;
import javax.enterprise.event.Event;
import javax.inject.Inject;
import javax.persistence.EntityManager;

import bo.buffalo.model.Producto;


//The @Stateless annotation eliminates the need for manual transaction demarcation
@Stateless
public class ProductoRegistration {
	
	@Inject
    private Logger log;

    @Inject
    private EntityManager em;

    @Inject
    private Event<Producto> productoEventSrc;
    
    public void register(Producto producto) throws Exception {
        log.info("Registering Producto: " + producto.getNombreProducto());
        em.persist(producto);
        productoEventSrc.fire(producto);
    }
    
    public void updated(Producto producto) throws Exception {
    	log.info("Update Producto: " + producto.getNombreProducto());
        em.merge(producto);
        productoEventSrc.fire(producto);
    }
    
    public void remover(Producto producto){
    	log.info("Remove Producto: " + producto.getNombreProducto());
    	producto.setEstado("RM");
        em.merge(producto);
        productoEventSrc.fire(producto);
    }
	
}
