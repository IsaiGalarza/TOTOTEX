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

import bo.buffalo.data.DetalleAlmacenPrendaRepository;
import bo.buffalo.data.AlmacenProductoRepository;
import bo.buffalo.data.AlmacenRepository;
import bo.buffalo.data.DetalleOrdenProduccionRepository;
import bo.buffalo.data.FichaDetalleProductoRepository;
import bo.buffalo.data.OrdenProduccionRepository;
import bo.buffalo.model.Almacen;
import bo.buffalo.model.AlmacenPrenda;
import bo.buffalo.model.DetalleAlmacenPrenda;
import bo.buffalo.model.DetalleOrdenProduccion;
import bo.buffalo.model.FichaDetalleProducto;
import bo.buffalo.model.FichaTecnica;
import bo.buffalo.model.OrdenProduccion;
import bo.buffalo.model.ProcesoEmpaqueFinal;
import bo.buffalo.model.Usuario;
import bo.buffalo.service.AlmacenPrendaFallaRegistration;
import bo.buffalo.service.AlmacenPrendaRegistration;
import bo.buffalo.service.OrdenProduccionRegistration;
import bo.buffalo.service.ProcesoEmpaqueFinalRegistration;
import bo.buffalo.util.SessionMain;

@Named(value = "almacenPrendaController")
@ConversationScoped
public class AlmacenPrendaController implements Serializable{

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
	private @Inject AlmacenRepository almacenRepository;
	private @Inject FichaDetalleProductoRepository fichaDetalleProductoRepository;
	private @Inject DetalleAlmacenPrendaRepository almacenPrendaFallaRepository;

	//REGISTRATION
	private @Inject OrdenProduccionRegistration ordenProduccionRegistration;
	private @Inject ProcesoEmpaqueFinalRegistration procesoEmpaqueFinalRegistration;
	
	//ESTADO
	private boolean nuevo; /*Control para renderizar la vista*/
	private boolean verLista;
	private boolean aprobarFichaTecnica;
	private boolean buttonVerLista;
	private boolean modificar; 
	private boolean administrador;
	private boolean detalle;
	private boolean aprobar;

	//ORDEN PRODUCCION
	private OrdenProduccion selectedOrdenProduccion;
	private DetalleOrdenProduccion selectedDetalleOrdenProduccion;

	//OBJECT
	private Almacen selectedAlmacen;
	private AlmacenPrenda newAlmacenPrenda;
	private DetalleAlmacenPrenda selectedAlmacenPrendaFalla;

	//LIST
	private List<Almacen> listAlmacen;
	private List<FichaDetalleProducto> listaFichaProducto;
	private List<DetalleAlmacenPrenda> listDetalleAlmacenPrenda;

	//VAR
	private String codigoPrenda;
	private String textConfeccionista;
	private FichaTecnica entity;
	private String titulo;
	private String urlVista;
	private String textoAutoCompleteAlmacen;
	private Date fechaActual;

	//SESION
	private @Inject SessionMain sessionMain; //variable del login
	private String usuario;
	private String password;
	private Usuario usuarioSistema;

	//ORDEN PRODUCCION

	HttpServletRequest request ;

	@PostConstruct
	public void init(){
		System.out.println("init() - Almacen");
		request = (HttpServletRequest) FacesContext.getCurrentInstance().getExternalContext().getRequest();
		usuarioSistema = sessionMain.getUsuarioSistema();
		administrador = sessionMain.isAdministrador();

		listAlmacen = almacenRepository.findAllOrderedByID();
		textoAutoCompleteAlmacen = "";
		
		newAlmacenPrenda = new AlmacenPrenda();
		selectedAlmacen = new Almacen();
		selectedAlmacenPrendaFalla = new DetalleAlmacenPrenda();//null;

		listaFichaProducto = new ArrayList<>();
		listDetalleAlmacenPrenda = new ArrayList<>();
		fechaActual = new Date();

		String pIdOrdenProduccion = sessionMain.getAttributeSession("pIdOrdenProduccion");
		System.out.println("pIdOrdenProduccion:"+pIdOrdenProduccion);
		if(pIdOrdenProduccion != null){
			//cargar la orden de produccion
			selectedOrdenProduccion = ordenProduccionRepository.findById(Integer.parseInt(pIdOrdenProduccion));
			System.out.println("selectedOrdenProduccion :"+selectedOrdenProduccion.getId());
			//cargar el detalle orden de produccion
			selectedDetalleOrdenProduccion = detalleOrdenProduccionRepository.findbyOrdenProduccion(selectedOrdenProduccion);
			//VERIFICAR:  SI YA SE CREO
			if(selectedDetalleOrdenProduccion.getProcesoEmpaqueFinal().getAlmacenPrenda() == null){
				nuevo   = true ;
				aprobar = false ;
				listaFichaProducto = fichaDetalleProductoRepository.findByFichaCorte(selectedDetalleOrdenProduccion.getProcesoCorte().getFichaCorte()); //obtener por ficha
				System.out.println("1 - listaFichaProducto.size: "+listaFichaProducto.size());
				cargarNewDetalleAlmacenPrenda();
			}else if(selectedDetalleOrdenProduccion.getProcesoEmpaqueFinal().getAlmacenPrenda().getEstado().equals("AC")){
				nuevo   = false ;
				aprobar = true ;
				newAlmacenPrenda = selectedDetalleOrdenProduccion.getProcesoEmpaqueFinal().getAlmacenPrenda();
				selectedAlmacen = newAlmacenPrenda.getAlmacen();
				textoAutoCompleteAlmacen = selectedAlmacen.getNombre();
				listDetalleAlmacenPrenda = almacenPrendaFallaRepository.findByAlmacenPrenda(newAlmacenPrenda);
				//listaFichaProducto = fichaDetalleProductoRepository.findByFichaCorte(selectedDetalleOrdenProduccion.getProcesoCorte().getFichaCorte()); //
				System.out.println("2 - listaFichaProducto.size: "+listDetalleAlmacenPrenda.size());
				if(listDetalleAlmacenPrenda.size()==0){
					listaFichaProducto = fichaDetalleProductoRepository.findByFichaCorte(selectedDetalleOrdenProduccion.getProcesoCorte().getFichaCorte()); 
					cargarNewDetalleAlmacenPrenda();
				}				
			}else{
				nuevo = false ;
				aprobar = false ;
				newAlmacenPrenda = selectedDetalleOrdenProduccion.getProcesoEmpaqueFinal().getAlmacenPrenda();
				selectedAlmacen = newAlmacenPrenda.getAlmacen();
				textoAutoCompleteAlmacen = selectedAlmacen.getNombre();
				listDetalleAlmacenPrenda = almacenPrendaFallaRepository.findByAlmacenPrenda(newAlmacenPrenda);
				//listaFichaProducto = fichaDetalleProductoRepository.findByFichaCorte(selectedDetalleOrdenProduccion.getProcesoCorte().getFichaCorte()); //
				System.out.println("3 - listaFichaProducto.size: "+listDetalleAlmacenPrenda.size());
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

	private void cargarNewDetalleAlmacenPrenda(){
		System.out.println("cargarNewAlmacenPrendaFalla() size: "+listaFichaProducto.size());
		for(FichaDetalleProducto ficha: listaFichaProducto){
			DetalleAlmacenPrenda prenda = new DetalleAlmacenPrenda();
			prenda.setEstado("AC");
			prenda.setFechaRegistro(new Date());
			prenda.setFichaDetalleProducto(ficha);
			prenda.setUsuarioRegistro(usuarioSistema.getLogin());
			listDetalleAlmacenPrenda.add(prenda);
		}
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
	
	private @Inject AlmacenPrendaFallaRegistration almacenPrendaFallaRegistration;
	private @Inject AlmacenPrendaRegistration almacenPrendaRegistration;

	public void registrarAlmacenPrenda(){
		System.out.println("registrarAlmacenPrenda()");
		if(selectedAlmacen.getId()==0){
			FacesUtil.infoMessage("Seleccione un Almacen");
			return;
		}
		try{
			Date fechaActual = new Date();
			newAlmacenPrenda.setEstado("AC");
			newAlmacenPrenda.setFechaRegistro(fechaActual);
			newAlmacenPrenda.setUsuarioRegistro(usuarioSistema.getLogin());
			newAlmacenPrenda.setAlmacen(selectedAlmacen);
			newAlmacenPrenda = almacenPrendaRegistration.register(newAlmacenPrenda);
			for(DetalleAlmacenPrenda prenda: listDetalleAlmacenPrenda){
				prenda.setAlmacenPrenda(newAlmacenPrenda);
				prenda.setEstado("AC");
				prenda.setFechaRegistro(fechaActual);
				prenda.setUsuarioRegistro(usuarioSistema.getLogin());
				almacenPrendaFallaRegistration.register(prenda);
			}
			ProcesoEmpaqueFinal empaque = selectedDetalleOrdenProduccion.getProcesoEmpaqueFinal();
			empaque.setAlmacenPrenda(newAlmacenPrenda);
			procesoEmpaqueFinalRegistration.updated(empaque);
			//enviando los parametros necesarios para cargar el controller
			//String path = request.getContextPath()+"/pages/produccion/produccion-home.xhtml";
			//FacesUtil.redirect(path);
			String page = "/pages/produccion/produccion-home.xhtml";
			FacesUtil.redirect("http://localhost:8080/buffalo"+page);
		}catch(Exception e){
			System.out.println("Error "+e.getMessage());
		}
	}

	public void aprobarAlmacenPrenda(){
		System.out.println("aprobarAlmacenPrenda()");
		try{
			Date fechaActual = new Date();
			newAlmacenPrenda.setEstado("AP");
			newAlmacenPrenda.setFechaAprobacion(fechaActual);
			almacenPrendaRegistration.updated(newAlmacenPrenda);
			finalizarOrdenProduccion();
			//enviando los parametros necesarios para cargar el controller
			//String path = request.getContextPath()+"/pages/produccion/produccion-home.xhtml";
			//FacesUtil.redirect(path);
			String page = "/pages/produccion/produccion-home.xhtml";
			FacesUtil.redirect("http://localhost:8080/buffalo"+page);
		}catch(Exception e){
			System.out.println("Error "+e.getMessage());
		}
	}

	private void finalizarOrdenProduccion(){
		selectedOrdenProduccion.setEstado("AP");
		selectedOrdenProduccion.setPorcentajeTotal(100);
		selectedOrdenProduccion.setProcesoActual("FINAL");
		selectedOrdenProduccion.setFechaAprobacion(new Date());
		ordenProduccionRegistration.updated(selectedOrdenProduccion);
	}

	//autocomplete almacen
	public List<String> completeAlmacen(String query) {
		String upperQuery = query.toUpperCase();
		List<String> results = new ArrayList<String>();
		for (Almacen i : listAlmacen) {
			if(i.getNombre().toUpperCase().startsWith(upperQuery)){
				results.add(i.getNombre());
			}
		}
		return results;
	}

	public void onRowSelectAlmacenClick(SelectEvent event) {
		String nombre =  event.getObject().toString();
		for (Almacen i : listAlmacen) {
			if(i.getNombre().equals(nombre)){
				selectedAlmacen = i;
				//textoAutoCompleteConfeccionista = i.getNombre();
				return;
			}
		}
	}

	public void onRowSelectAlmacenPrendaFalla(SelectEvent event) {
		System.out.println("onRowSelectAlmacenPrendaFalla: "+event.getObject());
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

	public List<Almacen> getListAlmacen() {
		return listAlmacen;
	}

	public void setListAlmacen(List<Almacen> listAlmacen) {
		this.listAlmacen = listAlmacen;
	}

	public String getTextoAutoCompleteAlmacen() {
		return textoAutoCompleteAlmacen;
	}

	public void setTextoAutoCompleteAlmacen(String textoAutoCompleteAlmacen) {
		this.textoAutoCompleteAlmacen = textoAutoCompleteAlmacen;
	}

	public Almacen getSelectedAlmacen() {
		return selectedAlmacen;
	}

	public void setSelectedAlmacen(Almacen selectedAlmacen) {
		this.selectedAlmacen = selectedAlmacen;
	}

	public Date getFechaActual() {
		return fechaActual;
	}

	public void setFechaActual(Date fechaActual) {
		this.fechaActual = fechaActual;
	}

	public List<FichaDetalleProducto> getListaFichaProducto() {
		return listaFichaProducto;
	}

	public void setListaFichaProducto(List<FichaDetalleProducto> listaFichaProducto) {
		this.listaFichaProducto = listaFichaProducto;
	}

	public List<DetalleAlmacenPrenda> getListDetalleAlmacenPrenda() {
		return listDetalleAlmacenPrenda;
	}

	public void setListDetalleAlmacenPrenda(List<DetalleAlmacenPrenda> listDetalleAlmacenPrenda) {
		this.listDetalleAlmacenPrenda = listDetalleAlmacenPrenda;
	}

	public AlmacenPrenda getNewAlmacenPrenda() {
		return newAlmacenPrenda;
	}

	public void setNewAlmacenPrenda(AlmacenPrenda newAlmacenPrenda) {
		this.newAlmacenPrenda = newAlmacenPrenda;
	}

	public DetalleAlmacenPrenda getSelectedAlmacenPrendaFalla() {
		return selectedAlmacenPrendaFalla;
	}

	public void setSelectedAlmacenPrendaFalla(DetalleAlmacenPrenda selectedAlmacenPrendaFalla) {
		this.selectedAlmacenPrendaFalla = selectedAlmacenPrendaFalla;
	}

}
