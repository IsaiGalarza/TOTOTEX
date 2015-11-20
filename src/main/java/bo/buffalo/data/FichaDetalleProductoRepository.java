package bo.buffalo.data;

import javax.enterprise.context.ApplicationScoped;

import bo.buffalo.model.FichaDetalleProducto;

/**
 * @author Arturo.Herrera
 *
 */
@ApplicationScoped
public class FichaDetalleProductoRepository extends DataAccessRepository<FichaDetalleProducto>{

	public FichaDetalleProductoRepository() {
		super(FichaDetalleProducto.class);
	}

}
