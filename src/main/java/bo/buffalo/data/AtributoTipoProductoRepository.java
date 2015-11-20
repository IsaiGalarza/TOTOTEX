package bo.buffalo.data;

import javax.enterprise.context.ApplicationScoped;

import bo.buffalo.model.AtributoTipoProducto;

/**
 * @author Arturo.Herrera
 *
 */
@ApplicationScoped
public class AtributoTipoProductoRepository extends DataAccessRepository<AtributoTipoProducto>{
	public AtributoTipoProductoRepository(){
		super(AtributoTipoProducto.class);
	}
}
