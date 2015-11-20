package bo.buffalo.service;

import java.util.List;
import java.util.logging.Logger;

import javax.ejb.Stateless;
import javax.enterprise.event.Event;
import javax.inject.Inject;
import javax.persistence.EntityManager;

import bo.buffalo.model.AtributoTipoProducto;
import bo.buffalo.model.TipoProducto;

//The @Stateless annotation eliminates the need for manual transaction demarcation
@Stateless
public class TipoProductoRegistration {
	
	@Inject
    private Logger log;

    @Inject
    private EntityManager em;

    @Inject
    private Event<TipoProducto> tipoProductoEventSrc;
    
    public void register(TipoProducto tipoProducto,List<AtributoTipoProducto> atributos) throws Exception {
        log.info("Registering TipoProducto: " + tipoProducto.getDescripcion());
        em.persist(tipoProducto);
        em.flush();
        em.refresh(tipoProducto);
        tipoProductoEventSrc.fire(tipoProducto);
        for (AtributoTipoProducto atributoTipoProducto : atributos) {
			atributoTipoProducto.setTipoProducto(tipoProducto);
			em.persist(atributoTipoProducto);
		}
    }
    
    public void updated(TipoProducto tipoProducto,List<AtributoTipoProducto> atributos) throws Exception {
    	log.info("Update TipoProducto: " + tipoProducto.getDescripcion());
        em.merge(tipoProducto);
        tipoProductoEventSrc.fire(tipoProducto);
        for (AtributoTipoProducto atributoTipoProducto : atributos) {
			if(atributoTipoProducto.getId()!=null){
				if(atributoTipoProducto.isBaja()){
					em.remove(em.merge(atributoTipoProducto));
				}else{
					em.merge(atributoTipoProducto);
				}
			}else{
				if(!atributoTipoProducto.isBaja()){
					atributoTipoProducto.setTipoProducto(tipoProducto);
					em.persist(atributoTipoProducto);
					em.flush();
				}
			}
		}
    }
    
    public void remover(TipoProducto tipoProducto){
    	log.info("Remove TipoProducto: "  + tipoProducto.getDescripcion());
    	tipoProducto.setEstado("RM");
        em.merge(tipoProducto);
        tipoProductoEventSrc.fire(tipoProducto);
    }
	
}
