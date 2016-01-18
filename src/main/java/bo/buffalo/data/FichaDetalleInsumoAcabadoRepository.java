package bo.buffalo.data;

import java.util.List;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.persistence.EntityManager;

import bo.buffalo.model.FichaDetalleInsumoAcabado;
import bo.buffalo.model.FichaInsumoAcabado;

/**
 * @author Arturo.Herrera
 *
 */
@ApplicationScoped
public class FichaDetalleInsumoAcabadoRepository extends DataAccessRepository<FichaDetalleInsumoAcabado>{
	
	private @Inject EntityManager em;
	
	public FichaDetalleInsumoAcabadoRepository(){
		super(FichaDetalleInsumoAcabado.class);
	}
	
    @SuppressWarnings("unchecked")
	public List<FichaDetalleInsumoAcabado> findAllByFichaInsumoAcabado(FichaInsumoAcabado fichaInsumoAcabado) {
    	String query = "select em from FichaDetalleInsumoAcabado em where ( em.estado='AC' or em.estado='AP') and em.fichaInsumoAcabado.id="+fichaInsumoAcabado.getId()+" order by em.id desc";
    	System.out.println("Query FichaDetalleInsumoAcabado: "+query);
    	return em.createQuery(query).getResultList();
    }
	
}
