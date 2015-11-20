package bo.buffalo.data;

import javax.enterprise.context.ApplicationScoped;

import bo.buffalo.model.FichaDetalleInsumoAcabado;

/**
 * @author Arturo.Herrera
 *
 */
@ApplicationScoped
public class FichaDetalleInsumoAcabadoRepository extends DataAccessRepository<FichaDetalleInsumoAcabado>{
	
	public FichaDetalleInsumoAcabadoRepository(){
		super(FichaDetalleInsumoAcabado.class);
	}
}
