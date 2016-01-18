package bo.buffalo.data;

import java.util.List;

import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.persistence.EntityManager;

import bo.buffalo.model.AlmacenPrenda;
import bo.buffalo.model.DetalleAlmacenPrenda;
import bo.buffalo.model.DetalleOrdenProduccion;

@Stateless
public class DetalleAlmacenPrendaRepository {

	@Inject
	private EntityManager em;

	public DetalleOrdenProduccion findById(int id) {
		return em.find(DetalleOrdenProduccion.class, id);
	}
	
	@SuppressWarnings("unchecked")
	public List<DetalleAlmacenPrenda> findAll() {
		String query = "select em from AlmacenPrendaFalla em";
		System.out.println("Query AlmacenPrenda: " + query);
		return em.createQuery(query).getResultList();
	}
	
	@SuppressWarnings("unchecked")
	public List<DetalleAlmacenPrenda> findByAlmacenPrenda(AlmacenPrenda almacenPrenda) {
		String query = "select em from DetalleAlmacenPrenda em where em.almacenPrenda.id="+almacenPrenda.getId() ;
		System.out.println("Query AlmacenPrenda: " + query);
		return em.createQuery(query).getResultList();
	}

}
