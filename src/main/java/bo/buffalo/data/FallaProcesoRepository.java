
package bo.buffalo.data;

import java.util.List;

import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.persistence.EntityManager;

import bo.buffalo.model.FallaProceso;

@Stateless
public class FallaProcesoRepository {
	
	@Inject
    private EntityManager em;

    public FallaProceso findById(int id) {
        return em.find(FallaProceso.class, id);
    }

    @SuppressWarnings("unchecked")
	public List<FallaProceso> findById() {
    	String query = "select em from FallaProceso order by em.id desc";
    	System.out.println("Query FallaProceso: "+query);
    	return em.createQuery(query).getResultList();
    }

	
}
