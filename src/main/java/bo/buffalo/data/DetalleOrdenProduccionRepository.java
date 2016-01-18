package bo.buffalo.data;

import java.util.List;

import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.persistence.EntityManager;

import bo.buffalo.model.DetalleOrdenProduccion;
import bo.buffalo.model.OrdenProduccion;

@Stateless
public class DetalleOrdenProduccionRepository {

	@Inject
	private EntityManager em;

	public DetalleOrdenProduccion findById(int id) {
		return em.find(DetalleOrdenProduccion.class, id);
	}
	
	@SuppressWarnings("unchecked")
	public List<DetalleOrdenProduccion> findAllActivos() {
		String query = "select em from DetalleOrdenProduccion em where ( em.estado='AC' or em.estado='IN' or em.estado='PR' )";
		System.out.println("Query OrdenProduccion: " + query);
		return em.createQuery(query).getResultList();
	}
	
	public DetalleOrdenProduccion findbyOrdenProduccion(OrdenProduccion ordenProduccion) {
		String query = "select em from DetalleOrdenProduccion em where ( em.estado='AC' or em.estado='IN' or em.estado='PR' ) and em.ordenProduccion.id="+ordenProduccion.getId();
		System.out.println("Query DetalleOrdenProduccion: " + query);
		return (DetalleOrdenProduccion) em.createQuery(query).getSingleResult();
	}

}
