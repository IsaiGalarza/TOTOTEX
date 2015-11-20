package bo.buffalo.service;

import java.util.Date;
import java.util.List;
import java.util.logging.Logger;

import javax.ejb.Stateless;
import javax.enterprise.event.Event;
import javax.inject.Inject;
import javax.persistence.EntityManager;

import bo.buffalo.model.Atributo;
import bo.buffalo.model.AtributoProducto;
import bo.buffalo.model.DetalleProducto;
import bo.buffalo.model.Producto;
import bo.buffalo.model.Proforma;



//The @Stateless annotation eliminates the need for manual transaction demarcation
@Stateless
public class ProductopRegistration {
	
	@Inject
    private Logger log;

    @Inject
    private EntityManager em;

    @Inject
    private Event<Producto> ProductopEventSrc;
    
    @Inject
    private Event<DetalleProducto> detalleProductoEventSrc;    
    
    
    public void register(Producto productop,List<AtributoProducto> atributo) throws Exception {
        log.info("Registering Producto: " + productop.getNombreProducto());
        em.persist(productop);
        this.em.flush();
        this.em.refresh(productop);
        ProductopEventSrc.fire(productop);
        for (AtributoProducto ap : atributo) {
			ap.setProducto(productop);
			em.persist(ap);
		}
    }

	public void registerAndDetail(Producto Productop,
			List<DetalleProducto> listaDetalleProducto, String user)
			throws Exception {
		// log.info("Registering Receta y lista RecetaProductos: " +
		// compra.getDescripcion()+", nombre: "+compra.getNombre()+", fecha: "+compra.getFechaRegistro());

		log.info("[registerAndDetail] Iniciando registro de Receta y detalle>  Receta: "
				+ Productop.getNombreProducto()+ ", fecha: " + Productop.getFechaRegistro());

		em.persist(Productop);
		ProductopEventSrc.fire(Productop);

		for (int j = listaDetalleProducto.size() - 1; j >= 0; j--) {

			DetalleProducto concepto = listaDetalleProducto.get(j);
			DetalleProducto detalle = new DetalleProducto();
			detalle.setIncidencia(concepto.getIncidencia());
			detalle.setUnidadIncidencia(concepto.getUnidadIncidencia());
			detalle.setProducto(Productop);//producto padre
			detalle.setEstado("AC");
			detalle.setConcentracion(concepto.getConcentracion());
			detalle.setCorrelativo(concepto.getCorrelativo());
			detalle.setFechaRegistro(new Date());
			detalle.setUsuarioRegistro(user);
        	detalle.setCantidad(concepto.getCantidad());			
			detalle.setProductoCompuesto(concepto.getProductoCompuesto());//producto hijo
			// registrar detalle
			registrarDetalleProducto(detalle);
		}

	}
	public void registrarDetalleProducto(DetalleProducto detalle) throws Exception {
        try {
        	log.info("[registrarDetalleProducto] Iniciando Registro> DetalleProducto: " + detalle.getProducto().getNombreProducto()+
        			"| incidencia="+detalle.getIncidencia()+"|unidad_incidencia="+detalle.getUnidadIncidencia()); 
            em.persist(detalle);
           // recetaProductoEventSrc.fire(detalle);
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}
    }
	
	 public void updatedAndDetail(Producto producto,List<DetalleProducto> newListDetalleProducto,List<DetalleProducto> oldListDetalleProducto,String user) throws Exception {
	    	log.info("Updated Receta: "+ producto.getNombreProducto()+", nombre: "+producto.getNombreGenerico()+", fecha: "+producto.getFechaRegistro());
	        em.merge(producto);     
	        ProductopEventSrc.fire(producto);
	        
	        removerAllDetalleProductoForProducto(oldListDetalleProducto); 
	        
	        for (int j = newListDetalleProducto.size()-1; j >=0 ; j--) {
	        	DetalleProducto concepto=newListDetalleProducto.get(j);
	        	System.out.println("Correlativo : "+concepto.getCorrelativo());
	        	DetalleProducto detalle = new DetalleProducto();
	        	detalle.setIncidencia(concepto.getIncidencia());
	        	detalle.setUnidadIncidencia(concepto.getUnidadIncidencia());
	        	detalle.setProducto(producto);//producto padre
	           	detalle.setEstado("AC");
	           	detalle.setConcentracion(concepto.getConcentracion());
	           	detalle.setCorrelativo(concepto.getCorrelativo());
	        	detalle.setFechaRegistro(new Date());
	        	detalle.setUsuarioRegistro(user);
	        	detalle.setCantidad(concepto.getCantidad());
	        	
	        	detalle.setProductoCompuesto(concepto.getProductoCompuesto());//producto hijo
	        	
	        	//registrar detalleProduto
	        	registerDetalleProducto(detalle);        
	 
	       }      
	    }
	 
	 public void removerAllDetalleProductoForProducto(List<DetalleProducto> lrp) throws Exception{
         log.info("[removerAllDetalleProductoForProducto] Ingresando a metodo..|lista size = "+lrp.size()+""); 
         for (DetalleProducto detalleProducto : lrp) {
        	 detalleProducto.setEstado("RM");
             em.merge(detalleProducto);
             detalleProductoEventSrc.fire(detalleProducto);
		} 
    }
	 public void registerDetalleProducto(DetalleProducto detalle) throws Exception {
	        try {
	        	log.info("[registerDetalleProducto] Iniciando Registro> DetalleProducto: " + detalle.getProducto().getNombreProducto()+
	        			"| incidencia="+detalle.getIncidencia()+"|unidad_incidencia="+detalle.getUnidadIncidencia()); 
	            em.persist(detalle);
	            detalleProductoEventSrc.fire(detalle);
			} catch (Exception e) {
				// TODO: handle exception
				e.printStackTrace();
			}
	    }
    
    public void updated(Producto Productop,List<AtributoProducto> atributoProducto) throws Exception {
    	log.info("Update Producto: " + Productop.getNombreProducto());
        em.merge(Productop);
        ProductopEventSrc.fire(Productop);
        for (AtributoProducto ap : atributoProducto) {
			if(ap.getId()!=null){
				if(ap.isBaja()){
					em.remove(ap);
				}else{
					em.merge(ap);
				}
				
			}else{
				if(!ap.isBaja()){
					ap.setProducto(Productop);
					em.persist(ap);
				}
			}
		}
    }
    
    public void remover(Producto Productop){
    	log.info("Remove Producto: " + Productop.getNombreProducto());
    	Productop.setEstado("RM");
        em.merge(Productop);
        ProductopEventSrc.fire(Productop);
    }
    
    
	
}
