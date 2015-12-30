package bo.buffalo.data;

import java.util.List;

import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.persistence.EntityManager;

import bo.buffalo.model.OrdenProduccion;

@Stateless
public class OrdenProduccionRepository {

	@Inject
	private EntityManager em;

	public OrdenProduccion findById(int id) {
		return em.find(OrdenProduccion.class, id);
	}
	
	@SuppressWarnings("unchecked")
	public List<OrdenProduccion> findAllActivos() {
		String query = "select em from OrdenProduccion em where ( em.estado='AC' or em.estado='IN' or em.estado='PR' )";
		System.out.println("Query OrdenProduccion: " + query);
		return em.createQuery(query).getResultList();
	}

}
