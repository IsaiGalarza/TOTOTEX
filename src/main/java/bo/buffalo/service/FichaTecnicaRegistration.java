package bo.buffalo.service;

import java.util.List;

import javax.ejb.Stateless;

import com.ahosoft.utils.FacesUtil;

import bo.buffalo.model.FichaDetalleInsumoAcabado;
import bo.buffalo.model.FichaInsumoCorte;
import bo.buffalo.model.FichaDetalleProducto;
import bo.buffalo.model.FichaTecnica;

/**
 * @author Arturo.Herrera
 *
 */
@Stateless
public class FichaTecnicaRegistration extends DataAccessService<FichaTecnica>{
	public FichaTecnicaRegistration(){
		super(FichaTecnica.class);
	}
	
	public FichaTecnica register(FichaTecnica t,List<FichaDetalleProducto> listaProducto,List<FichaInsumoCorte> listaCorte,
			List<FichaDetalleInsumoAcabado> listaAcabado){
    	try {
    		getLog().info("Registering "+ t.toString());
    		this.getEm().persist(t);
            this.getEm().flush();
            this.getEm().refresh(t);
            
            for (FichaDetalleProducto p : listaProducto) {
				p.setFechaRegistro(t.getFechaRegistro());
				p.setUsuarioRegistro(t.getUsuarioRegistro());
				p.setFichaTecnica(t);
				getEm().persist(p);
				getEm().refresh(p);
				p.setCodigoBarra(""+t.getMarca().getId()+t.getId()+t.getColorAtraque().getId()+p.getTalla());
				getEm().merge(p);

			}
            
            for (FichaInsumoCorte c : listaCorte) {
            	c.setFechaRegistro(t.getFechaRegistro());
				c.setUsuarioRegistro(t.getUsuarioRegistro());
				c.setFichaTecnica(t);
				getEm().persist(c);
			}
            
            for (FichaDetalleInsumoAcabado a : listaAcabado) {
            	a.setFechaRegistro(t.getFechaRegistro());
				a.setUsuarioRegistro(t.getUsuarioRegistro());
				a.setFichaTecnica(t);
				getEm().persist(a);
			}
            getLog().info("register complet:  "+t);
            return t;
		} catch (Exception e) {
			System.out.println("Error "+e.getCause());
			String cause=e.getMessage();
			if(cause.equals("org.hibernate.exception.ConstraintViolationException: could not execute statement")){
				FacesUtil.errorMessage("Ya existe un registro con el mismo nombre.!");
			}else{
				FacesUtil.errorMessage("Ocurrio un error en el registro.!");
			}
			return null;
		}
    		
    }
	
	 public FichaTecnica updated(FichaTecnica item,List<FichaDetalleProducto> listaProducto,List<FichaInsumoCorte> listaCorte,
				List<FichaDetalleInsumoAcabado> listaAcabado){
		 FichaTecnica t= (FichaTecnica) this.getEm().merge(item);
		 for (FichaDetalleProducto p : listaProducto) {
				p.setFechaRegistro(t.getFechaRegistro());
				p.setUsuarioRegistro(t.getUsuarioRegistro());
				p.setFichaTecnica(t);
				if(item.getBaja()){
					p.setBaja(item.getBaja());
				}
				if(p.getId()!=null){
					getEm().merge(p);
				}else{
					getEm().persist(p);
				}
			}
         
         for (FichaInsumoCorte c : listaCorte) {
         	c.setFechaRegistro(t.getFechaRegistro());
				c.setUsuarioRegistro(t.getUsuarioRegistro());
				c.setFichaTecnica(t);
				if(item.getBaja()){
					c.setBaja(item.getBaja());
				}
				if(c.getId()!=null){
					getEm().merge(c);
				}else{
					getEm().persist(c);
				}
			}
         
         for (FichaDetalleInsumoAcabado a : listaAcabado) {
         	a.setFechaRegistro(t.getFechaRegistro());
				a.setUsuarioRegistro(t.getUsuarioRegistro());
				a.setFichaTecnica(t);
				if(item.getBaja()){
					a.setBaja(item.getBaja());
				}
				if(a.getId()!=null){
					getEm().merge(a);
				}else{
					getEm().persist(a);
				}
			}
         getLog().info("update complet:  "+item);
         return t;      
	 }
}
