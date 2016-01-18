package bo.buffalo.data;

import java.util.List;

import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.persistence.EntityManager;

import bo.buffalo.model.FichaCorte;
import bo.buffalo.model.FichaInsumoCorte;
import bo.buffalo.model.FichaDetalleProducto;

/**
 * 
 * @author mauriciobejaranorivera
 *
 */
@Stateless
public class FichaInsumoCorteRepository extends DataAccessRepository<FichaInsumoCorte>{

	private @Inject EntityManager em;

	public FichaInsumoCorteRepository(){
		super(FichaInsumoCorte.class);
	}


	@SuppressWarnings("unchecked")
	public List<FichaInsumoCorte> findByFichaCorte(FichaCorte fichaCorte) {
		String query = "select em from FichaInsumoCorte em where  em.estado='AC' and em.fichaCorte.id="+fichaCorte.getId();
		System.out.println("Query FichaInsumoCorte: " + query);
		return em.createQuery(query).getResultList();
	}
}
