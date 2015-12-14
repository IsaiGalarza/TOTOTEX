package bo.buffalo.data;

import java.util.List;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.persistence.EntityManager;

import bo.buffalo.model.Permiso;

@ApplicationScoped
public class PermisoRepository {

	@Inject
	private EntityManager em;


	public Permiso findById(int id) {
		return em.find(Permiso.class, id);
	}
	
	@SuppressWarnings("unchecked")
	public List<Permiso> findAllOrderByName() {
		String query = "select em from Permiso em where em.estado='AC' order by em.nombre desc ";
		System.out.println("Query Permiso: " + query);
		return em.createQuery(query).getResultList();
	}
}