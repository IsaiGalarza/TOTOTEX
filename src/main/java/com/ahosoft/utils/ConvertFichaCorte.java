package com.ahosoft.utils;

import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;
import javax.faces.convert.FacesConverter;

import bo.buffalo.model.Producto;

@FacesConverter("productoConverter")
public class ConvertFichaCorte implements Converter{

	@Override
	public Object getAsObject(FacesContext context, UIComponent component,
			String value) {
		try {
			 Producto p=new Producto();
			 p.setId(Integer.valueOf(value));
			 return p;
		} catch (Exception e) {
			e.printStackTrace();
		}
		   
		return null;
	}

	@Override
	public String getAsString(FacesContext context, UIComponent component,
			Object value) {
		try {
			return String.valueOf(((Producto) value).getId());
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	
	

}
