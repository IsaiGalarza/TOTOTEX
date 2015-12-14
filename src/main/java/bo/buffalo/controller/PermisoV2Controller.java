package bo.buffalo.controller;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import javax.faces.context.FacesContext;
import javax.inject.Inject;
import javax.servlet.http.HttpServletRequest;

import org.primefaces.event.SelectEvent;
import org.primefaces.model.DefaultTreeNode;
import org.primefaces.model.TreeNode;

import bo.buffalo.data.RolesRepository;
import bo.buffalo.data.UsuarioRepository;
import bo.buffalo.model.Modulo;
import bo.buffalo.model.Permiso;
import bo.buffalo.model.Privilegio;
import bo.buffalo.model.Roles;
import bo.buffalo.model.Usuario;
import bo.buffalo.util.EDPermiso;
import bo.buffalo.model.UsuarioRol;
import bo.buffalo.data.ModuloRepository;
import bo.buffalo.data.PermisoRepository;
import bo.buffalo.data.PrivilegioRepository;
import bo.buffalo.service.PrivilegioRegistration;
import bo.buffalo.data.UsuarioRolRepository;

import com.ahosoft.utils.FacesUtil;

@ManagedBean
@SessionScoped
public class PermisoV2Controller implements Serializable{
	
	private static final long serialVersionUID = 4124982403628644372L;
	
	@Inject
	private FacesContext facesContext;

	private @Inject UsuarioRepository usuarioRepository;
	private @Inject RolesRepository rolesRepository;
	private @Inject ModuloRepository moduloRepository;
	private @Inject PermisoRepository permisoReppository;
	private @Inject PrivilegioRepository privilegioRepository;
	
	private @Inject UsuarioRolRepository usuerRolRepository;
	
	private @Inject PrivilegioRegistration privilegioRegistration;

	private Usuario usuarioSistema;
	private boolean nuevo; /*Control para renderizar la vista*/
	private boolean modificar; 
	
	//OBJECT
	private Roles newRoles;
	private Roles selectedRoles;
	
	//
	private String tituloPanel;
	
	//LIST
	private List<Roles> listaRoles = new ArrayList<Roles>();
	private List<Permiso> listPermiso = new ArrayList<Permiso>();
	private List<Modulo> listModulo = new ArrayList<Modulo>();
	private List<Privilegio> listPrivilegio = new ArrayList<Privilegio>();
	
	//tree
	private TreeNode root;
	private TreeNode[] selectedNodes;
	private String selectionModeTreeNode;

	
	@PostConstruct
	public void init(){
		setUsuarioSistema(usuarioRepository.findByLogin(FacesUtil.getUserSession()));
		
		this.nuevo=false;
		
		listModulo = moduloRepository.findAllOrderByID();
		listPermiso = permisoReppository.findAllOrderByName();
		listaRoles = rolesRepository.findAllOrderedByName();
		loadDefault();
	}
	
	private void loadDefault(){
		selectedRoles = new Roles();
		selectionModeTreeNode = "none";
		cargarPermisos();
	}
	
	/**
	 * cargar todos los permisos por defecto no seleccionados 
	 */
	private void cargarPermisos(){
		root = new DefaultTreeNode(new EDPermiso(0, "",null, null,false), null);
		for(Modulo mod : listModulo){
			TreeNode tn1 = new DefaultTreeNode(new EDPermiso(0, mod.getNombre(),mod, null,false),root);
			tn1.setExpanded(true);
			tn1.setSelected(false);
			List<Permiso> listPermmiso = obtenerPermisoByModulo(mod);
			for(Permiso p: listPermmiso){
				TreeNode tn2 = new DefaultTreeNode("1",new EDPermiso(0,p.getNombre(), mod,p,false),tn1);
				tn2.setExpanded(true);
				tn1.setSelected(false);
			}
		}
	}
	
	/**
	 * cargar todos los permisos por privilegio 
	 */
	private void cargarPermisosConPrivilegios(){
		root = new DefaultTreeNode(new EDPermiso(0, "",null, null,false), null);
		for(Modulo mod : listModulo){
			TreeNode tn1 = new DefaultTreeNode(new EDPermiso(0, mod.getNombre(),mod, null,false),root);
			tn1.setExpanded(true);
			tn1.setSelected(existModulo(mod));
			List<Permiso> listPermmiso = obtenerPermisoByModulo(mod);
			for(Permiso p: listPermmiso){
				TreeNode tn2 = new DefaultTreeNode("1",new EDPermiso(0,p.getNombre(), mod,p,false),tn1);
				tn2.setExpanded(true);
				tn2.setSelected(existPermiso(p));
			}
		}
	}

	private boolean existPermiso(Permiso permiso){
		for(Privilegio p : listPrivilegio){
			if(p.getPermiso().equals(permiso)){
				return true;
			}
		}
		return false;
	}

	private boolean existModulo(Modulo modulo){
		for(Privilegio p : listPrivilegio){
			if(p.getPermiso().getModulo().equals(modulo)){
				return true;
			}
		}
		return false;
	}

	private List<Permiso> obtenerPermisoByModulo(Modulo modulo){
		List<Permiso> listPermisoAux = new ArrayList<Permiso>();
		for(Permiso p: this.listPermiso){
			if(p.getModulo().equals(modulo)){
				listPermisoAux.add(p);
			}
		}
		return listPermisoAux;
	}

	public void onRowSelectRol(SelectEvent event) {
		modificar = true ;
		selectionModeTreeNode = "checkbox";
		listPrivilegio = privilegioRepository.findAllByRol(selectedRoles);
		cargarPermisosConPrivilegios();
	}

	public void guardar(){
		try{
			System.out.println("guardar");
			System.out.println("guardar length "+selectedNodes.length);
			List<Privilegio> listPrivilegio = privilegioRepository.findAllByRol(selectedRoles);
			for(Privilegio p : listPrivilegio){
				p.setFechaModificacion(new Date());
				privilegioRegistration.remover(p);
			}
			for(TreeNode t: selectedNodes){
				EDPermiso e = (EDPermiso)t.getData();
				System.out.println("EDPermiso : "+e.getNombre());
				if(e.getPermiso()!=null){
					Privilegio privilegio = new Privilegio();
					privilegio.setEstado("AC");
					privilegio.setFechaRegistro(new Date());
					privilegio.setPermiso(e.getPermiso());
					privilegio.setRoles(selectedRoles);
					privilegioRegistration.register(privilegio);
				}
			}
			if(selectedNodes.length>0){
				//verificar si el rol modificado es el loggeado, para cargar los nuevos datos
				if(isRolUsuarioLogin(selectedRoles)){
					FacesMessage m = new FacesMessage(FacesMessage.SEVERITY_INFO,
							"Permisos Registrados!", "total "+selectedNodes.length+ "   Los cambios se aplicaran despues de reiniciar la Sesion");
					facesContext.addMessage(null, m);
				}else{
				FacesMessage m = new FacesMessage(FacesMessage.SEVERITY_INFO,
						"Permisos Registrados!", "total "+selectedNodes.length);
				facesContext.addMessage(null, m);
				}
			}
		}catch(Exception e){
			System.out.println("guardar ERROR: "+e.getMessage());
		}
	}
	
	private boolean isRolUsuarioLogin(Roles roles){
		UsuarioRol userRolesAux = usuerRolRepository.findByUsuario(getUsuarioSession());
		if(roles.getId()==userRolesAux.getRoles().getId()){
			System.out.println("isRolUsuarioLogin -> true");
			return true;
		}
		System.out.println("isRolUsuarioLogin -> false");
		return false;
	}

	public Usuario getUsuarioSession() {
		HttpServletRequest request = (HttpServletRequest) facesContext
				.getExternalContext().getRequest();
		request.getUserPrincipal().getName();
		Usuario usuarioSession = usuarioRepository.findByLogin(request.getUserPrincipal().getName());
		return usuarioSession;
	}
	
	public void cancelar(){
		System.out.println("cancelar()");
		modificar = false;
		loadDefault();
	}
	
	//-----------------------------------------

	/* Get and Set */

	public Usuario getUsuarioSistema() {
		return usuarioSistema;
	}


	public void setUsuarioSistema(Usuario usuarioSistema) {
		this.usuarioSistema = usuarioSistema;
	}

	public boolean isNuevo() {
		return nuevo;
	}

	public void setNuevo(boolean nuevo) {
		this.nuevo = nuevo;
	}

	public boolean isModificar() {
		return modificar;
	}

	public void setModificar(boolean modificar) {
		this.modificar = modificar;
	}

	public Roles getNewRoles() {
		return newRoles;
	}

	public void setNewRoles(Roles newRoles) {
		this.newRoles = newRoles;
	}

	public String getTituloPanel() {
		return tituloPanel;
	}

	public void setTituloPanel(String tituloPanel) {
		this.tituloPanel = tituloPanel;
	}

	public Roles getSelectedRoles() {
		return selectedRoles;
	}

	public void setSelectedRoles(Roles selectedRoles) {
		this.selectedRoles = selectedRoles;
	}

	public List<Roles> getListaRoles() {
		return listaRoles;
	}

	public void setListaRoles(List<Roles> listaRoles) {
		this.listaRoles = listaRoles;
	}

	public TreeNode getRoot() {
		return root;
	}

	public void setRoot(TreeNode root) {
		this.root = root;
	}

	public TreeNode[] getSelectedNodes() {
		return selectedNodes;
	}

	public void setSelectedNodes(TreeNode[] selectedNodes) {
		System.out.println("setSelectedNodes()");
		this.selectedNodes = selectedNodes;
	}

	public String getSelectionModeTreeNode() {
		return selectionModeTreeNode;
	}

	public void setSelectionModeTreeNode(String selectionModeTreeNode) {
		this.selectionModeTreeNode = selectionModeTreeNode;
	}
	
	

	
	
	
	
}
