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

import bo.buffalo.data.DetalleOrdenProduccionRepository;
import bo.buffalo.data.DetalleReciboLavanderiaRepository;
import bo.buffalo.data.FichaDetalleProductoRepository;
import bo.buffalo.data.LavanderiaRepository;
import bo.buffalo.data.OrdenProduccionRepository;
import bo.buffalo.data.ProcesoLavanderiaRepository;
import bo.buffalo.model.DetalleOrdenProduccion;
import bo.buffalo.model.DetalleReciboLavanderia;
import bo.buffalo.model.FichaDetalleProducto;
import bo.buffalo.model.Lavanderia;
import bo.buffalo.model.OrdenProduccion;
import bo.buffalo.model.ProcesoLavanderia;
import bo.buffalo.model.ReciboLavanderia;
import bo.buffalo.model.Usuario;
import bo.buffalo.service.DetalleOrdenProduccionRegistration;
import bo.buffalo.service.DetalleReciboLavanderiaRegistration;
import bo.buffalo.service.LavanderiaRegistration;
import bo.buffalo.service.OrdenProduccionRegistration;
import bo.buffalo.service.ProcesoLavanderiaRegistration;
import bo.buffalo.service.ReciboLavanderiaRegistration;
import bo.buffalo.util.SessionMain;

@Named(value = "lavadoController")
@ConversationScoped
public class LavadoController implements Serializable{

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
	private @Inject LavanderiaRepository lavanderiaRepository;
	private @Inject ProcesoLavanderiaRepository procesoLavanderiaRepository;
	private @Inject FichaDetalleProductoRepository fichaDetalleProductoRepository;
	private @Inject DetalleReciboLavanderiaRepository detalleReciboLavanderiaRepository;

	//REGISTRATION
	private @Inject LavanderiaRegistration lavanderiaRegistration;
	private @Inject ReciboLavanderiaRegistration reciboLavanderiaRegistration;
	private @Inject ProcesoLavanderiaRegistration procesoLavanderiaRegistration;
	private @Inject DetalleReciboLavanderiaRegistration detalleReciboLavanderiaRegistration;
	private @Inject DetalleOrdenProduccionRegistration detalleOrdenProduccionRegistration;
	private @Inject OrdenProduccionRegistration ordenProduccionRegistration;

	//ESTADO
	private boolean nuevo; /*Control para renderizar la vista*/
	private boolean verLista;
	private boolean aprobar;
	private boolean buttonCargarFichaTecnica;
	private boolean buttonVerLista;
	private boolean modificar; 
	private boolean administrador;
	private boolean detalle;
	private boolean imprimir;

	//OBJECT
	private ReciboLavanderia newReciboLavanderia;
	private Lavanderia selectedLavanderia;
	//LIST
	private List<Lavanderia> listLavanderia;
	private List<DetalleReciboLavanderia> listDetalleReciboLavanderia;
	private List<FichaDetalleProducto> listaFichaProducto;

	//VAR
	private String codigoPrenda;
	private String titulo;
	private String urlVista;
	private String textoAutoCompleteLavanderia;

	//SESION
	private @Inject SessionMain sessionMain; //variable del login
	private String usuario;
	private String password;
	private Usuario usuarioSistema;

	//ORDEN PRODUCCION
	private OrdenProduccion selectedOrdenProduccion;
	private DetalleOrdenProduccion selectedDetalleOrdenProduccion;

	HttpServletRequest request ;

	@PostConstruct
	public void init(){
		System.out.println("init() - Lavado");
		request = (HttpServletRequest) FacesContext.getCurrentInstance().getExternalContext().getRequest();
		usuarioSistema = sessionMain.getUsuarioSistema();
		administrador = sessionMain.isAdministrador();

		nuevo = true;
		aprobar = false;
		imprimir = false;

		textoAutoCompleteLavanderia = "";

		newReciboLavanderia = new ReciboLavanderia();
		selectedLavanderia = new Lavanderia();
		
		listDetalleReciboLavanderia = new ArrayList<>();
		listaFichaProducto = new ArrayList<>();

		listLavanderia = lavanderiaRepository.findAllOrderedByNombre();

		String pIdOrdenProduccion = sessionMain.getAttributeSession("pIdOrdenProduccion");
		System.out.println("pIdOrdenProduccion:"+pIdOrdenProduccion);
		if(pIdOrdenProduccion != null){
			//cargar la orden de produccion
			selectedOrdenProduccion = ordenProduccionRepository.findById(Integer.parseInt(pIdOrdenProduccion));
			System.out.println("selectedOrdenProduccion :"+selectedOrdenProduccion.getId());
			//cargar el detalle orden de produccion
			selectedDetalleOrdenProduccion = detalleOrdenProduccionRepository.findbyOrdenProduccion(selectedOrdenProduccion);
			//VERIFICAR:  SI YA SE CREO
			if(selectedDetalleOrdenProduccion.getProcesoLavanderia() == null){
				cargarDetalleReciboLavanderia();
			}else if(selectedDetalleOrdenProduccion.getProcesoLavanderia().getReciboLavanderia() == null){
				nuevo = true;
				aprobar = false;
				imprimir = false;
				cargarDetalleReciboLavanderia();
			}else if(selectedDetalleOrdenProduccion.getProcesoLavanderia().getReciboLavanderia().getEstado().equals("AC")){
				nuevo = false;
				aprobar = true;
				imprimir = true;
				newReciboLavanderia =selectedDetalleOrdenProduccion.getProcesoLavanderia().getReciboLavanderia();
				selectedLavanderia  = newReciboLavanderia.getLavanderia();
				textoAutoCompleteLavanderia = selectedLavanderia.getNombre();
				
				listDetalleReciboLavanderia = detalleReciboLavanderiaRepository.findByReciboLavanderia(newReciboLavanderia);
				if(listDetalleReciboLavanderia.size()==0){
					cargarDetalleReciboLavanderia();
				}
			}else{
				nuevo = false;
				aprobar = false;
				imprimir = true;
				newReciboLavanderia =selectedDetalleOrdenProduccion.getProcesoLavanderia().getReciboLavanderia();
				selectedLavanderia  = newReciboLavanderia.getLavanderia();
				textoAutoCompleteLavanderia = selectedLavanderia.getNombre();
				
				listDetalleReciboLavanderia = detalleReciboLavanderiaRepository.findByReciboLavanderia(newReciboLavanderia);
			}
		}
	}
	
	private void cargarDetalleReciboLavanderia(){
		System.out.println("cargarDetalleReciboLavanderia()");
		listaFichaProducto = fichaDetalleProductoRepository.findByFichaCorte(selectedDetalleOrdenProduccion.getProcesoCorte().getFichaCorte());
		System.out.println("listaFichaProducto.size: "+listaFichaProducto.size());
		for(FichaDetalleProducto ficha: listaFichaProducto){
			DetalleReciboLavanderia detalle = new DetalleReciboLavanderia();
			detalle.setCantidadVerificada(0);
			detalle.setEstado("AC");
			detalle.setFechaRegistro(new Date());
			detalle.setFichaDetalleProducto(ficha);
			detalle.setReciboLavanderia(newReciboLavanderia);
			detalle.setUsuarioRegistro(usuarioSistema.getLogin());
			listDetalleReciboLavanderia.add(detalle);
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
		return "lavado.xhtml?faces-redirect=true";
	}

	public void registrarReciboEnvio(){
		if(this.textoAutoCompleteLavanderia.trim().isEmpty()){
			FacesUtil.warmMessage("Ingrese un Confeccionista.");
			return;
		}else{
			if(this.selectedLavanderia.getId()==0){
				selectedLavanderia = new Lavanderia();
				selectedLavanderia.setNombre(textoAutoCompleteLavanderia);
				selectedLavanderia.setDireccion("Sin especificar");
				selectedLavanderia.setEstado("AC");
				selectedLavanderia.setFechaRegistro(new Date());
				selectedLavanderia.setTelefono("0");
				selectedLavanderia.setUsuarioRegistro(usuarioSistema.getLogin());
				selectedLavanderia = lavanderiaRegistration.register(selectedLavanderia);
			}
		}
		try{
			//registration
			//newReciboLavanderia = new ReciboLavanderia();
			newReciboLavanderia.setCantidad(0);
			newReciboLavanderia.setCostoTotal(0);
			newReciboLavanderia.setCostoUnitario(0);
			newReciboLavanderia.setEstado("AC");
			newReciboLavanderia.setFechaRegistro(new Date());
			newReciboLavanderia.setLavanderia(selectedLavanderia);
			newReciboLavanderia.setNombre("Nombre Ninguno");
			newReciboLavanderia.setPrenda("Prenda Ninguno");
			newReciboLavanderia.setServicio("Servicio Ninguno");
			newReciboLavanderia.setUsuarioRegistro(usuarioSistema.getLogin());
			newReciboLavanderia = reciboLavanderiaRegistration.register(newReciboLavanderia);
			System.out.println("newReciboLavanderia.id: "+newReciboLavanderia.getId());
			//Proceso
			ProcesoLavanderia proceso = new ProcesoLavanderia();
			proceso.setCorrelativo(0);
			proceso.setEstado("AC");
			proceso.setFechaRegistro(new Date());
			proceso.setUsuarioRegistro(usuarioSistema.getLogin());
			proceso.setReciboLavanderia(newReciboLavanderia);
			proceso.setUsuarioAprobacion(null);
			proceso = procesoLavanderiaRegistration.register(proceso);
			System.out.println("proceso.id: "+proceso.getId());
			//Detalle
			selectedDetalleOrdenProduccion.setProcesoLavanderia(proceso);
			detalleOrdenProduccionRegistration.updated(selectedDetalleOrdenProduccion);
			
			//list detalle Recibo lavanderia
			System.out.println("listDetalleReciboLavanderia.size: "+listDetalleReciboLavanderia.size());
			for(DetalleReciboLavanderia recibo : listDetalleReciboLavanderia){
				recibo.setReciboLavanderia(newReciboLavanderia);
				detalleReciboLavanderiaRegistration.register(recibo);
				System.out.println("recibo.id: "+recibo.getId());
			}

			//enviando los parametros necesarios para cargar el controller
			//String path = request.getContextPath()+"/pages/produccion/produccion-home.xhtml";
			//FacesUtil.redirect(path);
			
			String page = "/pages/produccion/produccion-home.xhtml";
			FacesUtil.redirect("http://localhost:8080/buffalo"+page);
		}catch(Exception e){
			e.printStackTrace();
			System.out.println("Error: "+e.getMessage());
		}
	}

	public void aprobarReciboEnvio(){
		try{
			newReciboLavanderia.setEstado("AP");
			reciboLavanderiaRegistration.updated(newReciboLavanderia);
			
			//aprobar proceso de corte
			ProcesoLavanderia proceso = selectedDetalleOrdenProduccion.getProcesoLavanderia();
			proceso.setEstado("AP");
			procesoLavanderiaRegistration.updated(proceso);
			//String path = request.getContextPath()+"/pages/produccion/produccion-home.xhtml";
			//FacesUtil.redirect(path);
			selectedOrdenProduccion.setProcesoActual("EMPAQUE");
			selectedOrdenProduccion.setPorcentajeTotal(60);
			ordenProduccionRegistration.updated(selectedOrdenProduccion);
			
			String page = "/pages/produccion/produccion-home.xhtml";
			FacesUtil.redirect("http://localhost:8080/buffalo"+page);
		}catch(Exception e){
			System.out.println("aprobarFichaInsumoCorte() Error: "+e.getMessage());
		}
	}
	
	public void imprimirRecibo(){
		System.out.println("imprimirRecibo()");
		armarURLVista();
		FacesUtil.updateComponent("formModalVistaPrevia");
		FacesUtil.showDialog("dlgVistaPrevia");
	}

	//autocomplete lavanderia
	public List<String> completeLavanderia(String query) {
		String upperQuery = query.toUpperCase();
		List<String> results = new ArrayList<String>();
		for (Lavanderia i : listLavanderia) {
			if(i.getNombre().toUpperCase().startsWith(upperQuery)){
				results.add(i.getNombre());
			}
		}
		return results;
	}

	public void onRowSelectLavanderiaClick(SelectEvent event) {
		String nombre =  event.getObject().toString();
		for (Lavanderia i : listLavanderia) {
			if(i.getNombre().equals(nombre)){
				selectedLavanderia = i;
				//textoAutoCompleteConfeccionista = i.getNombre();
				return;
			}
		}
	}

	//@SuppressWarnings("deprecation")
	public void armarURLVista(){
		try {
			System.out.println("Ingreso a armarURL..."); 
			HttpServletRequest request = (HttpServletRequest) FacesContext.getCurrentInstance().getExternalContext().getRequest();  
			String urlPath = request.getRequestURL().toString();
			urlPath = urlPath.substring(0, urlPath.length() - request.getRequestURI().length()) + request.getContextPath() + "/";
			System.out.println("urlPath >> "+urlPath);
			urlVista = urlPath+"ReportReciboLavanderia?pId="+newReciboLavanderia.getId()+"&pUsuario="+usuarioSistema.getLogin();
			System.out.println("URL Reporte Vista: "+urlVista);

		} catch (Exception e) {
			System.out.println("Error en armarURLVista: "+e.getMessage());
		}
	}


	/* Get and Set */
	public boolean isNuevo() {
		return nuevo;
	}

	public void setNuevo(boolean nuevo) {
		this.nuevo = nuevo;
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

	public ReciboLavanderia getNewReciboLavanderia() {
		return newReciboLavanderia;
	}

	public void setNewReciboLavanderia(ReciboLavanderia newReciboLavanderia) {
		this.newReciboLavanderia = newReciboLavanderia;
	}

	public Lavanderia getSelectedLavanderia() {
		return selectedLavanderia;
	}

	public void setSelectedLavanderia(Lavanderia selectedLavanderia) {
		this.selectedLavanderia = selectedLavanderia;
	}

	public String getTextoAutoCompleteLavanderia() {
		return textoAutoCompleteLavanderia;
	}

	public void setTextoAutoCompleteLavanderia(
			String textoAutoCompleteLavanderia) {
		this.textoAutoCompleteLavanderia = textoAutoCompleteLavanderia;
	}

	public List<Lavanderia> getListLavanderia() {
		return listLavanderia;
	}

	public void setListLavanderia(List<Lavanderia> listLavanderia) {
		this.listLavanderia = listLavanderia;
	}

	public boolean isAprobar() {
		return aprobar;
	}

	public void setAprobar(boolean aprobar) {
		this.aprobar = aprobar;
	}

	public List<DetalleReciboLavanderia> getListDetalleReciboLavanderia() {
		return listDetalleReciboLavanderia;
	}

	public void setListDetalleReciboLavanderia(
			List<DetalleReciboLavanderia> listDetalleReciboLavanderia) {
		this.listDetalleReciboLavanderia = listDetalleReciboLavanderia;
	}

	public List<FichaDetalleProducto> getListaFichaProducto() {
		return listaFichaProducto;
	}

	public void setListaFichaProducto(List<FichaDetalleProducto> listaFichaProducto) {
		this.listaFichaProducto = listaFichaProducto;
	}

	public boolean isImprimir() {
		return imprimir;
	}

	public void setImprimir(boolean imprimir) {
		this.imprimir = imprimir;
	}

}
