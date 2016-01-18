package bo.buffalo.data;

import java.util.List;

import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.persistence.EntityManager;

import bo.buffalo.model.ProcesoLavanderia;

@Stateless
public class ProcesoLavanderiaRepository {

	@Inject
	private EntityManager em;

	public ProcesoLavanderia findById(int id) {
		return em.find(ProcesoLavanderia.class, id);
	}
	
	@SuppressWarnings("unchecked")
	public List<ProcesoLavanderia> findAllActivos() {
		String query = "select em from ProcesoLavanderia em where ( em.estado='AC' or em.estado='IN' or em.estado='PR' )";
		System.out.println("Query ProcesoLavanderia: " + query);
		return em.createQuery(query).getResultList();
	}

}
