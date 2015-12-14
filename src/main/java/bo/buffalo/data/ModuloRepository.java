package bo.buffalo.data;

import java.util.List;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.persistence.EntityManager;

import bo.buffalo.model.Modulo;

@ApplicationScoped
public class ModuloRepository {

	@Inject
	private EntityManager em;


	public Modulo findById(int id) {
		return em.find(Modulo.class, id);
	}
	
	@SuppressWarnings("unchecked")
	public List<Modulo> findAllOrderByName() {
		String query = "select em from Modulo em where em.estado='AC' order by em.nombre desc ";
		System.out.println("Query Modulo: " + query);
		return em.createQuery(query).getResultList();
	}
	
	@SuppressWarnings("unchecked")
	public List<Modulo> findAllOrderByID() {
		String query = "select em from Modulo em where em.estado='AC' order by em.id asc ";
		System.out.println("Query Modulo: " + query);
		return em.createQuery(query).getResultList();
	}
}