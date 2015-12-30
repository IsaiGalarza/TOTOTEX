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

import org.richfaces.cdi.push.Push;

import bo.buffalo.data.OrdenProduccionRepository;
import bo.buffalo.data.UsuarioRepository;
import bo.buffalo.model.Atributo;
import bo.buffalo.model.OrdenProduccion;
import bo.buffalo.model.Usuario;
import bo.buffalo.util.SessionMain;

import com.ahosoft.utils.FacesUtil;

/**
 * 
 * @author mauriciobejaranorivera
 *
 */
@Named(value="ordenProduccionController")
@ConversationScoped
public class OrdenProduccionController implements Serializable{

	private static final long serialVersionUID = 4407471120290597394L;

	public static final String PUSH_CDI_TOPIC = "pushCdi";

	@Inject
	@Push(topic = PUSH_CDI_TOPIC)
	Event<String> pushEventSucursal;

	@Inject
	Conversation conversation;

	//REPOSITORY
	private @Inject UsuarioRepository usuarioRepository;
	private @Inject OrdenProduccionRepository ordenProduccionRepository;

	//REGISTRATION

	//ESTADO
	private boolean nuevo; /*Control para renderizar la vista*/
	private boolean modificar; 
	private boolean administrador;
	private boolean detalle;
	private boolean verClasePrenda;
	private boolean verProceso;

	//OBJECT
	private Atributo newAtributo;
	private Atributo selectedAtributo;
	private OrdenProduccion selectedOrdenProduccion;

	//LIST
	private List<OrdenProduccion> listOrdenProduccion;
	private ArrayList<String> frms;

	//VAR
	private String urlVista;
	private String usuario;
	private String password;
	private String clasePrenda;
	private String codigoPrenda;

	//SESION
	private @Inject SessionMain sessionMain;
	private Usuario usuarioSistema;

	@PostConstruct
	public void init(){
		usuarioSistema= usuarioRepository.findByLogin(FacesUtil.getUserSession());
		newEntity();
	}

	private void newEntity(){
		clasePrenda = "";
		codigoPrenda = "";
		
		nuevo     = true;
		modificar = false;
		detalle   = false;
		verClasePrenda = false;
		verProceso = false;

		selectedOrdenProduccion = new OrdenProduccion();

		newAtributo = new Atributo();
		newAtributo.setUsuarioRegistro(usuarioSistema);
		newAtributo.setFechaRegistro(new Date());
		selectedAtributo = new Atributo();
		listOrdenProduccion = ordenProduccionRepository.findAllActivos();
		
		String pIdOrdenProduccion = sessionMain.getAttributeSession("pIdOrdenProduccion");
		System.out.println("- pIdOrdenProduccion:"+pIdOrdenProduccion+" -");
		if(pIdOrdenProduccion != null){
			if(pIdOrdenProduccion.equals("0")){
				verClasePrenda = true;
				verProceso = false;
				return;
			}
			verClasePrenda = false;
			verProceso = true;
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
		return "orden_ingreso.xhtml?faces-redirect=true";
	}

	public void nuevaOrdenProduccion(){
		System.out.println("nuevaOrdenProduccion()");
		//sessionMain.setAttributeSession("pIdOrdenProduccion", String.valueOf( selectedOrdenProduccion.getId() ));
		this.nuevo = false;
		this.verClasePrenda = true;
		//FacesUtil.updateComponet("form001");
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
	}
	
	public void cargarProcesoDeOrdenProduccion(String clasePrenda){
		System.out.println("cargarProcesoDeOrdenProduccion() clasePrenda:"+clasePrenda);
		this.nuevo = false;
		this.verClasePrenda = false;
		this.clasePrenda = clasePrenda;
		this.verProceso = true;
		//sessionMain.setAttributeSession("pIdOrdenProduccion", String.valueOf( selectedOrdenProduccion.getId() ));
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

	public boolean isVerProceso() {
		return verProceso;
	}

	public void setVerProceso(boolean verProceso) {
		this.verProceso = verProceso;
	}

	public String getCodigoPrenda() {
		return codigoPrenda;
	}

	public void setCodigoPrenda(String codigoPrenda) {
		this.codigoPrenda = codigoPrenda;
	}

}
