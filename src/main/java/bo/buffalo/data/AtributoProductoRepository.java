package bo.buffalo.data;

import javax.enterprise.context.ApplicationScoped;

import bo.buffalo.model.AtributoProducto;

/**
 * @author Arturo.Herrera
 *
 */
@ApplicationScoped
public class AtributoProductoRepository extends DataAccessRepository<AtributoProducto>{
	public AtributoProductoRepository(){
		super(AtributoProducto.class);
	}
}
