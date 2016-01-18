
package bo.buffalo.data;

import java.util.List;

import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.persistence.EntityManager;

import bo.buffalo.model.DetalleReciboLavanderia;
import bo.buffalo.model.ReciboLavanderia;

@Stateless
public class DetalleReciboLavanderiaRepository {
	
	@Inject
    private EntityManager em;

    public DetalleReciboLavanderia findById(int id) {
        return em.find(DetalleReciboLavanderia.class, id);
    }

    @SuppressWarnings("unchecked")
	public List<DetalleReciboLavanderia> findByReciboLavanderia(ReciboLavanderia reciboLavanderia) {
    	String query = "select em from DetalleReciboLavanderia em where em.reciboLavanderia.id="+reciboLavanderia.getId()+" order by em.id desc";
    	System.out.println("Query DetalleReciboLavanderia: "+query);
    	return em.createQuery(query).getResultList();
    }
    
    @SuppressWarnings("unchecked")
	public List<DetalleReciboLavanderia> findActivosOrderedByFechaRegistro() {
    	String query = "select em from DetalleReciboLavanderia em where em.estado='AC' order by em.fechaRegistro desc";
    	System.out.println("Query DetalleReciboLavanderia: "+query);
    	return em.createQuery(query).getResultList();
    }
    
    @SuppressWarnings("unchecked")
	public List<DetalleReciboLavanderia> findAllOrderedByFechaRegistro() {
    	String query = "select em from DetalleReciboLavanderia em where em.estado='AC' or em.estado='IN' order by em.fechaRegistro desc";
    	System.out.println("Query DetalleReciboLavanderia: "+query);
    	return em.createQuery(query).getResultList();
    }
    
	
}
