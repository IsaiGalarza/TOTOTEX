package bo.buffalo.controller;

import java.io.IOException;
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

import org.richfaces.cdi.push.Push;

import bo.buffalo.data.DetalleOrdenProduccionRepository;
import bo.buffalo.data.FallaProcesoRepository;
import bo.buffalo.data.OrdenProduccionRepository;
import bo.buffalo.data.UsuarioRepository;
import bo.buffalo.model.Atributo;
import bo.buffalo.model.DetalleOrdenProduccion;
import bo.buffalo.model.FallaProceso;
import bo.buffalo.model.OrdenProduccion;
import bo.buffalo.model.ProcesoCorte;
import bo.buffalo.model.ProcesoEmpaqueFinal;
import bo.buffalo.model.ProcesoLavanderia;
import bo.buffalo.model.Usuario;
import bo.buffalo.service.DetalleOrdenProduccionRegistration;
import bo.buffalo.service.FallaProcesoRegistration;
import bo.buffalo.service.OrdenProduccionRegistration;
import bo.buffalo.service.ProcesoCorteRegistration;
import bo.buffalo.service.ProcesoEmpaqueFinalRegistration;
import bo.buffalo.service.ProcesoLavanderiaRegistration;
import bo.buffalo.util.EstructuraFlujoProceso;
import bo.buffalo.util.SessionMain;

import com.ahosoft.utils.FacesUtil;

/**
 * 
 * @author mauriciobejaranorivera
 *
 */
@Named(value="produccionHomeController")
@ConversationScoped
public class ProduccionHomeController implements Serializable{

	private static final long serialVersionUID = 3597361106153808002L;

	public static final String PUSH_CDI_TOPIC = "pushCdi";

	@Inject
	@Push(topic = PUSH_CDI_TOPIC)
	Event<String> pushEventSucursal;

	@Inject
	Conversation conversation;

	HttpServletRequest request ;

	//REPOSITORY
	private @Inject UsuarioRepository usuarioRepository;
	private @Inject OrdenProduccionRepository ordenProduccionRepository;
	private @Inject DetalleOrdenProduccionRepository detalleOrdenProduccionRepository;
	private @Inject FallaProcesoRepository fallaProcesoRepository;

	//REGISTRATION
	private @Inject OrdenProduccionRegistration ordenProduccionRegistration;
	private @Inject DetalleOrdenProduccionRegistration detalleOrdenProduccionRegistration;
	private @Inject ProcesoCorteRegistration procesoCorteRegistration;
	private @Inject ProcesoLavanderiaRegistration procesoLavanderiaRegistration;
	private @Inject ProcesoEmpaqueFinalRegistration procesoEmpaqueFinalRegistration;
	private @Inject FallaProcesoRegistration fallaProcesoRegistration;

	//ESTADO
	private boolean nuevo; /*Control para renderizar la vista*/
	private boolean modificar; 
	private boolean administrador;
	private boolean detalle;
	private boolean verProceso;
	private boolean verClasePrenda;
	private boolean buttonCargar;
	private boolean buttonObservacion;

	//ESTADO DE LOS PROCESOS

	//OBJECT
	private Atributo newAtributo;
	private Atributo selectedAtributo;
	private OrdenProduccion selectedOrdenProduccion;
	private OrdenProduccion newOrdenProduccion;
	private EstructuraFlujoProceso estructuraFlujoProceso;
	private DetalleOrdenProduccion selectedDetalleOrdenProduccion;
	private FallaProceso fallaProceso;

	//LIST
	private List<OrdenProduccion> listOrdenProduccion;
	private ArrayList<String> frms;

	//VAR
	private String urlVista;
	private String usuario;
	private String password;
	private String clasePrenda;
	private String textTipoProceso;

	//SESION
	private @Inject SessionMain sessionMain;
	private Usuario usuarioSistema;

	@PostConstruct
	public void init(){
		usuarioSistema= usuarioRepository.findByLogin(FacesUtil.getUserSession());
		request = (HttpServletRequest) FacesContext.getCurrentInstance().getExternalContext().getRequest();
		newEntity();

		//si se le paso idOrdenProduccion por session
		//if(){
		//selectedOrdenProduccion = 
		//cargarOrdenProduccion();
		//}
	}

	private void newEntity(){
		fallaProceso = new FallaProceso();
		selectedDetalleOrdenProduccion= new DetalleOrdenProduccion();
		estructuraFlujoProceso = new EstructuraFlujoProceso();
		clasePrenda = "";

		textTipoProceso = "LAVANDERIA";
		buttonObservacion = false;
		nuevo     = true;
		modificar = false;
		detalle   = false;
		verProceso = false;
		verClasePrenda = false;
		buttonCargar= false;

		selectedOrdenProduccion = new OrdenProduccion();
		newOrdenProduccion  = new OrdenProduccion();

		newAtributo = new Atributo();
		newAtributo.setUsuarioRegistro(usuarioSistema);
		newAtributo.setFechaRegistro(new Date());
		selectedAtributo = new Atributo();
		listOrdenProduccion = ordenProduccionRepository.findAllActivos();
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
		return "produccion-home.xhtml?faces-redirect=true";
	}
	
	public void cargarObservacion(){
		System.out.println("cargarObservacion()");
		FacesUtil.showDialog("dlgDetalleObservacion");
	}

	public void nuevaOrdenProduccion(){
		System.out.println("nuevaOrdenProduccion()");
		this.nuevo = false;
		this.verClasePrenda = true;
		this.verProceso = false;
		FacesUtil.updateComponet("form001");
	}

	/* Action */
	public void nuevoEntity(){
		newEntity();
		this.nuevo=true;
		this.modificar=false;
		FacesUtil.updateComponets(frms);
	}

	public void cancelarEntity(){
		this.nuevo=false;
		this.modificar=false;
		FacesUtil.updateComponets(frms);
	}

	public void onRowSelectedOrdenProduccion(){
		System.out.println("onRowSelectedOrdenProduccion()");
		buttonCargar= true;
		nuevo = true;
		if(selectedOrdenProduccion.getEstado().equals("AC")){
			buttonObservacion = true;
		}else{
			buttonObservacion = false;
		}
		FacesUtil.updateComponet("form001");
	}

	/**
	 * Click button Cargar, al seleccionar una orden de produccion de la lista
	 */
	public void cargarOrdenProduccion(){
		System.out.println("cargarOrdenProduccion() ");
		this.nuevo = false;
		this.verClasePrenda = false;
		this.verProceso = true;
		this.clasePrenda = selectedOrdenProduccion.getClasePrenda();

		selectedDetalleOrdenProduccion = detalleOrdenProduccionRepository.findbyOrdenProduccion(selectedOrdenProduccion);
		System.out.println("selectedOrdenProduccion() "+selectedOrdenProduccion.getId());
		System.out.println("selectedDetalleOrdenProduccion() "+selectedDetalleOrdenProduccion.getId());
		//Aqui tiene que verificar y cargar los estados de los procesos
		//====================================================================
		//1.- Verificar Proceso Corte
		cargarEstructuraProcesoCorte();
		//====================================================================
		//2.- Verificar Proceso de Maquilador
		cargarEstructuraProcesoMaquilador();
		//====================================================================
		//3.- Verificar Proceso de lavado
		if(selectedDetalleOrdenProduccion.getProcesoCorte()!=null){
			if(selectedDetalleOrdenProduccion.getProcesoCorte().getEstado().equals("AP")){
				cargarEstructuraProcesoLavado();
			}
		}
		//====================================================================
		//4.- Verificar Proceso de Acabado Final
		if(selectedDetalleOrdenProduccion.getProcesoLavanderia()!=null){
			if(selectedDetalleOrdenProduccion.getProcesoLavanderia().getEstado().equals("AP")){
				cargarEstructuraProcesoEmpaqueFinal();
			}
		}
	}

	private void cargarEstructuraProcesoCorte(){
		System.out.println("cargarEstructuraProcesoLavado() ");
		if(selectedDetalleOrdenProduccion.getProcesoCorte()==null){//verifica si se creo el proceso de corte
			estructuraFlujoProceso.setEstadoProcesoCorte("IN");
			estructuraFlujoProceso.setPorcentajeProcesoCorte(0);//0%
		}else if(selectedDetalleOrdenProduccion.getProcesoCorte().getEstado().equals("AP")){
			estructuraFlujoProceso.setEstadoProcesoCorte("AP");
			estructuraFlujoProceso.setPorcentajeProcesoCorte(100);//100%
			//ficha tecnica
			estructuraFlujoProceso.setEstadoSubProcesoFichaTecnica("AP");
			//insumo corte
			estructuraFlujoProceso.setEstadoSubProcesoInsumoCorte("AP");
		}else{
			estructuraFlujoProceso.setEstadoProcesoCorte("AC");
			//1,1.- Verificar el proceso de la ficha tecnica
			if(selectedDetalleOrdenProduccion.getProcesoCorte().getFichaTecnica()==null){
				estructuraFlujoProceso.setEstadoSubProcesoFichaTecnica("IN");
				estructuraFlujoProceso.setPorcentajeProcesoCorte(0);//0%
			}else if(selectedDetalleOrdenProduccion.getProcesoCorte().getFichaTecnica().getEstado().equals("AC")){
				estructuraFlujoProceso.setEstadoSubProcesoFichaTecnica("AC");
			}else if(selectedDetalleOrdenProduccion.getProcesoCorte().getFichaTecnica().getEstado().equals("AP")){
				estructuraFlujoProceso.setEstadoSubProcesoFichaTecnica("AP");
				estructuraFlujoProceso.setEstadoProcesoCorte("AP");
				estructuraFlujoProceso.setPorcentajeProcesoCorte(50);//50%
				//verificar para cargar insumo de corte
				if(selectedDetalleOrdenProduccion.getProcesoCorte().getFichaCorte()==null){
					estructuraFlujoProceso.setEstadoSubProcesoInsumoCorte("IN");
				}else if(selectedDetalleOrdenProduccion.getProcesoCorte().getFichaCorte().getEstado().equals("AC")){
					estructuraFlujoProceso.setEstadoSubProcesoInsumoCorte("AC");
				}else if(selectedDetalleOrdenProduccion.getProcesoCorte().getFichaCorte().getEstado().equals("AP")){
					estructuraFlujoProceso.setEstadoSubProcesoInsumoCorte("AP");
					estructuraFlujoProceso.setEstadoProcesoCorte("AP");
					estructuraFlujoProceso.setPorcentajeProcesoCorte(100);//100%
				}

			}
		}
	}

	private void cargarEstructuraProcesoMaquilador(){

	}

	private void cargarEstructuraProcesoLavado(){
		System.out.println("cargarEstructuraProcesoLavado() ");
		if(selectedDetalleOrdenProduccion.getProcesoLavanderia() == null ){//verifica si se creo el proceso de 
			estructuraFlujoProceso.setEstadoProcesoLavado("AC");
			estructuraFlujoProceso.setPorcentajeProcesoLavado(0);//0%
			ProcesoLavanderia proceso = new ProcesoLavanderia();
			proceso.setCorrelativo(0);
			proceso.setEstado("AC");
			proceso.setFechaRegistro(new Date());
			proceso.setReciboLavanderia(null);
			proceso.setUsuarioAprobacion(null);
			proceso.setUsuarioRegistro(usuarioSistema.getLogin());
			proceso = procesoLavanderiaRegistration.register(proceso);
			selectedDetalleOrdenProduccion.setProcesoLavanderia(proceso);
			detalleOrdenProduccionRegistration.updated(selectedDetalleOrdenProduccion);			
		}else if(selectedDetalleOrdenProduccion.getProcesoLavanderia().getEstado().equals("AP")){
			estructuraFlujoProceso.setEstadoProcesoLavado("AP");
			estructuraFlujoProceso.setPorcentajeProcesoLavado(100);//100%
			//Recibo
			estructuraFlujoProceso.setEstadoSubProcesoReciboLavanderia("AP");
		}else{
			estructuraFlujoProceso.setEstadoProcesoLavado("AC");
			//1,1.- Verificar el proceso de la ficha tecnica
			if(selectedDetalleOrdenProduccion.getProcesoLavanderia().getReciboLavanderia()==null){
				estructuraFlujoProceso.setEstadoSubProcesoReciboLavanderia("IN");
				estructuraFlujoProceso.setPorcentajeProcesoLavado(0);//0%
				//ReciboLavanderia recibo = new ReciboLavanderia();
				//recibo.set
			}else if(selectedDetalleOrdenProduccion.getProcesoLavanderia().getReciboLavanderia().getEstado().equals("AC")){
				estructuraFlujoProceso.setEstadoSubProcesoReciboLavanderia("AC");
				estructuraFlujoProceso.setPorcentajeProcesoLavado(0);//0%
			}else if(selectedDetalleOrdenProduccion.getProcesoLavanderia().getReciboLavanderia().getEstado().equals("AP")){
				estructuraFlujoProceso.setEstadoSubProcesoReciboLavanderia("AP");
				estructuraFlujoProceso.setEstadoProcesoLavado("AP");
				estructuraFlujoProceso.setPorcentajeProcesoLavado(100);//50%
			}
		}
	}

	private void cargarEstructuraProcesoEmpaqueFinal(){
		System.out.println("cargarEstructuraProcesoEmpaqueFinal() ");
		if(selectedDetalleOrdenProduccion.getProcesoEmpaqueFinal() == null){//verifica si se creo el proceso de corte
			estructuraFlujoProceso.setEstadoProcesoEmpaqueFinal("AC");
			estructuraFlujoProceso.setPorcentajeProcesoEmpaqueFinal(0);//0%
			//crear proceso empaque
			ProcesoEmpaqueFinal procesoEmpaqueFinal = new ProcesoEmpaqueFinal();
			procesoEmpaqueFinal.setAlmacenPrenda(null);
			procesoEmpaqueFinal.setCorrelativo(0);
			procesoEmpaqueFinal.setEstado("AC");
			procesoEmpaqueFinal.setFechaRegistro(new Date());
			procesoEmpaqueFinal.setFichaInsumoAcabado(null);
			procesoEmpaqueFinal.setUsuarioRegistro(usuarioSistema.getLogin());
			procesoEmpaqueFinalRegistration.register(procesoEmpaqueFinal);
			selectedDetalleOrdenProduccion.setProcesoEmpaqueFinal(procesoEmpaqueFinal);
			detalleOrdenProduccionRegistration.updated(selectedDetalleOrdenProduccion);
		}else if(selectedDetalleOrdenProduccion.getProcesoEmpaqueFinal().getEstado().equals("AP")){
			estructuraFlujoProceso.setEstadoProcesoEmpaqueFinal("AP");
			estructuraFlujoProceso.setPorcentajeProcesoEmpaqueFinal(100);//100%
			//insumo final
			estructuraFlujoProceso.setEstadoSubProcesoInsumoFinal("AP");
			//almacen prenda
			estructuraFlujoProceso.setEstadoSubProcesoAlmacenPrenda("AP");
		}else{
			estructuraFlujoProceso.setEstadoProcesoEmpaqueFinal("AC");
			//1,1.- Verificar el proceso de la ficha tecnica
			if(selectedDetalleOrdenProduccion.getProcesoEmpaqueFinal().getFichaInsumoAcabado()==null){
				estructuraFlujoProceso.setEstadoSubProcesoInsumoFinal("IN");
			}else if(selectedDetalleOrdenProduccion.getProcesoEmpaqueFinal().getFichaInsumoAcabado().getEstado().equals("AC")){
				estructuraFlujoProceso.setEstadoSubProcesoInsumoFinal("AC");
			}else if(selectedDetalleOrdenProduccion.getProcesoEmpaqueFinal().getFichaInsumoAcabado().getEstado().equals("AP")){
				estructuraFlujoProceso.setEstadoSubProcesoInsumoFinal("AP");
				estructuraFlujoProceso.setPorcentajeProcesoEmpaqueFinal(50);//50%
				//verificar para cargar insumo de corte
				if(selectedDetalleOrdenProduccion.getProcesoEmpaqueFinal().getAlmacenPrenda() ==null){
					estructuraFlujoProceso.setEstadoSubProcesoAlmacenPrenda("AC");
				}else if(selectedDetalleOrdenProduccion.getProcesoEmpaqueFinal().getAlmacenPrenda().getEstado().equals("AC")){
					estructuraFlujoProceso.setEstadoSubProcesoAlmacenPrenda("AC");
				}else if(selectedDetalleOrdenProduccion.getProcesoEmpaqueFinal().getAlmacenPrenda().getEstado().equals("AP")){
					estructuraFlujoProceso.setEstadoSubProcesoAlmacenPrenda("AP");
					estructuraFlujoProceso.setEstadoProcesoEmpaqueFinal("AP");
					estructuraFlujoProceso.setPorcentajeProcesoEmpaqueFinal(100);//100%
				}
			}
		}
		System.out.println("EstadoProcesoEmpaqueFinal: "+estructuraFlujoProceso.getEstadoProcesoEmpaqueFinal());
		System.out.println("EstadoSubProcesoInsumoFinal: "+estructuraFlujoProceso.getEstadoSubProcesoInsumoFinal());
		System.out.println("EstadoSubProcesoAlmacenPrenda: "+estructuraFlujoProceso.getEstadoSubProcesoAlmacenPrenda());
	}

	/**
	 * Obtiene la clase de prenda seleccionada
	 * @param clasePrenda
	 */
	public void cargarProcesoDeOrdenProduccion(String clasePrenda){
		System.out.println("cargarProcesoDeOrdenProduccion() clasePrenda:"+clasePrenda);
		this.nuevo = false;
		this.verClasePrenda = false;
		this.verProceso = true;
		this.clasePrenda = clasePrenda;
	}

	public void cargarFichaTecnica(){
		sessionMain.setAttributeSession("pIdOrdenProduccion", String.valueOf( selectedOrdenProduccion.getId() ));
		cargarPagina("/pages/produccion/ficha-tecnicaV2.xhtml");
	}
	
	public void cargarInsumoCorte(){
		sessionMain.setAttributeSession("pIdOrdenProduccion", String.valueOf( selectedOrdenProduccion.getId() ));
		cargarPagina("/pages/produccion/ficha-corte.xhtml");
	}

	/**
	 * se establece el proceso de corte, de acuerdo al estado que tenga, se
	 * cargaran los datos
	 */
	public void cargarProcesoCorte(){
		System.out.println("cargarProcesoCorte()");
		//PASOS
		//=====================================================================================
		//1.- Verificar si la orden de produccion es nueva, redireccionar direcamente a cargar la ficha tecnica y termina
		System.out.println("PASO 1.-");
		if(selectedOrdenProduccion.getId() == 0 ){
			//1.1.-Crear instancia de orden produccion
			newOrdenProduccion = new OrdenProduccion();
			newOrdenProduccion.setCorrelativo(1);
			newOrdenProduccion.setEstado("AC");
			newOrdenProduccion.setClasePrenda(this.clasePrenda);
			newOrdenProduccion.setFechaRegistro(new Date());
			newOrdenProduccion.setObservacion("Ninguna");
			newOrdenProduccion.setUsuarioRegistro(usuarioSistema.getLogin());
			newOrdenProduccion = ordenProduccionRegistration.register(newOrdenProduccion);
			System.out.println("newOrdenProduccion: "+newOrdenProduccion.getId());

			//1.2 Crear instancia del procesocorte
			ProcesoCorte procesoCorte = new ProcesoCorte();
			procesoCorte.setCorrelativo(0);
			procesoCorte.setEstado("AC");
			procesoCorte.setFechaRegistro(new Date());
			procesoCorte.setUsuarioRegistro(usuarioSistema.getName());
			procesoCorte.setFichaTecnica(null);
			procesoCorte.setFichaCorte(null);
			procesoCorte.setUsuarioAprobacion(null);
			procesoCorte = procesoCorteRegistration.register(procesoCorte);
			System.out.println("procesoCorte: "+procesoCorte.getId());

			//1.3 Crear instancia del detalle orden produccion
			DetalleOrdenProduccion detalleOP = new DetalleOrdenProduccion();
			detalleOP.setEstado("AC");
			detalleOP.setFechaRegistro(new Date());
			detalleOP.setObservacion("Ninguna"); 
			detalleOP.setProcesoCorte(procesoCorte);
			detalleOP.setProcesoMaquilador(null);
			detalleOP.setProcesoLavanderia(null);
			detalleOP.setProcesoEmpaqueFinal(null);
			detalleOP.setOrdenProduccion(newOrdenProduccion);
			detalleOP.setUsuarioRegistro(usuarioSistema.getLogin()); 
			detalleOP = detalleOrdenProduccionRegistration.register(detalleOP);
			System.out.println("detalleOP: "+detalleOP.getId());

			//1.4 enviar por parametro el id de la orden de produccion
			sessionMain.setAttributeSession("pIdOrdenProduccion", String.valueOf( newOrdenProduccion.getId() ));
			cargarPagina("/pages/produccion/ficha-tecnicaV2.xhtml");
			return;
		}
		//+=================================================================================
		System.out.println("PASO 2.-");
		// 2.- Verificar en que proceso esta.
		sessionMain.setAttributeSession("pIdOrdenProduccion", String.valueOf( selectedOrdenProduccion.getId() ));
		if(selectedDetalleOrdenProduccion.getProcesoCorte().getFichaTecnica()==null){
			cargarPagina("/pages/produccion/ficha-tecnicaV2.xhtml");
		} else if(selectedDetalleOrdenProduccion.getProcesoCorte().getFichaTecnica().getEstado().equals("AC")){
			cargarPagina("/pages/produccion/ficha-tecnicaV2.xhtml");
		}else if(selectedDetalleOrdenProduccion.getProcesoCorte().getFichaTecnica().getEstado().equals("AP")) {
			cargarPagina("/pages/produccion/ficha-corte.xhtml");
		}  
	}

	private void cargarPagina(String page){
		try {
			//http://localhost:8080/buffalo/pages/produccion/produccion-home.xhtml
			String path = request.getContextPath()+page;//"/pages/produccion/ficha-tecnicaV2.xhtml";
			FacesUtil.redirect("http://localhost:8080/buffalo"+page);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void cargarProcesoMaquilador(){
		System.out.println("cargarProcesoMaquilador()");
		sessionMain.setAttributeSession("pIdOrdenProduccion", String.valueOf( selectedOrdenProduccion.getId() ));
	}

	public void cargarProcesoLavado(){
		System.out.println("cargarProcesoLavado()");
		System.out.println("PASO 1.-");
		sessionMain.setAttributeSession("pIdOrdenProduccion", String.valueOf( selectedOrdenProduccion.getId() ));
		cargarPagina("/pages/produccion/lavado.xhtml");
		
	}
	
	public void cargarEmpaque(){
		sessionMain.setAttributeSession("pIdOrdenProduccion", String.valueOf( selectedOrdenProduccion.getId() ));
		cargarPagina("/pages/produccion/empaque.xhtml");
	}
	
	public void cargarAlmacenPrenda(){
		sessionMain.setAttributeSession("pIdOrdenProduccion", String.valueOf( selectedOrdenProduccion.getId() ));
		cargarPagina("/pages/produccion/almacen-prenda.xhtml");
	}

	public void cargarProcesoEmpaque(){
		System.out.println("cargarProcesoEmpaque()");
		sessionMain.setAttributeSession("pIdOrdenProduccion", String.valueOf( selectedOrdenProduccion.getId() ));
		//verifica si cargar empaque o almacen prenda
		if(selectedDetalleOrdenProduccion.getProcesoEmpaqueFinal() == null){
			cargarPagina("/pages/produccion/empaque.xhtml");
			return;
		}
		if(selectedDetalleOrdenProduccion.getProcesoEmpaqueFinal().getFichaInsumoAcabado() == null){
			cargarPagina("/pages/produccion/empaque.xhtml");
		} else if(selectedDetalleOrdenProduccion.getProcesoEmpaqueFinal().getFichaInsumoAcabado().getEstado().equals("AC")){
			cargarPagina("/pages/produccion/empaque.xhtml");
		}else if(selectedDetalleOrdenProduccion.getProcesoEmpaqueFinal().getFichaInsumoAcabado().getEstado().equals("AP")) {
			cargarPagina("/pages/produccion/almacen-prenda.xhtml");
		}
	} 

	/* ------------ Get and Set ---------- */
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

	public Atributo getNewAtributo() {
		return newAtributo;
	}

	public void setNewAtributo(Atributo newAtributo) {
		this.newAtributo = newAtributo;
	}

	public Atributo getSelectedAtributo() {
		return selectedAtributo;
	}

	public void setSelectedAtributo(Atributo selectedAtributo) {
		this.selectedAtributo = selectedAtributo;
	}

	public List<OrdenProduccion> getListOrdenProduccion() {
		return listOrdenProduccion;
	}

	public void setListOrdenProduccion(List<OrdenProduccion> listOrdenProduccion) {
		this.listOrdenProduccion = listOrdenProduccion;
	}

	public OrdenProduccion getSelectedOrdenProduccion() {
		return selectedOrdenProduccion;
	}

	public void setSelectedOrdenProduccion(OrdenProduccion selectedOrdenProduccion) {
		this.selectedOrdenProduccion = selectedOrdenProduccion;
	}

	public boolean isVerProceso() {
		return verProceso;
	}

	public void setVerProceso(boolean verProceso) {
		this.verProceso = verProceso;
	}

	public boolean isVerClasePrenda() {
		return verClasePrenda;
	}

	public void setVerClasePrenda(boolean verClasePrenda) {
		this.verClasePrenda = verClasePrenda;
	}

	public String getClasePrenda() {
		return clasePrenda;
	}

	public void setClasePrenda(String clasePrenda) {
		this.clasePrenda = clasePrenda;
	}

	public OrdenProduccion getNewOrdenProduccion() {
		return newOrdenProduccion;
	}

	public void setNewOrdenProduccion(OrdenProduccion newOrdenProduccion) {
		this.newOrdenProduccion = newOrdenProduccion;
	}

	public boolean isButtonCargar() {
		return buttonCargar;
	}

	public void setButtonCargar(boolean buttonCargar) {
		this.buttonCargar = buttonCargar;
	}

	public EstructuraFlujoProceso getEstructuraFlujoProceso() {
		return estructuraFlujoProceso;
	}

	public void setEstructuraFlujoProceso(EstructuraFlujoProceso estructuraFlujoProceso) {
		this.estructuraFlujoProceso = estructuraFlujoProceso;
	}

	public DetalleOrdenProduccion getSelectedDetalleOrdenProduccion() {
		return selectedDetalleOrdenProduccion;
	}

	public void setSelectedDetalleOrdenProduccion(
			DetalleOrdenProduccion selectedDetalleOrdenProduccion) {
		this.selectedDetalleOrdenProduccion = selectedDetalleOrdenProduccion;
	}

	public FallaProceso getFallaProceso() {
		return fallaProceso;
	}

	public void setFallaProceso(FallaProceso fallaProceso) {
		this.fallaProceso = fallaProceso;
	}

	public String getTextTipoProceso() {
		return textTipoProceso;
	}

	public void setTextTipoProceso(String textTipoProceso) {
		this.textTipoProceso = textTipoProceso;
	}

	public boolean isButtonObservacion() {
		return buttonObservacion;
	}

	public void setButtonObservacion(boolean buttonObservacion) {
		this.buttonObservacion = buttonObservacion;
	}

}
