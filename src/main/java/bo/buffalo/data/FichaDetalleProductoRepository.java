package bo.buffalo.data;

import java.util.List;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.persistence.EntityManager;

import bo.buffalo.model.FichaCorte;
import bo.buffalo.model.FichaDetalleProducto;
import bo.buffalo.model.FichaTecnica;

/**
 * @author Arturo.Herrera
 *
 */
@ApplicationScoped
public class FichaDetalleProductoRepository extends DataAccessRepository<FichaDetalleProducto>{

	private @Inject EntityManager em; 
	
	public FichaDetalleProductoRepository() {
		super(FichaDetalleProducto.class);
	}

    @SuppressWarnings("unchecked")
	public List<FichaDetalleProducto> findByFichaCorte(FichaCorte fichaCorte) {
    	String query = "select ser from FichaDetalleProducto ser where ser.fichaCorte.id="+fichaCorte.getId()+" order by ser.id desc";
    	System.out.println("Query FichaDetalleProducto: "+query);
    	return em.createQuery(query).getResultList();
    }
    
    @SuppressWarnings("unchecked")
    public List<FichaDetalleProducto> findByFichaTecnica(FichaTecnica fichaTecnica){
    	return null;
    }
    
}
