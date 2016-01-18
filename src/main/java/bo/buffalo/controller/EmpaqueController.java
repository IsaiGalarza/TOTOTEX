package bo.buffalo.controller;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.enterprise.context.Conversation;
import javax.enterprise.context.ConversationScoped;
import javax.enterprise.event.Event;
import javax.faces.context.FacesContext;
import javax.inject.Inject;
import javax.inject.Named;
import javax.servlet.http.HttpServletRequest;

import org.primefaces.event.SelectEvent;
import org.richfaces.cdi.push.Push;

import com.ahosoft.utils.FacesUtil;

import bo.buffalo.data.AlmacenProductoRepository;
import bo.buffalo.data.DetalleOrdenProduccionRepository;
import bo.buffalo.data.FichaDetalleInsumoAcabadoRepository;
import bo.buffalo.data.OrdenProduccionRepository;
import bo.buffalo.data.ProductoRepository;
import bo.buffalo.model.AlmProducto;
import bo.buffalo.model.DetalleOrdenProduccion;
import bo.buffalo.model.FichaDetalleInsumoAcabado;
import bo.buffalo.model.FichaInsumoAcabado;
import bo.buffalo.model.FichaTecnica;
import bo.buffalo.model.OrdenProduccion;
import bo.buffalo.model.ProcesoEmpaqueFinal;
import bo.buffalo.model.Producto;
import bo.buffalo.model.Usuario;
import bo.buffalo.service.FichaDetalleInsumoAcabadoRegistration;
import bo.buffalo.service.FichaInsumoAcabadoRegistration;
import bo.buffalo.service.OrdenProduccionRegistration;
import bo.buffalo.service.ProcesoEmpaqueFinalRegistration;
import bo.buffalo.util.SessionMain;

@Named(value = "empaqueController")
@ConversationScoped
public class EmpaqueController implements Serializable{

	private static final long serialVersionUID = -3756873687377670050L;

	public static final String PUSH_CDI_TOPIC = "pushCdi";

	@Inject
	Conversation conversation;

	@Inject
	@Push(topic = PUSH_CDI_TOPIC)
	Event<String> pushEventObject;

	//REPOSITORY
	private @Inject OrdenProduccionRepository ordenProduccionRepository;
	private @Inject DetalleOrdenProduccionRepository detalleOrdenProduccionRepository;
	private @Inject AlmacenProductoRepository almacenProductoRepository;
	private @Inject ProductoRepository productoRepository;
	private @Inject FichaDetalleInsumoAcabadoRepository fichaDetalleInsumoAcabadoRepository;

	//REGISTRATION
	private @Inject FichaInsumoAcabadoRegistration fichaInsumoAcabadoRegistration;
	private @Inject ProcesoEmpaqueFinalRegistration procesoEmpaqueFinalRegistration;
	private @Inject FichaDetalleInsumoAcabadoRegistration fichaDetalleInsumoAcabadoRegistration;
	private @Inject OrdenProduccionRegistration ordenProduccionRegistration;

	//ESTADO
	private boolean nuevo; /*Control para renderizar la vista*/
	private boolean verLista;
	private boolean aprobarFichaTecnica;
	private boolean buttonCargarFichaTecnica;
	private boolean buttonVerLista;
	private boolean modificar; 
	private boolean administrador;
	private boolean detalle;
	private boolean aprobar;

	//ORDEN PRODUCCION
	private OrdenProduccion selectedOrdenProduccion;
	private DetalleOrdenProduccion selectedDetalleOrdenProduccion;

	//OBJECT
	private FichaInsumoAcabado newFichaInsumoAcabado;
	private FichaDetalleInsumoAcabado fichaInsumoAcabado;
	private FichaDetalleInsumoAcabado selectedFichaDetalleInsumoAcabado;

	//LIST
	private List<FichaDetalleInsumoAcabado> listaFichaInsumoAcabado;
	private List<Producto> listaProductoInsumo;
	private List<FichaDetalleInsumoAcabado> deleteListaFichaInsumoAcabado;

	//VAR
	private String codigoPrenda;
	private String textConfeccionista;
	private FichaTecnica entity;
	private String titulo;
	private String urlVista;

	//SESION
	private @Inject SessionMain sessionMain; //variable del login
	private String usuario;
	private String password;
	private Usuario usuarioSistema;

	//ORDEN PRODUCCION

	HttpServletRequest request ;

	@PostConstruct
	public void init(){
		System.out.println("init() - Empaque");
		request = (HttpServletRequest) FacesContext.getCurrentInstance().getExternalContext().getRequest();
		usuarioSistema = sessionMain.getUsuarioSistema();
		administrador = sessionMain.isAdministrador();

		selectedFichaDetalleInsumoAcabado = new FichaDetalleInsumoAcabado();
		fichaInsumoAcabado = new FichaDetalleInsumoAcabado();
		newFichaInsumoAcabado = new FichaInsumoAcabado();

		listaFichaInsumoAcabado = new ArrayList<>();
		deleteListaFichaInsumoAcabado = new ArrayList<>();

		cargarListaProductoInsumo();

		String pIdOrdenProduccion = sessionMain.getAttributeSession("pIdOrdenProduccion");
		System.out.println("pIdOrdenProduccion:"+pIdOrdenProduccion);
		if(pIdOrdenProduccion != null){
			//cargar la orden de produccion
			selectedOrdenProduccion = ordenProduccionRepository.findById(Integer.parseInt(pIdOrdenProduccion));
			System.out.println("selectedOrdenProduccion :"+selectedOrdenProduccion.getId());
			//cargar el detalle orden de produccion
			selectedDetalleOrdenProduccion = detalleOrdenProduccionRepository.findbyOrdenProduccion(selectedOrdenProduccion);
			//VERIFICAR:  SI YA SE CREO
			if(selectedDetalleOrdenProduccion.getProcesoEmpaqueFinal().getFichaInsumoAcabado() == null){
				nuevo = true ;
				aprobar = false ;
			}else if(selectedDetalleOrdenProduccion.getProcesoEmpaqueFinal().getFichaInsumoAcabado().getEstado().equals("AC")){
				nuevo = false ;
				aprobar = true ;
				newFichaInsumoAcabado = selectedDetalleOrdenProduccion.getProcesoEmpaqueFinal().getFichaInsumoAcabado();
				listaFichaInsumoAcabado = fichaDetalleInsumoAcabadoRepository.findAllByFichaInsumoAcabado(newFichaInsumoAcabado);
			}else{
				nuevo = false ;
				aprobar = false ;
				newFichaInsumoAcabado = selectedDetalleOrdenProduccion.getProcesoEmpaqueFinal().getFichaInsumoAcabado();
				listaFichaInsumoAcabado = fichaDetalleInsumoAcabadoRepository.findAllByFichaInsumoAcabado(newFichaInsumoAcabado);
			}
		}
	}

	private void cargarListaProductoInsumo(){
		listaProductoInsumo=new ArrayList<>();
		if(usuarioSistema.getAlmacen()!=null){
			List<AlmProducto> ap=almacenProductoRepository.findProductosForAlmacen(usuarioSistema.getAlmacen());
			for (AlmProducto almProducto : ap) {
				listaProductoInsumo.add(almProducto.getProducto());
			}
		}
	}

	public void initConversation() {
		if (!FacesContext.getCurrentInstance().isPostback() && conversation.isTransient()) {
			conversation.begin();
			System.out.println(">>>>>>>>>> CONVERSACION INICIADA...");
		}
	}

	public String endConversation() {
		if (!conversation.isTransient()) {
			conversation.end();
			System.out.println(">>>>>>>>>> CONVERSACION TERMINADA...");
		}
		return "ficha-tecnica.xhtml?faces-redirect=true";
	}

	public void armarURLVista(){
		try {
			System.out.println("Ingreso a armarURLVentasNSF..."); 
			HttpServletRequest request = (HttpServletRequest) FacesContext.getCurrentInstance().getExternalContext().getRequest();  
			String urlPath = request.getRequestURL().toString();
			urlPath = urlPath.substring(0, urlPath.length() - request.getRequestURI().length()) + request.getContextPath() + "/";
			System.out.println("urlPath >> "+urlPath);
			urlVista = urlPath+"ficha-tecnica?pId="+entity.getId();
			System.out.println("URL Reporte Vista: "+urlVista);

		} catch (Exception e) {
			System.out.println("Error en armarURLVista: "+e.getMessage());
		}
	}

	public void registrarEmpaque(){
		System.out.println("registrarEmpaque");
		try{
			newFichaInsumoAcabado.setEstado("AC");
			newFichaInsumoAcabado.setFechaRegistro(new Date());
			newFichaInsumoAcabado.setUsuarioRegistro(usuarioSistema.getLogin());
			newFichaInsumoAcabado= fichaInsumoAcabadoRegistration.register(newFichaInsumoAcabado);
			
			for(FichaDetalleInsumoAcabado detalle: listaFichaInsumoAcabado){
				detalle.setFichaInsumoAcabado(newFichaInsumoAcabado);
				detalle.setEstado("AC");
				fichaDetalleInsumoAcabadoRegistration.register(detalle);
			}

			ProcesoEmpaqueFinal proceso = selectedDetalleOrdenProduccion.getProcesoEmpaqueFinal();
			proceso.setFichaInsumoAcabado(newFichaInsumoAcabado);
			procesoEmpaqueFinalRegistration.updated(proceso);
			
			//enviando los parametros necesarios para cargar el controller
			String page = "/pages/produccion/produccion-home.xhtml";
			FacesUtil.redirect("http://localhost:8080/buffalo"+page);
		}catch(Exception e){
			System.out.println("Error "+e.getMessage());
		}
	}

	public void aprobarEmpaque(){
		System.out.println("aprobarEmpaque()");
		try{
			newFichaInsumoAcabado.setEstado("AP");
			fichaInsumoAcabadoRegistration.updated(newFichaInsumoAcabado);
			//enviando los parametros necesarios para cargar el controller
			selectedOrdenProduccion.setPorcentajeTotal(80);
			ordenProduccionRegistration.updated(selectedOrdenProduccion);
			String page = "/pages/produccion/produccion-home.xhtml";
			FacesUtil.redirect("http://localhost:8080/buffalo"+page);
		}catch(Exception e){
			System.out.println("Error "+e.getMessage());
		}
	}

	public void onRowSelectFichaDetalleInsumoAcabado(SelectEvent event) {
		System.out.println("Seleccionado insumo acabado: "+event.getObject());
	}

	public void agregarInsumoAcabado(){
		if(this.usuarioSistema.getAlmacen()!=null){
			System.out.println("Almacen: "+usuarioSistema.getAlmacen());
			this.fichaInsumoAcabado = new FichaDetalleInsumoAcabado();
			FacesUtil.updateComponent("frm_list");
			FacesUtil.showDialog("wv_dlg_insumo_acabado");
		}else{
			FacesUtil.warmMessage("No tiene asignano un almacen, Contactese con el administrador.!");
		}
	}

	public void modificarInsumoAcabado(){
		if(this.usuarioSistema.getAlmacen()!=null){
			if(fichaInsumoAcabado!=null){
				if(fichaInsumoAcabado.getProducto().getId()!=null){
					//FacesUtil.updateComponent("frm_insumo_acabado");
					FacesUtil.showDialog("wv_dlg_insumo_acabado");
				}else{
					FacesUtil.warmMessage("Seleccione un detalle.!");
				}
			}else{
				FacesUtil.warmMessage("Seleccione un detalle.!");
			}
		}else{
			FacesUtil.warmMessage("No tiene asignano un almacen, Contactese con el administrador.!");
		}
	}

	public void eliminarInsumoAcabado(){
		if(this.usuarioSistema.getAlmacen()!=null){
			if(fichaInsumoAcabado!=null){
				if(fichaInsumoAcabado.getProducto().getId()!=null){
					FacesUtil.showDialog("wv_delete_insumo_acabado");
				}else{
					FacesUtil.warmMessage("Seleccione un detalle.!");
				}
			}else{
				FacesUtil.warmMessage("Seleccione un detalle.!");
			}
		}else{
			FacesUtil.warmMessage("No tiene asignano un almacen, Contactese con el administrador.!");
		}
	}

	public List<Producto> completeProducto(String query){
		List<Producto> result= new ArrayList<>();
		for (Producto producto : listaProductoInsumo) {
			if(producto.getNombreProducto().toUpperCase().startsWith(query.toUpperCase())){
				result.add(producto);
			}
		}
		return result;
	}

	public void onItemSelectAcabado(SelectEvent event) {
		System.out.println("Insumo acabado: "+event.getObject());
		Producto pr = ((Producto)event.getObject());
		pr = productoRepository.findById(pr.getId());
		if(pr!=null){
			System.out.println("Producto: "+pr);
			if(fichaInsumoAcabado==null){
				fichaInsumoAcabado=new FichaDetalleInsumoAcabado();
			}
			fichaInsumoAcabado.setProducto(pr);
		}
	}

	public void accionAgregarInsumoAcabado(){
		if(validarInsumoAcabo()){
			fichaInsumoAcabado.setProducto(productoRepository.findById(fichaInsumoAcabado.getProducto().getId()));
			listaFichaInsumoAcabado.add(fichaInsumoAcabado);
			fichaInsumoAcabado=new FichaDetalleInsumoAcabado();
			FacesUtil.updateComponent("frm_list");
			//FacesUtil.updateComponent("frm_insumo_acabado");
		}
	}

	private boolean validarInsumoAcabo(){
		if(fichaInsumoAcabado.getProducto().getId()!=null &&fichaInsumoAcabado.getProducto().getId()>0){
			if(listaFichaInsumoAcabado==null){
				listaFichaInsumoAcabado=new ArrayList<>();
				return true;
			}
			if(listaFichaInsumoAcabado.size()>0){
				for (FichaDetalleInsumoAcabado fc : listaFichaInsumoAcabado) {
					if(fc.getProducto().getId()==fichaInsumoAcabado.getProducto().getId()){
						if(fc.getId()==fichaInsumoAcabado.getId()){
							fc=fichaInsumoAcabado;
							fichaInsumoAcabado=new FichaDetalleInsumoAcabado();
							FacesUtil.infoMessage("Modificado correctamente.!");
							FacesUtil.updateComponent("frm_list");
							//FacesUtil.updateComponent("frm_insumo_acabado");
							return false;
						}else{
							FacesUtil.warmMessage("Ya se agrego el insumo.!");
							return false;
						}
					}
				}
			}
		}else{
			FacesUtil.warmMessage("Seleccione un producto.!");
			return false;
		}
		return true;
	}

	public void accionEliminarInsumoAcabado(){
		for (int i = 0; i < listaFichaInsumoAcabado.size(); i++) {
			FichaDetalleInsumoAcabado fa = listaFichaInsumoAcabado.get(i);
			if(fa.getProducto().getId() == fichaInsumoAcabado.getProducto().getId()){
				listaFichaInsumoAcabado.remove(i);
				if(fa.getId()!=null){
					fa.setBaja(true);
					deleteListaFichaInsumoAcabado.add(fa);
				}
				FacesUtil.updateComponent("frm_list");
				FacesUtil.hideDialog("wv_delete_insumo_acabado");
				FacesUtil.infoMessage("Registro Eliminado.!");
				break;
			}
		}
	}

	/* Get and Set */
	public boolean isNuevo() {
		return nuevo;
	}
	public void setNuevo(boolean nuevo) {
		this.nuevo = nuevo;
	}
	public FichaTecnica getEntity() {
		return entity;
	}
	public void setEntity(FichaTecnica entity) {
		this.entity = entity;
	}

	public Usuario getUsuarioSistema() {
		return usuarioSistema;
	}

	public void setUsuarioSistema(Usuario usuarioSistema) {
		this.usuarioSistema = usuarioSistema;
	}

	public boolean isModificar() {
		return modificar;
	}

	public void setModificar(boolean modificar) {
		this.modificar = modificar;
	}

	public String getTitulo() {
		return titulo;
	}

	public void setTitulo(String titulo) {
		this.titulo = titulo;
	}

	public boolean isAdministrador() {
		return administrador;
	}

	public void setAdministrador(boolean administrador) {
		this.administrador = administrador;
	}

	public boolean isDetalle() {
		return detalle;
	}

	public void setDetalle(boolean detalle) {
		this.detalle = detalle;
	}


	public String getUrlVista() {
		return urlVista;
	}

	public void setUrlVista(String urlVista) {
		this.urlVista = urlVista;
	}

	public String getUsuario() {
		return usuario;
	}

	public void setUsuario(String usuario) {
		this.usuario = usuario;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public String getTextConfeccionista() {
		return textConfeccionista;
	}

	public void setTextConfeccionista(String textConfeccionista) {
		this.textConfeccionista = textConfeccionista;
	}

	public String getCodigoPrenda() {
		return codigoPrenda;
	}

	public void setCodigoPrenda(String codigoPrenda) {
		this.codigoPrenda = codigoPrenda;
	}

	public boolean isVerLista() {
		return verLista;
	}

	public void setVerLista(boolean verLista) {
		this.verLista = verLista;
	}

	public boolean isAprobarFichaTecnica() {
		return aprobarFichaTecnica;
	}

	public void setAprobarFichaTecnica(boolean aprobarFichaTecnica) {
		this.aprobarFichaTecnica = aprobarFichaTecnica;
	}

	public boolean isButtonCargarFichaTecnica() {
		return buttonCargarFichaTecnica;
	}

	public void setButtonCargarFichaTecnica(boolean buttonCargarFichaTecnica) {
		this.buttonCargarFichaTecnica = buttonCargarFichaTecnica;
	}

	public boolean isButtonVerLista() {
		return buttonVerLista;
	}

	public void setButtonVerLista(boolean buttonVerLista) {
		this.buttonVerLista = buttonVerLista;
	}

	public boolean isAprobar() {
		return aprobar;
	}

	public void setAprobar(boolean aprobar) {
		this.aprobar = aprobar;
	}

	public FichaInsumoAcabado getNewFichaInsumoAcabado() {
		return newFichaInsumoAcabado;
	}

	public void setNewFichaInsumoAcabado(FichaInsumoAcabado newFichaInsumoAcabado) {
		this.newFichaInsumoAcabado = newFichaInsumoAcabado;
	}

	public List<FichaDetalleInsumoAcabado> getListaFichaInsumoAcabado() {
		return listaFichaInsumoAcabado;
	}

	public void setListaFichaInsumoAcabado(List<FichaDetalleInsumoAcabado> listaFichaInsumoAcabado) {
		this.listaFichaInsumoAcabado = listaFichaInsumoAcabado;
	}

	public FichaDetalleInsumoAcabado getSelectedFichaDetalleInsumoAcabado() {
		return selectedFichaDetalleInsumoAcabado;
	}

	public void setSelectedFichaDetalleInsumoAcabado(
			FichaDetalleInsumoAcabado selectedFichaDetalleInsumoAcabado) {
		this.selectedFichaDetalleInsumoAcabado = selectedFichaDetalleInsumoAcabado;
	}

	public FichaDetalleInsumoAcabado getFichaInsumoAcabado() {
		return fichaInsumoAcabado;
	}

	public void setFichaInsumoAcabado(FichaDetalleInsumoAcabado fichaInsumoAcabado) {
		this.fichaInsumoAcabado = fichaInsumoAcabado;
	}

	public List<Producto> getListaProductoInsumo() {
		return listaProductoInsumo;
	}

	public void setListaProductoInsumo(List<Producto> listaProductoInsumo) {
		this.listaProductoInsumo = listaProductoInsumo;
	}

}
