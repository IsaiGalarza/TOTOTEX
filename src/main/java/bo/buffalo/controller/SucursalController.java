package bo.buffalo.controller;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.enterprise.context.Conversation;
import javax.enterprise.context.ConversationScoped;
import javax.enterprise.event.Event;
import javax.enterprise.inject.Produces;
import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
import javax.inject.Inject;
import javax.inject.Named;
import javax.persistence.EntityManager;
import javax.servlet.http.HttpServletRequest;

import org.primefaces.event.SelectEvent;
import org.richfaces.cdi.push.Push;

import com.ahosoft.utils.FacesUtil;

import bo.buffalo.data.CiudadRepository;
import bo.buffalo.data.EmpresaRepository;
import bo.buffalo.data.SucursalRepository;
import bo.buffalo.data.UsuarioRepository;
import bo.buffalo.model.Ciudad;
import bo.buffalo.model.Dosificacion;
import bo.buffalo.model.Empresa;
import bo.buffalo.model.Sucursal;
import bo.buffalo.model.Usuario;
import bo.buffalo.service.SucursalRegistration;

@Named(value="sucursalController")
@ConversationScoped
public class SucursalController implements Serializable{
	
	private static final long serialVersionUID = -7306337910273944584L;

	public static final String PUSH_CDI_TOPIC = "pushCdi";
	
	private @Inject EntityManager em;
    private @Inject FacesContext facesContext;
    @Inject Conversation conversation;
    private @Inject SucursalRegistration sucursalRegistration;
    private @Inject EmpresaRepository empresaRepository;
    private  @Inject UsuarioRepository usuarioRepository;
    private @Inject SucursalRepository sucursalRepository;
    
    @Inject
    @Push(topic = PUSH_CDI_TOPIC)
    Event<String> pushEventSucursal;
    private Usuario usuarioSession;
    private String tituloPanel = "Registrar Sucursal";
    private boolean modificar = false;
    private int ciudadID;
	private Sucursal newSucursal;
	private Sucursal selectedSucursal;
	private List<Sucursal> sucursales;
	private @Inject CiudadRepository ciudadRepository;
	private List<Empresa> listaEmpresas = new ArrayList<Empresa>();
	private List<Ciudad> listaCiudad= new ArrayList<Ciudad>();
	private List<Dosificacion> listaDosificacion;
	private List<Dosificacion> listaDosificacionDelete;
	private Dosificacion nuevaDosificacion=new Dosificacion();
    private Boolean nuevoSucrusal; 
   
    @Produces
    @Named
    public List<Sucursal> getSucursales() {
        return sucursales;
    }
	
	@PostConstruct
	public void initNewSucursal() {

		// initConversation();
		beginConversation();

		HttpServletRequest request = (HttpServletRequest) facesContext
				.getExternalContext().getRequest();
		System.out.println("init Sucursal*********************************");
		System.out.println("request.getClass().getName():"
				+ request.getClass().getName());
		System.out.println("isVentas:" + request.isUserInRole("ventas"));
		System.out.println("remoteUser:" + request.getRemoteUser());
		System.out.println("userPrincipalName:"
				+ (request.getUserPrincipal() == null ? "null" : request
						.getUserPrincipal().getName()));

		usuarioSession = usuarioRepository.findByLogin(request
				.getUserPrincipal().getName());
		System.out.println("Sucursal Usuario: "
				+ usuarioSession.getSucursal().getNombre());
		ciudadID = 0;
		newSucursal = new Sucursal();
		newSucursal.setEstado("AC");
		newSucursal.setFechaRegistro(new Date());
		newSucursal.setUsuarioRegistro(usuarioSession);
		
		listaEmpresas.clear();
		listaEmpresas = empresaRepository.traerEmpresasActivas();

		// traer sucursales
		sucursales = sucursalRepository.traerSucursales();
		tituloPanel = "Registrar Sucursal";
		modificar = false;
		setListaCiudad(ciudadRepository.traerCiudad());
		listaDosificacion=new ArrayList<>();
		this.nuevaDosificacion=new Dosificacion();
		this.nuevoSucrusal=false;
	}
	
	
	public void limpiarCampos(){
		ciudadID = 0;
		newSucursal = new Sucursal();
		newSucursal.setEstado("AC");
		newSucursal.setFechaRegistro(new Date());
		newSucursal.setUsuarioRegistro(usuarioSession);
		tituloPanel = "Registrar Sucursal";
		modificar = false;
		setListaCiudad(ciudadRepository.traerCiudad());
		listaDosificacion=new ArrayList<>();
		this.nuevaDosificacion=new Dosificacion();
		FacesUtil.resetComponent("formRegistroSucursal");
	}
	
	public void accionNuvaSucursal(){
		this.newSucursal= new Sucursal();
		newSucursal.setEstado("AC");
		newSucursal.setFechaRegistro(new Date());
		newSucursal.setUsuarioRegistro(usuarioSession);
		tituloPanel = "Registrar Sucursal";
		ciudadID = 0;
		this.selectedSucursal=new Sucursal();
		this.nuevaDosificacion=new Dosificacion();
		this.listaDosificacion=new ArrayList<>();
		this.modificar=false;
		this.nuevoSucrusal=true;
	}
	
	public void accionModificarSucursal(){
		if(this.newSucursal.getId()!=null){
			this.nuevoSucrusal=true;
			this.modificar=true;
		}else{
			FacesUtil.warmMessage("Seleccione una sucursal");
		}
	}
	
	public void accionesCancelarSucursal(){
		this.newSucursal= new Sucursal();
		this.selectedSucursal=new Sucursal();
		this.nuevaDosificacion=new Dosificacion();
		this.listaDosificacion=new ArrayList<>();
		this.nuevoSucrusal=false;	
	}
	
	public void accionesEliminarSucursal(){
		if(this.newSucursal.getId()!=null){
			FacesUtil.showDialog("id_confirm_delete");
		}else{
			FacesUtil.warmMessage("Seleccione una sucursal");
		}
	}
	
	
	public void cancelarAcciones(){
		
		System.out.println("Ingreso a Cancelar Acciones...");
		newSucursal = new Sucursal();
		initNewSucursal();
		
	}
	
	public void beginConversation()
	   {
	      if (conversation.isTransient())
	      {
	    	  System.out.println("beginning conversation : " + this.conversation);
	          conversation.begin();
	          System.out.println("---> Init Conversation");
	      }
	   }

	   public void endConversation()
	   {
	      if (!conversation.isTransient())
	      {
	          conversation.end();
	      }
	   }
	
	public void registrarSucursal(){
        try {
        	System.out.println("Ingreso a register...."+newSucursal.getId());
        	newSucursal.setCiudad(em.find(Ciudad.class, this.getCiudadID()));
        	if(listaDosificacion==null){
        		listaDosificacion=new ArrayList<>();
        	}
        	sucursalRegistration.register(newSucursal,listaDosificacion);
        	FacesMessage m = new FacesMessage(FacesMessage.SEVERITY_INFO, "Registered!", "Registration successful");
            facesContext.addMessage(null, m);
            pushEventSucursal.fire(String.format("New Sucursal added: %s (id: %d)", newSucursal.getNombre(), newSucursal.getId()));
            initNewSucursal();
            
        } catch (Exception e) {
        	e.printStackTrace();
            String errorMessage = getRootErrorMessage(e);
            FacesMessage m = new FacesMessage(FacesMessage.SEVERITY_ERROR, errorMessage, "Registration unsuccessful");
            facesContext.addMessage(null, m);
        }
    }
	
	public void modificarSucursal(){
        try {
        	System.out.println("Ingreso a modificarSucursal...."+newSucursal.getId());
        	newSucursal.setCiudad(em.find(Ciudad.class, this.getCiudadID()));
        	if(listaDosificacion==null){
        		listaDosificacion=new ArrayList<>();
        	}
        	if(listaDosificacionDelete!=null){
        		for (Dosificacion dosificacion : listaDosificacionDelete) {
        			dosificacion.setBaja(true);
					this.listaDosificacion.add(dosificacion);
				}
        	}
        	
        	sucursalRegistration.updated(newSucursal,listaDosificacion);
        	FacesMessage m = new FacesMessage(FacesMessage.SEVERITY_INFO, "Modificado!", "Modificado Correctamente.");
            facesContext.addMessage(null, m);
            pushEventSucursal.fire(String.format("Sucursal Modificada: %s (id: %d)", newSucursal.getNombre(), newSucursal.getId()));
            initNewSucursal();
            
        } catch (Exception e) {
        	e.printStackTrace();
            String errorMessage = getRootErrorMessage(e);
            FacesMessage m = new FacesMessage(FacesMessage.SEVERITY_ERROR, errorMessage, "Registration unsuccessful");
            facesContext.addMessage(null, m);
        }
    }
	
	public void eliminarSucursal(){
        try {
        	System.out.println("Ingreso a eliminarSucursal...."+newSucursal.getId());
        	listaDosificacion=sucursalRepository.dosificacionesActivas(newSucursal);
        	if(listaDosificacion==null){
        		listaDosificacion=new ArrayList<>();
        	}
        	sucursalRegistration.remove(newSucursal,listaDosificacion);
        	FacesMessage m = new FacesMessage(FacesMessage.SEVERITY_INFO, "Eliminado!", "Eliminado Correctamente.");
            facesContext.addMessage(null, m);
            pushEventSucursal.fire(String.format("Sucursal Eliminada: %s (id: %d)", newSucursal.getNombre(), newSucursal.getId()));
            initNewSucursal();
            
        } catch (Exception e) {
        	e.printStackTrace();
            String errorMessage = getRootErrorMessage(e);
            FacesMessage m = new FacesMessage(FacesMessage.SEVERITY_ERROR, errorMessage, "ELiminar unsuccessful");
            facesContext.addMessage(null, m);
        }
    }
	
	//SELECT SUCURSAL CLICK
  	public void onRowSelectSucursalClick(SelectEvent event) {
  		try {
  			Sucursal sucursal = (Sucursal) event.getObject();
  			System.out.println("onRowSelectSucursalClick  "+ sucursal.getId());
  			selectedSucursal = sucursal;
  			newSucursal = em.find(Sucursal.class, sucursal.getId());
  			newSucursal.setFechaRegistro(new Date());
  			newSucursal.setUsuarioRegistro(usuarioSession);
  			ciudadID = newSucursal.getCiudad().getId();
  			tituloPanel = "Modificar Sucursal";
  			modificar = true;
  			listaDosificacion=sucursalRepository.dosificacionesActivas(selectedSucursal);
  			
  		} catch (Exception e) {
  			// TODO: handle exception
  			System.out.println("Error in onRowSelectSucursalClick: "+e.getMessage());
  		}
  	}
	
	private String getRootErrorMessage(Exception e) {
        // Default to general error message that registration failed.
        String errorMessage = "Registration failed. See server log for more information";
        if (e == null) {
            // This shouldn't happen, but return the default messages
            return errorMessage;
        }

        // Start with the exception and recurse to find the root cause
        Throwable t = e;
        while (t != null) {
            // Get the message from the Throwable class instance
            errorMessage = t.getLocalizedMessage();
            t = t.getCause();
        }
        // This is the root cause message
        return errorMessage;
    }
	
	
	/*-----medotos dosificacion-----------*/
	
	public void cancelarAccionDosificacion(){
		this.nuevaDosificacion=new Dosificacion();
	}
	
	public void limpiarDosificacion(){
		this.nuevaDosificacion=new Dosificacion();
		FacesUtil.resetComponent("dlgDosificacion");
	}
	
	public void agregarDosificacion(){
		if(listaDosificacion==null){
			listaDosificacion=new ArrayList<>();
		}
		boolean existe=false;
		for (Dosificacion dosificacion : listaDosificacion) {
			if(dosificacion.getLlaveControl().equals(nuevaDosificacion.getLlaveControl())){
				existe=true;
			}
		}
		if(existe){
			FacesUtil.warmMessage("Llave de dosificacion duplicada.!");
		}else{
			listaDosificacion.add(nuevaDosificacion);
			FacesUtil.updateComponent("formRegistroSucursal:id_table_dosificacion");
			FacesUtil.hideDialog("wv_dosificacion");
		}
	}
	
	public void validarModificarDosificacion(){
		if(nuevaDosificacion.getLlaveControl()!=null){
			FacesUtil.updateComponent("dlgDosificacion");
			FacesUtil.showDialog("wv_dosificacion");
		}else{
			FacesUtil.warmMessage("Seleccione una dosificacion");
		}
	}
	
	public void modificarDosificacion(){
		try {
			for (Dosificacion dosificacion : listaDosificacion) {
				if(dosificacion.getLlaveControl().equals(nuevaDosificacion.getLlaveControl())){
					dosificacion=nuevaDosificacion;
					break;
				}
			}
			
			nuevaDosificacion=new Dosificacion();
			FacesUtil.updateComponent("formRegistroSucursal:id_table_dosificacion");
			FacesUtil.hideDialog("wv_dosificacion");
		} catch (Exception e) {
			FacesUtil.warmMessage("No se pudo modificar la dosificacion.!");
		}
		
	}
	
	public void validarEliminarDosificacion(){
		if(nuevaDosificacion.getLlaveControl()!=null){
			FacesUtil.showDialog("wv_confirm_dosificacion");
		}else{
			FacesUtil.warmMessage("Seleccione una dosificacion");
		}
	}
	
	public void eliminarDosificacion(){
		if(nuevaDosificacion!=null){
			for (Dosificacion dosificacion : listaDosificacion) {
				if(dosificacion.getLlaveControl().equals(nuevaDosificacion.getLlaveControl())){
					listaDosificacion.remove(dosificacion);
					if(listaDosificacionDelete==null){
						listaDosificacionDelete=new ArrayList<>();
					}
					dosificacion.setBaja(true);
					dosificacion.setActivo(false);
					listaDosificacionDelete.add(dosificacion);
					break;
				}
			}
			FacesUtil.updateComponent("formRegistroSucursal:id_table_dosificacion");
			FacesUtil.hideDialog("wv_confirm_dosificacion");
		}else{
			FacesUtil.warmMessage("Seleccione una dosificacion");
		}
	}
	
	public void validarActivacionDosificacion(){
		if(nuevaDosificacion.getLlaveControl()!=null){
			FacesUtil.showDialog("id_confirm_dosificacion_activado");
		}else{
			FacesUtil.warmMessage("Seleccione una dosificacion");
		}
	}
	
	public void activarDosificacion(){
		for (Dosificacion dosificacion : listaDosificacion) {
			if(dosificacion.getLlaveControl().equals(nuevaDosificacion.getLlaveControl())){
				dosificacion.setActivo(true);
			}else{
				dosificacion.setActivo(false);
			}
		}
		
		FacesUtil.updateComponent("formRegistroSucursal:id_table_dosificacion");
		FacesUtil.hideDialog("id_confirm_dosificacion_activado");
	}
	
 	public void onRowSelectDosificacionlClick(SelectEvent event) {
  		try {
  			Dosificacion sucursal = (Dosificacion) event.getObject();
  			System.out.println("onRowSelectSucursalClick  "+ sucursal.getId());

  			
  		} catch (Exception e) {
  			// TODO: handle exception
  			System.out.println("Error in onRowSelectSucursalClick: "+e.getMessage());
  		}
  	}
	
	/*------fin dosificacion---------------*/
	
	
	//get and set
	public Sucursal getNewSucursal() {
		return newSucursal;
	}

	public void setNewSucursal(Sucursal newSucursal) {
		this.newSucursal = newSucursal;
	}

	public Sucursal getSelectedSucursal() {
		return selectedSucursal;
	}

	public void setSelectedSucursal(Sucursal selectedSucursal) {
		this.selectedSucursal = selectedSucursal;
	}

	public String getTituloPanel() {
		return tituloPanel;
	}

	public void setTituloPanel(String tituloPanel) {
		this.tituloPanel = tituloPanel;
	}

	public boolean isModificar() {
		return modificar;
	}

	public void setModificar(boolean modificar) {
		this.modificar = modificar;
	}

	public List<Ciudad> getListaCiudad() {
		return listaCiudad;
	}

	public void setListaCiudad(List<Ciudad> listaCiudad) {
		this.listaCiudad = listaCiudad;
	}

	public List<Empresa> getListaEmpresas() {
		return listaEmpresas;
	}

	public void setListaEmpresas(List<Empresa> listaEmpresas) {
		this.listaEmpresas = listaEmpresas;
	}

	public int getCiudadID() {
		return ciudadID;
	}

	public void setCiudadID(int ciudadID) {
		this.ciudadID = ciudadID;
	}

	public List<Dosificacion> getListaDosificacion() {
		return listaDosificacion;
	}

	public void setListaDosificacion(List<Dosificacion> listaDosificacion) {
		this.listaDosificacion = listaDosificacion;
	}

	public Dosificacion getNuevaDosificacion() {
		return nuevaDosificacion;
	}

	public void setNuevaDosificacion(Dosificacion nuevaDosificacion) {
		this.nuevaDosificacion = nuevaDosificacion;
	}

	public List<Dosificacion> getListaDosificacionDelete() {
		return listaDosificacionDelete;
	}

	public void setListaDosificacionDelete(List<Dosificacion> listaDosificacionDelete) {
		this.listaDosificacionDelete = listaDosificacionDelete;
	}

	public Boolean getNuevoSucrusal() {
		return nuevoSucrusal;
	}

	public void setNuevoSucrusal(Boolean nuevoSucrusal) {
		this.nuevoSucrusal = nuevoSucrusal;
	}
	
}
