package bo.buffalo.controller;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.enterprise.context.Conversation;
import javax.enterprise.context.ConversationScoped;
import javax.enterprise.event.Event;
import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
import javax.inject.Inject;
import javax.inject.Named;
import javax.persistence.EntityManager;
import javax.servlet.http.HttpServletRequest;

import org.primefaces.event.SelectEvent;
import org.richfaces.cdi.push.Push;

import com.ahosoft.utils.FacesUtil;

import bo.buffalo.data.AtributoRepository;
import bo.buffalo.data.AtributoTipoProductoRepository;
import bo.buffalo.data.TipoProductoRepository;
import bo.buffalo.data.UsuarioRepository;
import bo.buffalo.model.Atributo;
import bo.buffalo.model.AtributoTipoProducto;
import bo.buffalo.model.TipoProducto;
import bo.buffalo.model.Usuario;
import bo.buffalo.service.TipoProductoRegistration;

@Named(value="tipoProductoController")
@ConversationScoped
public class TipoProductoController implements Serializable{

	private static final long serialVersionUID = 5920847122773806675L;

	public static final String PUSH_CDI_TOPIC = "pushCdi";

    @Inject
    private FacesContext facesContext;
    
    @Inject
    private EntityManager em;
    
    @Inject 
	Conversation conversation;
    
    @Inject
    private TipoProductoRegistration tipoProductoRegistration;
    
    @Inject
    private TipoProductoRepository tipoProductoRepository;
    
    @Inject 
    UsuarioRepository usuarioRepository;
    private Usuario usuarioSession;
    
    private @Inject AtributoRepository atributoRepository;
    private @Inject AtributoTipoProductoRepository atributoTipoProductoRepository;
    
    @Inject
    @Push(topic = PUSH_CDI_TOPIC)
    Event<String> pushEventSucursal;
	
    private boolean modificar = false;
    private String tituloPanel = "Registrar Tipo Producto";
	private TipoProducto newTipoProducto;
	private List<TipoProducto> listaTipoProducto;
	private List<AtributoTipoProducto> listaAtributo;
	private List<AtributoTipoProducto> listaAtributoDelete;
	private AtributoTipoProducto nuevoAtributoTipoProducto;
	private List<Atributo> listaAt;
	private boolean nuevo;
	private String atributo;
	

	@PostConstruct
    public void initNewTipoProducto() {
		
		// initConversation();
    	beginConversation();
    	
    	HttpServletRequest request = (HttpServletRequest) facesContext
				.getExternalContext().getRequest();
		System.out.println("init Tipo Producto*********************************");
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
		
		newTipoProducto = new TipoProducto();
		newTipoProducto.setEstado("AC");
		newTipoProducto.setFechaRegistro(new Date());
		newTipoProducto.setUsuarioRegistro(usuarioSession.getLogin());
		newTipoProducto.setDescripcion("");
		
		//traer todos los TipoProduct ordenados por ID
		listaTipoProducto = tipoProductoRepository.findAllOrderedByID();
		listaAtributo=new ArrayList<>();
		listaAtributoDelete=new ArrayList<>();
		listaAt=atributoRepository.findAllByParameterIsNull("atributoPadre");
		nuevoAtributoTipoProducto=new AtributoTipoProducto();
		modificar = false;
		setNuevo(false);
		this.atributo="";
    }
	
	public void nuevoTipoProducto(){
		newTipoProducto = new TipoProducto();
		newTipoProducto.setEstado("AC");
		newTipoProducto.setFechaRegistro(new Date());
		newTipoProducto.setUsuarioRegistro(usuarioSession.getLogin());
		newTipoProducto.setDescripcion("");
		this.listaAtributo=new ArrayList<>();
		this.listaAtributoDelete=new ArrayList<>();
		this.nuevoAtributoTipoProducto=new AtributoTipoProducto();
		tituloPanel = "Registrar Tipo Producto";
		modificar = false;
		this.nuevo=true;
		ArrayList<String> frm=new ArrayList<>();
		frm.add("frm_action");frm.add("frm_list");frm.add("frm"); frm.add("frm_list_atributo");
		FacesUtil.updateComponets(frm);
	}
	
	public void cancelarTipoProducto(){
		newTipoProducto = new TipoProducto();
		newTipoProducto.setEstado("AC");
		newTipoProducto.setFechaRegistro(new Date());
		newTipoProducto.setUsuarioRegistro(usuarioSession.getLogin());
		newTipoProducto.setDescripcion("");
		modificar = false;
		this.nuevo=false;
		this.listaAtributo=new ArrayList<>();
		this.listaAtributoDelete=new ArrayList<>();
		this.nuevoAtributoTipoProducto=new AtributoTipoProducto();
		ArrayList<String> frm=new ArrayList<>();
		frm.add("frm_action");frm.add("frm_list");frm.add("frm"); frm.add("frm_list_atributo");
		FacesUtil.updateComponets(frm);
	}
	
	public void accionModificarTipoProducto(){
		if(this.newTipoProducto.getId()!=null){
			modificar=true;
			nuevo=true;
			this.listaAtributo=atributoTipoProductoRepository.findAllByParameterObject("tipoProducto", newTipoProducto);
			this.listaAtributoDelete=new ArrayList<>();
			this.nuevoAtributoTipoProducto=new AtributoTipoProducto();
			ArrayList<String> frm=new ArrayList<>();
			frm.add("frm_action");frm.add("frm_list");frm.add("frm"); frm.add("frm_list_atributo");
			FacesUtil.updateComponets(frm);
		}else{
			FacesUtil.warmMessage("Seleccione un tipo de producto.!");
		}
	}
	
	public void cancelar(){
		newTipoProducto = new TipoProducto();
		newTipoProducto.setEstado("AC");
		newTipoProducto.setFechaRegistro(new Date());
		newTipoProducto.setUsuarioRegistro(usuarioSession.getLogin());
		newTipoProducto.setDescripcion("");
		tituloPanel = "Registrar Tipo Producto";
		modificar = false;
		ArrayList<String> frm=new ArrayList<>();
		frm.add("frm"); frm.add("frm_list_atributo");
		FacesUtil.updateComponets(frm);
		FacesUtil.resetComponent("frm");
	}
	
	
	public void modificarEstado(){
		modificar=true;
		setNuevo(true);
	}

	
	public void beginConversation() {

		if (conversation.isTransient()) {
			System.out.println("beginning conversation : " + this.conversation);
			conversation.begin();
			System.out.println("---> Init Conversation");
		}
	}

	public void endConversation() {
		if (!conversation.isTransient()) {
			conversation.end();
		}
	}

	// SELECT TIPO PRODUCTO CLICK
	public void onRowSelectTipoProductoClick(SelectEvent event) {
		try {
			TipoProducto tipoProducto = (TipoProducto) event.getObject();
			System.out.println("onRowSelectTipoProductoClick  " + tipoProducto.getId());
			newTipoProducto = em.find(TipoProducto.class, tipoProducto.getId());
			newTipoProducto.setFechaRegistro(new Date());
			newTipoProducto.setUsuarioRegistro(usuarioSession.getLogin());

			tituloPanel = "Modificar Tipo Producto";
			modificar = true;
			setNuevo(false);

		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
			System.out.println("Error in onRowSelectTipoProductoClick: "
					+ e.getMessage());
		}
	}
	
	// SELECT TIPO PRODUCTO CLICK
	public void onRowSelectTipoProductoDBLClick(SelectEvent event) {
		try {
			TipoProducto tipoProducto = (TipoProducto) event.getObject();
			System.out.println("onRowSelectTipoProductoClick  " + tipoProducto.getId());
			newTipoProducto = em.find(TipoProducto.class, tipoProducto.getId());
			newTipoProducto.setFechaRegistro(new Date());
			newTipoProducto.setUsuarioRegistro(usuarioSession.getLogin());

			tituloPanel = "Modificar Tipo Producto";
			modificar = true;
			setNuevo(true);

		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
			System.out.println("Error in onRowSelectTipoProductoClick: "
					+ e.getMessage());
		}
	}
	
	public void registrarTipoProducto(){
        try {
        	System.out.println("Ingreso a registrarTipoProducto: "+newTipoProducto.getId());
        	tipoProductoRegistration.register(newTipoProducto,listaAtributo);
            FacesMessage m = new FacesMessage(FacesMessage.SEVERITY_INFO, "Registered!", "Registration successful");
            facesContext.addMessage(null, m);
            pushEventSucursal.fire(String.format("Nuevo Tipo Producto Registrado: %s (id: %d)", newTipoProducto.getDescripcion(), newTipoProducto.getId()));
            initNewTipoProducto();
            ArrayList<String> frm=new ArrayList<>();
			frm.add("frm_action");frm.add("frm_list");frm.add("frm"); frm.add("frm_list_atributo");
			FacesUtil.updateComponets(frm);
        } catch (Exception e) {
            String errorMessage = getRootErrorMessage(e);
            FacesMessage m = new FacesMessage(FacesMessage.SEVERITY_ERROR, errorMessage, "Registro Incorrecto.");
            facesContext.addMessage(null, m);
        }
    }
	

	
	public void modificarTipoProducto(){
        try {
        	System.out.println("Ingreso a modificarTipoProducto: "+newTipoProducto.getId());
        	listaAtributo.addAll(listaAtributoDelete);
        	tipoProductoRegistration.updated(newTipoProducto,listaAtributo);
            FacesMessage m = new FacesMessage(FacesMessage.SEVERITY_INFO, "Modificado!", "Modificado successful");
            facesContext.addMessage(null, m);
            pushEventSucursal.fire(String.format("Tipo Producto Modificado: %s (id: %d)", newTipoProducto.getDescripcion(), newTipoProducto.getId()));
            initNewTipoProducto();
            ArrayList<String> frm=new ArrayList<>();
			frm.add("frm_action");frm.add("frm_list");frm.add("frm"); frm.add("frm_list_atributo");
			FacesUtil.updateComponets(frm);
            
        } catch (Exception e) {
            String errorMessage = getRootErrorMessage(e);
            FacesMessage m = new FacesMessage(FacesMessage.SEVERITY_ERROR, errorMessage, "Modificado Incorrecto.");
            facesContext.addMessage(null, m);
        }
    }
	
	public void eliminarTipoProducto(){
        try {
        	System.out.println("Ingreso a eliminarTipoProducto: "+newTipoProducto.getId());
        	tipoProductoRegistration.remover(newTipoProducto);
            FacesMessage m = new FacesMessage(FacesMessage.SEVERITY_INFO, "Borrado!", "Borrado successful");
            facesContext.addMessage(null, m);
            pushEventSucursal.fire(String.format("Servicio Borrado: %s (id: %d)", newTipoProducto.getDescripcion(), newTipoProducto.getId()));
            initNewTipoProducto();
            ArrayList<String> frm=new ArrayList<>();
			frm.add("frm_action");frm.add("frm_list");frm.add("frm"); frm.add("frm_list_atributo");
			FacesUtil.updateComponets(frm);
            
        } catch (Exception e) {
            String errorMessage = getRootErrorMessage(e);
            FacesMessage m = new FacesMessage(FacesMessage.SEVERITY_ERROR, errorMessage, "Borrado Incorrecto.");
            facesContext.addMessage(null, m);
        }
    }
	
	public void nuevoAtributo(){
		nuevoAtributoTipoProducto=new AtributoTipoProducto();
		FacesUtil.updateComponent("frm_atributo");
		FacesUtil.showDialog("wv_dialog_atributo");
	}
	
	public void cancelarNuevoAtributo(){
		nuevoAtributoTipoProducto=new AtributoTipoProducto();
		FacesUtil.updateComponent("frm_list_atributo");
	}
	
	
	public List<String> completeAtributo(String query){
		List<String> resultado=new ArrayList<>();
		for (Atributo atributo : listaAt) {
			if(atributo.getNombre().toUpperCase().startsWith(query.toUpperCase())){
				resultado.add(atributo.getNombre());
			}
		}
		return resultado;
	}
	
	
	public void onRowSelectAtributoClick(SelectEvent event) {
		this.nuevoAtributoTipoProducto=(AtributoTipoProducto) event.getObject();
		System.out.println("Seleccionado atributo:"+ nuevoAtributoTipoProducto.getAtributo());
		this.atributo=nuevoAtributoTipoProducto.getAtributo().getNombre();
	}
	
	public void onItemSelect(SelectEvent event) {
		System.out.println("Seleccionado item: "+ event.getObject());
		atributo=(String) event.getObject();
	}
	
	public void agregarItem(){
		Atributo atributo= atributoRepository.findByParameterObject("nombre", this.atributo);
		if(atributo!=null){
			if(!existeAtributo(atributo)){
				
				nuevoAtributoTipoProducto.setAtributo(atributo);
				listaAtributo.add(nuevoAtributoTipoProducto);
				nuevoAtributoTipoProducto=new AtributoTipoProducto();
				this.atributo="";
				FacesUtil.updateComponent("frm_list_atributo");
				FacesUtil.hideDialog("wv_dialog_atributo");
			}else{
				FacesUtil.warmMessage("El atributo ya se encuentra registrado.!");
			}
		}else{
			FacesUtil.warmMessage("Seleccione un atributo.!");
		}
	}
	
	public void eliminarItem(){
		if(this.nuevoAtributoTipoProducto.getAtributo().getId()!=null){
			for (int i = 0; i < listaAtributo.size(); i++) {
				AtributoTipoProducto atributoTipo=listaAtributo.get(i);
				if(atributoTipo.getAtributo().getId()==nuevoAtributoTipoProducto.getAtributo().getId()){
					listaAtributo.remove(i);
					atributoTipo.setBaja(true);
					listaAtributoDelete.add(atributoTipo);
					nuevoAtributoTipoProducto=new AtributoTipoProducto();
					FacesUtil.updateComponent("frm_list_atributo");
					break;
				}
			}

		}else{
			FacesUtil.warmMessage("Seleccione un atributo.!");
		}
	}
	
	private boolean existeAtributo(Atributo atributo){
		for (AtributoTipoProducto atributoTipoProducto : listaAtributo) {
			if(atributoTipoProducto.getAtributo().getId()==atributo.getId()){
				return true;
			}
		}
		return false;
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
	
	
	//get and set
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

	public TipoProducto getNewTipoProducto() {
		return newTipoProducto;
	}

	public void setNewTipoProducto(TipoProducto newTipoProducto) {
		this.newTipoProducto = newTipoProducto;
	}


	public boolean isNuevo() {
		return nuevo;
	}

	public void setNuevo(boolean nuevo) {
		this.nuevo = nuevo;
	}

	public List<TipoProducto> getListaTipoProducto() {
		return listaTipoProducto;
	}

	public void setListaTipoProducto(List<TipoProducto> listaTipoProducto) {
		this.listaTipoProducto = listaTipoProducto;
	}

	public List<AtributoTipoProducto> getListaAtributo() {
		return listaAtributo;
	}

	public void setListaAtributo(List<AtributoTipoProducto> listaAtributo) {
		this.listaAtributo = listaAtributo;
	}

	public AtributoTipoProducto getNuevoAtributoTipoProducto() {
		return nuevoAtributoTipoProducto;
	}

	public void setNuevoAtributoTipoProducto(AtributoTipoProducto nuevoAtributoTipoProducto) {
		this.nuevoAtributoTipoProducto = nuevoAtributoTipoProducto;
	}

	public List<Atributo> getListaAt() {
		return listaAt;
	}

	public void setListaAt(List<Atributo> listaAt) {
		this.listaAt = listaAt;
	}

	public String getAtributo() {
		return atributo;
	}

	public void setAtributo(String atributo) {
		this.atributo = atributo;
	}

	public List<AtributoTipoProducto> getListaAtributoDelete() {
		return listaAtributoDelete;
	}

	public void setListaAtributoDelete(List<AtributoTipoProducto> listaAtributoDelete) {
		this.listaAtributoDelete = listaAtributoDelete;
	}
	
  
	
	
	
}
