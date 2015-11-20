package bo.buffalo.service;

import java.util.logging.Logger;

import javax.ejb.Stateless;
import javax.enterprise.event.Event;
import javax.inject.Inject;
import javax.persistence.EntityManager;

import bo.buffalo.model.Producto;
import bo.buffalo.model.ProductoProveedor;


//The @Stateless annotation eliminates the need for manual transaction demarcation
@Stateless
public class ProductoProveedorRegistration {
	
	@Inject
    private Logger log;

    @Inject
    private EntityManager em;

    @Inject
    private Event<ProductoProveedor> productoDetalleEventSrc;
    
    public void register(ProductoProveedor productoDetalle) throws Exception {
        log.info("Registering ProductoProveedor: " + productoDetalle.getUsuarioRegistro());
        em.persist(productoDetalle);
        productoDetalleEventSrc.fire(productoDetalle);
    }
    
    public void updated(ProductoProveedor productoProveedor) throws Exception {
    	log.info("Update ProductoProveedor: " + productoProveedor.getUsuarioRegistro());
        em.merge(productoProveedor);
        productoDetalleEventSrc.fire(productoProveedor);
    }
    
    public void remover(ProductoProveedor productoDetalle){
    	log.info("Remove ProductoProveedor: " + productoDetalle.getUsuarioRegistro());
    	productoDetalle.setEstado("RM");
        em.merge(productoDetalle);
        productoDetalleEventSrc.fire(productoDetalle);
    }
    
    public void removerProductoProveedor(Producto producto){
    	log.info("Remover DetallePreparado: " + producto.getId());
    	String query = "delete from ProductoProveedor pro where  pro.producto.id="+producto.getId();
    	System.out.println("Query Servicios: "+query);
    	em.createQuery(query).executeUpdate();
    	 
    }
	
}
