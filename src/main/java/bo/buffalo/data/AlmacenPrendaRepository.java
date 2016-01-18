package bo.buffalo.data;

import java.util.List;

import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.persistence.EntityManager;

import bo.buffalo.model.AlmacenPrenda;
import bo.buffalo.model.DetalleOrdenProduccion;
import bo.buffalo.model.OrdenProduccion;

@Stateless
public class AlmacenPrendaRepository {

	@Inject
	private EntityManager em;

	public DetalleOrdenProduccion findById(int id) {
		return em.find(DetalleOrdenProduccion.class, id);
	}
	
	@SuppressWarnings("unchecked")
	public List<AlmacenPrenda> findAllActivos() {
		String query = "select em from AlmacenPrenda em where ( em.estado='AC' or em.estado='IN' or em.estado='PR' )";
		System.out.println("Query AlmacenPrenda: " + query);
		return em.createQuery(query).getResultList();
	}
	
	public AlmacenPrenda findBy() {
		String query = "select em from AlmacenPrenda em where ( em.estado='AC' or em.estado='IN' or em.estado='PR' )" ;
		System.out.println("Query AlmacenPrenda: " + query);
		return (AlmacenPrenda) em.createQuery(query).getSingleResult();
	}

}
