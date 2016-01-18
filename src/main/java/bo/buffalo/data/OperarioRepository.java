package bo.buffalo.data;

import java.util.List;

import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.persistence.EntityManager;

import bo.buffalo.model.Operario;

@Stateless
public class OperarioRepository {
	
	@Inject
    private EntityManager em;

    public Operario findById(int id) {
        return em.find(Operario.class, id);
    }

    @SuppressWarnings("unchecked")
	public List<Operario> findAllOrderedByNombre() {
    	String query = "select em from Operario em where em.estado='AC' order by em.nombre desc";
    	System.out.println("Query Operario: "+query);
    	return em.createQuery(query).getResultList();
    }
    
    @SuppressWarnings("unchecked")
	public List<Operario> findActivosOrderedByFechaRegistro() {
    	String query = "select em from Operario em where em.estado='AC' order by em.fechaRegistro desc";
    	System.out.println("Query Operario: "+query);
    	return em.createQuery(query).getResultList();
    }
    
    @SuppressWarnings("unchecked")
	public List<Operario> findAllOrderedByFechaRegistro() {
    	String query = "select em from Operario em where em.estado='AC' or em.estado='IN' order by em.fechaRegistro desc";
    	System.out.println("Query Operario: "+query);
    	return em.createQuery(query).getResultList();
    }
    
	
}
