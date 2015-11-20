package bo.buffalo.validators;

import java.util.List;
import javax.faces.application.FacesMessage;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.validator.FacesValidator;
import javax.faces.validator.Validator;
import javax.faces.validator.ValidatorException;
import javax.inject.Inject;

import bo.buffalo.data.ProductoRepository;
import bo.buffalo.model.Producto;

@FacesValidator("productoValidator")
public class ProductoValidator implements Validator {
	
	private final static String EMAIL_PATTERN = "^[_A-Za-z0-9-\\+]+(\\.[_A-Za-z0-9-]+)*@[A-Za-z0-9-]+(\\.[A-Za-z0-9]+)*(\\.[A-Za-z]{2,})$";
    
    
    @Inject
//    private final static ProductoRepository productoRepository = null;
    ProductoRepository productoRepository;
    
    /**
     * Validate method.
     */
    public void validate(FacesContext fc, UIComponent c, Object o) throws ValidatorException {
    	
    	
    	try {
    		productoRepository = new ProductoRepository();
    		System.out.println("Validando nombre del producto si es repedito..."+(String)o);
    		List<Producto> listaProducto = productoRepository.buscarProductoNombreExacto((String)o);
    		for(Producto producto : listaProducto){
    			System.out.println("Resultado Encontrado: "+producto.getNombreProducto());
    		}
    		System.out.println("!IF: "+!listaProducto.isEmpty());
    		System.out.println("IF: "+listaProducto.isEmpty());
    		if(!listaProducto.isEmpty()){
    			FacesMessage msg = new FacesMessage("Invalido Producto Existe!", (String)o);
                msg.setSeverity(FacesMessage.SEVERITY_ERROR);
                throw new ValidatorException(msg);
    		}
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}
		
//    	
//        // No value is not ok
//        if (o == null || "".equals((String)o)) {
//            FacesMessage msg = new FacesMessage("No email value!", "Email Validation Error");
//            msg.setSeverity(FacesMessage.SEVERITY_ERROR);
//            throw new ValidatorException(msg);
//        }
//         
//        // The email matcher
//        Matcher matcher = EMAIL_COMPILED_PATTERN.matcher((String)o);
//         
//        if (!matcher.matches()) {   // Email doesn't match
//            FacesMessage msg = new FacesMessage("Invalid email value!", "Email Validation Error");
//            msg.setSeverity(FacesMessage.SEVERITY_ERROR);
//            throw new ValidatorException(msg);
//        }
         
    }
	
}
