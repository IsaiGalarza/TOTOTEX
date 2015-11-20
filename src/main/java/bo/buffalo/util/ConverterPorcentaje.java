package bo.buffalo.util;

import javax.faces.application.FacesMessage;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;
import javax.faces.convert.FacesConverter;

@FacesConverter("converterPorcentaje")
public class ConverterPorcentaje implements Converter{

	@Override
	public Object getAsObject(FacesContext fc, UIComponent uic, String value) {
		if(value != null && value.trim().length() > 0){
			try {
				Double numero= Double.valueOf(value);
				numero=numero/100;
				System.out.println("Porcentaje: "+numero);
				return numero;
			} catch (Exception e) {
				saveMessage("Ingrese un valor numerico entre 0-100");
			}
		
		}else{
			saveMessage("Ingrese un Valor");
		}
		return null;
	}

	@Override
	public String getAsString(FacesContext fc, UIComponent uic, Object object) {
		try {
			double numero = (Double)object;
			numero=numero*100;
			return String.valueOf(numero);
		} catch (Exception e) {
			saveMessage("Error Conversion");
		}
		return null;
	}

	public void saveMessage(String msj) {
        FacesContext context = FacesContext.getCurrentInstance();
        context.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO,"Advertencia!", msj));
    }
}
