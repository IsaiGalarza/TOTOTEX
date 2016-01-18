package bo.buffalo.data;

import java.util.List;

import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.persistence.EntityManager;

import bo.buffalo.model.ProcesoEmpaqueFinal;

@Stateless
public class ProcesoEmpaqueFinalRepository {

	@Inject
	private EntityManager em;

	public ProcesoEmpaqueFinal findById(int id) {
		return em.find(ProcesoEmpaqueFinal.class, id);
	}
	
	@SuppressWarnings("unchecked")
	public List<ProcesoEmpaqueFinal> findAllActivos() {
		String query = "select em from ProcesoEmpaqueFinal em where ( em.estado='AC' or em.estado='IN' or em.estado='PR' )";
		System.out.println("Query ProcesoCorte: " + query);
		return em.createQuery(query).getResultList();
	}

}
