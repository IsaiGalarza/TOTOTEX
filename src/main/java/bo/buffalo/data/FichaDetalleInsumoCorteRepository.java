package bo.buffalo.data;

import javax.enterprise.context.ApplicationScoped;

import bo.buffalo.model.FichaDetalleInsumoCorte;

/**
 * @author Arturo.Herrera
 *
 */
@ApplicationScoped
public class FichaDetalleInsumoCorteRepository extends DataAccessRepository<FichaDetalleInsumoCorte>{
	
	public FichaDetalleInsumoCorteRepository(){
		super(FichaDetalleInsumoCorte.class);
	}
}
