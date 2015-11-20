package bo.buffalo.service;

import java.util.logging.Logger;

import javax.ejb.Stateless;
import javax.enterprise.event.Event;
import javax.inject.Inject;
import javax.persistence.EntityManager;

import bo.buffalo.model.Empresa;

//The @Stateless annotation eliminates the need for manual transaction demarcation
@Stateless
public class EmpresaRegistration {
	
	@Inject
    private Logger log;

    @Inject
    private EntityManager em;

    @Inject
    private Event<Empresa> empresaEventSrc;
    
    public void register(Empresa empresa) throws Exception {
        log.info("Registering Empresa: " + empresa.getNombre());
        em.persist(empresa);
        empresaEventSrc.fire(empresa);
    }
    
    public void updated(Empresa empresa) throws Exception {
    	log.info("Updated Empresa: " + empresa.getNombre());
        em.merge(empresa);
        empresaEventSrc.fire(empresa);
    }
    
    public void remover(Empresa empresa){
    	log.info("Remover Empresa: " + empresa.getNombre());
    	empresa.setEstado("RM");
        em.merge(empresa);
        empresaEventSrc.fire(empresa);
    }
	
}
