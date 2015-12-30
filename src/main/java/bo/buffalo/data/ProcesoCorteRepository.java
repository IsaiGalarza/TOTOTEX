package bo.buffalo.data;

import java.util.List;

import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.persistence.EntityManager;

import bo.buffalo.model.ProcesoCorte;

@Stateless
public class ProcesoCorteRepository {

	@Inject
	private EntityManager em;

	public ProcesoCorte findById(int id) {
		return em.find(ProcesoCorte.class, id);
	}
	
	@SuppressWarnings("unchecked")
	public List<ProcesoCorte> findAllActivos() {
		String query = "select em from ProcesoCorte em where ( em.estado='AC' or em.estado='IN' or em.estado='PR' )";
		System.out.println("Query ProcesoCorte: " + query);
		return em.createQuery(query).getResultList();
	}

}
