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
import bo.buffalo.data.OrdenProduccionRepository;
import bo.buffalo.data.UsuarioRepository;
import bo.buffalo.model.Atributo;
import bo.buffalo.model.DetalleOrdenProduccion;
import bo.buffalo.model.OrdenProduccion;
import bo.buffalo.model.ProcesoCorte;
import bo.buffalo.model.Usuario;
import bo.buffalo.service.DetalleOrdenProduccionRegistration;
import bo.buffalo.service.OrdenProduccionRegistration;
import bo.buffalo.service.ProcesoCorteRegistration;
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

	//REGISTRATION
	private @Inject OrdenProduccionRegistration ordenProduccionRegistration;
	private @Inject DetalleOrdenProduccionRegistration detalleOrdenProduccionRegistration;
	private @Inject ProcesoCorteRegistration procesoCorteRegistration;

	//ESTADO
	private boolean nuevo; /*Control para renderizar la vista*/
	private boolean modificar; 
	private boolean administrador;
	private boolean detalle;
	private boolean verProceso;
	private boolean verClasePrenda;
	private boolean buttonCargar;

	//ESTADO DE LOS PROCESOS

	//OBJECT
	private Atributo newAtributo;
	private Atributo selectedAtributo;
	private OrdenProduccion selectedOrdenProduccion;
	private OrdenProduccion newOrdenProduccion;
	private EstructuraFlujoProceso estructuraFlujoProceso;
	private DetalleOrdenProduccion selectedDetalleOrdenProduccion;

	//LIST
	private List<OrdenProduccion> listOrdenProduccion;
	private ArrayList<String> frms;

	//VAR
	private String urlVista;
	private String usuario;
	private String password;
	private String clasePrenda;

	//SESION
	private @Inject SessionMain sessionMain;
	private Usuario usuarioSistema;

	@PostConstruct
	public void init(){
		usuarioSistema= usuarioRepository.findByLogin(FacesUtil.getUserSession());
		request = (HttpServletRequest) FacesContext.getCurrentInstance().getExternalContext().getRequest();
		newEntity();
	}

	private void newEntity(){
		selectedDetalleOrdenProduccion= new DetalleOrdenProduccion();
		estructuraFlujoProceso = new EstructuraFlujoProceso();
		clasePrenda = "";

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
		return "orden_ingreso.xhtml?faces-redirect=true";
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
		//Aqui tiene que verificar y cargar los estados de los procesos
		//====================================================================
		//1.- Verificar Proceso Corte
		cargarEstructuraProcesoCorte();
		//====================================================================
		//2.- Verificar Proceso de Maquilador
		cargarEstructuraProcesoMaquilador();
		//====================================================================
		//3.- Verificar Proceso de lavado
		cargarEstructuraProcesoLavado();
		//====================================================================
		//4.- Verificar Proceso de Acabado Final
		cargarEstructuraProcesoEmpaqueFinal();
	}

	private void cargarEstructuraProcesoCorte(){
		if(selectedDetalleOrdenProduccion.getProcesoCorte()==null){//verifica si se creo el proceso de corte
			estructuraFlujoProceso.setEstadoProcesoCorte("IN");
			estructuraFlujoProceso.setPorcentajeProcesoCorte(0);//0%
		}else if(selectedDetalleOrdenProduccion.getProcesoCorte().getEstado().equals("PR")){
			estructuraFlujoProceso.setEstadoProcesoCorte("PR");
			estructuraFlujoProceso.setPorcentajeProcesoCorte(25);//25%
			//ficha tecnica
			estructuraFlujoProceso.setEstadoSubProcesoFichaTecnica("PR");
		}else{
			estructuraFlujoProceso.setEstadoProcesoCorte("AC");
			//1,1.- Verificar el proceso de la ficha tecnica
			if(selectedDetalleOrdenProduccion.getProcesoCorte().getFichaTecnica()==null){
				estructuraFlujoProceso.setEstadoSubProcesoFichaTecnica("IN");
			}else if(selectedDetalleOrdenProduccion.getProcesoCorte().getFichaTecnica().getEstado().equals("PR")){
				estructuraFlujoProceso.setEstadoSubProcesoFichaTecnica("PR");
			}else{
				estructuraFlujoProceso.setEstadoSubProcesoFichaTecnica("AC");
			}
		}
	}

	private void cargarEstructuraProcesoMaquilador(){

	}

	private void cargarEstructuraProcesoLavado(){

	}

	private void cargarEstructuraProcesoEmpaqueFinal(){

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
		if(selectedOrdenProduccion.getId()==0){
			//1.1.-Crear instancia de orden produccion
			newOrdenProduccion = new OrdenProduccion();
			newOrdenProduccion.setCorrelativo(1);
			newOrdenProduccion.setEstado("AC");
			newOrdenProduccion.setClasePrenda(this.clasePrenda);
			newOrdenProduccion.setFechaRegistro(new Date());
			newOrdenProduccion.setObservacion("Ninguna");
			newOrdenProduccion.setUsuarioRegistro(usuarioSistema.getLogin());
			newOrdenProduccion = ordenProduccionRegistration.register(newOrdenProduccion);

			//1.2 Crear instancia del procesocorte
			ProcesoCorte procesoCorte = new ProcesoCorte();
			procesoCorte.setCorrelativo(0);
			procesoCorte.setEstado("AC");
			procesoCorte.setFechaRegistro(new Date());
			procesoCorte.setUsuarioRegistro(usuarioSistema.getName());
			procesoCorte = procesoCorteRegistration.register(procesoCorte);

			//1.3 Crear instancia del detalle orden produccion
			DetalleOrdenProduccion detalleOP = new DetalleOrdenProduccion();
			detalleOP.setEstado("AC");
			detalleOP.setFechaRegistro(new Date());
			detalleOP.setObservacion("Ninguna");  
			detalleOP.setOrdenProduccion(newOrdenProduccion);
			detalleOP.setUsuarioRegistro(usuarioSistema.getLogin());
			detalleOP.setProcesoCorte(procesoCorte);
			detalleOP = detalleOrdenProduccionRegistration.register(detalleOP);

			//1.4 enviar por parametro el id de la orden de produccion
			sessionMain.setAttributeSession("pIdOrdenProduccion", String.valueOf( newOrdenProduccion.getId() ));
			try {
				String path = request.getContextPath()+"/pages/produccion/ficha-tecnica.xhtml";
				FacesUtil.redirect(path);
			} catch (IOException e) {
				e.printStackTrace();
			}
			return;
		}
		//+=================================================================================
		System.out.println("PASO 2.-");
		// 2.- Verificar en que proceso esta.
		//2.1 Verificar estado ficha tecnica
		sessionMain.setAttributeSession("pIdOrdenProduccion", String.valueOf( selectedOrdenProduccion.getId() ));
		try {
			String path = request.getContextPath()+"/pages/produccion/ficha-tecnica.xhtml";
			FacesUtil.redirect(path);
		} catch (IOException e) {
			e.printStackTrace();
		}
		//2.2 Verificar 


		System.out.println("PASO 3.-");
		// 3.-  
	}

	public void cargarProcesoMaquilador(){
		System.out.println("cargarProcesoMaquilador()");
		sessionMain.setAttributeSession("pIdOrdenProduccion", String.valueOf( selectedOrdenProduccion.getId() ));
	}

	public void cargarProcesoLavado(){
		System.out.println("cargarProcesoLavado()");
		sessionMain.setAttributeSession("pIdOrdenProduccion", String.valueOf( selectedOrdenProduccion.getId() ));
	}

	public void cargarProcesoEmpaque(){
		System.out.println("cargarProcesoEmpaque()");
		sessionMain.setAttributeSession("pIdOrdenProduccion", String.valueOf( selectedOrdenProduccion.getId() ));
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

}
